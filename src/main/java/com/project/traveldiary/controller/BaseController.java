package com.project.traveldiary.controller;

import com.project.traveldiary.security.TokenProvider;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseController {

    private TokenProvider tokenProvider;

    String getCurrentUserId(String token) {
        return tokenProvider.getUsername(token);
    }

    String createToken(String userId) {
        return tokenProvider.createToken(userId);
    }
}
