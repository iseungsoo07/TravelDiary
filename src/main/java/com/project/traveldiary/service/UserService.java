package com.project.traveldiary.service;

import com.project.traveldiary.dto.SignInRequest;
import com.project.traveldiary.dto.SignUpRequest;
import com.project.traveldiary.dto.SignUpResponse;
import com.project.traveldiary.dto.UpdateNicknameRequest;
import com.project.traveldiary.dto.UpdatePasswordRequest;
import com.project.traveldiary.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    SignUpResponse signUp(SignUpRequest signUpRequest);

    User login(SignInRequest signInRequest);

    void updateNickname(UpdateNicknameRequest updateUserRequest, String userId);

    void updatePassword(UpdatePasswordRequest updatePasswordRequest, String userId);
  
    void deleteUser(String userId);
}
