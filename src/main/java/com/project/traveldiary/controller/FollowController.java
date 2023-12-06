package com.project.traveldiary.controller;

import com.project.traveldiary.dto.FollowCountResponse;
import com.project.traveldiary.dto.FollowListResponse;
import com.project.traveldiary.dto.FollowResponse;
import com.project.traveldiary.security.TokenProvider;
import com.project.traveldiary.service.FollowService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/follow")
public class FollowController extends BaseController {

    private final FollowService followService;

    public FollowController(TokenProvider tokenProvider, FollowService followService) {
        super(tokenProvider);
        this.followService = followService;
    }

    @PostMapping("/{id}")
    public ResponseEntity<FollowResponse> follow(@PathVariable Long id,
        @RequestHeader("X-AUTH-TOKEN") String token) {

        String userId = getCurrentUserId(token);

        FollowResponse followResponse = followService.follow(userId, id);
        followResponse.setMessage(
            followResponse.getFollower() + "님이 " + followResponse.getFollowing() + "님을 팔로우 했습니다.");

        return ResponseEntity.ok(followResponse);

    }

    @DeleteMapping("/cancel/{id}")
    public ResponseEntity<FollowResponse> cancelFollow(@PathVariable Long id,
        @RequestHeader("X-AUTH-TOKEN") String token) {

        String userId = getCurrentUserId(token);

        return ResponseEntity.ok(followService.cancelFollow(userId, id));
    }

    @GetMapping("/{id}/follower")
    public ResponseEntity<List<FollowListResponse>> getFollowerList(@PathVariable Long id,
        @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(followService.getFollowerList(id, page, size));
    }

    @GetMapping("/{id}/following")
    public ResponseEntity<List<FollowListResponse>> getFollowingList(@PathVariable Long id,
        @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(followService.getFollowingList(id, page, size));
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
