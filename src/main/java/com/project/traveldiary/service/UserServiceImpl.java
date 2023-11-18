package com.project.traveldiary.service;

import static com.project.traveldiary.type.ErrorCode.ALREADY_USING_ID;
import static com.project.traveldiary.type.ErrorCode.ALREADY_USING_NICKNAME;

import com.project.traveldiary.dto.SignUpRequest;
import com.project.traveldiary.dto.SignUpResponse;
import com.project.traveldiary.entity.User;
import com.project.traveldiary.exception.UserException;
import com.project.traveldiary.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public SignUpResponse signUp(SignUpRequest signUpRequest) {
        log.info("signUp 수행 시작");
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
}
