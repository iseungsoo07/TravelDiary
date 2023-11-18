package com.project.traveldiary.service;

import com.project.traveldiary.dto.SignUpRequest;
import com.project.traveldiary.dto.SignUpResponse;

public interface UserService {

    SignUpResponse signUp(SignUpRequest signUpRequest);

}
