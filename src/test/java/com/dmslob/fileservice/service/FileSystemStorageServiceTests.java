package com.dmslob.fileservice.service;

import com.dmslob.fileservice.config.StorageProperties;
import com.dmslob.fileservice.exception.StorageException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;

public class FileSystemStorageServiceTests {

	private StorageProperties properties = new StorageProperties();
	private FileSystemStorageService service;

	@BeforeEach
	public void init() {
		properties.setLocation("target/files/" + Math.abs(new Random().nextLong()));
		service = new FileSystemStorageService(properties);
		service.init();
	}

	@Test
	public void loadNonExistent() {
		assertThat(service.load("foo.txt")).doesNotExist();
	}

	@Test
	public void saveAndLoad() {
		service.store(new MockMultipartFile("foo", "foo.txt", MediaType.TEXT_PLAIN_VALUE,
				"Hello, World".getBytes()));
		assertThat(service.load("foo.txt")).exists();
	}

	@Test
	public void saveNotPermitted() {
		assertThrows(StorageException.class, () -> {
			service.store(new MockMultipartFile("foo", "../foo.txt",
			MediaType.TEXT_PLAIN_VALUE, "Hello, World".getBytes()));
		});
	}

	@Test
	public void savePermitted() {
		service.store(new MockMultipartFile("foo", "bar/../foo.txt",
				MediaType.TEXT_PLAIN_VALUE, "Hello, World".getBytes()));
	}
}
