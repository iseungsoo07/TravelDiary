package com.project.traveldiary.controller;

import com.project.traveldiary.dto.FollowCountResponse;
import com.project.traveldiary.dto.FollowListResponse;
import com.project.traveldiary.dto.FollowResponse;
import com.project.traveldiary.entity.Follow;
import com.project.traveldiary.security.TokenProvider;
import com.project.traveldiary.service.FollowService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/follow")
public class FollowController {

    private final FollowService followService;
    private final TokenProvider tokenProvider;

    @PostMapping("/{id}")
    public ResponseEntity<FollowResponse> follow(@PathVariable Long id,
        @RequestHeader("X-AUTH-TOKEN") String token) {

        String userId = tokenProvider.getUsername(token);

        return ResponseEntity.ok(followService.follow(userId, id));
    }

    @GetMapping("/{id}/follower")
    public ResponseEntity<List<FollowListResponse>> getFollowerList(@PathVariable Long id,
        @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {

        Page<Follow> followerList = followService.getFollowerList(id, page, size);

        return ResponseEntity.ok(FollowListResponse.follwerList(followerList));
    }

    @GetMapping("/{id}/following")
    public ResponseEntity<List<FollowListResponse>> getFollowingList(@PathVariable Long id,
        @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {

        Page<Follow> followingList = followService.getFollowingList(id, page, size);

        return ResponseEntity.ok(FollowListResponse.follwerList(followingList));
    }

    @GetMapping("/{id}/follower/count")
    public ResponseEntity<FollowCountResponse> getFollowerCount(@PathVariable Long id) {
        long followerCount = followService.getFollowerCount(id);

        return ResponseEntity.ok(FollowCountResponse.builder()
            .count(followerCount)
            .build());
    }

    @GetMapping("/{id}/following/count")
    public ResponseEntity<FollowCountResponse> getFollowingCount(@PathVariable Long id) {
        long followingCount = followService.getFollowingCount(id);

        return ResponseEntity.ok(FollowCountResponse.builder()
            .count(followingCount)
            .build());
    }
}
