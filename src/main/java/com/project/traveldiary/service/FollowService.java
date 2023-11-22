package com.project.traveldiary.service;

import com.project.traveldiary.dto.FollowResponse;
import com.project.traveldiary.entity.Follow;
import org.springframework.data.domain.Page;

public interface FollowService {

    FollowResponse follow(String userId, Long id);

    Page<Follow> getFollowerList(Long id, int page, int size);

    Page<Follow> getFollowingList(Long id, int page, int size);

    long getFollowerCount(Long id);

    long getFollowingCount(Long id);
}
