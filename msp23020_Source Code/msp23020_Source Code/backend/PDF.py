import os
import json
import re
import csv
from google.cloud import storage, vision
from datetime import datetime
import fitz  # PyMuPDF
from fuzzywuzzy import fuzz

# Constants
BUCKET_NAME = 'your-bucket-name'
SOURCE_FILE_NAME = 'path/to/your/pdf/file.pdf'
DESTINATION_BLOB_NAME = 'Raw_T&C.pdf'
GCS_SOURCE_URI = f'gs://{BUCKET_NAME}/{DESTINATION_BLOB_NAME}'
GCS_DESTINATION_URI = f'gs://{BUCKET_NAME}/ocr-output/'
GOOGLE_APPLICATION_CREDENTIALS = '/path/to/your/GoogleCloudPlatform.json'
CSV_INPUT_PATH = 'path/to/your/input.csv'
CSV_OUTPUT_PATH = 'path/to/your/output_coordinates.csv'
PDF_OUTPUT_PATH = 'path/to/your/Annotated_Raw_T&C.pdf'
SIMILARITY_THRESHOLD = 80

# Set Google Cloud credentials
os.environ["GOOGLE_APPLICATION_CREDENTIALS"] = GOOGLE_APPLICATION_CREDENTIALS

def upload_to_gcs(bucket_name, source_file_name, destination_blob_name):
    """Uploads a file to Google Cloud Storage."""
    storage_client = storage.Client()
    bucket = storage_client.bucket(bucket_name)
    blob = bucket.blob(destination_blob_name)
    blob.upload_from_filename(source_file_name)
    print(f"File {source_file_name} uploaded to {destination_blob_name}.")

def async_detect_document(gcs_source_uri, gcs_destination_uri):
    """Performs async document OCR with PDF/TIFF as source files on GCS"""
    mime_type = "application/pdf"
    batch_size = 100
    client = vision.ImageAnnotatorClient()

    feature = vision.Feature(type_=vision.Feature.Type.DOCUMENT_TEXT_DETECTION)
    gcs_source = vision.GcsSource(uri=gcs_source_uri)
    input_config = vision.InputConfig(gcs_source=gcs_source, mime_type=mime_type)

    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    gcs_destination = vision.GcsDestination(uri=f"{gcs_destination_uri}{timestamp}/")
    output_config = vision.OutputConfig(gcs_destination=gcs_destination, batch_size=batch_size)

    async_request = vision.AsyncAnnotateFileRequest(
        features=[feature], input_config=input_config, output_config=output_config
    )

    operation = client.async_batch_annotate_files(requests=[async_request])
    print("Waiting for the OCR operation to finish.")
    operation.result(timeout=600)

    storage_client = storage.Client()
    match = re.match(r"gs://([^/]+)/(.+)", gcs_destination.uri)
    bucket_name = match.group(1)
    prefix = match.group(2)
    bucket = storage_client.get_bucket(bucket_name)
    blob_list = list(bucket.list_blobs(prefix=prefix))

    print("Output files:")
    for blob in blob_list:
        print(blob.name)

    if blob_list:
        output = blob_list[0]
        json_string = output.download_as_string()
        response = json.loads(json_string)
        return response
    else:
        print("No output files found.")
        return None

def process_ocr_results(response, pdf_path, output_pdf_path, csv_input_path, csv_output_path):
    """Process OCR results and annotate PDF"""
    document = fitz.open(pdf_path)
    rows = []

    with open(csv_input_path, newline='', encoding='utf-8') as csvfile:
        reader = csv.DictReader(csvfile)
        rows = list(reader)

    for page_num, page_response in enumerate(response['responses']):
        page = document.load_page(page_num)
        annotations = page_response['fullTextAnnotation']
        page_text = annotations['text']

        for row in rows:
            content = row['Content'].strip()
            content_normalized = re.sub(r'\s+', ' ', content, flags=re.UNICODE).strip().lower()
            page_text_normalized = re.sub(r'\s+', ' ', page_text, flags=re.UNICODE).strip().lower()

            similarity = fuzz.token_set_ratio(content_normalized, page_text_normalized)

            if similarity >= SIMILARITY_THRESHOLD:
                for paragraph in annotations['pages'][0]['blocks']:
                    paragraph_text = ''.join([word['symbols'][0]['text'] for word in paragraph['paragraphs'][0]['words']])
                    paragraph_normalized = re.sub(r'\s+', ' ', paragraph_text, flags=re.UNICODE).strip().lower()

                    if fuzz.token_set_ratio(content_normalized, paragraph_normalized) >= SIMILARITY_THRESHOLD:
                        vertices = paragraph['boundingBox']['vertices']
                        x0, y0 = vertices[0]['x'], vertices[0]['y']
                        x1, y1 = vertices[2]['x'], vertices[2]['y']

                        page.draw_rect([x0, y0, x1, y1], color=(1, 0, 0), width=1)
                        row['Coordinates'] = f"({x0/612}, {y0/866.173828125}), ({x1/612}, {y1/866.173828125})"
                        break
                else:
                    row['Coordinates'] = "Not found"
            else:
                row['Coordinates'] = "Not found"

    document.save(output_pdf_path)
    print(f"Annotated PDF saved as: {output_pdf_path}")

    with open(csv_output_path, mode='w', newline='', encoding='utf-8') as file:
        fieldnames = ['Coordinates'] + list(rows[0].keys())
        writer = csv.DictWriter(file, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(rows)

    print(f"CSV with coordinates saved as: {csv_output_path}")

def main():
    # Upload PDF to Google Cloud Storage
    upload_to_gcs(BUCKET_NAME, SOURCE_FILE_NAME, DESTINATION_BLOB_NAME)

    # Perform OCR
    ocr_response = async_detect_document(GCS_SOURCE_URI, GCS_DESTINATION_URI)

    if ocr_response:
        # Process OCR results and annotate PDF
        process_ocr_results(ocr_response, SOURCE_FILE_NAME, PDF_OUTPUT_PATH, CSV_INPUT_PATH, CSV_OUTPUT_PATH)
    else:
        print("OCR process failed. Please check your inputs and try again.")

if __name__ == "__main__":
    main()