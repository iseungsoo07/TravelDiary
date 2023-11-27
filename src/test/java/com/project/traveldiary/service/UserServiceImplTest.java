package com.project.traveldiary.service;

import static com.project.traveldiary.type.ErrorCode.ALREADY_USING_ID;
import static com.project.traveldiary.type.ErrorCode.ALREADY_USING_NICKNAME;
import static com.project.traveldiary.type.ErrorCode.MISMATCH_PASSWORD;
import static com.project.traveldiary.type.ErrorCode.NOT_FOUND_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.project.traveldiary.dto.SignInRequest;
import com.project.traveldiary.dto.SignUpRequest;
import com.project.traveldiary.dto.UpdateNicknameRequest;
import com.project.traveldiary.dto.UpdatePasswordRequest;
import com.project.traveldiary.entity.User;
import com.project.traveldiary.exception.UserException;
import com.project.traveldiary.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserServiceImpl userService;

    @Test
    @DisplayName("회원가입 성공")
    void successSignUp() {
        // given
        SignUpRequest signUpRequest = SignUpRequest.builder()
            .userId("apple")
            .password("apple")
            .nickname("apple01")
            .build();

        given(userRepository.existsByUserId(anyString()))
            .willReturn(false);

        given(userRepository.existsByNickname(anyString()))
            .willReturn(false);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        // when
        userService.signUp(signUpRequest);

        // then
        verify(userRepository, times(1)).save(captor.capture());
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 아이디")
    void failSignUp_duplicateId() {
        // given
        SignUpRequest signUpRequest = SignUpRequest.builder()
            .userId("apple")
            .password("apple")
            .nickname("apple01")
            .build();

        given(userRepository.existsByUserId(anyString()))
            .willReturn(true);

        // when
        UserException userException = assertThrows(UserException.class,
            () -> userService.signUp(signUpRequest));

        // then
        assertEquals(ALREADY_USING_ID, userException.getErrorCode());
    }

    @Test
    @DisplayName("회원가입 실패 - 중복된 닉네임")
    void failSignUp_duplicateNickname() {
        // given
        SignUpRequest signUpRequest = SignUpRequest.builder()
            .userId("apple")
            .password("apple")
            .nickname("apple01")
            .build();

        given(userRepository.existsByUserId(anyString()))
            .willReturn(false);

        given(userRepository.existsByNickname(anyString()))
            .willReturn(true);

        // when
        UserException userException = assertThrows(UserException.class,
            () -> userService.signUp(signUpRequest));

        // then
        assertEquals(ALREADY_USING_NICKNAME, userException.getErrorCode());
    }

    @Test
    @DisplayName("로그인 성공")
    void successLogin() {
        // given
        SignInRequest signInRequest = SignInRequest.builder()
            .userId("apple")
            .password("apple")
            .build();

        User user = User.builder()
            .userId("apple")
            .password("asldkfjaasdfasdf")
            .nickname("apple01")
            .build();

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.of(user));

        given(passwordEncoder.matches(any(), anyString()))
            .willReturn(true);

        // when
        String loginUserId = userService.login(signInRequest);

        // then
        assertEquals("apple", loginUserId);
    }

    @Test
    @DisplayName("로그인 실패 - 사용자 정보 없음")
    void failLogin_NotFoundUser() {
        // given
        SignInRequest signInRequest = SignInRequest.builder()
            .userId("apple")
            .password("apple")
            .build();

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.empty());

        // when
        UserException userException = assertThrows(UserException.class,
            () -> userService.login(signInRequest));

        // then
        assertEquals(NOT_FOUND_USER, userException.getErrorCode());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void failLogin_MismatchPassword() {
        // given
        SignInRequest signInRequest = SignInRequest.builder()
            .userId("apple")
            .password("apple")
            .build();

        User user = User.builder()
            .userId("apple")
            .password("asldkfjaasdfasdf")
            .nickname("apple01")
            .build();

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.of(user));

        given(passwordEncoder.matches(any(), anyString()))
            .willReturn(false);

        // when
        UserException userException = assertThrows(UserException.class,
            () -> userService.login(signInRequest));

        // then
        assertEquals(NOT_FOUND_USER, userException.getErrorCode());
    }

    @Test
    @DisplayName("닉네임 수정 성공")
    void successUpdateNickname() {
        // given
        UpdateNicknameRequest updateNicknameRequest = UpdateNicknameRequest.builder()
            .nickname("banana")
            .build();

        User user = User.builder()
            .userId("apple")
            .password("asldkfjaasdfasdf")
            .nickname("apple01")
            .build();

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.of(user));

        // when
        userService.updateNickname(updateNicknameRequest, user.getUserId());

        // then
        assertEquals("banana", user.getNickname());
    }

    @Test
    @DisplayName("닉네임 수정 실패 - 사용자 정보 없음")
    void failUpdateNickname_NotFoundUser() {
        // given
        UpdateNicknameRequest updateNicknameRequest = UpdateNicknameRequest.builder()
            .nickname("banana")
            .build();

        String userId = "apple";

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.empty());

        // when
        UserException userException = assertThrows(UserException.class,
            () -> userService.updateNickname(updateNicknameRequest, userId));

        // then
        assertEquals(NOT_FOUND_USER, userException.getErrorCode());
    }

    @Test
    @DisplayName("비밀번호 수정 성공")
    void successUpdatePassword() {
        // given
        UpdatePasswordRequest updatePasswordRequest = UpdatePasswordRequest.builder()
            .currentPassword("apple")
            .newPassword("apple123")
            .build();

        User user = User.builder()
            .userId("apple")
            .password("asldkfjaasdfasdf")
            .nickname("apple01")
            .build();

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.of(user));

        given(passwordEncoder.matches(any(), anyString()))
            .willReturn(true);

        given(passwordEncoder.encode(anyString()))
            .willReturn("asdfasdfasdf");

        // when
        userService.updatePassword(updatePasswordRequest, user.getUserId());

        // then
        assertEquals("asdfasdfasdf", user.getPassword());
    }

    @Test
    @DisplayName("비밀번호 수정 실패 - 사용자 정보 없음")
    void failUpdatePassword_NotFoundUser() {
        // given
        UpdatePasswordRequest updatePasswordRequest = UpdatePasswordRequest.builder()
            .currentPassword("apple")
            .newPassword("apple123")
            .build();

        String userId = "apple";

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.empty());

        // when
        UserException userException = assertThrows(UserException.class,
            () -> userService.updatePassword(updatePasswordRequest, userId));

        // then
        assertEquals(NOT_FOUND_USER, userException.getErrorCode());
    }

    @Test
    @DisplayName("비밀번호 수정 실패 - 현재 비밀번호 불일치")
    void failUpdatePassword_MismatchPassword() {
        // given
        UpdatePasswordRequest updatePasswordRequest = UpdatePasswordRequest.builder()
            .currentPassword("apple")
            .newPassword("apple123")
            .build();

        User user = User.builder()
            .userId("apple")
            .password("asldkfjaasdfasdf")
            .nickname("apple01")
            .build();

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.of(user));

        given(passwordEncoder.matches(any(), anyString()))
            .willReturn(false);

        // when
        UserException userException = assertThrows(UserException.class,
            () -> userService.updatePassword(updatePasswordRequest, user.getUserId()));

        // then
        assertEquals(MISMATCH_PASSWORD, userException.getErrorCode());
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void successDeleteUser() {
        // given
        User user = User.builder()
            .userId("apple")
            .password("asldkfjaasdfasdf")
            .nickname("apple01")
            .build();

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.of(user));

        // when
        userService.deleteUser(user.getUserId());

        // then
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    @DisplayName("회원 탈퇴 실패")
    void failDeleteUser() {
        // given
        String userId = "apple";

        given(userRepository.findByUserId(anyString()))
            .willReturn(Optional.empty());

        // when
        UserException userException = assertThrows(UserException.class,
            () -> userService.deleteUser(userId));

        // then
        assertEquals(NOT_FOUND_USER, userException.getErrorCode());
    }
}