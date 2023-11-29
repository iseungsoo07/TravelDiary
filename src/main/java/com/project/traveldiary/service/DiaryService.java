package com.project.traveldiary.service;

import com.project.traveldiary.dto.DiaryDetailResponse;
import com.project.traveldiary.dto.DiaryLikeResponse;
import com.project.traveldiary.dto.DiaryResponse;
import com.project.traveldiary.dto.DiaryUpdateRequest;
import com.project.traveldiary.dto.DiaryUpdateResponse;
import com.project.traveldiary.dto.DiaryUploadRequest;
import com.project.traveldiary.dto.DiaryUploadResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface DiaryService {

    DiaryUploadResponse uploadDiary(List<MultipartFile> files,
        DiaryUploadRequest diaryUploadRequest, String userId) throws IOException;

    DiaryDetailResponse getDiary(Long id);

    Page<DiaryResponse> getDiaries(Long userId, int page, int size, String sort);

    DiaryUpdateResponse updateDiary(Long id, List<MultipartFile> files,
        DiaryUpdateRequest diaryUpdateRequest, String userId);

    void deleteDiary(Long id, String userId);

    DiaryLikeResponse likeDiary(Long id, String userId);

    DiaryLikeResponse cancelLikeDiary(Long id, String userId);
}
