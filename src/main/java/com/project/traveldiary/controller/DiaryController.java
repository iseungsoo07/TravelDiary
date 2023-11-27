package com.project.traveldiary.controller;

import com.project.traveldiary.dto.DiaryDeleteResponse;
import com.project.traveldiary.dto.DiaryDetailResponse;
import com.project.traveldiary.dto.DiaryLikeResponse;
import com.project.traveldiary.dto.DiaryListResponse;
import com.project.traveldiary.dto.DiaryUpdateRequest;
import com.project.traveldiary.dto.DiaryUpdateResponse;
import com.project.traveldiary.dto.DiaryUploadRequest;
import com.project.traveldiary.dto.DiaryUploadResponse;
import com.project.traveldiary.security.TokenProvider;
import com.project.traveldiary.service.DiaryService;
import java.io.IOException;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<DiaryUploadResponse> uploadDiary(
        @RequestPart("file") List<MultipartFile> files,
        @RequestPart("diaryUploadRequest") @Valid DiaryUploadRequest diaryUploadRequest,
        @RequestHeader("X-AUTH-TOKEN") String token) throws IOException {

        String userId = tokenProvider.getUsername(token);

        return ResponseEntity.ok(diaryService.uploadDiary(files, diaryUploadRequest, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiaryDetailResponse> getDiary(@PathVariable Long id) {
        return ResponseEntity.ok(diaryService.getDiary(id));
    }

    @GetMapping("/list/{id}")
    public ResponseEntity<List<DiaryListResponse>> getDiaries(@PathVariable Long id,
        @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "date") String sort) {

        return ResponseEntity.ok(diaryService.getDiaries(id, page, size, sort));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiaryUpdateResponse> updateDiary(@PathVariable Long id,
        @RequestPart("file") List<MultipartFile> files,
        @RequestPart("diaryUpdateRequest") @Valid DiaryUpdateRequest diaryUpdateRequest,
        @RequestHeader("X-AUTH-TOKEN") String token) {

        String userId = tokenProvider.getUsername(token);

        return ResponseEntity.ok(diaryService.updateDiary(id, files, diaryUpdateRequest, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DiaryDeleteResponse> deleteDiary(@PathVariable Long id,
        @RequestHeader("X-AUTH-TOKEN") String token) {

        String userId = tokenProvider.getUsername(token);

        diaryService.deleteDiary(id, userId);

        return ResponseEntity.ok(DiaryDeleteResponse.builder()
            .message("일기 삭제가 완료되었습니다.")
            .build());
    }

    @PatchMapping("/{id}/like")
    public ResponseEntity<DiaryLikeResponse> likeDiary(@PathVariable Long id,
        @RequestHeader("X-AUTH-TOKEN") String token) {

        String userId = tokenProvider.getUsername(token);

        return ResponseEntity.ok(diaryService.likeDiary(id, userId));
    }
}
