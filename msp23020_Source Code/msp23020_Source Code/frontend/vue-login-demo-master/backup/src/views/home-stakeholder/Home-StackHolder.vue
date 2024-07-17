<template>
  <div class="background">
    <div class="container">
      <h2>Welcome StackHolder {{ user.uname }}! Department:{{ user.department }} Uid: {{ user.uid }}. </h2>
      <div class="header">
        <el-button @click="logout">Log out</el-button>
      </div>

      <div class="grid-container">
          <div class="grid-item">
            <div class="today-date">{{ currentDate + "TODO"}}</div>
            <div class="file-row" @click="viewPdf(file)">
                <img :src="pageImagePath" alt="pic" class="file-icon" />
                <p class="file-name">{{ "Dummy T&C_Extract.pdf"}}</p>
                <p class="file-date">{{ "2024/6/21 00:26:15" }}</p>
                <p class="file-size">{{ "143.58KB" }}</p>
            </div>
          </div>
        
        <div class="grid-item">
          <div>All Tasks</div>
          <!-- 显示所有任务的内容 -->
        </div>
      </div>

    </div>
  </div>
</template>

<script>
//   import Upload from '../upload/Upload.vue'; // 导入上传组件

export default {
// components: {
//   Upload // 注册上传组件
// },
data() {
  return {
    user: {
      uname: "",
      uid: null,
    },
    newUploadFiles: [], // 用于存储当次上传的文件信息
    recentFiles: [],
    todoFiles: [],
    currentDate: new Date().toLocaleDateString(),
    showNewUploaded: true,
  };
},
computed: {
pageImagePath: function() {
    
        return require('@/views/picture/pdf.png');
  
}
},
methods: {
  // 点击文件名时触发的方法，进行编程式导航到PdfDetail页面
  viewPdf(pdfData) {
      console.log('Navigating to PdfDetail with user:', this.user);
      console.log('Navigating to PdfDetail with pdfdata:', pdfData);
      this.$router.push({ name: 'PdfDetail', params: { pdfData, user: this.user } });
  },

  clearNewUploads() {
      this.newUploadFiles = [];
  },

  // 切换到显示新上传内容
  showNewUploads() {
    this.showNewUploaded = true;
  },
  // 切换到显示最近内容
  showRecentUploads() {
    this.showNewUploaded = false;
  },

  addUploadedFile(fileInfo) {
    this.newUploadFiles.push(fileInfo);
    this.recentFiles.push(fileInfo);
  },
  
  //根据文件类型显示图标
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
    // 移除本地用户登录信息
    sessionStorage.removeItem('userInfo');
    // 跳转页面到登录页
    this.$router.push('/login');
  }
},
mounted() {
  if (sessionStorage.getItem('userInfo')) {
    // 将用户信息存储到sessionStorage中
    this.user = JSON.parse(sessionStorage.getItem('userInfo'));
  }
},
  
};
</script>
  
  <style scoped>
  .container {
    display: flex;
    flex-direction: column;
    height: 100vh; /* 设置容器高度为视窗高度 */
  }
  

  .grid-item {
  background-color: #ffffff;
  padding: 20px;
  text-align: left;
  font-weight: bold;
  font-size: 1.2rem; /* 调整字号为1.2rem（根据需要调整大小） */
  border-radius: 10px;
  overflow: auto; /* 添加滚动条 */
  max-height: calc(100vh - 20px); 
}

  .header {
    display: flex;
    justify-content: flex-end;
    padding: 10px;
  }
  
  h2 {
    text-align: left;
    font-family: Arial, sans-serif; /* 设置字体 */
    flex-grow: 1;
    margin-left: 2%;
    margin-top:3%;
  }
  
  .grid-container {
    display: grid;
    grid-template-columns: repeat(2, 1fr); /* 两列 */
    grid-gap: 20px; /* 框格之间的间距 */
    height: 6000px; /* 将容器高度设置为页面高度 */
    padding: 20px 20px;
    border-radius: 10px;
    overflow: auto; /* 添加滚动条 */
    
  }
  
  .grid-item {
    background-color: #ffffff;
    padding: 20px;
    text-align: left;
    font-weight: bold;
    font-size: 1.2rem; /* 调整字号为1.2rem（根据需要调整大小） */
    border-radius: 10px;
    overflow: auto; /* 添加滚动条 */
    max-height: calc(100vh - 20px); 
  }
  
  .today-date {
    font-size: 1.2rem;
    font-weight: bold;
    margin-bottom: 10px;
  }
  

  .background {
    background-image: url('../picture/hku-ChiWai3.png');
    background-size: cover; /* 背景图填充整个容器 */
    background-position: center; /* 背景图居中 */
    height: 100vh; /* 设置背景图高度为整个视口高度 */
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
  font-family: Arial, sans-serif; /* Change to your desired font */
  font-size: 15px;
  flex-basis: 40%;
  white-space: nowrap; /* 不换行 */
  overflow: hidden; /* 隐藏溢出部分 */
  text-overflow: ellipsis; /* 使用省略号表示截断的文本 */
}

.file-date {
  margin-left: auto;
  font-family: Arial, sans-serif; /* Change to your desired font */
  font-size: 15px;
  flex-basis: 30%;
}

.file-size {
  margin-left: auto;
  font-family: Arial, sans-serif; /* Change to your desired font */
  font-size: 15px;
  flex-basis: 10%;
}
  
  
  </style>
  