<template>
	<div class="container">
		<el-button class="btn-upload" type="primary" @click="handleUpdate">Upload files</el-button>
		<el-dialog
			title="UPLOAD PLATFORM"
			:visible.sync="dialogVisible"
			width="30%"
			>
			<span>
				<el-upload class="upload-demo"
					ref="upload"
					drag 
					action="/api/user/upload" 
					multiple
					:auto-upload="false"
					:limit="5"
					:on-success="handleFilUploadSuccess"
					:on-remove="handleRemove"
					>
					<i class="el-icon-upload"></i>
					<div class="el-upload__text">Drag the file(s) here, or <em>click "UPLOAD"</em></div>
					<div class="el-upload__tip" slot="tip">Only upload Word/PDF files and the size should be under 500kb</div>
				</el-upload>
			</span>
			<span slot="footer" class="dialog-footer">
				<el-button @click="dialogVisible = false">Cancel</el-button>
				<el-button type="primary" @click="handleUpload">Upload</el-button>
			</span>
		</el-dialog>
	</div>
</template>

<script>
	export default {
		name: "Upload",
		data () {
			return {
				dialogVisible: false
			}
		},
		methods: {
			handleRemove(file,fileList) {
				console.log(file,fileList);
			},
			submitUpload() {
				this.$refs.upload.submit();
			},
			// 文件上传成功时的函数
			handleFilUploadSuccess (res,file,fileList) {
				console.log(res,file,fileList)
				this.$message.success("Successfully uploaded!")
			},
			handleUpdate () {
				this.dialogVisible = true;
			},
			// 处理文件上传的函数
			handleUpload () {
				//console.log(res,file)
				this.submitUpload()
				this.dialogVisible = false
			}
		}
	}
</script>

<style scoped="scoped">

	.btn-upload {
		top: 18%;
		left: 1%;
		position: absolute;
		z-index: 100;
		border-radius: 30px;
		box-shadow: 0 2px 12px 0 rgba(91, 156, 255, 0.9)
	}
	
	.el-upload {
		margin: 5px;
	}
</style>
