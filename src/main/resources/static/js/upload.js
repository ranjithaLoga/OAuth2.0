var total_File_Length, total_File_Uploaded, file_Count, files_Uploaded;

// To log everything on console
function debug(s) {
	var debug_element = document.getElementById('debug');
	if (debug_element) {
		debug_element.innerHTML = debug_element.innerHTML + '<br/>' + s;
	}
}

// Will be called when upload is completed
function on_Upload_Complete(e) {
	total_File_Uploaded += document.getElementById('files').files[files_Uploaded].size;
	files_Uploaded++;
	debug_element('complete ' + files_Uploaded + " of " + file_Count);
	debug_element('TotalFileUploaded: ' + total_File_Uploaded);
	if (files_Uploaded < file_Count) {
		uploadNext();
	} else {
		var bar = document.getElementById('bar');
		bar.style.width = '100%';
		bar.innerHTML = '100 % complete';
		bootbox.alert('Finished uploading file(s)');

	}
}

// Will be called when user select the files in file control
function on_File_Select(e) {
	var files = e.target.files; 
	var _output = [];
	file_Count = files.length;
	total_File_Length = 0;
	for (var i = 0; i < file_Count; i++) {
		var file = files[i];
		_output.push(file.name, ' (', file.size, ' bytes, ',
				file.lastModifiedDate.toLocaleDateString(), ')');
		_output.push('<br/>');
		debug_element('add ' + file.size);
		total_File_Length += file.size;
	}
	document.getElementById('selectedFiles').innerHTML = _output.join('');
}

// This will continuously update the progress bar based on the percentage of image uploaded
function on_Upload_Progress(e) {
	if (e.lengthComputable) {
		var percentComplete = parseInt((e.loaded + total_File_Uploaded) * 100 / total_File_Length);
		
		if(percentComplete>100)
			percentComplete = 100;
		var _bar = document.getElementById('bar');
		_bar.style.width = percentComplete + '%';
		_bar.innerHTML = percentComplete + ' % complete';
	} else {
		debug_element('unable to compute');
	}
}

// Any other errors in uploading files will be handled here.
function on_Upload_Failed(e) {
	bootbox.alert("Error uploading file");
}

// Pick the next file in queue and upload it to remote server
function up_load_Next() {
	var _xhr = new XMLHttpRequest();
	var _fd = new FormData();
	var file = document.getElementById('files').files[files_Uploaded];
	_fd.append("multipartFile", file);
	_xhr.upload.addEventListener("progress", on_Upload_Progress, false);
	_xhr.addEventListener("load", on_Upload_Complete, false);
	_xhr.addEventListener("error", on_Upload_Failed, false);
	_xhr.open("POST", "upload");
    console.log("fd", file);
_xhr.send(fd);
}

function null_Validation(){
	var file = document.getElementById("files").files;
	var _length = file.length;
	
	if(_length <= 0) {
		
		return false;
	}
	else {
		return true;
	}			
}

function start_Upload() {
	if(!null_Validation()){
		bootbox.alert("Please select an image to upload!");
	}
	else{

	total_File_Uploaded = files_Uploaded = 0;
	up_load_Next();

	}	
}

function reset_All(){
	document.getElementById("imageUpload").reset();
	document.getElementById("selectedFiles").value=" ";
	var _bar = document.getElementById('bar');
	_bar.style.width = 0;
	_bar.innerHTML = " ";

}

// Event listeners for button clicks
window.onload = function() {
	document.getElementById('files').addEventListener('change', on_File_Select, false);
	document.getElementById('uploadButton').addEventListener('click', start_Upload, false);
	document.getElementById('resetButton').addEventListener('click', reset_All, false);
	
}

