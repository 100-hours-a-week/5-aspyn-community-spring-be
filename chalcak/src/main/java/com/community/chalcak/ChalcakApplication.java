package com.community.chalcak;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class ChalcakApplication {

	static {
		System.setProperty("com.amazonaws.sdk.disableEc2Metadata", "true");  // 추가
	}

	@Value("${upload.directory}")
	private String uploadDirectory;

	public static void main(String[] args) {

		SpringApplication.run(ChalcakApplication.class, args);
	}

	@PostConstruct
	public void init() {
		File directory = new File(uploadDirectory);
		if (!directory.exists()) {
			directory.mkdirs(); // 디렉토리 생성
		}
	}

}
