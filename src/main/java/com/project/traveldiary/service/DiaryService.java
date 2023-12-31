package com.project.traveldiary.service;

import com.project.traveldiary.dto.CommentRequest;
import com.project.traveldiary.dto.CommentResponse;
import com.project.traveldiary.dto.CreateCommentResponse;
import com.project.traveldiary.dto.DiaryDetailResponse;
import com.project.traveldiary.dto.DiaryLikeResponse;
import com.project.traveldiary.dto.DiaryResponse;
import com.project.traveldiary.dto.DiaryUpdateRequest;
import com.project.traveldiary.dto.DiaryUpdateResponse;
import com.project.traveldiary.dto.DiaryUploadRequest;
import com.project.traveldiary.dto.DiaryUploadResponse;
import com.project.traveldiary.dto.ReplyCommentResponse;
import com.project.traveldiary.dto.ReplyResponse;
import com.project.traveldiary.es.DiaryDocument;
import com.project.traveldiary.es.SearchCond;
import java.io.IOException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<DiaryDocument> searchDiaries(SearchCond searchCond, Pageable pageable);

    CreateCommentResponse createComment(Long id, CommentRequest commentRequest, String userId);

    ReplyCommentResponse replyComment(Long diaryId, Long commentId, CommentRequest commentRequest,
        String userId);

    Page<CommentResponse> getComments(Long id, Pageable pageable);

    Page<ReplyResponse> getReplies(Long commentId, Pageable pageable);
}
