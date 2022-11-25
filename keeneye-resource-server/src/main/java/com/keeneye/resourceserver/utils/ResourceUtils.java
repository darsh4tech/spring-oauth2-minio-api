package com.keeneye.resourceserver.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

public class ResourceUtils {

	public static void validateFileSize(MultipartFile file, FileType fileType) {
		long fileSize = file.getSize();
		if (fileSize == 0) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid size");
		}
		if (fileSize > fileType.getSize()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "INVALID LOGO SIZE");
		}
	}
}
