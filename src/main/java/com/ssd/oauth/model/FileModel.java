
package com.sliit.ssd.oauth.model;

import org.springframework.web.multipart.MultipartFile;

public class FileModel {
	
	private static final long serialVersionUID = 1L;
	private MultipartFile multi_part_File;

	public MultipartFile getMultipartFile() {
		return multi_part_File;
	}

	public void setMultipartFile(MultipartFile multipartFile) {
		this.multi_part_File = multi_part_File;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
