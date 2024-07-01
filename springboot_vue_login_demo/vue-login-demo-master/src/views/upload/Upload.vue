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
					:action="`/api/user/upload?uid=${user.uid}&uname=${user.uname}`"
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
		props: ['user'], // 接收来自父组件的 user 数据
		// mounted() {
		// 	console.log('Received user data:', this.user);
		// },

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

			// 文件大小格式化函数
			formatFileSize(size) {
				const units = ['Bytes', 'KB', 'MB', 'GB', 'TB'];
				let unitIndex = 0;
				
				while (size >= 1024 && unitIndex < units.length - 1) {
					size /= 1024;
					unitIndex++;
				}
				console.log('size:', typeof(size));
				return size.toFixed(2) + ' ' + units[unitIndex];
			},

			// 文件上传成功时的函数
			handleFilUploadSuccess (res,file,fileList) {
				console.log("This fileList here!:",fileList)
				console.log("File size:", file.size);
				// 上传成功后移除已上传的文件
				const uploadedFileIndex = fileList.findIndex(item => item.name === file.name);
				if (uploadedFileIndex !== -1) {
					fileList.splice(uploadedFileIndex, 1);
					console.log("delete uploadFileIndex:",uploadedFileIndex);
				}

				// 构造文件信息对象
				const fileInfo = {
					file_name: file.name,
					create_time: new Date().toLocaleString(),
					// file_size: this.formatFileSize(file.size), // 调用文件大小格式化函数
					file_size: file.size,
					
				};
				// 通过 $emit 将文件信息传递给父组件
				console.log('return fileinfo:', fileInfo);
				this.$emit('file-uploaded', fileInfo);

				this.$message.success("Successfully uploaded!")
			},

			handleUpdate () {
				this.dialogVisible = true;
			},

			// 处理文件上传的函数
			handleUpload () {
				// 清空当次上传的文件信息数组
        		this.$emit('clear-new-uploads');

				//console.log(res,file)
				this.submitUpload()
				this.dialogVisible = false
			}
		}
	}
</script>

<style scoped="scoped">

	.btn-upload {
		top: 15%;
		left: 5%;
		position: absolute;
		z-index: 100;
		border-radius: 30px;
		box-shadow: 0 2px 12px 0 rgba(91, 156, 255, 0.9)
	}
	
	.el-upload {
		margin: 5px;
	}
</style>
