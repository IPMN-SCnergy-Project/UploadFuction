<template>

  <div class="pdf-detail">

    <div class="image-container" style="position: relative;">
      <img :src="pageImagePath" alt="Page Image" class="page-image" @click="logClickPosition">
      <canvas ref="myCanvas" width="875" height="1237" style="position: absolute; top: 0%; left: 0.05%; pointer-events: none;" ></canvas>
      <div class="red-box" v-if="showRedBox"></div>
      <div class="page-navigation">
        <button @click="prevPage">Previous Page</button>
        <button @click="nextPage">Next Page</button>
        <span>&nbsp;&nbsp;&nbsp; Go to Page</span>
        <input v-model="inputPage" type="number" min="1" max="2" />
        <button @click="goToPage">Confirm</button>
        <div v-if="showAlert" class="alert-popup">{{ alertMessage }}</div>
      </div>
      
      <svg v-if="lineVisible" class="line-svg">
        <line :x1="lineStart.x" :y1="lineStart.y" :x2="lineEnd.x" :y2="lineEnd.y" stroke="red" />
      </svg>

      
      
    </div>

    <div class="comments-section">
      <button class="export-button" @click="exportComments">Export Comments</button>
      <button class="exit-button" @click="goToHome">Exit Co-edit Platform</button>
      <div class="buttons-container">
        <button
          v-for="button in filteredButtons"
          :key="button.id"
          :id="button.id"
          :class="['department-button', button.status, { active: activeButton === button.id }]"
          @dblclick="openPopup(button.id)"
          @click="drawLine(button.id)"
        >
          {{ button.text }} <strong>Department:</strong> {{ button.department }} &nbsp;&nbsp;&nbsp;<strong>Status:</strong> {{ button.status }}
        </button>
      </div>
     

      <div v-if="showPopup" class="popup">
        <div class="popup-content">

          <div class="popup-header">
            <h3>Department {{ activeButton }} Comments</h3>
            <button class="close-button" @click="closePopup">X</button>
          </div>

          <div class="popup-originalText">
            <h4>Original Text</h4>
            <div class="origin">
                <div v-for="(comment, index) in OriginalText" :key="index" class="comment-box">
                  <div class="comment-content">
                    {{ comment.text }}
                  </div>
                </div>
              </div>
          </div>

          <div class="popup-body">
            <div class="comments-history" style="width: 40%;">
              <h4>Comment History</h4>
              <div class="comments-list">
                <div v-for="(comment, index) in comments" :key="index" class="comment-box">
                  <div class="comment-content">
                    <span v-html="highlightKeywords(comment.text)"></span>
                  </div>
                  <div class="comment-info">Comment by: {{ comment.author }}, Time: {{ comment.timestamp }}</div>
                </div>
              </div>
            </div>
            <div class="add-comment" style="width: 40%;">
              <h4>Add a Comment</h4>
              <textarea v-model="newComment" placeholder="Add a comment"></textarea>
              
              <button class="submit-button" @click="submitComment">Submit Comments</button>
              <button class="approve-button">Approve Deviation</button>
              <button class="reject-button">Reject Deviation</button>
            </div>
            <div class="search" style="width: 20%;">
              <input type="text" placeholder="Search comments" style="width: 100%;">
            </div>
          </div>
          
        </div>
      </div>

      <!-- 在右半边显示评论 -->
      <!--div v-for="(comment, index) in comments" :key="index" class="comment-box">
        <div class="comment-content">{{ comment.text }}</div>
        <div class="comment-info">Comment by: {{ comment.author }}, Time: {{ comment.timestamp }}</div>
      </div>
      <textarea v-model="newComment" placeholder="Add a comment"></textarea>
      <button @click="submitComment">Submit</button-->
    </div>
    
  </div>
</template>

<script>
export default {
  props: ['user'],
  data() {
    return {
      currentPage: 1, // 初始设定为第一页
      inputPage: '',
      showAlert: false,
      alertMessage: '',
      activeButton: "", // 当前高亮的按钮
      lineVisible: false, // 控制线的显示
      lineStart: { x: 0, y: 0 }, // 线起点
      lineEnd: { x: 0, y: 0 }, // 线终点
      canvasWidth: 874.0625,
      canvasHeight: 1236.375,
      showRedBox: true, // 初始状态为显示
      
      comments: // 存储评论的数组
        [
          {
            "fileID": "1",
            "page": "1",
            "blockID": "1",
            "author": "hkuTeam@gmail.com",
            "text": "deleted",
            "timestamp": "2024-07-02 10:15:00 GMT+0800 (香港标准时间)"
          },
          {
            "fileID": "1",
            "page": "1",
            "blockID": "2",
            "author": "hkuTeam@gmail.com",
            "text": "modified to: a copy of each of the Finance Documents duly executed by the parties thereto;",
            "timestamp": "2024-07-02 10:15:00 GMT+0800 (香港标准时间)"
          },
          {
            "fileID": "1",
            "page": "1",
            "blockID": "3",
            "author": "hkuTeam@gmail.com",
            "text": "modified to: any documentation or other evidence which is reasonably requested by the Bank for the purpose of any 'know your customer' requirements;",
            "timestamp": "2024-07-02 10:15:00 GMT+0800 (香港标准时间)"
          },
          {
            "fileID": "1",
            "page": "1",
            "blockID": "4",
            "author": "hkuTeam@gmail.com",
            "text": "modified to: evidence that all fees and expenses due and payable under the Facility Letter have been or will be paid by the first utilisation date under the Facility",
            "timestamp": "2024-07-02 10:15:00 GMT+0800 (香港标准时间)"
          },
          {
            "fileID": "1",
            "page": "1",
            "blockID": "5",
            "author": "hkuTeam@gmail.com",
            "text": "added: the principle that equitable remedies may be granted or refused at the discretion of a Hong Kong court and the limitation of enforcement by laws relating to insolvency, reorganisation and other laws generally affecting the rights of creditors in Hong Kong;",
            "timestamp": "2024-07-02 10:15:00 GMT+0800 (香港标准时间)"
          },
          {
            "fileID": "1",
            "page": "1",
            "blockID": "6",
            "author": "hkuTeam@gmail.com",
            "text": "added: the time barring of claims under limitation provisions of the laws of Hong Kong, the possibility that an undertaking to assume liability for or indemnify a person against non- payment of Hong Kong stamp duty may be void and defences of set-off or counterclaim;",
            "timestamp": "2024-07-02 10:15:00 GMT+0800 (香港标准时间)"
          },
          {
            "fileID": "1",
            "page": "1",
            "blockID": "7",
            "author": "hkuTeam@gmail.com",
            "text": "added: similar principles, rights and defences under the laws of any other jurisdiction where a relevant person conducts its business or is incorporated; and",
            "timestamp": "2024-07-02 10:15:00 GMT+0800 (香港标准时间)"
          },
          {
            "fileID": "1",
            "page": "1",
            "blockID": "8",
            "author": "hkuTeam@gmail.com",
            "text": "added: any other matters which are set out as qualifications or reservations as to matters of law of general application in any legal opinion delivered to the Bank pursuant to any Finance Document.",
            "timestamp": "2024-07-02 10:15:00 GMT+0800 (香港标准时间)"
          },
          {
            "fileID": "1",
            "page": "2",
            "blockID": "1",
            "author": "hkuTeam@gmail.com",
            "text": "modified to: Authorisations: Subject to Legal Reservations, all authorisations required to enable it to lawfully enter into exercise its rights and comply with its obligations in the Finance Documents to which it is a party have been obtained or effected and are in full force and effect.  Authorisations: All authorisations required or desirable to enable each Obligor to lawfully enter into, exercise its rights and comply with its obligations under the Finance Documents to which it is a party and to carry on its business have been obtained or effected and are in full force and effect.Subject to Legal Reservations, all authorisations required to enable it to lawfully enter into exercise its rights and comply with its obligations in the Finance Documents to which it is a party have been obtained or effected and are in full force and effect. ",
            "timestamp": "2024-07-02 10:15:00 GMT+0800 (香港标准时间)"
          },
          {
            "fileID": "1",
            "page": "2",
            "blockID": "2",
            "author": "hkuTeam@gmail.com",
            "text": "modified to: _Pari passu ranking: Each Borrower shall ensure that its payment bligations under each Finance Document rank and continue to rank at least pari passu with the claims of all of its other unsecured and unsubordinated creditors, except for obligations mandatorily preferred by law applying to companies generally.",
            "timestamp": "2024-07-02 12:05:00 GMT+0800 (香港标准时间)"
          },
          {
            "fileID": "1",
            "page": "2",
            "blockID": "3",
            "author": "hkuTeam@gmail.com",
            "text": "modified to: _Amendments:. Any variation or amendment to the Agreement will be binding as between each Borrower and the Bank only if the Designated Borrower and the Bank has agreed to such variation or amendment in writing.",
            "timestamp": "2024-07-02 17:08:00 GMT+0800 (香港标准时间)"
          },
          {
            "fileID": "1",
            "page": "2",
            "blockID": "4",
            "author": "hkuTeam@gmail.com",
            "text": "modified to: _Assignments and transfers by the Bank: The Bank shall not at any time assign or transfer any of its rights and obligations under the Agreement to any other person or change its lending office without the prior consent of the Designated Borrower. The Designated Borrower is deemed to have given consent if no response is received within five Banking Days from the Designated Borrower receiving a written request from the Bank for such assignment and transfer.",
            "timestamp": "2024-07-02 19:45:00 GMT+0800 (香港标准时间)"
          },
          {
            "fileID": "1",
            "page": "2",
            "blockID": "5",
            "author": "hkuTeam@gmail.com",
            "text": "deleted",
            "timestamp": "2024-07-03 09:55:00 GMT+0800 (香港标准时间)"
          },
          {
            "fileID": "1",
            "page": "2",
            "blockID": "6",
            "author": "hkuTeam@gmail.com",
            "text": "deleted",
            "timestamp": "2024-07-03 11:00:00 GMT+0800 (香港标准时间)"
          }
        ], 
      OriginalText:[
          {
            "fileID": "1",
            "page": "1",
            "blockID": "1",
            "author": "hkuTeam@gmail.com",
            "text": "where required by the Bank, a copy of a resolution signed by all shareholders of each Borrower approving the terms of, and the transactions contemplated by the Finance Documents (to which it is a party);",
            "timestamp": "2024-07-02 10:15:00 GMT+0800 (香港标准时间)"
          },
          // {
          //   "fileID": "1",
          //   "page": "1",
          //   "blockID": "2",
          //   "author": "hkuTeam@gmail.com",
          //   "text": "each of the Finance Documents duly executed by the parties thereto;",
          //   "timestamp": "2024-07-02 10:15:00 GMT+0800 (香港标准时间)"
          // },
          // {
          //   "fileID": "1",
          //   "page": "1",
          //   "blockID": "3",
          //   "author": "hkuTeam@gmail.com",
          //   "text": "any documentation or other evidence reasonably requested by the Bank for the purpose of any 'know your customer' requirements;",
          //   "timestamp": "2024-07-02 10:15:00 GMT+0800 (香港标准时间)"
          // },
          // {
          //   "fileID": "1",
          //   "page": "1",
          //   "blockID": "4",
          //   "author": "hkuTeam@gmail.com",
          //   "text": "evidence that all fees and expenses due and payable under the Finance Documents have been or will be paid by the first utilisation date under any Facility;",
          //   "timestamp": "2024-07-02 10:15:00 GMT+0800 (香港标准时间)"
          // },
          // {
          //   "fileID": "1",
          //   "page": "1",
          //   "blockID": "5",
          //   "author": "hkuTeam@gmail.com",
          //   "text": "",
          //   "timestamp": "2024-07-02 10:15:00 GMT+0800 (香港标准时间)"
          // },
          // {
          //   "fileID": "1",
          //   "page": "1",
          //   "blockID": "6",
          //   "author": "hkuTeam@gmail.com",
          //   "text": "",
          //   "timestamp": "2024-07-02 10:15:00 GMT+0800 (香港标准时间)"
          // },
          // {
          //   "fileID": "1",
          //   "page": "1",
          //   "blockID": "7",
          //   "author": "hkuTeam@gmail.com",
          //   "text": "",
          //   "timestamp": "2024-07-02 10:15:00 GMT+0800 (香港标准时间)"
          // },
          // {
          //   "fileID": "1",
          //   "page": "1",
          //   "blockID": "8",
          //   "author": "hkuTeam@gmail.com",
          //   "text": "",
          //   "timestamp": "2024-07-02 10:15:00 GMT+0800 (香港标准时间)"
          // },
          // {
          //   "fileID": "1",
          //   "page": "2",
          //   "blockID": "1",
          //   "author": "hkuTeam@gmail.com",
          //   "text": "Authorisations: All authorisations required or desirable to enable each Obligor to lawfully enter into, exercise its rights and comply with its obligations under the Finance Documents to which it is a party and to carry on its business have been obtained or effected and are in full force and effect. ",
          //   "timestamp": "2024-07-02 10:15:00 GMT+0800 (香港标准时间)"
          // },
          // {
          //   "fileID": "1",
          //   "page": "2",
          //   "blockID": "2",
          //   "author": "hkuTeam@gmail.com",
          //   "text": "Pari passu ranking: Each Borrower undertakes that its obligations and liabilities under each Finance Document will at all times rank (except in respect of statutory preferential debts) at least pari passu with all its present and future unsecured indebtedness.",
          //   "timestamp": "2024-07-02 12:05:00 GMT+0800 (香港标准时间)"
          // },
          // {
          //   "fileID": "1",
          //   "page": "2",
          //   "blockID": "3",
          //   "author": "hkuTeam@gmail.com",
          //   "text": "Amendments: Without prejudice to Clause (Screen Rate replacement), the Agreement may be varied or amended by the Bank in its sole and absolute discretion by notice to the relevant Borrower. Any such variation or amendment will be binding on all Parties.",
          //   "timestamp": "2024-07-02 17:08:00 GMT+0800 (香港标准时间)"
          // },
          // {
          //   "fileID": "1",
          //   "page": "2",
          //   "blockID": "4",
          //   "author": "hkuTeam@gmail.com",
          //   "text": "Assignments and transfers by the Bank: The Bank may assign any of its rights or transfer or otherwise deal with any of its rights and obligations under any Finance Document or change its lending office without the prior consent of the applicable Obligor.",
          //   "timestamp": "2024-07-02 19:45:00 GMT+0800 (香港标准时间)"
          // },
          // {
          //   "fileID": "1",
          //   "page": "2",
          //   "blockID": "5",
          //   "author": "hkuTeam@gmail.com",
          //   "text": "PROCESS AGENT",
          //   "timestamp": "2024-07-03 09:55:00 GMT+0800 (香港标准时间)"
          // },
          // {
          //   "fileID": "1",
          //   "page": "2",
          //   "blockID": "6",
          //   "author": "hkuTeam@gmail.com",
          //   "text": "Where required by the Bank, a Borrower will irrevocably appoint a process agent under the Finance Documents for service of process in any proceedings before the courts of the Jurisdiction in connection with any Finance Document. The relevant Borrower must notify the Bank of any process agent's name and address. If a Borrower fails to comply, the Bank may (on such Borrower鈥檚 behalf) appoint a process agent and the Bank will notify that Borrower of the name and address of that process agent. In this regard, service of any legal process on a process agent shall constitute service on that Borrower.",
          //   "timestamp": "2024-07-03 11:00:00 GMT+0800 (香港标准时间)"
          // }
        ], 
      newComment: "", // 新评论的内容
      showPopup: false,
      pdfData: "/home/ipmn/test/upload/1-relationalDB.pdf", // 存储PDF数据的变量，如URL等
      buttons: [
        { id: "btn1", text: "1.", department: "Legal", status: "pending", page: 1 },
        { id: "btn2", text: "2.", department: "Legal", status: "approved", page: 1 },
        { id: "btn3", text: "3.", department: "Legal", status: "rejected", page: 1 },
        { id: "btn4", text: "4.", department: "Legal", status: "pending", page: 1 },
        { id: "btn5", text: "5.", department: "Legal", status: "approved", page: 1 },
        { id: "btn6", text: "6.", department: "Legal", status: "pending", page: 1 },
        { id: "btn7", text: "7.", department: "Legal", status: "pending", page: 1 },
        { id: "btn8", text: "8.", department: "Legal", status: "pending", page: 1 },
        { id: "btn1", text: "1.", department: "Legal", status: "pending", page: 2 },
        { id: "btn2", text: "2.", department: "CAM CREDIT", status: "pending", page: 2 },
        { id: "btn3", text: "3.", department: "Legal", status: "rejected", page: 2 },
        { id: "btn4", text: "4.", department: "CPM", status: "pending", page: 2 },
        { id: "btn5", text: "5.", department: "EA", status: "rejected", page: 2 },
        { id: "btn6", text: "6.", department: "EA", status: "approved", page: 2 }
      ]
    };
  },
  computed: {
    pageImagePath: function() {
        if (this.currentPage === 1) {
            return require('@/views/picture/file1.png');
        } else {
            return require('@/views/picture/file2.png');
        }
    },
    
    filteredButtons() {
      // 返回当前页面的按钮，如果用户的部门是ClientManager，则不考虑部门过滤
      return this.buttons.filter(button => 
        button.page === this.currentPage && 
        (this.user.department === 'ClientManager' || button.department === this.user.department)
      );
    },

    
  },
  mounted() {
    console.log('Received user:', this.user);
    
    this.filteredSquares();
    // this.loadPdfViewer();
  },
  
  methods: {

    
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
        this.filteredSquares();
      }
      this.highlightButton('');
      this.lineVisible = false;
    },
    nextPage() {
      if (this.currentPage < 2) {
        this.currentPage++;
        this.filteredSquares();
      }
      this.highlightButton('');
      this.lineVisible = false;
    },

    goToPage() {
      const page = parseInt(this.inputPage, 10);
      if (page === 1 || page === 2) {
        this.currentPage = page;
        this.errorMessage = '';
        this.filteredSquares();
      } else {
        this.showAlertMessage('Out of range');
        this.inputPage = '';
      }
    },

    showAlertMessage(message) {
      this.alertMessage = message;
      this.showAlert = true;
      setTimeout(() => {
        this.showAlert = false;
      }, 1000); // 弹窗显示1秒后消失
    },
    
    logClickPosition(event) {
      const rect = event.target.getBoundingClientRect();
      const x = event.clientX - rect.left;
      console.log("width=",rect.right - rect.left);
      console.log("height=",rect.bottom - rect.top);

      const x_percentage = x / (rect.right - rect.left);
      const y = event.clientY - rect.top;
      const y_percentage = y / (rect.bottom - rect.top);
      console.log(`Click position: x=${x}, y=${y}, x_percentage=${x_percentage}, y_percentage=${y_percentage}`);

      // 判断是否在指定范围内
      if (this.currentPage === 2 && x_percentage >= 0.11 && x_percentage <= 0.49 && y_percentage >= 0.18 && y_percentage <= 0.24) {
        this.highlightButton('btn1');
        // 计算线的终点，即按钮的位置
        const btn = document.getElementById('btn1');
        const btnRect = btn.getBoundingClientRect();
        console.log(`Button position: left=${btnRect.left}, top=${btnRect.top}`);
        // const btnX = btnRect.left + btnRect.width / 2 - rect.left;
        // const btnY = btnRect.top + btnRect.height / 2 - rect.top;
        const btnX = btnRect.left;
        const btnY = btnRect.top + btnRect.height - rect.top;

        this.lineStart = { x: x, y: y };
        this.lineEnd = { x: btnX, y: btnY };
        this.lineVisible = true;
      } 
      else if (this.currentPage === 2 && x_percentage >= 0.11 && x_percentage <= 0.49 && y_percentage >= 0.50 && y_percentage <= 0.57) {
        this.highlightButton('btn2');
        // 计算线的终点，即按钮的位置
        const btn = document.getElementById('btn2');
        const btnRect = btn.getBoundingClientRect();
        console.log(`Button position: left=${btnRect.left}, top=${btnRect.top}`);
        // const btnX = btnRect.left + btnRect.width / 2 - rect.left;
        // const btnY = btnRect.top + btnRect.height / 2 - rect.top;
        const btnX = btnRect.left;
        const btnY = btnRect.top + btnRect.height - rect.top;

        this.lineStart = { x: x, y: y };
        this.lineEnd = { x: btnX, y: btnY };
        this.lineVisible = true;
      }
      else if (this.currentPage === 2 && x_percentage >= 0.11 && x_percentage <= 0.49 && y_percentage >= 0.63 && y_percentage <= 0.69) {
        this.highlightButton('btn3');
        // 计算线的终点，即按钮的位置
        const btn = document.getElementById('btn3');
        const btnRect = btn.getBoundingClientRect();
        console.log(`Button position: left=${btnRect.left}, top=${btnRect.top}`);
        // const btnX = btnRect.left + btnRect.width / 2 - rect.left;
        // const btnY = btnRect.top + btnRect.height / 2 - rect.top;
        const btnX = btnRect.left;
        const btnY = btnRect.top + btnRect.height - rect.top;

        this.lineStart = { x: x, y: y };
        this.lineEnd = { x: btnX, y: btnY };
        this.lineVisible = true;
      }
      else if (this.currentPage === 2 && x_percentage >= 0.54 && x_percentage <= 0.93 && y_percentage >= 0.16 && y_percentage <= 0.22) {
        this.highlightButton('btn4');
        // 计算线的终点，即按钮的位置
        const btn = document.getElementById('btn4');
        const btnRect = btn.getBoundingClientRect();
        console.log(`Button position: left=${btnRect.left}, top=${btnRect.top}`);
        // const btnX = btnRect.left + btnRect.width / 2 - rect.left;
        // const btnY = btnRect.top + btnRect.height / 2 - rect.top;
        const btnX = btnRect.left;
        const btnY = btnRect.top + btnRect.height - rect.top;

        this.lineStart = { x: x, y: y };
        this.lineEnd = { x: btnX, y: btnY };
        this.lineVisible = true;
      }
      else if (this.currentPage === 2 && x_percentage >= 0.54 && x_percentage <= 0.93 && y_percentage >= 0.36 && y_percentage <= 0.49) {
        this.highlightButton('btn5');
        // 计算线的终点，即按钮的位置
        const btn = document.getElementById('btn5');
        const btnRect = btn.getBoundingClientRect();
        console.log(`Button position: left=${btnRect.left}, top=${btnRect.top}`);
        // const btnX = btnRect.left + btnRect.width / 2 - rect.left;
        // const btnY = btnRect.top + btnRect.height / 2 - rect.top;
        const btnX = btnRect.left;
        const btnY = btnRect.top + btnRect.height - rect.top;

        this.lineStart = { x: x, y: y };
        this.lineEnd = { x: btnX, y: btnY };
        this.lineVisible = true;
      }
      else if (this.currentPage === 2 && x_percentage >= 0.54 && x_percentage <= 0.93 && y_percentage >= 0.38 && y_percentage <= 0.49) {
        this.highlightButton('btn6');
        // 计算线的终点，即按钮的位置
        const btn = document.getElementById('btn6');
        const btnRect = btn.getBoundingClientRect();
        console.log(`Button position: left=${btnRect.left}, top=${btnRect.top}`);
        // const btnX = btnRect.left + btnRect.width / 2 - rect.left;
        // const btnY = btnRect.top + btnRect.height / 2 - rect.top;
        const btnX = btnRect.left;
        const btnY = btnRect.top + btnRect.height - rect.top;

        this.lineStart = { x: x, y: y };
        this.lineEnd = { x: btnX, y: btnY };
        this.lineVisible = true;
      }
      else if (this.currentPage === 1 && x_percentage >= 0.15 && x_percentage <= 0.50 && y_percentage >= 0.78 && y_percentage <= 0.83) {
        this.highlightButton('btn1');
        // 计算线的终点，即按钮的位置
        const btn = document.getElementById('btn1');
        const btnRect = btn.getBoundingClientRect();
        console.log(`Button position: left=${btnRect.left}, top=${btnRect.top}`);
        // const btnX = btnRect.left + btnRect.width / 2 - rect.left;
        // const btnY = btnRect.top + btnRect.height / 2 - rect.top;
        const btnX = btnRect.left;
        const btnY = btnRect.top + btnRect.height - rect.top;

        this.lineStart = { x: x, y: y };
        this.lineEnd = { x: btnX, y: btnY };
        this.lineVisible = true;
      }
      else if (this.currentPage === 1 && x_percentage >= 0.15 && x_percentage <= 0.50 && y_percentage >= 0.83 && y_percentage <= 0.85) {
        this.highlightButton('btn2');
        // 计算线的终点，即按钮的位置
        const btn = document.getElementById('btn2');
        const btnRect = btn.getBoundingClientRect();
        console.log(`Button position: left=${btnRect.left}, top=${btnRect.top}`);
        // const btnX = btnRect.left + btnRect.width / 2 - rect.left;
        // const btnY = btnRect.top + btnRect.height / 2 - rect.top;
        const btnX = btnRect.left;
        const btnY = btnRect.top + btnRect.height - rect.top;

        this.lineStart = { x: x, y: y };
        this.lineEnd = { x: btnX, y: btnY };
        this.lineVisible = true;
      }
      else if (this.currentPage === 1 && x_percentage >= 0.60 && x_percentage <= 0.95 && y_percentage >= 0.17 && y_percentage <= 0.21) {
        this.highlightButton('btn3');
        // 计算线的终点，即按钮的位置
        const btn = document.getElementById('btn3');
        const btnRect = btn.getBoundingClientRect();
        console.log(`Button position: left=${btnRect.left}, top=${btnRect.top}`);
        // const btnX = btnRect.left + btnRect.width / 2 - rect.left;
        // const btnY = btnRect.top + btnRect.height / 2 - rect.top;
        const btnX = btnRect.left;
        const btnY = btnRect.top + btnRect.height - rect.top;

        this.lineStart = { x: x, y: y };
        this.lineEnd = { x: btnX, y: btnY };
        this.lineVisible = true;
      }
      else if (this.currentPage === 1 && x_percentage >= 0.60 && x_percentage <= 0.95 && y_percentage >= 0.21 && y_percentage <= 0.25) {
        this.highlightButton('btn4');
        // 计算线的终点，即按钮的位置
        const btn = document.getElementById('btn4');
        const btnRect = btn.getBoundingClientRect();
        console.log(`Button position: left=${btnRect.left}, top=${btnRect.top}`);
        // const btnX = btnRect.left + btnRect.width / 2 - rect.left;
        // const btnY = btnRect.top + btnRect.height / 2 - rect.top;
        const btnX = btnRect.left;
        const btnY = btnRect.top + btnRect.height - rect.top;

        this.lineStart = { x: x, y: y };
        this.lineEnd = { x: btnX, y: btnY };
        this.lineVisible = true;
      }
      else {
        this.highlightButton('');
        this.lineVisible = false;
      }
    
    },

    highlightButton(buttonId) {
      // 先移除所有按钮的 active 类
      const buttons = document.querySelectorAll('.department-button');
      buttons.forEach(button => {
        button.classList.remove('active');
      });
      // 然后为指定按钮添加 active 类
      const activeBtn = document.getElementById(buttonId);
      if (activeBtn) {
        activeBtn.classList.add('active');
      }
    },   

    filteredSquares() {
      const canvas = this.$refs.myCanvas;
      this.ctx = canvas.getContext("2d");
      this.ctx.clearRect(0, 0, canvas.width, canvas.height);
      // 画出当前页面的框
      console.log("x,y,start,end outside",this.currentPage,this.user.department);
      if (this.currentPage === 2 && this.user.department==="Legal" ){
        const xMin = 0.11; // X轴最小比例
        const xMax = 0.49; // X轴最大比例
        const yMin = 0.18; // Y轴最小比例
        const yMax = 0.24; // Y轴最大比例
        const xMin2 = 0.11; // X轴最小比例
        const xMax2 = 0.49; // X轴最大比例
        const yMin2 = 0.63; // Y轴最小比例
        const yMax2 = 0.7; // Y轴最大比例

        // 计算方框的坐标
        const xStart = xMin * this.canvasWidth;
        const xEnd = xMax * this.canvasWidth;
        const yStart = yMin * this.canvasHeight;
        const yEnd = yMax * this.canvasHeight;
        const xStart2 = xMin2 * this.canvasWidth;
        const xEnd2 = xMax2 * this.canvasWidth;
        const yStart2 = yMin2 * this.canvasHeight;
        const yEnd2 = yMax2 * this.canvasHeight;

        // 绘制红色方框
        this.ctx.strokeStyle = "red";
        this.ctx.lineWidth = 2;
        console.log("x,y,start,end",xStart,yStart, xEnd - xStart, yEnd - yStart);
        this.ctx.strokeRect(xStart, yStart, xEnd - xStart, yEnd - yStart);
        // this.ctx.strokeRect(0,0,100,100);
        this.ctx.strokeRect(xStart2, yStart2, xEnd2 - xStart2, yEnd2 - yStart2);
        console.log("x2,y2,start2,end2",xStart2, yStart2, xEnd2 - xStart2, yEnd2 - yStart2);


      }
        
      else if (this.currentPage === 1 && this.user.department==="Legal" ){
        console.log("test page1",this.currentPage);
        const xMin = [0.15,0.15,0.60,0.60]; // X轴最小比例
        const xMax = [0.50,0.50,0.95,0.95]; // X轴最大比例
        const yMin = [0.78,0.83,0.17,0.21]; // Y轴最小比例
        const yMax = [0.83,0.85,0.21,0.25]; // Y轴最大比例
        
        // 计算方框的坐标
        for(let i=0;i<4;i++){
          console.log("test page1",i);
          const xStart = xMin[i] * this.canvasWidth;
          const xEnd = xMax[i] * this.canvasWidth;
          const yStart = yMin[i] * this.canvasHeight;
          const yEnd = yMax[i] * this.canvasHeight;
        
        

        // 绘制红色方框
          this.ctx.strokeStyle = "red";
          this.ctx.lineWidth = 2;
          console.log("x1,y1,start1,end1",xStart,yStart, xEnd - xStart, yEnd - yStart);
          this.ctx.strokeRect(xStart, yStart, xEnd - xStart, yEnd - yStart);
          // this.ctx.strokeRect(0,0,100,100);
        // this.ctx.strokeRect(xStart2, yStart2, xEnd2 - xStart2, yEnd2 - yStart2);
        // console.log("x2,y2,start2,end2",xStart2, yStart2, xEnd2 - xStart2, yEnd2 - yStart2);
        }

      }
    },

    submitComment() {
      let blockID = this.activeButton.match(/\d+/)[0];
      // 将新评论添加到评论数组中
      this.comments.push({ page: this.currentPage, blockID: blockID, text: this.newComment, author: this.user.uname, timestamp: new Date() });
      // 清空输入框
      this.newComment = "";
      // 可以与后端API交互，保存评论数据
    },

    openPopup(buttonId) {
      console.log('Received user in Popup', this.user);
      this.activeButton = buttonId;
      this.showPopup = true;
    },
    closePopup() {
      this.showPopup = false;
    },

    drawLine() {

    },

    getCurrentFormattedDateTime() {
      const now = new Date();
      const year = now.getFullYear();
      const month = String(now.getMonth() + 1).padStart(2, '0');
      const day = String(now.getDate()).padStart(2, '0');
      const hours = String(now.getHours()).padStart(2, '0');
      const minutes = String(now.getMinutes()).padStart(2, '0');
      const seconds = String(now.getSeconds()).padStart(2, '0');

      return `${year}${month}${day}${hours}${minutes}${seconds}`;
    },

    highlightKeywords(text) {
      const keywords = ['Authorisations', 'modified', 'exercise']; // 要标记为红色的关键词
      return text.replace(
        new RegExp(keywords.join('|'), 'g'),
        match => `<span style="color: red;">${match}</span>`
      );
    },

    exportComments() {
      // Implement your export logic here
      console.log('Exporting comments');

      // Helper function to escape and quote CSV values
      const escapeCsvValue = (value) => {
          if (typeof value === 'string') {
              // If the value contains a comma, newline or double quote, enclose it in double quotes and escape existing double quotes
              if (value.includes(',') || value.includes('\n') || value.includes('"')) {
                  return `"${value.replace(/"/g, '""')}"`;
              }
          }
          return value;
      };

      // 创建 CSV 头部
      let csvContent = "data:text/csv;charset=utf-8,";
      csvContent += "Page,Block,Timestamp,Author,Comment\n";

      // 添加每一行评论
      this.comments.forEach(comment => {
        const row = `${escapeCsvValue(comment.page)},${escapeCsvValue(comment.blockID)},${escapeCsvValue(comment.timestamp)},${escapeCsvValue(comment.author)},${escapeCsvValue(comment.text)}`;
        csvContent += row + "\n";
      });

      // 创建一个隐藏的下载链接
      const encodedUri = encodeURI(csvContent);
      const link = document.createElement("a");
      const currentDateTime = this.getCurrentFormattedDateTime();
      console.log(currentDateTime);

      link.setAttribute("href", encodedUri);
      link.setAttribute("download", "Dummy T&C_Extract_Comments_"+currentDateTime+".csv");

      // 模拟点击以触发下载
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },

    goToHome() {
      this.$router.push('/home'); // 根据实际情况修改路由路径
    }
  }
};
</script>

<style scoped>
    /* 样式设置 */
    body {
        margin: 0;
        padding: 0;
    }

    .main-container {
        padding-top: 1px; /* 调整顶边内边距 */
    }

    .pdf-detail {
        display: flex;
        position: relative; /* 确保父容器相对定位 */
        margin-top: 1px; /* 减少顶边外边距 */
    }

    .image-container {
        position: relative;
        max-width: 70%;
        margin-bottom: 1px;
    }

    .page-image {
        max-width: 100%;
        height: auto;
    }

    .page-navigation {
        position: absolute;
        bottom: 1px;
        left: 50%;
        transform: translateX(-50%);
        display: flex;
        gap: 10px;
    }

    .line-svg {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        pointer-events: none; /* 禁用鼠标事件 */
    }

    .comments-section {
        /* 右半边样式设置 */
        width: 35%;
        padding: 20px;
        padding-right: 0; /* 设置距离右侧边框 */
        display: flex;
        flex-direction: column;
        align-items: flex-end; /* 使内容靠右对齐 */
        gap: 20px; /* 添加这个gap来保证内容之间的间距 */
    }

    .buttons-container {
        display: flex;
        flex-direction: column;
        gap: 20px;
        margin-bottom: 10px;
        width: 100%;
    }

    .export-button {
        position: absolute;
        top: 5px;
        right: 210px;
        font-size: 20px;
        background-color: #ff6347;
        color: #fff;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        margin-bottom: 20px;
    }

    .exit-button {
        position: absolute;
        top: 5px;
        right: 5px;
        font-size: 20px;
        background-color: #c7ccd2fb;
        color: #0e0101;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        margin-bottom: 20px; /* 设置与下方 div 的间距 */
    }

    .department-button {
        position: relative;    /* 相对定位以便内部元素使用绝对定位 */
        margin-top: 20px;
        width: 100%;
        height: 100px;
        background-color: #b2cbe2;
        border: 1px solid #83bedf;
        font-size: 16px;
        cursor: pointer;
        text-align: left;      /* 左对齐 */
        padding-left: 20px;    /* 添加左边距 */
        overflow: hidden;      /* 防止内部元素溢出 */
    }

    .department-button::before {
        content: "COMMENTS";
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        font-size: 36px;        /* 调整字体大小 */
        color: rgba(255, 255, 255, 0.3);  /* 半透明白色 */
        pointer-events: none;   /* 让文字不可点击 */
        white-space: nowrap;    /* 防止文字换行 */
    }

    .department-button.active {
        background-color: #c1d732;  /* 高亮显示背景颜色 */
    }

    .comment-box {
        margin-bottom: 10px;
        border: 1px solid #ccc;
        padding: 1px;
    }

    .comment-info {
        font-size: 12px;
        color: #666;
    }

    .popup {
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.5);
        display: flex;
        justify-content: center;
        align-items: center;
    }

    .popup-content {
        background: #fff;
        padding: 20px;
        width: 80%;
        max-width: 800px;
        border-radius: 10px;
        display: flex;
        flex-direction: column;
    }

    .popup-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .popup-header h3 {
        margin: 0; /* 去掉标题的外边距 */
    }

    .close-button {
        background: red;
        color: white;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        font-size: 14px;
        padding: 5px 10px; /* 调整按钮的内边距 */
        height: auto; /* 确保按钮高度自动适应内容 */
    }

    .popup-body {
        display: flex;
        justify-content: space-between;
        margin-top: 2px;
    }

    .comments-history {
        flex: 1;
        overflow-y: auto;
        max-height: 400px; /* 设置最大高度，并自动出现滚动条 */
    }

    .comments-list .comment-box {
        border-bottom: 1px solid #ccc;
        padding-bottom: 10px;
        margin-bottom: 10px;
    }

    .add-comment {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: 10px;
        margin-left: 20px;
    }

    .add-comment textarea {
        flex: 1;
        resize: none;
    }

    .submitComment {
        background-color: #478ad2;
        color: white;
        border: none;
        padding: 10px;
        border-radius: 40px;
        cursor: pointer;
    }

    .submit-button {
        background-color: #b81b1b;
        color: white;
        cursor: pointer;
    }

    .approve-button {
        border: none;
        padding: 10px;
        cursor: pointer;
        width: 100%;
        margin-top: 10px;
    }

    .reject-button {
        border: none;
        padding: 10px;
        cursor: pointer;
        width: 100%;
        margin-top: 10px;
    }

    .alert-popup {
      position: fixed;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      background-color: rgba(255, 0, 0, 0.8);
      color: white;
      padding: 10px 20px;
      border-radius: 5px;
      z-index: 1000;
    }
</style>
