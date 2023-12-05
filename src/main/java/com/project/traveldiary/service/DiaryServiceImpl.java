package com.project.traveldiary.service;

import static com.project.traveldiary.type.ErrorCode.ALREADY_LIKE_DIARY;
import static com.project.traveldiary.type.ErrorCode.CAN_DELETE_OWN_DIARY;
import static com.project.traveldiary.type.ErrorCode.CAN_UPDATE_OWN_DIARY;
import static com.project.traveldiary.type.ErrorCode.FAIL_DELETE_FILE;
import static com.project.traveldiary.type.ErrorCode.FAIL_UPLOAD_FILE;
import static com.project.traveldiary.type.ErrorCode.NOT_FOUND_COMMENT;
import static com.project.traveldiary.type.ErrorCode.NOT_FOUND_DIARY;
import static com.project.traveldiary.type.ErrorCode.NOT_FOUND_LIKE;
import static com.project.traveldiary.type.ErrorCode.NOT_FOUND_USER;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.project.traveldiary.aop.DistributedLock;
import com.project.traveldiary.dto.CommentRequest;
import com.project.traveldiary.dto.CommentResponse;
import com.project.traveldiary.dto.DiaryDetailResponse;
import com.project.traveldiary.dto.DiaryLikeResponse;
import com.project.traveldiary.dto.DiaryResponse;
import com.project.traveldiary.dto.DiaryUpdateRequest;
import com.project.traveldiary.dto.DiaryUpdateResponse;
import com.project.traveldiary.dto.DiaryUploadRequest;
import com.project.traveldiary.dto.DiaryUploadResponse;
import com.project.traveldiary.entity.Comment;
import com.project.traveldiary.entity.Diary;
import com.project.traveldiary.entity.Likes;
import com.project.traveldiary.entity.User;
import com.project.traveldiary.es.DiaryDocument;
import com.project.traveldiary.es.SearchCond;
import com.project.traveldiary.exception.CommentException;
import com.project.traveldiary.exception.DiaryException;
import com.project.traveldiary.exception.LikeException;
import com.project.traveldiary.exception.UserException;
import com.project.traveldiary.repository.CommentRepository;
import com.project.traveldiary.repository.DiaryRepository;
import com.project.traveldiary.repository.DiarySearchQueryRepository;
import com.project.traveldiary.repository.DiarySearchRepository;
import com.project.traveldiary.repository.LikesRepository;
import com.project.traveldiary.repository.UserRepository;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryServiceImpl implements DiaryService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;
    private final LikesRepository likesRepository;
    private final ObjectMetadata objectMetadata;
    private final AmazonS3Client amazonS3;
    private final DiarySearchRepository diarySearchRepository;
    private final DiarySearchQueryRepository diarySearchQueryRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public DiaryUploadResponse uploadDiary(List<MultipartFile> files,
        DiaryUploadRequest diaryUploadRequest, String userId) {

        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        List<String> filePaths = uploadFiles(files);

        Diary diary = Diary.builder()
            .user(user)
            .title(diaryUploadRequest.getTitle())
            .content(diaryUploadRequest.getContent())
            .filePath(filePaths)
            .hashtags(diaryUploadRequest.getHashtags())
            .build();

        diaryRepository.save(diary);
        saveDiaryDocuments();

        return DiaryUploadResponse.builder()
            .title(diaryUploadRequest.getTitle())
            .content(diaryUploadRequest.getContent())
            .filePath(filePaths)
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

    @Override
    public DiaryDetailResponse getDiary(Long id) {

        Diary diary = diaryRepository.findById(id)
            .orElseThrow(() -> new DiaryException(NOT_FOUND_DIARY));

        List<String> filePaths = diary.getFilePath();

        return DiaryDetailResponse.builder()
            .title(diary.getTitle())
            .content(diary.getContent())
            .filePath(filePaths)
            .hashtags(diary.getHashtags())
            .writer(diary.getUser().getNickname())
            .likeCount(diary.getLikeCount())
            .commentCount(diary.getCommentCount())
            .createdAt(diary.getCreatedAt())
            .build();
    }

    @Override
    public Page<DiaryResponse> getDiaries(Long userId, int page, int size, String sort) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        Page<Diary> diaries = diaryRepository.findByUser(user, pageable);

        return DiaryResponse.diaryList(diaries);
    }

    @Override
    @Transactional
    public DiaryUpdateResponse updateDiary(Long id, List<MultipartFile> files,
        DiaryUpdateRequest diaryUpdateRequest, String userId) {

        Diary diary = diaryRepository.findById(id)
            .orElseThrow(() -> new DiaryException(NOT_FOUND_DIARY));

        if (!Objects.equals(diary.getUser().getUserId(), userId)) {
            throw new DiaryException(CAN_UPDATE_OWN_DIARY);
        }

        deleteFiles(diary);

        List<String> filePaths = uploadFiles(files);

        diary.update(diaryUpdateRequest, filePaths);

        diaryRepository.save(diary);
        deleteDiaryDocument(diary);
        saveDiaryDocuments();

        return DiaryUpdateResponse.builder()
            .title(diary.getTitle())
            .cotent(diary.getContent())
            .hashtags(diary.getHashtags())
            .filePath(filePaths)
            .build();

    }

    @Override
    @Transactional
    public void deleteDiary(Long id, String userId) {
        Diary diary = diaryRepository.findById(id)
            .orElseThrow(() -> new DiaryException(NOT_FOUND_DIARY));

        if (!Objects.equals(diary.getUser().getUserId(), userId)) {
            throw new DiaryException(CAN_DELETE_OWN_DIARY);
        }

        deleteFiles(diary);
        diaryRepository.delete(diary);
        deleteDiaryDocument(diary);
    }

    @Override
    @Transactional
    @DistributedLock(prefix = "like_diary")
    public DiaryLikeResponse likeDiary(Long id, String userId) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        Diary diary = diaryRepository.findById(id)
            .orElseThrow(() -> new DiaryException(NOT_FOUND_DIARY));

        if (likesRepository.existsByUserAndDiary(user, diary)) {
            throw new LikeException(ALREADY_LIKE_DIARY);
        }

        Likes savedLike = likesRepository.save(Likes.builder()
            .user(user)
            .diary(diary)
            .build());

        diary.increaseLikeCount();

        diaryRepository.save(diary);

        String fromUser = savedLike.getUser().getNickname();
        String toUser = savedLike.getDiary().getUser().getNickname();

        return DiaryLikeResponse.builder()
            .userId(fromUser)
            .writer(toUser)
            .build();
    }

    @Override
    @Transactional
    @DistributedLock(prefix = "like_diary")
    public DiaryLikeResponse cancelLikeDiary(Long id, String userId) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        Diary diary = diaryRepository.findById(id)
            .orElseThrow(() -> new DiaryException(NOT_FOUND_DIARY));

        Likes likes = likesRepository.findByUserAndDiary(user, diary)
            .orElseThrow(() -> new LikeException(NOT_FOUND_LIKE));

        String fromUser = likes.getUser().getNickname();
        String toUser = likes.getDiary().getUser().getNickname();

        likesRepository.delete(likes);

        diary.decreaseLikeCount();

        diaryRepository.save(diary);

        return DiaryLikeResponse.builder()
            .userId(fromUser)
            .writer(toUser)
            .build();
    }

    @Override
    public Page<DiaryDocument> searchDiaries(SearchCond searchCond, Pageable pageable) {
        return diarySearchQueryRepository.searchDiariesBySearchCond(searchCond, pageable);
    }

    @Override
    public CommentResponse createComment(Long id, CommentRequest commentRequest, String userId) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        Diary diary = diaryRepository.findById(id)
            .orElseThrow(() -> new DiaryException(NOT_FOUND_DIARY));

        Comment comment = Comment.builder()
            .diary(diary)
            .user(user)
            .parentCommentId(null)
            .content(commentRequest.getContent())
            .build();

        commentRepository.save(comment);

        return CommentResponse.builder()
            .parentCommentId(comment.getParentCommentId())
            .writer(comment.getUser().getNickname())
            .content(comment.getContent())
            .build();

    }

    @Override
    public CommentResponse replyComment(Long diaryId, Long commentId, CommentRequest commentRequest,
        String userId) {

        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        Diary diary = diaryRepository.findById(diaryId)
            .orElseThrow(() -> new DiaryException(NOT_FOUND_DIARY));

        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new CommentException(NOT_FOUND_COMMENT));

        Comment reply = Comment.builder()
            .diary(diary)
            .user(user)
            .parentCommentId(comment.getId())
            .content(commentRequest.getContent())
            .build();

        commentRepository.save(reply);

        return CommentResponse.builder()
            .parentCommentId(reply.getParentCommentId())
            .writer(reply.getUser().getNickname())
            .content(reply.getContent())
            .build();
    }

    @Override
    public Page<CommentResponse> getComments(Long id, Pageable pageable) {
        Diary diary = diaryRepository.findById(id)
            .orElseThrow(() -> new DiaryException(NOT_FOUND_DIARY));

        Page<Comment> comments = commentRepository.findByDiary(diary, pageable);

        return CommentResponse.commentList(comments);

    }

    private void saveDiaryDocuments() {
        List<DiaryDocument> diaryDocumentList = diaryRepository.findAll().stream()
            .map(DiaryDocument::from)
            .collect(Collectors.toList());

        diarySearchRepository.saveAll(diaryDocumentList);
    }

    private void deleteDiaryDocument(Diary diary) {
        DiaryDocument diaryDocument = DiaryDocument.from(diary);
        diarySearchRepository.delete(diaryDocument);
    }

    private List<String> uploadFiles(List<MultipartFile> files) {
        List<String> filePaths = new ArrayList<>();

        files.forEach(file -> {
            if (file != null && !file.isEmpty()) {
                String fileName = generateFileName(file);

                try {
                    amazonS3.putObject(bucket, fileName, file.getInputStream(),
                        getObjectMetadata(file));
                } catch (IOException e) {
                    throw new DiaryException(FAIL_UPLOAD_FILE);
                }

                String filePath = amazonS3.getUrl(bucket, fileName).toString();

                filePaths.add(filePath);
            }
        });
        return filePaths;
    }

    private void deleteFiles(Diary diary) {
        for (String filePath : diary.getFilePath()) {
            try {
                filePath = filePath.substring(filePath.lastIndexOf("/") + 1);

                String encoded = filePath.substring(filePath.indexOf("%"));
                String prefix = filePath.substring(0, filePath.indexOf("%"));
                String decode = URLDecoder.decode(encoded, StandardCharsets.UTF_8);
                String key = prefix + decode;

                amazonS3.deleteObject(bucket, key);
            } catch (Exception e) {
                throw new DiaryException(FAIL_DELETE_FILE);
            }
        }
    }
}
