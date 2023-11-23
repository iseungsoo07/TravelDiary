package com.project.traveldiary.controller;

import com.project.traveldiary.dto.DiaryUploadRequest;
import com.project.traveldiary.dto.DiaryUploadResponse;
import com.project.traveldiary.security.TokenProvider;
import com.project.traveldiary.service.DiaryService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diary")
public class DiaryController {

    private final DiaryService diaryService;
    private final TokenProvider tokenProvider;

    @PostMapping
    public ResponseEntity<DiaryUploadResponse> uploadDiary(@RequestPart("file") MultipartFile file,
        @RequestPart("diaryUploadRequest") DiaryUploadRequest diaryUploadRequest,
        @RequestHeader("X-AUTH-TOKEN") String token) throws IOException {

        String userId = tokenProvider.getUsername(token);

        return ResponseEntity.ok(diaryService.uploadDiary(file, diaryUploadRequest, userId));
    }

}
