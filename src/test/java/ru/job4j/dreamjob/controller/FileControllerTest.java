package ru.job4j.dreamjob.controller;

import org.checkerframework.checker.nullness.Opt;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.service.FileService;
import ru.job4j.dreamjob.service.SimpleFileService;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileControllerTest {
    private FileController fileController;
    private FileService fileService;
    private MultipartFile testFile;

    @BeforeEach
    public void init() {
        fileService = mock(FileService.class);
        fileController = new FileController(fileService);
        testFile = new MockMultipartFile("testFile.img", new byte[] {1, 2, 3});
    }

    @Test
    public void whenGetByIdTrue() throws IOException {
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        when(fileService.getFileById(any(Integer.class))).thenReturn(Optional.of(fileDto));

        var view = fileController.getById(1);
        var expected = ResponseEntity.ok(fileDto.getContent());
        assertThat(view).isEqualTo(expected);
    }

    @Test
    public void whenGetByIdFalse() {
        when(fileService.getFileById(any(Integer.class))).thenReturn(Optional.empty());

        var view = fileController.getById(1);
        var expected = ResponseEntity.notFound().build();
        assertThat(view).isEqualTo(expected);
    }
}