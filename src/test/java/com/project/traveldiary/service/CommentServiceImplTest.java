package com.project.traveldiary.service;

import static com.project.traveldiary.type.ErrorCode.CAN_DELETE_OWN_COMMENT;
import static com.project.traveldiary.type.ErrorCode.CAN_UPDATE_OWN_COMMENT;
import static com.project.traveldiary.type.ErrorCode.NOT_FOUND_COMMENT;
import static com.project.traveldiary.type.ErrorCode.NOT_FOUND_DIARY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.project.traveldiary.dto.CommentRequest;
import com.project.traveldiary.dto.CommentResponse;
import com.project.traveldiary.entity.Comment;
import com.project.traveldiary.entity.Diary;
import com.project.traveldiary.entity.User;
import com.project.traveldiary.exception.CommentException;
import com.project.traveldiary.exception.DiaryException;
import com.project.traveldiary.repository.CommentRepository;
import com.project.traveldiary.repository.DiaryRepository;
import com.project.traveldiary.service.impl.CommentServiceImpl;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    CommentRepository commentRepository;

    @Mock
    DiaryRepository diaryRepository;

    @InjectMocks
    CommentServiceImpl commentService;

    @Test
    @DisplayName("댓글 수정 성공")
    void successUpdateComment() {
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
            .content("수정된 댓글")
            .build();

        given(commentRepository.findById(anyLong()))
            .willReturn(Optional.of(comment));

        Comment updatedComment = Comment.builder()
            .id(1L)
            .diary(diary)
            .user(user)
            .parentCommentId(null)
            .content(commentRequest.getContent())
            .build();

        given(commentRepository.save(any()))
            .willReturn(updatedComment);

        // when
        CommentResponse commentResponse = commentService.updateComment(1L, commentRequest, "apple");

        // then
        assertEquals("수정된 댓글", commentResponse.getContent());
    }

    @Test
    @DisplayName("댓글 수정 실패 - 댓글 정보 없음")
    void failUpdateComment_NotFoundComment() {
        // given
        given(commentRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        // when
        CommentException commentException = assertThrows(CommentException.class,
            () -> commentService.updateComment(1L, null, "apple"));

        // then
        assertEquals(NOT_FOUND_COMMENT, commentException.getErrorCode());
    }

    @Test
    @DisplayName("댓글 수정 실패 - 자신의 댓글만 수정할 수 있음")
    void failUpdateComment_CanUpdateOwnComment() {
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

        given(commentRepository.findById(anyLong()))
            .willReturn(Optional.of(comment));

        // when
        CommentException commentException = assertThrows(CommentException.class,
            () -> commentService.updateComment(1L, null, "banana"));

        // then
        assertEquals(CAN_UPDATE_OWN_COMMENT, commentException.getErrorCode());
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void successDeleteComment() {
        // given
        User user = User.builder()
            .userId("apple")
            .nickname("apple")
            .password("123")
            .build();

        List<String> filePaths = new ArrayList<>();

        Diary diary = Diary.builder()
            .id(1L)
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

        given(commentRepository.findById(anyLong()))
            .willReturn(Optional.of(comment));

        given(diaryRepository.findById(anyLong()))
            .willReturn(Optional.of(diary));

        // when
        commentService.deleteComment(1L, "apple");

        // then
        verify(commentRepository, times(1)).deleteByParentCommentId(comment.getId());
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 댓글 정보 없음")
    void failDeleteComment_NotFoundComment() {
        // given
        given(commentRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        // when
        CommentException commentException = assertThrows(CommentException.class,
            () -> commentService.deleteComment(1L, "apple"));

        // then
        assertEquals(NOT_FOUND_COMMENT, commentException.getErrorCode());
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 본인 댓글만 삭제 가능")
    void failDeleteComment_CanDeleteOwnComment() {
        // given
        User user = User.builder()
            .userId("apple")
            .nickname("apple")
            .password("123")
            .build();

        List<String> filePaths = new ArrayList<>();

        Diary diary = Diary.builder()
            .id(1L)
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

        given(commentRepository.findById(anyLong()))
            .willReturn(Optional.of(comment));

        // when
        CommentException commentException = assertThrows(CommentException.class,
            () -> commentService.deleteComment(1L, "banana"));

        // then
        assertEquals(CAN_DELETE_OWN_COMMENT, commentException.getErrorCode());
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 일기 정보 없음")
    void failDeleteComment_NotFoundDiary() {
        // given
        User user = User.builder()
            .userId("apple")
            .nickname("apple")
            .password("123")
            .build();

        List<String> filePaths = new ArrayList<>();

        Diary diary = Diary.builder()
            .id(1L)
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

        given(commentRepository.findById(anyLong()))
            .willReturn(Optional.of(comment));

        given(diaryRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        // when
        DiaryException diaryException = assertThrows(DiaryException.class,
            () -> commentService.deleteComment(1L, "apple"));

        // then
        assertEquals(NOT_FOUND_DIARY, diaryException.getErrorCode());
    }
}