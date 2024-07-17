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
		props: ['user'], // Receive user data from the parent component
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

			// File size formatting function
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

			// Function when file upload is successful
			handleFilUploadSuccess (res,file,fileList) {
				console.log("This fileList here!:",fileList)
				console.log("File size:", file.size);
				// Remove uploaded files after successful upload
				const uploadedFileIndex = fileList.findIndex(item => item.name === file.name);
				if (uploadedFileIndex !== -1) {
					fileList.splice(uploadedFileIndex, 1);
					console.log("delete uploadFileIndex:",uploadedFileIndex);
				}

				// Construct file information object
				const fileInfo = {
					file_name: file.name,
					create_time: new Date().toLocaleString(),
					// file_size: this.formatFileSize(file.size),
					file_size: file.size,
					
				};
				// Pass file information to parent component through $emit
				console.log('return fileinfo:', fileInfo);
				this.$emit('file-uploaded', fileInfo);

				this.$message.success("Successfully uploaded!")
			},

			handleUpdate () {
				this.dialogVisible = true;
			},

			// Functions for handling file uploads
			handleUpload () {
				// Clear the file information array for the current upload
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
