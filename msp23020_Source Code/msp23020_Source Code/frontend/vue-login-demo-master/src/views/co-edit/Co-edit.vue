<template>
  <div class="pdf-detail">
    <div class="image-container" style="position: relative;">
      <img :src="pageImagePath" alt="Page Image" class="page-image" @load="onImageLoad" @dblclick="logClickPosition">
      <canvas ref="myCanvas" style="position: absolute; top: 0%; left: 0%; pointer-events: none;"></canvas>
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
        >
          {{ button.text }} <strong>Department:</strong> {{ button.department }} &nbsp;&nbsp;&nbsp;<strong>Status:</strong> {{ button.status }}
        </button>
      </div>
      <div v-if="showPopup" class="popup">
        <div class="popup-content">
          <div class="popup-header">
            <h3>Department: {{ getDepartment(activeButton) }} - {{ activeButton }} Comments</h3>
            <button class="close-button" @click="closePopup">X</button>
          </div>
          <!-- originaltext area -->
          <div class="popup-originalText">
            <h4>Original Text</h4>
            <div class="origin">
              <!-- <div v-for="(comment, index) in OriginalText" :key="index" class="comment-box"> -->
              <div v-if="activeButtonContent" class="comment-box">
                <div class="comment-content">
                  <!-- <span v-for="(part, partIndex) in getDiffResult(comment)" :key="partIndex"> -->
                    <!-- <span v-if="part.removed" class="highlight">{{ part.value }}</span> -->
                    <!-- <span v-else-if="!part.added">{{ part.value }}</span> -->
                  <!-- /span -->
                  {{ activeButtonContent }}
                </div>
              </div>
              <div v-else class="comment-box">
                <div class="comment-content">
                  No content available.
                </div>
              </div>
            </div>
          </div>
          <div class="popup-body">
            <!-- comment area -->
            <div class="comments-history" style="width: 40%;">
              <h4>Comment History</h4>
              <div class="comments-list">
                <div v-for="(comment, index) in filteredComments" :key="index" class="comment-box">
                  <div class="comment-content">
                    <!-- <span v-html="highlightDiff(OriginalText[index].text, comment.text)"></span> -->
                    <span v-html="highlightDiff(comment.content, comment.text)"></span>
                  </div>
                  <div class="comment-info">Comment by: {{ comment.author }}, Time: {{ comment.timestamp }}</div>
                </div>
              </div>
            </div>
            <div class="add-comment" style="width: 40%;">
              <h4>Add a Comment</h4>
              <textarea v-model="newComment" placeholder="Add a comment"></textarea>
              <button class="submit-button" @click="submitComment">Submit Comments</button>
              <button 
                v-if="user.department !== 'ClientManager'" 
                class="approve-button" 
                @click="updateStatus(2)"
              >
                Approve Deviation
              </button>
              <button 
                v-if="user.department !== 'ClientManager'" 
                class="reject-button" 
                @click="updateStatus(3)"
              >
                Reject Deviation
              </button>
            </div>
            <div class="search" style="width: 20%;">
              <input type="text" placeholder="Search comments" style="width: 100%;">
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>



<script>
import { diffWords } from 'diff';
import axios from 'axios';

export default { 
  props: ['user','fileinfo'],
  data() {
    return {
      currentPage: 1,
      inputPage: '',
      showAlert: false,
      alertMessage: '',
      activeButton: "",
      lineVisible: false,
      lineStart: { x: 0, y: 0 },
      lineEnd: { x: 0, y: 0 },
      canvasWidth: 0,
      canvasHeight: 0,
      showRedBox: true,
      receivedData: null,//data from backend
      blockNum_page1: null,
      blockNum_page2: null,
      // receivedDataFromNewComment: null,
      
      comments: [
        //   {
        //     "fileName": "",
        //     "page": "1",
        //     "blockID": "3",
        //     "author": "hkuteam",
        //     "content": "",
        //     "text": "any documentation or other evidence which is reasonably requested by the Bank for the purpose of any "know your customer" requirements;",
        //     "timestamp": "2024-07-02 10:15:00 GMT+0800 (香港标准时间)"
        //   }
      ],

      // OriginalText: [
      //   {
      //     "fileID": "1",
      //     "page": "1",
      //     "blockID": "3",
      //     "author": "hkuTeam@gmail.com",
      //     "text": "any documentation or other evidence reasonably requested by the Bank for the purpose of any 'know your customer' requirements;",
      //     "timestamp": "2024-07-02 10:15:00 GMT+0800 (香港标准时间)"
      //   }
      // ],
      
      newComment: "",
      showPopup: false,
      pdfData: "",
      buttons: [
        // { id: "btn3", text: "3.", department: "Legal", status: "pending", page: 1, content: "", left_blockId: "" }
      ],
      blockPositions: {
        1: [
          { id: "btn1", xMin: 0.0739, xMax: 0.4958, yMin: 0.0856, yMax: 0.1066, vis:false },
          { id: "btn2", xMin: 0.0756, xMax: 0.1482, yMin: 0.1130, yMax: 0.1233, vis:false },
          { id: "btn3", xMin: 0.1160, xMax: 0.4252, yMin: 0.1439, yMax: 0.1534, vis:false },
          { id: "btn4", xMin: 0.1160, xMax: 0.4958, yMin: 0.1629, yMax: 0.1931, vis:false },
          { id: "btn5", xMin: 0.1160, xMax: 0.4975, yMin: 0.2021, yMax: 0.2419, vis:false },
          { id: "btn6", xMin: 0.1210, xMax: 0.3715, yMin: 0.2509, yMax: 0.2604, vis:false },
          { id: "btn7", xMin: 0.1563, xMax: 0.2857, yMin: 0.2640, yMax: 0.2735, vis:false },
          { id: "btn8", xMin: 0.1563, xMax: 0.3545, yMin: 0.2782, yMax: 0.2866, vis:false },
          { id: "btn9", xMin: 0.1563, xMax: 0.4622, yMin: 0.2913, yMax: 0.2996, vis:false },
          { id: "btn10", xMin: 0.1160, xMax: 0.4067, yMin: 0.3068, yMax: 0.3175, vis:false },
          { id: "btn11", xMin: 0.1210, xMax: 0.4954, yMin: 0.3246, yMax: 0.3341, vis:false },
          { id: "btn12", xMin: 0.1160, xMax: 0.4975, yMin: 0.3412, yMax: 0.3890, vis:false },
          { id: "btn13", xMin: 0.1160, xMax: 0.4975, yMin: 0.3912, yMax: 0.4250, vis:false },
          { id: "btn14", xMin: 0.1160, xMax: 0.4958, yMin: 0.4304, yMax: 0.4602, vis:false },
          { id: "btn15", xMin: 0.1160, xMax: 0.4958, yMin: 0.4685, yMax: 0.5303, vis:false },
          { id: "btn16", xMin: 0.1160, xMax: 0.4958, yMin: 0.5398, yMax: 0.5600, vis:false },
          { id: "btn17", xMin: 0.1143, xMax: 0.4975, yMin: 0.5672, yMax: 0.5981, vis:false },
          { id: "btn18", xMin: 0.1143, xMax: 0.4958, yMin: 0.6076, yMax: 0.6468, vis:false },
          { id: "btn19", xMin: 0.1140, xMax: 0.4941, yMin: 0.6564, yMax: 0.6747, vis:false },
          { id: "btn20", xMin: 0.1563, xMax: 0.4622, yMin: 0.6801, yMax: 0.6885, vis:false },
          { id: "btn21", xMin: 0.1546, xMax: 0.4958, yMin: 0.6944, yMax: 0.7776, vis:false },
          { id: "btn22", xMin: 0.1563, xMax: 0.4958, yMin: 0.7836, yMax: 0.8252, vis:false },
          { id: "btn23", xMin: 0.1563, xMax: 0.4958, yMin: 0.8300, yMax: 0.8490, vis:false },
          { id: "btn24", xMin: 0.1563, xMax: 0.4353, yMin: 0.8537, yMax: 0.8633, vis:false },
          { id: "btn25", xMin: 0.1563, xMax: 0.4958, yMin: 0.8668, yMax: 0.8870, vis:false },
          { id: "btn26", xMin: 0.6034, xMax: 0.9462, yMin: 0.0856, yMax: 0.1379, vis:false },
          { id: "btn27", xMin: 0.6050, xMax: 0.9445, yMin: 0.1427, yMax: 0.1724, vis:false },
          { id: "btn28", xMin: 0.6034, xMax: 0.9462, yMin: 0.1760, yMax: 0.2081, vis:false },
          { id: "btn29", xMin: 0.6050, xMax: 0.9445, yMin: 0.2117, yMax: 0.2438, vis:false },
          { id: "btn30", xMin: 0.6034, xMax: 0.9429, yMin: 0.2473, yMax: 0.2675, vis:false },
          { id: "btn31", xMin: 0.6034, xMax: 0.9445, yMin: 0.2723, yMax: 0.3460, vis:false },
          { id: "btn32", xMin: 0.6034, xMax: 0.9445, yMin: 0.3520, yMax: 0.3710, vis:false },
          { id: "btn33", xMin: 0.5647, xMax: 0.8000, yMin: 0.3770, yMax: 0.3890, vis:false },
          { id: "btn34", xMin: 0.6034, xMax: 0.9462, yMin: 0.3924, yMax: 0.4340, vis:false },
          { id: "btn35", xMin: 0.6034, xMax: 0.9445, yMin: 0.4376, yMax: 0.4578, vis:false },
          { id: "btn36", xMin: 0.5647, xMax: 0.9445, yMin: 0.4828, yMax: 0.5018, vis:false },
          { id: "btn37", xMin: 0.5630, xMax: 0.9445, yMin: 0.5113, yMax: 0.5517, vis:false },
          { id: "btn38", xMin: 0.5647, xMax: 0.8891, yMin: 0.5874, yMax: 0.5957, vis:false },
          { id: "btn39", xMin: 0.5630, xMax: 0.9445, yMin: 0.6052, yMax: 0.6706, vis:false },
          { id: "btn40", xMin: 0.5653, xMax: 0.6108, yMin: 0.6768, yMax: 0.6873, vis:false },
          { id: "btn41", xMin: 0.6034, xMax: 0.9462, yMin: 0.6908, yMax: 0.7218, vis:false },
          { id: "btn42", xMin: 0.6050, xMax: 0.9445, yMin: 0.7265, yMax: 0.7551, vis:false },
          { id: "btn43", xMin: 0.5647, xMax: 0.9445, yMin: 0.7658, yMax: 0.7955, vis:false },
          { id: "btn44", xMin: 0.5647, xMax: 0.9445, yMin: 0.8038, yMax: 0.8335, vis:false },
          { id: "btn45", xMin: 0.6034, xMax: 0.9109, yMin: 0.8383, yMax: 0.8490, vis:false },
          { id: "btn46", xMin: 0.6034, xMax: 0.9445, yMin: 0.8512, yMax: 0.8714, vis:false }
        ],
        2: [
          { id: "btn47", xMin: 0.1525, xMax: 0.5065, yMin: 0.0997, yMax: 0.1300, vis:false },
          { id: "btn48", xMin: 0.1133, xMax: 0.5065, yMin: 0.1371, yMax: 0.1792, vis:false },
          { id: "btn49", xMin: 0.1133, xMax: 0.5065, yMin: 0.1854, yMax: 0.2392, vis:false },
          { id: "btn50", xMin: 0.1133, xMax: 0.5064, yMin: 0.2444, yMax: 0.3095, vis:false },
          { id: "btn51", xMin: 0.1133, xMax: 0.5064, yMin: 0.3159, yMax: 0.3407, vis:false },
          { id: "btn52", xMin: 0.1133, xMax: 0.3495, yMin: 0.3605, yMax: 0.3735, vis:false },
          { id: "btn53", xMin: 0.1133, xMax: 0.3939, yMin: 0.3797, yMax: 0.3899, vis:false },
          { id: "btn54", xMin: 0.1525, xMax: 0.5066, yMin: 0.3927, yMax: 0.4345, vis:false },
          { id: "btn55", xMin: 0.1525, xMax: 0.5065, yMin: 0.4373, yMax: 0.5057, vis:false },
          { id: "btn56", xMin: 0.1525, xMax: 0.4195, yMin: 0.5095, yMax: 0.5187, vis:false },

          { id: "btn57", xMin: 0.1133, xMax: 0.5065, yMin: 0.5269, yMax: 0.5775, vis:false },
          { id: "btn58", xMin: 0.1133, xMax: 0.5065, yMin: 0.5800, yMax: 0.6202, vis:false },
          { id: "btn59", xMin: 0.1133, xMax: 0.3712, yMin: 0.6387, yMax: 0.6517, vis:false },
          { id: "btn60", xMin: 0.1133, xMax: 0.5065, yMin: 0.6579, yMax: 0.7108, vis:false },
          { id: "btn61", xMin: 0.1133, xMax: 0.5065, yMin: 0.7169, yMax: 0.8064, vis:false },
          { id: "btn62", xMin: 0.1133, xMax: 0.5065, yMin: 0.8125, yMax: 0.8759, vis:false },

          { id: "btn63", xMin: 0.5498, xMax: 0.7984, yMin: 0.0964, yMax: 0.1124, vis:false },
          { id: "btn64", xMin: 0.5498, xMax: 0.9530, yMin: 0.1130, yMax: 0.1667, vis:false },
          { id: "btn65", xMin: 0.5498, xMax: 0.9529, yMin: 0.1688, yMax: 0.2267, vis:false },
          { id: "btn66", xMin: 0.5498, xMax: 0.9530, yMin: 0.2278, yMax: 0.2641, vis:false },
          { id: "btn67", xMin: 0.5890, xMax: 0.9530, yMin: 0.2669, yMax: 0.3303, vis:false },
          { id: "btn68", xMin: 0.5890, xMax: 0.9529, yMin: 0.3331, yMax: 0.3539, vis:false },
          { id: "btn69", xMin: 0.5498, xMax: 0.9530, yMin: 0.3727, yMax: 0.3880, vis:false },
          { id: "btn70", xMin: 0.5498, xMax: 0.9530, yMin: 0.3900, yMax: 0.5050, vis:false }
        ]
      }
    };
  },

  computed: {
    pageImagePath() {
      return this.currentPage === 1 ? require('@/views/picture/file1.png') : require('@/views/picture/file2.png');
    },

    filteredButtons() {
      return this.buttons.filter(button => {
        let buttonDepartment = button.department;
        if (buttonDepartment === 'NONE') {
          buttonDepartment = 'LEGAL';
        }
        return button.page === this.currentPage &&
          (this.user.department === 'ClientManager' || buttonDepartment === this.user.department);
      });
    },

    filteredComments() {
      const blockId = Number(this.activeButton.match(/\d+/)[0]);  // comments中的blockId是右侧buttons的id
      // console.log('comments shown in filteredComments(): ', this.comments);
      
      const filtered = this.comments.filter(comment => {
        const isBlockIDMatch = comment.blockId == blockId;
        const isPageMatch = comment.page == this.currentPage;

        // output each comment match result
        console.log('Comment:', comment);
        console.log('comment.page: ',comment.page)
        console.log('compare blockID:', comment.blockId, blockId);
        console.log('Block ID Match:', isBlockIDMatch);
        console.log('Page Match:', isPageMatch);

        return isBlockIDMatch && isPageMatch;
      });

      console.log('Filtered comments:', filtered);
      return filtered;
    },

    activeButtonContent() {
      const button = this.buttons.find(button => button.id === this.activeButton && button.page === this.currentPage);
      return button ? button.content : '';
    }
    
  },
  mounted() {
    
    window.addEventListener('resize', this.onResize);
    this.$nextTick(() => {
      this.drawBoxes();
    });
  },

  
  
  beforeDestroy() {
    window.removeEventListener('resize', this.onResize);
  },

  mounted() {
    // Fetch data when the component is mounted
    this.fetchDataFromBackend();
  },

  methods: {

    async fetchDataFromBackend() {
      console.log('send useruid to pdf:', this.user.uid);
      console.log('send pdfData to pdf:', this.fileinfo);
      try {
        const response = await axios({
            url: "/api/user/coedit",              
            method: "post",                       
            headers: {                            
              "Content-Type": "application/json",
            },
            params: {                             
              uid: this.user.uid,
              filename: this.fileinfo.file_name,
            },
          })
        // const response = await axios.post('/api/user/coedit', {
        //   uid: this.user.uid,
        //   filename: "1-relationalDB.pdf",
        // });
        
        // Assuming the backend returns JSON data
        this.receivedData = response.data;
        this.comments = this.receivedData.data.comment;
        console.log('Received file data from backend:', this.receivedData);
        console.log('comments:', this.comments);
        // console.log('try read:', this.receivedData.data.uploadTime, this.receivedData.data.uploadName);

        this.blockNum_page1 = this.receivedData.data.blockNum[0].blockNum;
        this.blockNum_page2 = this.receivedData.data.blockNum[1].blockNum;
        console.log('blockNum is: ', this.blockNum_page1, this.blockNum_page2);

        this.generateButtons();
        
    //     const newCommentResponse = await axios({
    //       url: "/api/user/newComment",
    //       method: "post",
    //      headers: {
    //         "Content-Type": "application/json",
    //       },
    //       params: {
    //         uid: this.user.uid,
    //         fileName: "Dummy T&C Dataset.pdf",
    //       },
    //     });

    // // Assuming the backend returns JSON data
    // this.receivedDataFromNewComment = newCommentResponse.data;
    // console.log('Received file data from "/api/user/newComment":', this.receivedDataFromNewComment);
      } catch (error) {
        console.error('Error fetching data from backend:', error);
      }
    },

    onImageLoad(event) {
      const img = event.target;
      this.canvasWidth = img.clientWidth;
      this.canvasHeight = img.clientHeight;
      this.$refs.myCanvas.width = this.canvasWidth;
      this.$refs.myCanvas.height = this.canvasHeight;
      this.drawBoxes();
    },
    onResize() {
      const img = document.querySelector('.page-image');
      if (img) {
        this.canvasWidth = img.clientWidth;
        this.canvasHeight = img.clientHeight;
        this.$refs.myCanvas.width = this.canvasWidth;
        this.$refs.myCanvas.height = this.canvasHeight;
        // this.drawBoxes();
      }
    },
    drawBoxes() {
      //renew to false
      Object.values(this.blockPositions).forEach(positions => {
        positions.forEach(pos => {
          pos.vis = false;
       });
      });
      // Iterate buttons arrays
      console.log("here in drawboxes buttons,", this.buttons);
      this.buttons.forEach(button => {
      const leftBlockId = button.left_blockId;
      const targetPage = button.page; // get button's page
      const targetPosition = this.blockPositions[targetPage].find(pos => pos.id === `btn${leftBlockId}`); // find blockPositions according to page
      if (targetPosition) {
        // console.log('targetPosition:', targetPage, leftBlockId);
        targetPosition.vis = true;
      }
      // console.log('new buttons:', targetPosition);
    });
    
      
      const canvas = this.$refs.myCanvas;
      const ctx = canvas.getContext("2d");
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      console.log("here in drawboxes blockPositions,", this.blockPositions);
      const positions = this.blockPositions[this.currentPage] || [];
      positions.forEach(pos => {
        if (pos.vis) {
        const xStart = pos.xMin * this.canvasWidth;
        const xEnd = pos.xMax * this.canvasWidth;
        const yStart = pos.yMin * this.canvasHeight;
        const yEnd = pos.yMax * this.canvasHeight;
        ctx.strokeStyle = "red";
        ctx.lineWidth = 2;
        ctx.strokeRect(xStart, yStart, xEnd - xStart, yEnd - yStart);

        // find the button with same pos.id number 
        const posIdNumber = pos.id.match(/\d+/)[0]; // extract pos.id number part
        const correspondingButton = this.buttons.find(button => button.left_blockId.toString() === posIdNumber);
        if (correspondingButton) {
          const buttonIdNumber = correspondingButton.id.match(/\d+/)[0]; // extract button.id number part
          // note left_blockId number part
          ctx.font = "20px Arial";
          ctx.fillStyle = "red";
          ctx.fillText(buttonIdNumber, xStart - 15, yStart - 5); // Label the upper left corner of the rectangular box with left_blockId number part
        }
        }
      });
    },
    logClickPosition(event) {
      const rect = event.target.getBoundingClientRect();
      const x = event.clientX - rect.left;
      const y = event.clientY - rect.top;
      const x_percentage = x / rect.width;
      const y_percentage = y / rect.height;
      console.log(`Click position: x=${x}, y=${y}, x_percentage=${x_percentage}, y_percentage=${y_percentage}`);
      
      this.checkClickedArea(x_percentage, y_percentage);
    },
    checkClickedArea(x_percentage, y_percentage) {
      const positions = this.blockPositions[this.currentPage] || [];
      for (let pos of positions) {
        if (x_percentage >= pos.xMin && x_percentage <= pos.xMax &&
            y_percentage >= pos.yMin && y_percentage <= pos.yMax && pos.vis===true) {
              const button = this.buttons.find(button => `btn${button.left_blockId}` === pos.id && button.page === this.currentPage);
              console.log("clicked left block to button:",button);
              console.log("clicked left block:",pos.id);
          this.openPopup(button.id);
          break;
        }
      }
    },

    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
        this.$nextTick(() => {
          this.drawBoxes();
        });
      }
      this.highlightButton('');
      this.lineVisible = false;
    },
    nextPage() {
      if (this.currentPage < 2) {
        this.currentPage++;
        this.$nextTick(() => {
          this.drawBoxes();
        });
      }
      this.highlightButton('');
      this.lineVisible = false;
    },
    goToPage() {
      const page = parseInt(this.inputPage, 10);
      if (page === 1 || page === 2) {
        this.currentPage = page;
        this.errorMessage = '';
        this.$nextTick(() => {
          this.drawBoxes();
        });
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
      }, 1000);
    },

    generateButtons() {
      this.buttons = [];
      let page1Counter = 1;
      let page2Counter = 1;

      let count1 = 0;
      let count2 = 0;

      for (let i = 0; i < this.blockNum_page1 + this.blockNum_page2; i++) {
        let status;
        switch(this.receivedData.data.blockStatus[i].approvedStatus) {
          case 1:
            status = 'pending';
            break;
          case 2:
            status = 'approved';
            break;
          case 3:
            status = 'rejected';
            break;
          default:
            status = 'unknown'; // other unpredicted status
        }
        
        let buttonId;
        if (this.receivedData.data.blockStatus[i].page === 2) {
          buttonId = `BTN${page2Counter}`;
          page2Counter++;
        } else {
          buttonId = `BTN${page1Counter}`;
          page1Counter++;
        }

        this.buttons.push({
          id: buttonId,
          text: `${buttonId.replace('BTN', '')}.`,
          department: this.receivedData.data.blockStatus[i].department,
          status: status,
          page: this.receivedData.data.blockStatus[i].page,
          left_blockId: this.receivedData.data.location[i].blockId,
          content: this.receivedData.data.location[i].content
        });

        let blockId_comment;
        if (this.receivedData.data.location[i].page === 1) {
            count1++;
            blockId_comment = count1;
        } else if (this.receivedData.data.location[i].page === 2) {
            count2++;
            blockId_comment = count2;
        }

        this.comments.push({
          fileName: this.receivedData.data.fileName,
          page: this.receivedData.data.location[i].page,
          blockId: blockId_comment,
          author: this.receivedData.data.uploadName, // assuming author field exists in blockStatus
          content: this.receivedData.data.location[i].content,
          text: this.receivedData.data.location[i].modifiedContent,
          timestamp: this.receivedData.data.uploadTime
        });
      }
      console.log('buttons generated:', this.buttons);
      console.log('initial comments:', this.comments);

      this.drawBoxes();
    },

    highlightButton(buttonId) {
      const buttons = document.querySelectorAll('.department-button');
      buttons.forEach(button => button.classList.remove('active'));
      const activeBtn = document.getElementById(buttonId);
      if (activeBtn) activeBtn.classList.add('active');
    },
    

    async submitComment() {
      
      try {
        const blockID = this.activeButton.match(/\d+/)[0];
        const button = this.buttons.find(button => button.id === this.activeButton && button.page === this.currentPage);

        const commentData = {
          fileName: this.fileinfo.file_name,
          page: this.currentPage,
          blockID: blockID,
          uid: this.user.uid,
          author: this.user.uname,
          content: button && button.content != null ? button.content : "",  // if content is null or undefined，pass ""  //original text
          text: this.newComment,
          timestamp: new Date(),
        };
        console.log('New commentData:', commentData);
        
        // send post to backend
        
        const response = await axios({
            url: "/api/user/newComment",               
            method: "post",                      
            headers: {                           
              "Content-Type": "application/json",
            },
            params: commentData,
          })

        // console.log('debug print:',this.currentPage,blockID,this.newComment,this.user.uname)
      
        // console.log('Response from backend for newcomment:', response.data);
        // console.log('push newcomment:', { fileName: this.fileinfo.file_name, page: this.currentPage, blockId:blockID, content: button && button.content != null ? button.content : "", text: this.newComment, author: this.user.uname, timestamp: new Date() });
        // console.log('push comments:',this.comments);
        this.comments.push({ fileName: this.fileinfo.file_name, page: this.currentPage, blockId: blockID, content: button && button.content != null ? button.content : "", text: this.newComment, author: this.user.uname, timestamp: new Date() });

        // Calculated properties are updated automatically, so there is no need to manually call
        console.log('Updated comments:', this.comments);
        console.log('Updated filteredComments:', this.filteredComments);
        // clear input block
        this.newComment = "";
      } catch (error) {
        console.error('Error sending comment to backend when newcomment:', error);
        // solve error
      }
    },

    getDepartment(activeButton) {
      const buttonData = this.buttons.find(button => {
        // console.log(`button.id: ${button.id}, activeButton: ${activeButton}, button.page: ${button.page}, currentPage: ${this.currentPage}`);
        return button.id === activeButton && button.page === this.currentPage;
      });

      // Returns information about the found department, or the default value if it is not found.
      return buttonData ? buttonData.department : 'Unknown Department';
    },
  
    openPopup(buttonId) {
      console.log('Received user in Popup', buttonId, this.user);
      this.activeButton = buttonId;
      this.showPopup = true;
    },
    closePopup() {
      this.showPopup = false;
      this.activeButton = null;
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

    async updateStatus(newStatus) {
      try {
        const blockID = this.activeButton.match(/\d+/)[0];
        const button = this.buttons.find(button => button.id === this.activeButton && button.page === this.currentPage);
        // update status
        if (button) {
          // 更新按钮状态
          button.status = newStatus === 2 ? 'approved' : newStatus === 3 ? 'rejected' : button.status;
        }

        
        const response = await axios({
            url: "/api/user/newApprove",              
            method: "post",                      
            headers: {                            
              "Content-Type": "application/json",
            },
            params: {fileName: this.fileinfo.file_name,
                     page: this.currentPage,
                     blockID: blockID,
                     approveStatus: newStatus,
                     content: button && button.content != null ? button.content : "",  // If content is null or undefined, pass the empty string.
                     department: button ? button.department : "",},
          })
        

        console.log('Response from backend:', response.data);

        // 显示成功提示
        if (newStatus === 2) {
          alert("Successfully approved!");
        } else if (newStatus === 3) {
          alert("Successfully rejected!");
        }
      } catch (error) {
        console.error('Error updating status:', error);
        
      }
    },
  
    exportComments() {
      console.log('Exporting comments');
      const escapeCsvValue = (value) => {
        if (typeof value === 'string') {
          if (value.includes(',') || value.includes('\n') || value.includes('"')) {
            return `"${value.replace(/"/g, '""')}"`;
          }
        }
        return value;
      };
      let csvContent = "data:text/csv;charset=utf-8,";
      csvContent += "Page,Block,Timestamp,Author,Content,Comment\n";
      this.comments.forEach(comment => {
        const row = `${escapeCsvValue(comment.page)},${escapeCsvValue(comment.blockId)},${escapeCsvValue(comment.timestamp)},${escapeCsvValue(comment.author)},${escapeCsvValue(comment.content)},${escapeCsvValue(comment.text)}`;
        csvContent += row + "\n";
      });
      const encodedUri = encodeURI(csvContent);
      const link = document.createElement("a");
      const currentDateTime = this.getCurrentFormattedDateTime();
      link.setAttribute("href", encodedUri);
      link.setAttribute("download", `${this.receivedData.data.fileName}_Comments_${currentDateTime}.csv`);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },

    goToHome() {
      if(this.user.department === 'ClientManager'){
        this.$router.push('/home');
      }else{
        this.$router.push('/home-stackholder');
      }
      
    },

    getDiffResult(originalComment) {
      // console.log('debug in getDiffResult -> originalComment:',originalComment)
      const modifiedComment = this.comments.find(c =>
        c.fileID === originalComment.fileID &&
        c.page === originalComment.page &&
        c.blockID === originalComment.blockId
      );
      return diffWords(originalComment.text, modifiedComment ? modifiedComment.text : originalComment.text);
    },
    highlightDiff(originalText, modifiedText) {
      // if originalText is undefined，get matched data from this.comments
      if (originalText === undefined) {
        // extract this.activeButton number part
        const activeButtonNumber = this.activeButton.match(/\d+/)[0];
        
        // find the matched comment from this.comments using blockId and activeButtonNumber 
        const matchingComment = this.comments.find(comment => comment.blockId == activeButtonNumber && comment.page == this.currentPage);
        
        // if find the matched comment，use content as originalText
        if (matchingComment) {
          console.log('match original text successfully: ', matchingComment)
          originalText = matchingComment.content;
        } else {
          console.error(`No matching comment found for blockId: ${activeButtonNumber}`);
          return; // if not find the matched comment,return
        }
      }
      
      // console.log('originalText in highlightDiff: ',originalText)
      // console.log('modifiedText in highlightDiff: ',modifiedText)
      const diff = diffWords(originalText, modifiedText);
      // console.log('diff: ',diff)

      return diff.map(part => {
        if (part.added) {
          return `<span style="background-color: yellow;">${part.value}</span>`;
        }
        if (part.removed) {
          return `<span style="background-color: pink; text-decoration: line-through;">${part.value}</span>`;
        }
        return part.value;
      }).join('');
    }
  },

  
  watch: {
    currentPage() {
      this.$nextTick(() => {
        this.drawBoxes();
      });
    },
    comments() {
      // 计算属性 filteredComments 会自动更新
    },
  }
};
</script>



<style scoped>
body {
    margin: 0;
    padding: 0;
}
.main-container {
    padding-top: 1px; /* Adjusting the top edge inner margin */
}
.pdf-detail {
    display: flex;
    position: relative; /* Ensure relative positioning of the parent container */
    margin-top: 1px; /* Reduction of top outer margin */
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
    pointer-events: none; /* Disable point events */
}
.comments-section {
    width: 35%;
    padding: 20px;
    padding-right: 0; /* Set the distance to the right-hand border */
    display: flex;
    flex-direction: column;
    align-items: flex-end; /* Right-align content */
    gap: 20px; /* Add this gap to ensure spacing between content */
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
    margin-bottom: 20px; /* Set the spacing to the div below */
}
.department-button {
    position: relative;    /* Relative positioning so that internal elements use absolute positioning */
    margin-top: 20px;
    width: 100%;
    height: 100px;
    background-color: #b2cbe2;
    border: 1px solid #83bedf;
    font-size: 16px;
    cursor: pointer;
    text-align: left;      /* Left-aligned */
    padding-left: 20px;    
    overflow: hidden;      /* Preventing internal element overflow */
}
.department-button::before {
    content: "COMMENTS";
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    font-size: 36px;       
    color: rgba(255, 255, 255, 0.3);  
    pointer-events: none;  
    white-space: nowrap;    /* Prevent line breaks in text */
}
.department-button.active {
    background-color: #c1d732;  /* Highlight background colour */
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
    overflow-y: auto;
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
    margin: 0; /* Remove margins from headings */
}
.close-button {
    background: red;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
    padding: 5px 10px; /* Adjust the inner margins of the buttons */
    height: auto; /* Ensure that button height automatically adapts to content */
}
.popup-body {
    display: flex;
    justify-content: space-between;
    margin-top: 2px;
}
.comments-history {
    flex: 1;
    overflow-y: auto;
    max-height: 400px; /* Setting the maximum height and automatic appearance of scrollbars */
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
.highlight {
  background-color: yellow;
  text-decoration: line-through;
}
</style>

