<template>
  <div class="background">
    <div class="image-standard">
      <img src="../picture/standard3.png" alt="Image standard">
    </div>
    <div class="image-hku">
      <img src="../picture/hku3.png" alt="Image hku">
    </div>
   
    <div class="title-text">
      <p class="custom-text">co-editing platform</p>
    </div>
    <div>
      <el-card class="box-card">
        <h2>Log in</h2>
        <el-form
          :model="ruleForm"
          status-icon
          :rules="rules"
          ref="ruleForm"
          label-position="left"
          label-width="90px"
          class="login-from"
        >
          <el-form-item label="User Name" prop="uname">
            <el-input v-model="ruleForm.uname"></el-input>
          </el-form-item>
          <el-form-item label="Password" prop="password">
            <el-input
              type="password"
              v-model="ruleForm.password"
              autocomplete="off"
            ></el-input>
          </el-form-item>
        </el-form>
        <div class="btnGroup">
          <el-button
            type="primary"
            @click="submitForm('ruleForm')"
            v-loading="loading"
            >Log in</el-button
          >
          <el-button @click="resetForm('ruleForm')">Reset</el-button>
          <router-link to="/register">
            <el-button style="margin-left: 10px">Register</el-button>
          </router-link>
        </div>
        
      </el-card>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      ruleForm: {
        uname: "",
        password: "",
      },
      rules: {
        uname: [
          { required: true, message: "User name can't be null!", trigger: "blur" },
        ],
        password: [
          { required: true, message: "Password can't be null!", trigger: "blur" },
        ],
      },
      loading: false, // 是否显示加载动画
    };
  },
  methods: {
    submitForm(formName) {
      // 验证表单中的账号密码是否有效，因为在上面rules中定义为了必填 required: true
      this.$refs[formName].validate((valid) => {
        // 点击登录后，让登录按钮开始转圈圈（展示加载动画）
        this.loading = true;
        // 如果经过校验，账号密码都不为空，则发送请求到后端登录接口
        if (valid) {
          let _this = this;
          // 使用 axios 将登录信息发送到后端
          this.axios({
            url: "/api/user/login",               // 请求地址
            method: "post",                       // 请求方法
            headers: {                            // 请求头
              "Content-Type": "application/json",
            },
            params: {                             // 请求参数
              uname: _this.ruleForm.uname,
              password: _this.ruleForm.password,
            },
          }).then((res) => { // 当收到后端的响应时执行该括号内的代码，res 为响应信息，也就是后端返回的信息
            if (res.data.code === "0" && res.data.data.user.status===1) {  // 当响应的编码为 0 时，说明登录成功
              // 将用户信息存储到sessionStorage中
              sessionStorage.setItem("userInfo", JSON.stringify(res.data.data.user));
              console.log("passed data:",JSON.stringify(res.data.data))
              // 跳转页面到首页
              this.$router.push({
              path: '/home',
              query: { myList: JSON.stringify(res.data.data) },
            });
              // 显示后端响应的成功信息
              this.$message({
                message: res.data.msg,
                type: "success",
              });
            } 
            else if (res.data.code === "0" && res.data.data.user.status===2) {  // 当响应的编码为 0 时，说明登录成功
              // 将用户信息存储到sessionStorage中
              sessionStorage.setItem("userInfo", JSON.stringify(res.data.data.user));
              // 跳转页面到首页
              this.$router.push({
              path: '/home-stackholder',
              query: { myList: JSON.stringify(res.data.data) },
            });
              // 显示后端响应的成功信息
              this.$message({
                  message: res.data.msg,
                  type: "success",
                });
            }else {  // 当响应的编码不为 0 时，说明登录失败
              // 显示后端响应的失败信息
              this.$message({
                message: res.data.msg,
                type: "warning",
              });
            }
            // 不管响应成功还是失败，收到后端响应的消息后就不再让登录按钮显示加载动画了
            _this.loading = false;
            console.log(res);
          });
        } else {  // 如果账号或密码有一个没填，就直接提示必填，不向后端请求
          console.log("error submit!!");
          this.loading = false;
          return false;
        }
      });
    },
    resetForm(formName) {
      this.$refs[formName].resetFields();
    },
  },
};
</script>

<style scoped>
/* 设置登录面板居中，宽度为400px */
.box-card {
  margin: auto ;
  width: 400px;
}
.background {
  background-image: url('../picture/hku-ChiWai.jpg');
  background-size: cover; /* 背景图填充整个容器 */
  background-position: center; /* 背景图居中 */
  height: 100vh; /* 设置背景图高度为整个视口高度 */
  display: flex;
  justify-content: right; /* 登录面板靠右 */
  align-items: center; /* 上下居中 */
  padding-right: 10%;
}

/* Set the position and size for image "a" */
.image-standard {
  position: absolute;
  top: 70px;
  left: 70px;
}

/* Set the position and size for image "b" */
.image-hku {
  position: absolute;
  top: 70px;
  left: 170px; 
}



/* Set the width and alignment for the left text */
.title-text {
 
  text-align: left;
  padding-left: 10%; /* Add some left padding */
}

/* Customize the text color and font */
.custom-text {
  color: white; /* Change to your desired color */
  font-family: Arial, sans-serif; /* Change to your desired font */
  font-size: 100px; /* Adjust font size as needed */
}

/* Prevent text from wrapping */
.login-from {
  white-space: nowrap;
}



</style>