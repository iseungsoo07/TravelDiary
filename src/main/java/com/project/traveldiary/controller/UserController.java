package com.project.traveldiary.controller;

import com.project.traveldiary.dto.SignInRequest;
import com.project.traveldiary.dto.SignUpRequest;
import com.project.traveldiary.dto.SignUpResponse;
import com.project.traveldiary.dto.UpdateNicknameRequest;
import com.project.traveldiary.dto.UpdatePasswordRequest;
import com.project.traveldiary.dto.UpdateUserResponse;
import com.project.traveldiary.entity.User;
import com.project.traveldiary.security.TokenProvider;
import com.project.traveldiary.service.UserService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
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

    @PatchMapping("/{id}/nickname")
    public ResponseEntity<UpdateUserResponse> updateNickname(@PathVariable Long id,
        @RequestBody @Valid UpdateNicknameRequest updateUserRequest,
        @RequestHeader("X-AUTH-TOKEN") String token) {

        String userId = tokenProvider.getUsername(token);

        return ResponseEntity.ok(userService.updateNickname(id, updateUserRequest, userId));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<UpdateUserResponse> updatePassword(@PathVariable Long id,
        @RequestBody @Valid UpdatePasswordRequest updatePasswordRequest,
        @RequestHeader("X-AUTH-TOKEN") String token) {

        String userId = tokenProvider.getUsername(token);

        return ResponseEntity.ok(userService.updatePassword(id, updatePasswordRequest, userId));
    }
}
