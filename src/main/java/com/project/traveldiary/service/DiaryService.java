package com.project.traveldiary.service;

import com.project.traveldiary.dto.DiaryDetailResponse;
import com.project.traveldiary.dto.DiaryLikeResponse;
import com.project.traveldiary.dto.DiaryListResponse;
import com.project.traveldiary.dto.DiaryUpdateRequest;
import com.project.traveldiary.dto.DiaryUpdateResponse;
import com.project.traveldiary.dto.DiaryUploadRequest;
import com.project.traveldiary.dto.DiaryUploadResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface DiaryService {

    DiaryUploadResponse uploadDiary(List<MultipartFile> files,
        DiaryUploadRequest diaryUploadRequest, String userId) throws IOException;

    DiaryDetailResponse getDiary(Long id);

    List<DiaryListResponse> getDiaries(Long id, int page, int size, String sort);

    DiaryUpdateResponse updateDiary(Long id, List<MultipartFile> files,
        DiaryUpdateRequest diaryUpdateRequest, String userId);

    void deleteDiary(Long id, String userId);

    DiaryLikeResponse likeDiary(Long id, String userId);
}
