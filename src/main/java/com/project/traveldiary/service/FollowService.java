package com.project.traveldiary.service;

import com.project.traveldiary.dto.FollowListResponse;
import com.project.traveldiary.dto.FollowResponse;
import java.util.List;

public interface FollowService {

    FollowResponse follow(String follower_id, Long following_id);

    List<FollowListResponse> getFollowerList(Long id, int page, int size);

    List<FollowListResponse> getFollowingList(Long id, int page, int size);

    long getFollowerCount(Long id);

    long getFollowingCount(Long id);
}
