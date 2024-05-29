<template>
  <div class="background">
    <div class="container">
      <h2>Welcome {{ user.uname }}! Your uid is {{ user.uid }}</h2>
      <div class="header">
        <el-button @click="logout">Log out</el-button>
      </div>

      <div class="grid-container">
        <div class="grid-item">
          <div class="today-date">{{ currentDate }}</div>
        </div>
        <div class="grid-item">
          <div class="switch-btns">
            <el-button @click="showNewUploaded = true" :type="showNewUploaded ? 'primary' : ''">New Upload</el-button>
            <el-button @click="showNewUploaded = false" :type="!showNewUploaded ? 'primary' : ''">Recent</el-button>
          </div>
          <div v-if="showNewUploaded">
            <!-- 显示新上传的内容 -->
          </div>
          <div v-else>
            <!-- 显示最近的内容 -->
          </div>
        </div>
        <div class="grid-item">
          <div>All Tasks</div>
          <!-- 显示所有任务的内容 -->
        </div>
      </div>

      <!-- 添加上传功能组件 -->
      <Upload />
    </div>
  </div>
</template>

<script>
import Upload from '../upload/Upload.vue'; // 导入上传组件

export default {
  components: {
    Upload // 注册上传组件
  },
  data() {
    return {
      user: {
        uname: "",
        uid: null,
      },
      currentDate: new Date().toLocaleDateString(),
      showNewUploaded: true,
    };
  },
  methods: {
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

.header {
  display: flex;
  justify-content: flex-end;
  padding: 10px;
}

h2 {
  text-align: left;
  flex-grow: 1;
}

.grid-container {
  display: grid;
  grid-template-columns: repeat(3, 1fr); /* 三列 */
  grid-gap: 20px; /* 框格之间的间距 */
  height: 100vh; /* 将容器高度设置为页面高度 */
}

.grid-item {
  background-color: #f1f1f1;
  padding: 20px;
  text-align: left;
  font-weight: bold;
  font-size: 1.2rem; /* 调整字号为1.2rem（根据需要调整大小） */
}

.today-date {
  font-size: 1.2rem;
  font-weight: bold;
  margin-bottom: 10px;
}

.switch-btns {
  margin-bottom: 10px;
}

.background {
  background-image: url('../picture/hku-ChiWai2.jpg');
  background-size: cover; /* 背景图填充整个容器 */
  background-position: center; /* 背景图居中 */
  height: 100vh; /* 设置背景图高度为整个视口高度 */
}
</style>
