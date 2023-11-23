package com.project.traveldiary.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.project.traveldiary.dto.DiaryUploadRequest;
import com.project.traveldiary.dto.DiaryUploadResponse;
import com.project.traveldiary.entity.Diary;
import com.project.traveldiary.entity.User;
import com.project.traveldiary.exception.UserException;
import com.project.traveldiary.repository.DiaryRepository;
import com.project.traveldiary.repository.UserRepository;
import com.project.traveldiary.type.ErrorCode;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;
    private final ObjectMetadata objectMetadata;
    private final AmazonS3Client amazonS3;

    @Override
    public DiaryUploadResponse uploadDiary(MultipartFile file,
        DiaryUploadRequest diaryUploadRequest,
        String userId) throws IOException {

        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));

        String fileName = null;
        String filePath = null;

        if (file != null && !file.isEmpty()) {
            fileName = generateFileName(file); // DB에 저장될 file명 생성
            amazonS3.putObject(bucket, fileName, file.getInputStream(), getObjectMetadata(file));
            filePath = amazonS3.getUrl(bucket, fileName).toString();
        }

        Diary diary = Diary.builder()
            .user(user)
            .title(diaryUploadRequest.getTitle())
            .content(diaryUploadRequest.getContent())
            .fileName(fileName)
            .filePath(filePath)
            .hashtags(diaryUploadRequest.getHashtags())
            .build();

        diaryRepository.save(diary);

        return DiaryUploadResponse.builder()
            .title(diaryUploadRequest.getTitle())
            .content(diaryUploadRequest.getContent())
            .fileName(fileName)
            .filePath(filePath)
            .hashtags(diaryUploadRequest.getHashtags())
            .message("일기 등록이 완료되었습니다.")
            .build();
    }

    private ObjectMetadata getObjectMetadata(MultipartFile file) {
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        return objectMetadata;
    }

    private String generateFileName(MultipartFile file) {
        return UUID.randomUUID().toString().concat(
            Objects.requireNonNull(file.getOriginalFilename()));
    }
}
