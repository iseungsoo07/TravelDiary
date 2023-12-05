package com.project.traveldiary.controller;

import com.project.traveldiary.dto.DiaryDeleteResponse;
import com.project.traveldiary.dto.DiaryDetailResponse;
import com.project.traveldiary.dto.DiaryLikeResponse;
import com.project.traveldiary.dto.DiaryResponse;
import com.project.traveldiary.dto.DiaryUpdateRequest;
import com.project.traveldiary.dto.DiaryUpdateResponse;
import com.project.traveldiary.dto.DiaryUploadRequest;
import com.project.traveldiary.dto.DiaryUploadResponse;
import com.project.traveldiary.es.DiaryDocument;
import com.project.traveldiary.es.SearchCond;
import com.project.traveldiary.security.TokenProvider;
import com.project.traveldiary.service.DiaryService;
import java.io.IOException;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;
    private final TokenProvider tokenProvider;

    @PostMapping("/diary")
    public ResponseEntity<DiaryUploadResponse> uploadDiary(
        @RequestPart("file") List<MultipartFile> files,
        @RequestPart("diaryUploadRequest") @Valid DiaryUploadRequest diaryUploadRequest,
        @RequestHeader("X-AUTH-TOKEN") String token) throws IOException {

        String userId = tokenProvider.getUsername(token);

        return ResponseEntity.ok(diaryService.uploadDiary(files, diaryUploadRequest, userId));
    }

    @GetMapping("/diary/{id}")
    public ResponseEntity<DiaryDetailResponse> getDiary(@PathVariable Long id) {
        return ResponseEntity.ok(diaryService.getDiary(id));
    }

    @GetMapping("/user/{userId}/diaries")
    public ResponseEntity<Page<DiaryResponse>> getDiaries(@PathVariable Long userId,
        @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "date") String sort) {

        return ResponseEntity.ok(diaryService.getDiaries(userId, page, size, sort));
    }

    @PutMapping("/diary/{id}")
    public ResponseEntity<DiaryUpdateResponse> updateDiary(@PathVariable Long id,
        @RequestPart("file") List<MultipartFile> files,
        @RequestPart("diaryUpdateRequest") @Valid DiaryUpdateRequest diaryUpdateRequest,
        @RequestHeader("X-AUTH-TOKEN") String token) {

        String userId = tokenProvider.getUsername(token);

        return ResponseEntity.ok(diaryService.updateDiary(id, files, diaryUpdateRequest, userId));
    }

    @DeleteMapping("/diary/{id}")
    public ResponseEntity<DiaryDeleteResponse> deleteDiary(@PathVariable Long id,
        @RequestHeader("X-AUTH-TOKEN") String token) {

        String userId = tokenProvider.getUsername(token);

        diaryService.deleteDiary(id, userId);

        return ResponseEntity.ok(DiaryDeleteResponse.builder()
            .message("일기 삭제가 완료되었습니다.")
            .build());
    }

    @PatchMapping("/diary/{id}/like")
    public ResponseEntity<DiaryLikeResponse> likeDiary(@PathVariable Long id,
        @RequestHeader("X-AUTH-TOKEN") String token) {

        String userId = tokenProvider.getUsername(token);

        DiaryLikeResponse diaryLikeResponse = diaryService.likeDiary(id, userId);

        String fromUser = diaryLikeResponse.getUserId();
        String toUser = diaryLikeResponse.getWriter();

        return ResponseEntity.ok(DiaryLikeResponse.builder()
            .userId(fromUser)
            .writer(toUser)
            .message(fromUser + "님이 " + toUser + "님의 일기에 좋아요를 눌렀습니다.")
            .build());
    }

    @DeleteMapping("/diary/{id}/cancel/like")
    public ResponseEntity<DiaryLikeResponse> cancelLikeDiary(@PathVariable Long id,
        @RequestHeader("X-AUTH-TOKEN") String token) {

        String userId = tokenProvider.getUsername(token);

        DiaryLikeResponse diaryLikeResponse = diaryService.cancelLikeDiary(id, userId);

        String fromUser = diaryLikeResponse.getUserId();
        String toUser = diaryLikeResponse.getWriter();

        return ResponseEntity.ok(DiaryLikeResponse.builder()
            .userId(fromUser)
            .writer(toUser)
            .build());
    }

    @GetMapping("/diary/search")
    public ResponseEntity<Page<DiaryDocument>> searchDiaries(@RequestBody SearchCond searchCond,
        @PageableDefault Pageable pageable) {
        return ResponseEntity.ok(diaryService.searchDiaries(searchCond, pageable));
    }

}
