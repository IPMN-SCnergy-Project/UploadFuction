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
      loading: false, // Display loading animation?
    };
  },
  methods: {
    submitForm(formName) {
      // Verify that the account credentials in the form are valid, as they are defined as required in the rules above: required: true.
      this.$refs[formName].validate((valid) => {
        // After clicking the login button, make the button start spinning (display a loading animation).
        this.loading = true;
        // If the validation confirms that both the account and password fields are not empty, send a request to the backend login API.
        if (valid) {
          let _this = this;
          // Send the login information to the backend using axios.
          this.axios({
            url: "/api/user/login",               // address
            method: "post",                      
            headers: {                            
              "Content-Type": "application/json",
            },
            params: {                             
              uname: _this.ruleForm.uname,
              password: _this.ruleForm.password,
            },
          }).then((res) => { // Execute the code inside the parentheses when a response is received from the backend. res is the response information, which is the data returned from the backend.
            if (res.data.code === "0" && res.data.data.user.status===1) {  // When the response code is 0, it indicates a successful login
              // Store user information in sessionStorage
              sessionStorage.setItem("userInfo", JSON.stringify(res.data.data.user));
              console.log("passed data:",JSON.stringify(res.data.data))
              // Jump to the homepage
              this.$router.push({
              path: '/home',
              query: { myList: JSON.stringify(res.data.data) },
            });
              // Display successful information of backend response
              this.$message({
                message: res.data.msg,
                type: "success",
              });
            } 
            else if (res.data.code === "0" && res.data.data.user.status===2) {  // When the response code is 0, it indicates a successful login
              // Store user information in sessionStorage
              sessionStorage.setItem("userInfo", JSON.stringify(res.data.data.user));
              // Jump to the homepage
              this.$router.push({
              path: '/home-stackholder',
              query: { myList: JSON.stringify(res.data.data) },
            });
              // Display successful information of backend response
              this.$message({
                  message: res.data.msg,
                  type: "success",
                });
            }else {  // When the response code is not 0, it indicates a login failure
              // Display failure information of backend response
              this.$message({
                message: res.data.msg,
                type: "warning",
              });
            }
            // Regardless of whether the response is successful or unsuccessful, 
            // the login button will no longer display the loading animation after receiving the backend response message
            _this.loading = false;
            console.log(res);
          });
        } else {  
          // If one of the account or password is not filled in,
          // it will be prompted as required without requesting from the backend
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
/* Set the login panel to be centered with a width of 400px */
.box-card {
  margin: auto ;
  width: 400px;
}
.background {
  background-image: url('../picture/hku-ChiWai.jpg');
  background-size: cover; /* Background image fills the entire container */
  background-position: center; /* Centered background image */
  height: 100vh; /* Set the background image height to the entire viewport height */
  display: flex;
  justify-content: right; /* Login panel on the right */
  align-items: center; /* Center up and down */
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