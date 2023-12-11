package com.project.traveldiary.service;

import static com.project.traveldiary.type.ErrorCode.ALREADY_LIKE_DIARY;
import static com.project.traveldiary.type.ErrorCode.CAN_DELETE_OWN_DIARY;
import static com.project.traveldiary.type.ErrorCode.CAN_REPLY_ON_COMMENT;
import static com.project.traveldiary.type.ErrorCode.CAN_UPDATE_OWN_DIARY;
import static com.project.traveldiary.type.ErrorCode.NOT_FOUND_COMMENT;
import static com.project.traveldiary.type.ErrorCode.NOT_FOUND_DIARY;
import static com.project.traveldiary.type.ErrorCode.NOT_FOUND_LIKE;
import static com.project.traveldiary.type.ErrorCode.NOT_FOUND_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
import com.project.traveldiary.entity.Comment;
import com.project.traveldiary.entity.Diary;
import com.project.traveldiary.entity.Likes;
import com.project.traveldiary.entity.User;
import com.project.traveldiary.es.DiaryDocument;
import com.project.traveldiary.exception.CommentException;
import com.project.traveldiary.exception.DiaryException;
import com.project.traveldiary.exception.LikeException;
import com.project.traveldiary.exception.UserException;
import com.project.traveldiary.repository.CommentRepository;
import com.project.traveldiary.repository.DiaryRepository;
import com.project.traveldiary.repository.DiarySearchRepository;
import com.project.traveldiary.repository.LikesRepository;
import com.project.traveldiary.repository.UserRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class DiaryServiceImplTest {

    @Mock
    DiaryRepository diaryRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    LikesRepository likesRepository;

    @Mock
    DiarySearchRepository diarySearchRepository;

    @Mock
    CommentRepository commentRepository;

    @InjectMocks
    DiaryServiceImpl diaryService;

    @Test
    @DisplayName("일기 작성 성공")
    void successUploadDiary() {
        // given
        User user = User.builder()
            .userId("apple")
            .nickname("apple")
            .password("123")
            .build();

        List<String> filePaths = new ArrayList<>(Arrays.asList("abc", "def"));
        List<String> hashtags = new ArrayList<>(Arrays.asList("해시, 태그"));

        DiaryUploadRequest diaryUploadRequest = DiaryUploadRequest.builder()
            .title("제목1")
            .content("내용1")
            .hashtags(Collections.singletonList("[해시, 태그]"))
            .build();

        Diary diary = Diary.builder()
            .user(user)
            .title("제목1")
            .content("내용1")
            .filePath(filePaths)
            .hashtags(hashtags)
            .build();

        List<MultipartFile> files = new ArrayList<>();

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.of(user));

        given(diaryRepository.save(any()))
            .willReturn(diary);

        given(diarySearchRepository.saveAll(any()))
            .willReturn(List.of(DiaryDocument.builder()
                .build()));

        // when
        DiaryUploadResponse diaryUploadResponse = diaryService.uploadDiary(files,
            diaryUploadRequest, "apple");

        // then
        assertEquals("제목1", diaryUploadResponse.getTitle());
        assertEquals("내용1", diaryUploadResponse.getContent());
        assertEquals("일기 등록이 완료되었습니다.", diaryUploadResponse.getMessage());
    }

    @Test
    @DisplayName("일기 작성 실패 - 사용자 정보 없음")
    void failUploadDiary_NotFoundUser() {
        // given

        DiaryUploadRequest diaryUploadRequest = DiaryUploadRequest.builder()
            .title("제목1")
            .content("내용1")
            .hashtags(Collections.singletonList("[해시, 태그]"))
            .build();

        List<MultipartFile> files = new ArrayList<>();

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.empty());

        // when
        UserException userException = assertThrows(UserException.class,
            () -> diaryService.uploadDiary(files, diaryUploadRequest, "apple"));

        // then
        assertEquals(NOT_FOUND_USER, userException.getErrorCode());
    }

    @Test
    @DisplayName("일기 정보 가져오기 성공")
    void successGetDiary() {
        // given
        User user = User.builder()
            .userId("apple")
            .nickname("apple")
            .password("123")
            .build();

        List<String> filePaths = new ArrayList<>(Arrays.asList("abc", "def"));
        List<String> hashtags = new ArrayList<>(Arrays.asList("해시, 태그"));

        Diary diary = Diary.builder()
            .user(user)
            .title("제목1")
            .content("내용1")
            .filePath(filePaths)
            .hashtags(hashtags)
            .build();

        given(diaryRepository.findById(anyLong()))
            .willReturn(Optional.of(diary));

        // when
        DiaryDetailResponse diaryDetailResponse = diaryService.getDiary(1L);

        // then
        assertEquals("제목1", diaryDetailResponse.getTitle());
        assertEquals("내용1", diaryDetailResponse.getContent());
        assertEquals("apple", diaryDetailResponse.getWriter());
    }

    @Test
    @DisplayName("일기 정보 가져오기 실패 - 일기 정보 없음")
    void failGetDiary_NotFoundDiary() {
        // given
        given(diaryRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        // when
        DiaryException diaryException = assertThrows(DiaryException.class,
            () -> diaryService.getDiary(1L));

        // then
        assertEquals(NOT_FOUND_DIARY, diaryException.getErrorCode());
    }

    @Test
    @DisplayName("일기 목록 가져오기 성공")
    void successGetDiaries() {
        // given
        User user = User.builder()
            .userId("apple")
            .nickname("apple")
            .password("123")
            .build();

        Diary diary1 = Diary.builder()
            .title("제목1")
            .content("내용1")
            .user(user)
            .build();
        Diary diary2 = Diary.builder()
            .title("제목2")
            .content("내용2")
            .user(user)
            .build();
        Diary diary3 = Diary.builder()
            .title("제목3")
            .content("내용3")
            .user(user)
            .build();

        List<Diary> diaryList = new ArrayList<>(Arrays.asList(diary1, diary2, diary3));

        given(userRepository.findById(anyLong()))
            .willReturn(Optional.of(user));

        given(diaryRepository.findByUser(any(), any()))
            .willReturn(new PageImpl<>(diaryList));

        // when
        Page<DiaryResponse> page = diaryService.getDiaries(1L, 0, 4, "createdAt");

        List<DiaryResponse> diaryResponseList = page.stream().collect(Collectors.toList());

        // then
        assertEquals("제목1", diaryResponseList.get(0).getTitle());
        assertEquals("제목2", diaryResponseList.get(1).getTitle());
        assertEquals("제목3", diaryResponseList.get(2).getTitle());
        assertEquals("apple", diaryResponseList.get(0).getWriter());
        assertEquals("apple", diaryResponseList.get(1).getWriter());
        assertEquals("apple", diaryResponseList.get(2).getWriter());
    }

    @Test
    @DisplayName("일기 목록 가져오기 실패 - 사용자 정보 없음")
    void failGetDiaries_NotFoundUser() {
        // given
        given(userRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        // when
        UserException userException = assertThrows(UserException.class,
            () -> diaryService.getDiaries(1L, 0, 5, "createdAt"));

        // then
        assertEquals(NOT_FOUND_USER, userException.getErrorCode());
    }

    @Test
    @DisplayName("일기 수정 성공")
    void successUpdateDiary() {
        // given
        User user = User.builder()
            .userId("apple")
            .nickname("apple")
            .password("123")
            .build();

        List<String> filePaths = new ArrayList<>();

        Diary diary = Diary.builder()
            .title("제목1")
            .content("내용1")
            .user(user)
            .filePath(filePaths)
            .build();

        Diary updatedDiary = Diary.builder()
            .title("수정된 제목")
            .content("수정된 내용")
            .user(user)
            .filePath(filePaths)
            .build();

        DiaryUpdateRequest diaryUpdateRequest = DiaryUpdateRequest.builder()
            .title("수정된 제목")
            .content("수정된 내용")
            .build();

        List<MultipartFile> files = new ArrayList<>();

        given(diaryRepository.findById(anyLong()))
            .willReturn(Optional.of(diary));

        given(diaryRepository.save(any()))
            .willReturn(updatedDiary);

        // when
        DiaryUpdateResponse diaryUpdateResponse = diaryService.updateDiary(1L, files,
            diaryUpdateRequest, "apple");

        // then
        assertEquals("수정된 제목", diaryUpdateResponse.getTitle());
        assertEquals("수정된 내용", diaryUpdateResponse.getContent());
    }

    @Test
    @DisplayName("일기 수정 실패 - 일기 정보 없음")
    void failUpdateDiary_NotFoundDiary() {
        // given
        given(diaryRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        // when
        DiaryException diaryException = assertThrows(DiaryException.class,
            () -> diaryService.updateDiary(1L, null, null, "apple"));

        // then
        assertEquals(NOT_FOUND_DIARY, diaryException.getErrorCode());
    }

    @Test
    @DisplayName("일기 수정 실패 - 자신의 일기만 수정 가능")
    void failUpdateDiary_CanUpdateOwnDiary() {
        // given
        User user = User.builder()
            .userId("apple")
            .nickname("apple")
            .password("123")
            .build();

        List<String> filePaths = new ArrayList<>();

        Diary diary = Diary.builder()
            .title("제목1")
            .content("내용1")
            .user(user)
            .filePath(filePaths)
            .build();

        String userId = "banana";

        given(diaryRepository.findById(anyLong()))
            .willReturn(Optional.of(diary));

        // when
        DiaryException diaryException = assertThrows(DiaryException.class,
            () -> diaryService.updateDiary(1L, null, null, userId));

        // then
        assertEquals(CAN_UPDATE_OWN_DIARY, diaryException.getErrorCode());
    }

    @Test
    @DisplayName("일기 삭제 성공")
    void successDeleteDiary() {
        // given
        User user = User.builder()
            .userId("apple")
            .nickname("apple")
            .password("123")
            .build();

        List<String> filePaths = new ArrayList<>();

        Diary diary = Diary.builder()
            .title("제목1")
            .content("내용1")
            .user(user)
            .filePath(filePaths)
            .build();

        given(diaryRepository.findById(anyLong()))
            .willReturn(Optional.of(diary));

        // when
        diaryService.deleteDiary(1L, "apple");

        // then
        verify(diaryRepository, times(1))
            .delete(diary);
    }

    @Test
    @DisplayName("일기 삭제 실패 - 사용자 정보 없음")
    void failDeleteDiary_NotFoundDiary() {
        // given
        given(diaryRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        // when
        DiaryException diaryException = assertThrows(DiaryException.class,
            () -> diaryService.deleteDiary(1L, "apple"));

        // then
        assertEquals(NOT_FOUND_DIARY, diaryException.getErrorCode());
    }

    @Test
    void failDeleteDiary_CanDeleteOwnDiary() {
        // given
        User user = User.builder()
            .userId("apple")
            .nickname("apple")
            .password("123")
            .build();

        List<String> filePaths = new ArrayList<>();

        Diary diary = Diary.builder()
            .title("제목1")
            .content("내용1")
            .user(user)
            .filePath(filePaths)
            .build();

        given(diaryRepository.findById(anyLong()))
            .willReturn(Optional.of(diary));

        // when
        DiaryException diaryException = assertThrows(DiaryException.class,
            () -> diaryService.deleteDiary(1L, "banana"));

        // then
        assertEquals(CAN_DELETE_OWN_DIARY, diaryException.getErrorCode());
    }

    @Test
    @DisplayName("일기 좋아요 성공")
    void sucessLikeDiary() {
        // given
        User user = User.builder()
            .userId("apple")
            .nickname("apple")
            .password("123")
            .build();

        List<String> filePaths = new ArrayList<>();

        Diary diary = Diary.builder()
            .title("제목1")
            .content("내용1")
            .user(user)
            .filePath(filePaths)
            .build();

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.of(user));

        given(diaryRepository.findById(anyLong()))
            .willReturn(Optional.of(diary));

        given(likesRepository.existsByUserAndDiary(any(), any()))
            .willReturn(false);

        Likes likes = Likes.builder()
            .user(user)
            .diary(diary)
            .build();

        given(likesRepository.save(any()))
            .willReturn(likes);

        // when
        DiaryLikeResponse diaryLikeResponse = diaryService.likeDiary(1L, "apple");

        // then
        assertEquals(likes.getUser().getNickname(), diaryLikeResponse.getUserId());
        assertEquals(likes.getDiary().getUser().getNickname(), diaryLikeResponse.getWriter());

    }

    @Test
    @DisplayName("일기 좋아요 실패 - 사용자 정보 없음")
    void failLikeDiary_NotFoundUser() {
        // given
        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.empty());

        // when
        UserException userException = assertThrows(UserException.class,
            () -> diaryService.likeDiary(1L, "apple"));

        // then
        assertEquals(NOT_FOUND_USER, userException.getErrorCode());
    }

    @Test
    @DisplayName("일기 좋아요 실패 - 일기 정보 없음")
    void failLikeDiary_NotFoundDiary() {
        // given
        User user = User.builder()
            .userId("apple")
            .nickname("apple")
            .password("123")
            .build();

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.of(user));

        given(diaryRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        // when
        DiaryException diaryException = assertThrows(DiaryException.class,
            () -> diaryService.likeDiary(1L, "apple"));

        // then
        assertEquals(NOT_FOUND_DIARY, diaryException.getErrorCode());
    }

    @Test
    @DisplayName("일기 좋아요 실패 - 이미 좋아요한 일기")
    void failLikeDiary_AlreadyLikeDiary() {
        // given
        User user = User.builder()
            .userId("apple")
            .nickname("apple")
            .password("123")
            .build();

        List<String> filePaths = new ArrayList<>();

        Diary diary = Diary.builder()
            .title("제목1")
            .content("내용1")
            .user(user)
            .filePath(filePaths)
            .build();

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.of(user));

        given(diaryRepository.findById(anyLong()))
            .willReturn(Optional.of(diary));

        given(likesRepository.existsByUserAndDiary(any(), any()))
            .willReturn(true);

        // when
        LikeException likeException = assertThrows(LikeException.class,
            () -> diaryService.likeDiary(1L, "apple"));

        // then
        assertEquals(ALREADY_LIKE_DIARY, likeException.getErrorCode());
    }

    @Test
    @DisplayName("일기 좋아요 취소 성공")
    void successCancelLikeDiary() {
        // given
        User user = User.builder()
            .userId("apple")
            .nickname("apple")
            .password("123")
            .build();

        List<String> filePaths = new ArrayList<>();

        Diary diary = Diary.builder()
            .title("제목1")
            .content("내용1")
            .user(user)
            .filePath(filePaths)
            .build();

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.of(user));

        given(diaryRepository.findById(anyLong()))
            .willReturn(Optional.of(diary));

        Likes likes = Likes.builder()
            .user(user)
            .diary(diary)
            .build();

        given(likesRepository.findByUserAndDiary(any(), any()))
            .willReturn(Optional.of(likes));

        // when
        diaryService.cancelLikeDiary(1L, "apple");

        // then
        verify(likesRepository, times(1)).delete(likes);
    }

    @Test
    @DisplayName("일기 좋아요 취소 실패 - 사용자 정보 없음")
    void failCancelLikeDiary_NotFoundUser() {
        // given
        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.empty());

        // when
        UserException userException = assertThrows(UserException.class,
            () -> diaryService.cancelLikeDiary(1L, "apple"));

        // then
        assertEquals(NOT_FOUND_USER, userException.getErrorCode());
    }

    @Test
    @DisplayName("일기 좋아요 취소 실패 - 일기 정보 없음")
    void failCancelLikeDiary_NotFoundDiary() {
        // given
        User user = User.builder()
            .userId("apple")
            .nickname("apple")
            .password("123")
            .build();

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.of(user));

        given(diaryRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        // when
        DiaryException diaryException = assertThrows(DiaryException.class,
            () -> diaryService.cancelLikeDiary(1L, "apple"));

        // then
        assertEquals(NOT_FOUND_DIARY, diaryException.getErrorCode());
    }

    @Test
    @DisplayName("일기 좋아요 취소 실패 - 좋아요 정보 없음")
    void failCancelLikeDiary_NotFoundLike() {
        // given
        User user = User.builder()
            .userId("apple")
            .nickname("apple")
            .password("123")
            .build();

        List<String> filePaths = new ArrayList<>();

        Diary diary = Diary.builder()
            .title("제목1")
            .content("내용1")
            .user(user)
            .filePath(filePaths)
            .build();

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.of(user));

        given(diaryRepository.findById(anyLong()))
            .willReturn(Optional.of(diary));

        given(likesRepository.findByUserAndDiary(any(), any()))
            .willReturn(Optional.empty());

        // when
        LikeException likeException = assertThrows(LikeException.class,
            () -> diaryService.cancelLikeDiary(1L, "apple"));

        // then
        assertEquals(NOT_FOUND_LIKE, likeException.getErrorCode());
    }

    @Test
    @DisplayName("댓글 작성 성공")
    void successCreateComment() {
        // given
        User user = User.builder()
            .userId("apple")
            .nickname("apple")
            .password("123")
            .build();

        List<String> filePaths = new ArrayList<>();

        Diary diary = Diary.builder()
            .title("제목1")
            .content("내용1")
            .user(user)
            .filePath(filePaths)
            .build();

        Comment comment = Comment.builder()
            .diary(diary)
            .user(user)
            .parentCommentId(null)
            .content("댓글")
            .build();

        CommentRequest commentRequest = CommentRequest.builder()
            .content("댓글")
            .build();

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.of(user));

        given(diaryRepository.findById(anyLong()))
            .willReturn(Optional.of(diary));

        // when
        CreateCommentResponse commentResponse = diaryService.createComment(1L, commentRequest,
            "apple");

        // then
        assertEquals("댓글", commentResponse.getContent());
        assertNull(commentResponse.getParentCommentId());
        assertEquals("apple", commentResponse.getWriter());
    }

    @Test
    @DisplayName("댓글 작성 실패 - 사용자 정보 없음")
    void failCreateComment_NotFoundUser() {
        // given
        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.empty());

        // when
        UserException userException = assertThrows(UserException.class,
            () -> diaryService.createComment(1L, null, "apple"));

        // then
        assertEquals(NOT_FOUND_USER, userException.getErrorCode());
    }

    @Test
    @DisplayName("댓글 작성 실패 - 일기 정보 없음")
    void failCreateComment_NotFoundDiary() {
        // given
        User user = User.builder()
            .userId("apple")
            .nickname("apple")
            .password("123")
            .build();

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.of(user));

        given(diaryRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        // when
        DiaryException diaryException = assertThrows(DiaryException.class,
            () -> diaryService.createComment(1L, null, "apple"));

        // then
        assertEquals(NOT_FOUND_DIARY, diaryException.getErrorCode());
    }

    @Test
    @DisplayName("답글 작성 성공")
    void successReplyComment() {
        // given
        User user = User.builder()
            .userId("apple")
            .nickname("apple")
            .password("123")
            .build();

        List<String> filePaths = new ArrayList<>();

        Diary diary = Diary.builder()
            .title("제목1")
            .content("내용1")
            .user(user)
            .filePath(filePaths)
            .build();

        Comment comment = Comment.builder()
            .id(1L)
            .diary(diary)
            .user(user)
            .parentCommentId(null)
            .content("댓글")
            .build();

        CommentRequest commentRequest = CommentRequest.builder()
            .content("댓글")
            .build();

        Comment reply = Comment.builder()
            .diary(diary)
            .user(user)
            .parentCommentId(comment.getId())
            .content(commentRequest.getContent())
            .build();

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.of(user));

        given(diaryRepository.findById(anyLong()))
            .willReturn(Optional.of(diary));

        given(commentRepository.findById(anyLong()))
            .willReturn(Optional.of(comment));

        // when
        ReplyCommentResponse replyCommentResponse = diaryService.replyComment(1L, 1L,
            commentRequest, "apple");

        // then
        assertEquals(1L, replyCommentResponse.getParentCommentId());
        assertEquals("apple", replyCommentResponse.getWriter());
        assertEquals("댓글", replyCommentResponse.getContent());
    }

    @Test
    @DisplayName("답글 작성 실패 - 사용자 정보 없음")
    void failReplyComment_NotFoundUser() {
        // given
        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.empty());

        // when
        UserException userException = assertThrows(UserException.class,
            () -> diaryService.replyComment(1L, 1L, null, "apple"));

        // then
        assertEquals(NOT_FOUND_USER, userException.getErrorCode());
    }

    @Test
    @DisplayName("답글 작성 실패 - 일기 정보 없음")
    void failReplyComment_NotFoundDiary() {
        // given
        User user = User.builder()
            .userId("apple")
            .nickname("apple")
            .password("123")
            .build();

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.of(user));

        given(diaryRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        // when
        DiaryException diaryException = assertThrows(DiaryException.class,
            () -> diaryService.replyComment(1L, 1L, null, "apple"));

        // then
        assertEquals(NOT_FOUND_DIARY, diaryException.getErrorCode());
    }

    @Test
    @DisplayName("답글 작성 실패 - 답글을 작성할 댓글 정보 없음")
    void failReplyComment_NotFoundComment() {
        // given
        User user = User.builder()
            .userId("apple")
            .nickname("apple")
            .password("123")
            .build();

        List<String> filePaths = new ArrayList<>();

        Diary diary = Diary.builder()
            .title("제목1")
            .content("내용1")
            .user(user)
            .filePath(filePaths)
            .build();

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.of(user));

        given(diaryRepository.findById(anyLong()))
            .willReturn(Optional.of(diary));

        given(commentRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        // when
        CommentException commentException = assertThrows(CommentException.class,
            () -> diaryService.replyComment(1L, 1L, null, "apple"));

        // then
        assertEquals(NOT_FOUND_COMMENT, commentException.getErrorCode());
    }

    @Test
    @DisplayName("답글 작성 실패 - 답글은 댓글에만 달 수 있음")
    void failReplyComment_CanReplyOnComment() {
        // given
        User user = User.builder()
            .userId("apple")
            .nickname("apple")
            .password("123")
            .build();

        List<String> filePaths = new ArrayList<>();

        Diary diary = Diary.builder()
            .title("제목1")
            .content("내용1")
            .user(user)
            .filePath(filePaths)
            .build();

        Comment comment = Comment.builder()
            .id(1L)
            .diary(diary)
            .user(user)
            .parentCommentId(1L)
            .content("댓글")
            .build();

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.of(user));

        given(diaryRepository.findById(anyLong()))
            .willReturn(Optional.of(diary));

        given(commentRepository.findById(anyLong()))
            .willReturn(Optional.of(comment));

        // when

        CommentException commentException = assertThrows(CommentException.class,
            () -> diaryService.replyComment(1L, 1L, null, "apple"));

        // then
        assertEquals(CAN_REPLY_ON_COMMENT, commentException.getErrorCode());
    }

    @Test
    @DisplayName("댓글 목록 가져오기 성공")
    void successGetComments() {
        // given
        User user = User.builder()
            .userId("apple")
            .nickname("apple")
            .password("123")
            .build();

        List<String> filePaths = new ArrayList<>();

        Diary diary = Diary.builder()
            .title("제목1")
            .content("내용1")
            .user(user)
            .filePath(filePaths)
            .build();

        Comment comment1 = Comment.builder()
            .parentCommentId(null)
            .user(user)
            .diary(diary)
            .content("댓글1")
            .build();
        Comment comment2 = Comment.builder()
            .parentCommentId(null)
            .user(user)
            .diary(diary)
            .content("댓글2")
            .build();
        Comment comment3 = Comment.builder()
            .parentCommentId(null)
            .user(user)
            .diary(diary)
            .content("댓글3")
            .build();

        List<Comment> commentList = new ArrayList<>(Arrays.asList(comment1, comment2, comment3));

        given(diaryRepository.findById(anyLong()))
            .willReturn(Optional.of(diary));

        given(
            commentRepository.findByDiaryAndParentCommentIdIsNullOrderByCreatedAtAsc(any(), any()))
            .willReturn(new PageImpl<>(commentList));

        // when
        Page<CommentResponse> commentResponsePage = diaryService.getComments(1L,
            PageRequest.of(0, 5));

        List<CommentResponse> commentResponseList = commentResponsePage.stream()
            .collect(Collectors.toList());

        // then
        assertNull(commentResponseList.get(0).getParentCommentId());
        assertNull(commentResponseList.get(1).getParentCommentId());
        assertNull(commentResponseList.get(2).getParentCommentId());

        assertEquals("댓글1", commentResponseList.get(0).getContent());
        assertEquals("댓글2", commentResponseList.get(1).getContent());
        assertEquals("댓글3", commentResponseList.get(2).getContent());
    }

    @Test
    @DisplayName("댓글 목록 가져오기 실패 - 일기 정보 없음")
    void failGetComments_NotFoundDiary() {
        // given
        given(diaryRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        // when
        DiaryException diaryException = assertThrows(DiaryException.class,
            () -> diaryService.getComments(1L, null));

        // then
        assertEquals(NOT_FOUND_DIARY, diaryException.getErrorCode());
    }

    @Test
    @DisplayName("답글 목록 가져오기 성공")
    void successGetReplies() {
        // given
        User user = User.builder()
            .userId("apple")
            .nickname("apple")
            .password("123")
            .build();

        List<String> filePaths = new ArrayList<>();

        Diary diary = Diary.builder()
            .title("제목1")
            .content("내용1")
            .user(user)
            .filePath(filePaths)
            .build();

        Comment comment = Comment.builder()
            .id(1L)
            .diary(diary)
            .user(user)
            .parentCommentId(null)
            .content("댓글")
            .build();

        Comment comment1 = Comment.builder()
            .parentCommentId(1L)
            .user(user)
            .diary(diary)
            .content("답글1")
            .build();
        Comment comment2 = Comment.builder()
            .parentCommentId(1L)
            .user(user)
            .diary(diary)
            .content("답글2")
            .build();
        Comment comment3 = Comment.builder()
            .parentCommentId(1L)
            .user(user)
            .diary(diary)
            .content("답글3")
            .build();

        List<Comment> commentList = new ArrayList<>(Arrays.asList(comment1, comment2, comment3));

        given(commentRepository.findById(anyLong()))
            .willReturn(Optional.of(comment));

        given(commentRepository.findByParentCommentIdOrderByCreatedAtAsc(anyLong(), any()))
            .willReturn(new PageImpl<>(commentList));

        // when
        Page<ReplyResponse> replyResponsePage = diaryService.getReplies(1L, PageRequest.of(0, 3));

        List<ReplyResponse> replyResponseList = replyResponsePage.stream()
            .collect(Collectors.toList());

        // then
        assertEquals("답글1", replyResponseList.get(0).getContent());
        assertEquals("답글2", replyResponseList.get(1).getContent());
        assertEquals("답글3", replyResponseList.get(2).getContent());

        assertEquals(1L, replyResponseList.get(0).getParentCommentId());
        assertEquals(1L, replyResponseList.get(1).getParentCommentId());
        assertEquals(1L, replyResponseList.get(2).getParentCommentId());
    }

    @Test
    @DisplayName("답글 목록 가져오기 실패 - 댓글 정보 없음")
    void failGetReplies_NotFoundComment() {
        // given
        given(commentRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        // when
        CommentException commentException = assertThrows(CommentException.class,
            () -> diaryService.getReplies(1L, null));

        // then
        assertEquals(NOT_FOUND_COMMENT, commentException.getErrorCode());
    }
}