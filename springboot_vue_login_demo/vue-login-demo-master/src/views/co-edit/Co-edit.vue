<template>

  <div class="pdf-detail">
    <div class="image-container">
      <img :src="pageImagePath" alt="Page Image" class="page-image" @click="logClickPosition">
      <div class="page-navigation">
        <button @click="prevPage">Previous Page</button>
        <button @click="nextPage">Next Page</button>
      </div>
    </div>

    <div class="comments-section">
      <button class="exit-button" @click="goToHome">Exit Co-edit Platform</button>
      <div class="buttons-container">
        <button id="btn1" v-if="currentPage === 1" class="department-button">1.Department:</button>
        <button id="btn2" v-if="currentPage === 1" class="department-button">2.Department:</button>
        <button id="btn3" v-if="currentPage === 1" class="department-button">3.Department:</button>
        <button id="btn4" v-if="currentPage === 1" class="department-button">4.Department:</button>
        <button id="btn5" v-if="currentPage === 1" class="department-button">5.Department:</button>
        <button id="btn6" v-if="currentPage === 1" class="department-button">6.Department:</button>
        <button id="btn7" v-if="currentPage === 1" class="department-button">7.Department:</button>
        <button id="btn8" v-if="currentPage === 1" class="department-button">8.Department:</button>

        <button id="btn1" v-if="currentPage === 2" class="department-button">1.Department:</button>
        <button id="btn2" v-if="currentPage === 2" class="department-button">2.Department:</button>
        <button id="btn3" v-if="currentPage === 2" class="department-button">3.Department:</button>
        <button id="btn4" v-if="currentPage === 2" class="department-button">4.Department:</button>
        <button id="btn5" v-if="currentPage === 2" class="department-button">5.Department:</button>
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
      comments: [], // 存储评论的数组
      newComment: "", // 新评论的内容
      pdfData: "/home/ipmn/test/upload/1-relationalDB.pdf" // 存储PDF数据的变量，如URL等
      
    };
  },
  computed: {
    pageImagePath: function() {
        if (this.currentPage === 1) {
            return require('@/views/picture/file1.png');
        } else {
            return require('@/views/picture/file2.png');
        }
    }
  },
  mounted() {
    console.log('Received user:', this.user);
    // this.loadPdfViewer();
  },
  
  methods: {
    prevPage() {
      if (this.currentPage > 1) {
        this.currentPage--;
      }
    },
    nextPage() {
      if (this.currentPage < 2) {
        this.currentPage++;
      }
    },
    
    logClickPosition(event) {
      const rect = event.target.getBoundingClientRect();
      const x = event.clientX - rect.left;
      const x_percentage = x / (rect.right - rect.left);
      const y = event.clientY - rect.top;
      const y_percentage = y / (rect.bottom - rect.top);
      console.log(`Click position: x=${x}, y=${y}, x_percentage=${x_percentage}, y_percentage=${y_percentage}`);
    },

    submitComment() {
      // 将新评论添加到评论数组中
      this.comments.push({ text: this.newComment, author: this.user.uname, timestamp: new Date() });
      // 清空输入框
      this.newComment = "";
      // 可以与后端API交互，保存评论数据
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

    .exit-button {
        position: absolute;
        top: 5px;
        right: 5px;
        font-size: 20px;
        background-color: #007bff;
        color: #fff;
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

</style>
