package com.project.traveldiary.controller;

import com.project.traveldiary.dto.DeleteUserResponse;
import com.project.traveldiary.dto.SignInRequest;
import com.project.traveldiary.dto.SignUpRequest;
import com.project.traveldiary.dto.SignUpResponse;
import com.project.traveldiary.entity.User;
import com.project.traveldiary.security.TokenProvider;
import com.project.traveldiary.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        return ResponseEntity.ok(userService.signUp(signUpRequest));
    }

    @PostMapping("/signin")
    public ResponseEntity<String> signIn(@RequestBody @Valid SignInRequest signInRequest) {

        User user = userService.login(signInRequest);

        String token = tokenProvider.createToken(user.getUserId());

        return ResponseEntity.ok(token);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<DeleteUserResponse> deleteUser(
        @RequestHeader("X-AUTH-TOKEN") String token) {

        String userId = getCurrentUser(token);

        userService.deleteUser(userId);

        return ResponseEntity.ok(DeleteUserResponse.builder()
            .message("회원 탈퇴가 완료되었습니다.")
            .build());
    }

    private String getCurrentUser(String token) {
        return tokenProvider.getUsername(token);
    }

}
