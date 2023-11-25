package com.project.traveldiary.service;

import com.project.traveldiary.dto.DiaryUploadRequest;
import com.project.traveldiary.dto.DiaryUploadResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface DiaryService {

    DiaryUploadResponse uploadDiary(List<MultipartFile> files, DiaryUploadRequest diaryUploadRequest, String userId) throws IOException;
}
