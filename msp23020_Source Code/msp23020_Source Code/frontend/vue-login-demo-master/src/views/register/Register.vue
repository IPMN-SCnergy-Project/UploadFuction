<template>
  <div>
    <el-card class="box-card">
      <h2>Register</h2>
      <el-form
        :model="ruleForm"
        status-icon
        :rules="rules"
        ref="ruleForm"
        label-position="left"
        label-width="140px"
        class="demo-ruleForm"
      >
        <el-form-item label="User Name" prop="uname" class="Username">
          <el-input v-model="ruleForm.uname"></el-input>
        </el-form-item>
        <el-form-item label="Password" prop="pass" class="Password">
          <el-input
            type="password"
            v-model="ruleForm.pass"
            autocomplete="off"
          ></el-input>
        </el-form-item>
        <el-form-item label="Confirm Password" prop="password" class="Confirm-Password">
          <el-input
            type="password"
            v-model="ruleForm.password"
            autocomplete="off"
          ></el-input>
        </el-form-item>
      </el-form>
      <div class="btnGroup">
        <el-button type="primary" @click="submitForm('ruleForm')"  v-loading="loading"
          >Submit</el-button
        >
        <el-button @click="resetForm('ruleForm')">Reset</el-button>
        <el-button @click="goBack">Go Back</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  data() {
    var validatePass = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("Please input the password:"));
      } else {
        if (this.ruleForm.checkPass !== "") {
          this.$refs.ruleForm.validateField("checkPass");
        }
        callback();
      }
    };
    var validatePass2 = (rule, value, callback) => {
      if (value === "") {
        callback(new Error("Please input the password agin"));
      } else if (value !== this.ruleForm.pass) {
        callback(new Error("Passwords entered twice do not match!"));
      } else {
        callback();
      }
    };
    return {
      ruleForm: {
        uname: "",
        pass: "",
        password: "",
      },
      rules: {
        uname: [
          { required: true, message: "User name can't be null!!", trigger: "blur" },
        ],
        pass: [{ required: true, validator: validatePass, trigger: "blur" }],
        password: [
          { required: true, validator: validatePass2, trigger: "blur" },
        ],
      },
      loading: false
    };
  },
  methods: {
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        this.loading = true;  // The submit button displays the loading animation
        if (valid) {
          let _this = this;
          this.axios({     // Axios initiates a request to the backend
            url: "/api/user/register",  // address
            method: "post",             
            headers: {                  
              "Content-Type": "application/json",
            },
            data: { // Request parameter（data) is different from the logged in params
              uname: _this.ruleForm.uname,
              password: _this.ruleForm.password,
            },
          }).then((res) => { 
            // When receiving a response from the backend,
            // execute the code in parentheses, where res is the response information, which is the information returned by the backend
            if (res.data.code === '0') {  // When the response code is 0, it indicates successful registration
              // Display successful information of backend response
              this.$message({
                message: res.data.msg,
                type: "success",
              });
            }else{  // When the response code is not 0, it indicates a registration failure
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
    goBack() {
      this.$router.go(-1);
    },
  },
};
</script>

<style scoped>
/* Set the login panel to be centered with a width of 400px */
.box-card {
  margin: auto auto;
  width: 500px;
}
/* Center the form in the login panel */
.login-from {
  margin: auto auto;
}

.Username {
  white-space: nowrap; /* Disable automatic line wrapping */
}

.Password {
  white-space: nowrap;
}

.Confirm-Password {
  white-space: nowrap;
}


</style>