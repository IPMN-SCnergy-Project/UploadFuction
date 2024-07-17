<template>
  <div class="background">
    <div class="container">
      <h2>Welcome StackHolder {{ user.uname }}! Department:{{ user.department }} Uid: {{ user.uid }}. </h2>
      <div class="header">
        <el-button @click="logout">Log out</el-button>
      </div>

      <div class="grid-container">
          <div class="grid-item">
            <div class="today-date">{{ currentDate + " TODO"}}</div>
            <div v-for="(file, index) in todoFiles" :key="index" class="file-row" @click="viewPdf(file)">
                <img :src="getIcon(file.file_name)" alt="pic" class="file-icon" />
                <p class="file-name">{{ file.file_name }}</p>
                <p class="file-date">{{ file.create_time }}</p>
                <p class="file-size">{{ formatFileSize(file.file_size) }}</p>
            </div>
          </div>
        
        <div class="grid-item">
          <div>All Tasks</div>
            <div v-for="(file, index) in finishedFiles" :key="index" class="file-row" @click="viewPdf(file)">
                <img :src="getIcon(file.file_name)" alt="pic" class="file-icon" />
                <p class="file-name">{{ file.file_name }}</p>
                <p class="file-date">{{ file.create_time }}</p>
                <p class="file-size">{{ formatFileSize(file.file_size) }}</p>
            </div>
          <!-- 显示所有任务的内容 -->
        </div>
      </div>

    </div>
  </div>
</template>

<script>

import axios from 'axios';

export default {





data() {
  return {
    user: {
      uname: "",
      uid: null,
    },
    newUploadFiles: [], 
    recentFiles: [],
    todoFiles: [],
    finishedFiles:[],
    currentDate: new Date().toLocaleDateString(),
    showNewUploaded: true,
    receivedData: null,
    // passedFileinfo: {
    //   file_name:"",
    //   file_size:"",
    //   file_id:"",
    //   create_time:"",
    // }
    
  };
},
methods: {

  
  async fetchDataFromBackend() {
      console.log('send useruid to pdf:', this.user.uid);
      try {
        const response = await axios({
            url: "/api/user/exitCoedit",               
            method: "post",                       
            headers: {                            
              "Content-Type": "application/json",
            },
            params: {                             
              uid: this.user.uid,
            },
          })
       
        this.receivedData = response.data;
        
        console.log('Received file data from backend in home from coedit:', this.receivedData);
        this.recentFiles = this.receivedData.data.fileList;
        this.todoFiles = this.recentFiles.filter(file => file.is_finished === 0);
        this.finishedFiles = this.recentFiles.filter(file => file.is_finished === 1);
        
        console.log('recentFiles:', this.recentFiles);
        console.log('todoFiles:', this.todoFiles);
        console.log('finishedFiles:', this.finishedFiles);
        
        

      } catch (error) {
        console.error('Error fetching data from backend:', error);
      }
    },
  
  
  
  // Methods triggered when clicking on a file name for programmatic navigation to the PdfDetail page
  viewPdf(pdfData) {
    console.log('Navigating to PdfDetail with user:', this.user);
    console.log('Navigating to PdfDetail with pdfdata:', pdfData);
    // this.$router.push({ name: 'PdfDetail', params: { pdfData, user: this.user } });
    this.$router.push({ name: 'PdfDetail', params: {  user: this.user,fileinfo:pdfData } });
    
    
  },

  clearNewUploads() {
      this.newUploadFiles = [];
  },

  // switch to newupload
  showNewUploads() {
    this.showNewUploaded = true;
  },
  // switch to recent
  showRecentUploads() {
    this.showNewUploaded = false;
  },

  addUploadedFile(fileInfo) {
    console.log("This fileInfo1!:",fileInfo);
    console.log("This newUploadFiles!:",this.newUploadFiles);
    console.log("This fileInfo1!:",this.recentFiles);
    this.newUploadFiles.push(fileInfo);
    this.recentFiles.push(fileInfo);
  },
  
 //Display icons according to file type
  getIcon(fileName) {
    const extension = fileName.split('.').pop().toLowerCase();
    if (extension == 'pdf') {
      return require('@/views/picture/pdf.png');
    } else if (extension == 'docx' || extension == 'doc') {
      return require('@/views/picture/word.png');
    } else {
      // Return a default icon for other file types
      return
    }
  },

  logout(){
    // Remove local user login information
    sessionStorage.removeItem('userInfo');
    // Jump page to login page
    this.$router.push('/login');
  },

  // File Size Formatting Functions
    formatFileSize(size) {
      const units = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
      let unitIndex = 0;
      
      while (size >= 1024 && unitIndex < units.length - 1) {
        size /= 1024;
        unitIndex++;
      }
      return size.toFixed(2) + ' ' + units[unitIndex];
    },
},
mounted() {
    if (sessionStorage.getItem('userInfo')) {
      // Storing user information in sessionStorage
      this.user = JSON.parse(sessionStorage.getItem('userInfo'));
      console.log( "order user: ", this.user);
      this.fetchDataFromBackend();
    }
  },

  // mounted() {
  //   console.log('The component has been mounted!');
  //   const myList = this.$route.query.myList;
  //   console.log('Received myList:', myList);
  //   console.log('Received myListFilelist:', JSON.parse(myList).fileList);
  //   // certain there is fileList in  myList 
  //   if (myList && JSON.parse(myList).fileList) {
  //     this.recentFiles = JSON.parse(myList).fileList;
  //   } else {
  //     console.warn('myList.fileList is missing or undefined.');
  //   }

  //   if (sessionStorage.getItem('userInfo')) {
  //     // Store user information in sessionStorage
  //     this.user = JSON.parse(sessionStorage.getItem('userInfo'));
  //     console.log('passed user data:', this.user);

  //   }
  // },


};
</script>
  
  <style scoped>
  .container {
    display: flex;
    flex-direction: column;
    height: 100vh; 
  }
  

  .grid-item {
  background-color: #ffffff;
  padding: 20px;
  text-align: left;
  font-weight: bold;
  font-size: 1.2rem; 
  border-radius: 10px;
  overflow: auto; 
  max-height: calc(100vh - 20px); 
}

  .header {
    display: flex;
    justify-content: flex-end;
    padding: 10px;
  }
  
  h2 {
    text-align: left;
    font-family: Arial, sans-serif; 
    flex-grow: 1;
    margin-left: 2%;
    margin-top:3%;
  }
  
  .grid-container {
    display: grid;
    grid-template-columns: repeat(2, 1fr); 
    grid-gap: 20px; 
    height: 6000px; 
    padding: 20px 20px;
    border-radius: 10px;
    overflow: auto; 
    
  }
  
  .grid-item {
    background-color: #ffffff;
    padding: 20px;
    text-align: left;
    font-weight: bold;
    font-size: 1.2rem; 
    border-radius: 10px;
    overflow: auto; 
    max-height: calc(100vh - 20px); 
  }
  
  .today-date {
    font-size: 1.2rem;
    font-weight: bold;
    margin-bottom: 10px;
  }
  

  .background {
    background-image: url('../picture/hku-ChiWai3.png');
    background-size: cover; 
    background-position: center; 
    height: 100vh; 
  }

  .file-row {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.file-icon {
  width: 24px;
  height: 24px;
  margin-right: 10px;
}

.file-name {
  font-weight: bold;
  font-family: Arial, sans-serif; 
  font-size: 15px;
  flex-basis: 40%;
  white-space: nowrap; 
  overflow: hidden; 
  text-overflow: ellipsis; /* Use of ellipses to indicate truncated text */
}

.file-date {
  margin-left: auto;
  font-family: Arial, sans-serif; 
  font-size: 15px;
  flex-basis: 30%;
}

.file-size {
  margin-left: auto;
  font-family: Arial, sans-serif; 
  font-size: 15px;
  flex-basis: 10%;
}
  
  
  </style>
  