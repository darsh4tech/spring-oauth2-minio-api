package com.keeneye.resourceserver.controller;


import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.springframework.http.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.keeneye.resourceserver.dto.FileDTO;
import com.keeneye.resourceserver.service.impl.MinIOFileService;
import com.keeneye.resourceserver.utils.FileType;
import com.keeneye.resourceserver.utils.ResourceUtils;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/resource")
@Slf4j
public class ResourceController {

	private final MinIOFileService minIOFileService;

	public ResourceController(MinIOFileService minIOFileService) {
		this.minIOFileService = minIOFileService;
	}

	@PostMapping
	public FileDTO uploadFile(@RequestParam("file") MultipartFile file,@RequestParam Integer productId) {

		ResourceUtils.validateFileSize(file, FileType.LOGO);

		InputStream inputStream;
		try {
			inputStream = file.getInputStream();
		} catch (IOException ioException) {
			log.error("EXCEPTION THROWN -> {}", ioException.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CORRUPTED FILE");
		}

		String fileId = minIOFileService.uploadFile(file.getOriginalFilename(), productId,
				inputStream);
		return FileDTO.builder().fileId(fileId).status("UPLOADED").build();
	}

	@GetMapping
	public ResponseEntity<byte[]> downloadFile(@RequestParam("fileId") String fileId,@RequestParam Integer productId) {

		Optional<InputStream> optionalInputStream = minIOFileService.retrieveFile(fileId);

		if (!optionalInputStream.isPresent()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "file not found");
		}
		try {
			InputStream inputStream = optionalInputStream.get();
			String fileName = fileId.split("_")[1];
			byte[] fileBytes = IOUtils.toByteArray(inputStream);
			HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
			httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION,
					ContentDisposition.builder("inline").filename(fileName).build().toString());
			return ResponseEntity.ok().headers(httpHeaders).body(fileBytes);
		} catch (IOException ioException) {
			log.error("Can't download the file because this exception -> {}", ioException.getMessage());
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "can't download the file");
		}
	}

	@DeleteMapping
	public ResponseEntity<FileDTO> deleteFile(@RequestParam("fileId") String fileId,@RequestParam Integer productId) {
		boolean state = minIOFileService.removeFile(fileId);
		if (state) {
			return ResponseEntity.ok(FileDTO.builder().deleted(true).build());
		}
		return new ResponseEntity<>(FileDTO.builder().deleted(false).build(), HttpStatus.NOT_FOUND);
	}
}
