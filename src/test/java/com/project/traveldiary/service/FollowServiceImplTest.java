package com.project.traveldiary.service;

import static com.project.traveldiary.type.ErrorCode.ALREADY_FOLLOWED_USER;
import static com.project.traveldiary.type.ErrorCode.NOT_FOUND_FOLLOW;
import static com.project.traveldiary.type.ErrorCode.NOT_FOUND_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.project.traveldiary.dto.FollowListResponse;
import com.project.traveldiary.dto.FollowResponse;
import com.project.traveldiary.entity.Follow;
import com.project.traveldiary.entity.User;
import com.project.traveldiary.exception.FollowException;
import com.project.traveldiary.exception.UserException;
import com.project.traveldiary.repository.FollowRepository;
import com.project.traveldiary.repository.UserRepository;
import com.project.traveldiary.service.impl.FollowServiceImpl;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@ExtendWith(MockitoExtension.class)
class FollowServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    FollowRepository followRepository;

    @InjectMocks
    FollowServiceImpl followService;

    @Test
    @DisplayName("팔로우 성공")
    void successFollow() {
        // given
        User follower = User.builder()
            .userId("apple")
            .password("apple")
            .nickname("apple123")
            .build();

        User following = User.builder()
            .id(2L)
            .userId("banana")
            .password("banana")
            .nickname("banana456")
            .build();

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.of(follower));

        given(userRepository.findById(anyLong()))
            .willReturn(Optional.of(following));

        given(followRepository.existsByFollowerAndFollowing(any(), any()))
            .willReturn(false);

        // when
        FollowResponse followResponse = followService.follow(follower.getUserId(),
            following.getId());

        // then
        assertEquals("apple123", followResponse.getFollower());
        assertEquals("banana456", followResponse.getFollowing());
    }

    @Test
    @DisplayName("팔로우 실패 - 사용자 정보 없음")
    void failFollow_NotFoundUser() {
        // given
        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.empty());

        // when
        UserException userException = assertThrows(UserException.class,
            () -> followService.follow("abc", 1L));

        // then
        assertEquals(NOT_FOUND_USER, userException.getErrorCode());
    }

    @Test
    @DisplayName("팔로우 실패 - 이미 팔로우 한 사용자")
    void failFollow_AlreadyFollow() {
        // given
        User follower = User.builder()
            .userId("apple")
            .password("apple")
            .nickname("apple123")
            .build();

        User following = User.builder()
            .id(2L)
            .userId("banana")
            .password("banana")
            .nickname("banana456")
            .build();

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.of(follower));

        given(userRepository.findById(anyLong()))
            .willReturn(Optional.of(following));

        given(followRepository.existsByFollowerAndFollowing(any(), any()))
            .willReturn(true);

        // when
        FollowException followException = assertThrows(FollowException.class,
            () -> followService.follow("abc", 1L));

        // then
        assertEquals(ALREADY_FOLLOWED_USER, followException.getErrorCode());
    }

    @Test
    @DisplayName("팔로우 취소 성공")
    void successCancelFollow() {
        // given
        User follower = User.builder()
            .nickname("apple")
            .build();

        User following = User.builder()
            .nickname("banana")
            .build();

        Follow follow = Follow.builder()
            .follower(follower)
            .following(following)
            .build();

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.of(follower));

        given(userRepository.findById(anyLong()))
            .willReturn(Optional.of(following));

        given(followRepository.findByFollowerAndFollowing(any(), any()))
            .willReturn(Optional.of(follow));

        // when
        FollowResponse followResponse = followService.cancelFollow("apple", 1L);

        // then
        verify(followRepository, times(1)).delete(any());
        assertEquals("apple", followResponse.getFollower());
        assertEquals("banana", followResponse.getFollowing());
    }

    @Test
    @DisplayName("팔로우 취소 실패 - 팔로워 정보 없음")
    void failCancelFollow_NotFoundFollower() {
        // given
        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.empty());

        // when
        UserException userException = assertThrows(UserException.class,
            () -> followService.cancelFollow("apple", 1L));

        // then
        assertEquals(NOT_FOUND_USER, userException.getErrorCode());
    }

    @Test
    @DisplayName("팔로우 취소 실패 - 팔로잉 정보 없음")
    void failCancelFollow_NotFoundFollowing() {
        // given

        User follower = User.builder()
            .nickname("apple")
            .build();

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.of(follower));

        given(userRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        // when
        UserException userException = assertThrows(UserException.class,
            () -> followService.cancelFollow("apple", 1L));

        // then
        assertEquals(NOT_FOUND_USER, userException.getErrorCode());
    }

    @Test
    @DisplayName("팔로우 취소 실패 - 팔로우 정보 없음")
    void failCancelFollow_NotFoundFollow() {
        // given
        User follower = User.builder()
            .nickname("apple")
            .build();

        User following = User.builder()
            .nickname("banana")
            .build();

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.of(follower));

        given(userRepository.findById(anyLong()))
            .willReturn(Optional.of(following));

        given(followRepository.findByFollowerAndFollowing(any(), any()))
            .willReturn(Optional.empty());

        // when
        FollowException followException = assertThrows(FollowException.class,
            () -> followService.cancelFollow("apple", 123L));

        // then
        assertEquals(NOT_FOUND_FOLLOW, followException.getErrorCode());
    }

    @Test
    @DisplayName("팔로워 목록 가져오기 성공")
    void successGetFollowerList() {
        // given

        User user = User.builder()
            .userId("apple")
            .password("apple")
            .nickname("apple123")
            .build();

        User follower1 = User.builder()
            .nickname("a")
            .build();

        User follower2 = User.builder()
            .nickname("b")
            .build();

        User follower3 = User.builder()
            .nickname("c")
            .build();

        Follow follow1 = Follow.builder()
            .follower(follower1)
            .following(user)
            .build();

        Follow follow2 = Follow.builder()
            .follower(follower2)
            .following(user)
            .build();
        Follow follow3 = Follow.builder()
            .follower(follower3)
            .following(user)
            .build();

        Page<Follow> followPage = new PageImpl<>(List.of(follow1, follow2, follow3));

        given(userRepository.findById(anyLong()))
            .willReturn(Optional.of(user));

        given(followRepository.findByFollowingOrderByFollowDateDesc(any(), any()))
            .willReturn(followPage);

        // when
        List<FollowListResponse> followerList = followService.getFollowerList(1L, 0, 3);

        // then
        assertEquals("a", followerList.get(0).getNickname());
        assertEquals("b", followerList.get(1).getNickname());
        assertEquals("c", followerList.get(2).getNickname());
    }

    @Test
    @DisplayName("팔로워 목록 가져오기 실패 - 사용자 정보 없음")
    void failGetFollowerList_NotFoundUser() {
        // given
        given(userRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        // when
        UserException userException = assertThrows(UserException.class,
            () -> followService.getFollowerList(1L, 0, 10));

        // then
        assertEquals(NOT_FOUND_USER, userException.getErrorCode());
    }

    @Test
    @DisplayName("팔로잉 목록 가져오기 성공")
    void successGetFollowingList() {
        // given
        User user = User.builder()
            .userId("apple")
            .password("apple")
            .nickname("apple123")
            .build();

        User following1 = User.builder()
            .nickname("a")
            .build();

        User following2 = User.builder()
            .nickname("b")
            .build();

        User following3 = User.builder()
            .nickname("c")
            .build();

        Follow follow1 = Follow.builder()
            .follower(user)
            .following(following1)
            .build();

        Follow follow2 = Follow.builder()
            .follower(user)
            .following(following2)
            .build();
        Follow follow3 = Follow.builder()
            .follower(user)
            .following(following3)
            .build();

        Page<Follow> followPage = new PageImpl<>(List.of(follow1, follow2, follow3));

        given(userRepository.findById(anyLong()))
            .willReturn(Optional.of(user));

        given(followRepository.findByFollowerOrderByFollowDateDesc(any(), any()))
            .willReturn(followPage);

        // when
        List<FollowListResponse> followingList = followService.getFollowingList(1L, 0, 10);

        // then
        assertEquals("a", followingList.get(0).getNickname());
        assertEquals("b", followingList.get(1).getNickname());
        assertEquals("c", followingList.get(2).getNickname());
    }

    @Test
    @DisplayName("팔로잉 목록 가져오기 실패 - 사용자 정보 없음")
    void failGetFollowingList_NotFoundUser() {
        // given
        given(userRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        // when
        UserException userException = assertThrows(UserException.class,
            () -> followService.getFollowingList(1L, 0, 10));

        // then
        assertEquals(NOT_FOUND_USER, userException.getErrorCode());
    }

    @Test
    @DisplayName("팔로워 수 가져오기 성공")
    void successGetFollowerCount() {
        // given
        User user = User.builder()
            .userId("apple")
            .password("apple")
            .nickname("apple123")
            .build();

        User follower1 = User.builder()
            .nickname("a")
            .build();

        User follower2 = User.builder()
            .nickname("b")
            .build();

        User follower3 = User.builder()
            .nickname("c")
            .build();

        Follow follow1 = Follow.builder()
            .follower(follower1)
            .following(user)
            .build();

        Follow follow2 = Follow.builder()
            .follower(follower2)
            .following(user)
            .build();

        Follow follow3 = Follow.builder()
            .follower(follower3)
            .following(user)
            .build();

        List<Follow> followList = List.of(follow1, follow2, follow3);

        given(userRepository.findById(anyLong()))
            .willReturn(Optional.of(user));

        given(followRepository.countByFollowing(any()))
            .willReturn((long) followList.size());

        // when
        long followerCount = followService.getFollowerCount(1L);

        // then
        assertEquals(3, followerCount);
    }

    @Test
    @DisplayName("팔로워 수 가져오기 실패 - 사용자 정보 없음")
    void failGetFollowerCount_NotFoundUser() {
        // given
        given(userRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        // when
        UserException userException = assertThrows(UserException.class,
            () -> followService.getFollowerCount(1L));

        // then
        assertEquals(NOT_FOUND_USER, userException.getErrorCode());
    }

    @Test
    @DisplayName("팔로잉 수 가져오기 성공")
    void successGetFollowingCount() {
        // given
        User user = User.builder()
            .userId("apple")
            .password("apple")
            .nickname("apple123")
            .build();

        User following1 = User.builder()
            .nickname("a")
            .build();

        User following2 = User.builder()
            .nickname("b")
            .build();

        User following3 = User.builder()
            .nickname("c")
            .build();

        Follow follow1 = Follow.builder()
            .follower(user)
            .following(following1)
            .build();

        Follow follow2 = Follow.builder()
            .follower(user)
            .following(following2)
            .build();
        Follow follow3 = Follow.builder()
            .follower(user)
            .following(following3)
            .build();

        List<Follow> followList = List.of(follow1, follow2, follow3);

        given(userRepository.findById(anyLong()))
            .willReturn(Optional.of(user));

        given(followRepository.countByFollower(any()))
            .willReturn((long) followList.size());

        // when
        long followingCount = followService.getFollowingCount(1L);

        // then
        assertEquals(3, followingCount);
    }


}