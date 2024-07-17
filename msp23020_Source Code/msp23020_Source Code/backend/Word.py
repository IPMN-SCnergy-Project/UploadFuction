import json
import os
import sys
from docx import Document
from difflib import SequenceMatcher

def lcs(a, b):
    lengths = [[0 for _ in range(len(b) + 1)] for _ in range(len(a) + 1)]
    for i, x in enumerate(a):
        for j, y in enumerate(b):
            if x == y:
                lengths[i + 1][j + 1] = lengths[i][j] + 1
            else:
                lengths[i + 1][j + 1] = max(lengths[i + 1][j], lengths[i][j + 1])
    result = []
    x, y = len(a), len(b)
    while x != 0 and y != 0:
        if lengths[x][y] == lengths[x - 1][y]:
            x -= 1
        elif lengths[x][y] == lengths[x][y - 1]:
            y -= 1
        else:
            result.insert(0, a[x - 1])
            x -= 1
            y -= 1
    return result

def compare_json_word(json_path, word_path, output_path, threshold=0.5):
    # Load JSON file
    with open(json_path, 'r', encoding='utf-8') as file:
        json_data = json.load(file)

    # Load Word document
    doc = Document(word_path)
    word_paragraphs = [para.text.strip() for para in doc.paragraphs if para.text.strip()]

    # Find LCS to identify unchanged blocks
    orig_content = [item['Content'] for item in json_data]
    lcs_content = lcs(orig_content, word_paragraphs)

    # Use two pointers to traverse orig_content and word_paragraphs
    orig_idx, new_idx = 0, 0
    compared_data = []
    added_data = []
    last_page = 1

    while orig_idx < len(orig_content) and new_idx < len(word_paragraphs):
        orig_text = orig_content[orig_idx]
        new_text = word_paragraphs[new_idx]

        if orig_text == new_text:
            compared_data.append((orig_text, new_text, 'null'))
            orig_idx += 1
            new_idx += 1
        elif orig_text in lcs_content:
            while new_idx < len(word_paragraphs) and word_paragraphs[new_idx] not in lcs_content:
                added_data.append((word_paragraphs[new_idx], 'added', last_page))
                new_idx += 1
        else:
            similarity = SequenceMatcher(None, orig_text, new_text).ratio()
            if similarity >= threshold:
                compared_data.append((orig_text, new_text, 'modified'))
                orig_idx += 1
                new_idx += 1
            else:
                next_match_idx = next((i for i, content in enumerate(word_paragraphs[new_idx:], start=new_idx) if content in lcs_content), None)
                if next_match_idx is not None:
                    matching_lcs_idx = lcs_content.index(word_paragraphs[next_match_idx])
                    while orig_idx < len(orig_content) and orig_content[orig_idx] != lcs_content[matching_lcs_idx]:
                        if new_idx < next_match_idx:
                            similarity = SequenceMatcher(None, orig_content[orig_idx], word_paragraphs[new_idx]).ratio()
                            if similarity >= threshold:
                                compared_data.append((orig_content[orig_idx], word_paragraphs[new_idx], 'modified'))
                                new_idx += 1
                            else:
                                compared_data.append((orig_content[orig_idx], '', 'deleted'))
                        else:
                            compared_data.append((orig_content[orig_idx], '', 'deleted'))
                        orig_idx += 1
                    new_idx = next_match_idx
                else:
                    compared_data.append((orig_text, '', 'deleted'))
                    orig_idx += 1

        if orig_idx < len(json_data):
            last_page = json_data[orig_idx]['Page']

    while orig_idx < len(orig_content):
        compared_data.append((orig_content[orig_idx], '', 'deleted'))
        orig_idx += 1

    while new_idx < len(word_paragraphs):
        added_data.append((word_paragraphs[new_idx], 'added', last_page))
        new_idx += 1

    # Create modified JSON structure
    updated_data = []
    new_id = len(json_data) + 1  # Start new IDs from the next available number

    # Process original data
    for i in range(len(json_data)):
        item = json_data[i]
        orig_content, mod_content, mod_status = compared_data[i]

        approve_status = 'pending' if mod_status != 'null' else 'null'
        page = item['Page']

        # Set Block to 1 if modified status is not null
        block = 1 if mod_status != 'null' else item.get("Block", -1)

        updated_data.append({
            "id": item['id'],
            "Location": item['Location'],
            "Page": page,
            "Content": orig_content,
            "Approver": item['Approver'],
            "Modified Content 1": mod_content,
            "Modified Status 1": mod_status,
            "Approve Status 1": approve_status,
            "Block": block
        })

    # Process new entries
    for mod_content, mod_status, page in added_data:
        block = 1 if mod_status != 'null' else len(updated_data) + 1

        updated_data.append({
            "id": new_id,
            "Location": "(0.0, 0.0), (0.0, 0.0)",
            "Page": page,
            "Content": "",
            "Approver": "NONE",
            "Modified Content 1": mod_content,
            "Modified Status 1": mod_status,
            "Approve Status 1": 'pending',
            "Block": block
        })
        new_id += 1

    formatted_data = format_json(updated_data)

    # Save updated and formatted JSON
    with open(output_path, 'w', encoding='utf-8') as file:
        json.dump(formatted_data, file, ensure_ascii=False, indent=4)

def format_json(data):
    id_counter = 1
    added_entries_by_page = {}
    non_added_entries = []

    for entry in data:
        if entry['Modified Status 1'] == 'added':
            if entry['Page'] not in added_entries_by_page:
                added_entries_by_page[entry['Page']] = []
            added_entries_by_page[entry['Page']].append(entry)
        else:
            entry['id'] = id_counter
            id_counter += 1
            non_added_entries.append(entry)

    current_page = -1
    block_counter = 0
    block_counters_by_page = {}

    for entry in non_added_entries:
        if entry['Page'] != current_page:
            current_page = entry['Page']
            block_counter = 0

        if entry['Block'] != -1:
            block_counter += 1
            entry['Block'] = block_counter
        else:
            entry['Block'] = -1

        block_counters_by_page[current_page] = block_counter

    for page, entries in added_entries_by_page.items():
        block_counter = block_counters_by_page.get(page, 0)
        for entry in entries:
            block_counter += 1
            entry['Block'] = block_counter
            entry['id'] = id_counter
            id_counter += 1
            non_added_entries.append(entry)

    return non_added_entries

def main():
    if len(sys.argv) != 4:
        print("Usage: python script.py <json_path> <word_path> <output_path>")
        sys.exit(1)

    json_path = sys.argv[1]
    word_path = sys.argv[2]
    output_path = sys.argv[3]

    compare_json_word(json_path, word_path, output_path)

if __name__ == "__main__":
    main()
