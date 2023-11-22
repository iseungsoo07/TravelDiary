package com.project.traveldiary.service;

import static com.project.traveldiary.type.ErrorCode.ALREADY_USING_ID;
import static com.project.traveldiary.type.ErrorCode.ALREADY_USING_NICKNAME;
import static com.project.traveldiary.type.ErrorCode.MISMATCH_PASSWORD;
import static com.project.traveldiary.type.ErrorCode.NOT_FOUND_USER;

import com.project.traveldiary.dto.SignInRequest;
import com.project.traveldiary.dto.SignUpRequest;
import com.project.traveldiary.dto.SignUpResponse;
import com.project.traveldiary.dto.UpdateNicknameRequest;
import com.project.traveldiary.dto.UpdatePasswordRequest;
import com.project.traveldiary.entity.User;
import com.project.traveldiary.exception.UserException;
import com.project.traveldiary.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUserId(username)
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));
    }

    @Override
    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        if (userRepository.existsByUserId(signUpRequest.getUserId())) {
            throw new UserException(ALREADY_USING_ID);
        }

        if (userRepository.existsByNickname(signUpRequest.getNickname())) {
            throw new UserException(ALREADY_USING_NICKNAME);
        }

        User user = User.builder()
            .userId(signUpRequest.getUserId())
            .password(passwordEncoder.encode(signUpRequest.getPassword()))
            .nickname(signUpRequest.getNickname())
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .build();

        userRepository.save(user);

        return SignUpResponse.builder()
            .message("회원 가입에 성공하셨습니다.")
            .build();
    }

    @Override
    public User login(SignInRequest signInRequest) {
        User user = userRepository.findByUserId(signInRequest.getUserId())
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        if (!passwordEncoder.matches(signInRequest.getPassword(), user.getPassword())) {
            throw new UserException(NOT_FOUND_USER);
        }

        return user;
    }

    @Override
    public void updateNickname(UpdateNicknameRequest updateUserRequest, String userId) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        user.updateNickname(updateUserRequest.getNickname());
        userRepository.save(user);
    }

    @Override
    public void updatePassword(UpdatePasswordRequest updatePasswordRequest, String userId) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        if (!passwordEncoder.matches(updatePasswordRequest.getCurrentPassword(),
            user.getPassword())) {
            throw new UserException(MISMATCH_PASSWORD);
        }

        String newPassword = passwordEncoder.encode(updatePasswordRequest.getNewPassword());
        user.updatePassword(newPassword);

        userRepository.save(user);
    }
  
    @Override
    public void deleteUser(String userId) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        userRepository.delete(user);
    }

}
