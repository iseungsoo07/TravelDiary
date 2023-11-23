package com.project.traveldiary.service;

import static com.project.traveldiary.type.ErrorCode.ALREADY_FOLLOWED_USER;
import static com.project.traveldiary.type.ErrorCode.NOT_FOUND_USER;

import com.project.traveldiary.dto.FollowListResponse;
import com.project.traveldiary.dto.FollowResponse;
import com.project.traveldiary.entity.Follow;
import com.project.traveldiary.entity.User;
import com.project.traveldiary.exception.FollowException;
import com.project.traveldiary.exception.UserException;
import com.project.traveldiary.repository.FollowRepository;
import com.project.traveldiary.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final UserRepository userRepository;
    private final FollowRepository followRepository;

    @Override
    public FollowResponse follow(String follower_id, Long following_id) {
        User follower = userRepository.findByUserId(follower_id)
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        User following = userRepository.findById(following_id)
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new FollowException(ALREADY_FOLLOWED_USER);
        }

        Follow follow = Follow.builder()
            .follower(follower)
            .following(following)
            .build();

        followRepository.save(follow);

        return FollowResponse.builder()
            .follower(follower.getNickname())
            .following(following.getNickname())
            .message(follower.getNickname() + "님이 " + following.getNickname() + "님을 팔로우 했습니다.")
            .build();

    }

    @Override
    public List<FollowListResponse> getFollowerList(Long id, int page, int size) {

        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        Pageable pageable = PageRequest.of(page, size);

        Page<Follow> follow = followRepository.findByFollowingOrderByFollowDateDesc(user, pageable);

        return FollowListResponse.follwerList(follow);

    }

    @Override
    public List<FollowListResponse> getFollowingList(Long id, int page, int size) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        Pageable pageable = PageRequest.of(page, size);

        Page<Follow> follow = followRepository.findByFollowerOrderByFollowDateDesc(user, pageable);

        return FollowListResponse.followingList(follow);
    }

    @Override
    public long getFollowerCount(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        return followRepository.countByFollowing(user);
    }

    @Override
    public long getFollowingCount(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserException(NOT_FOUND_USER));

        return followRepository.countByFollower(user);
    }
}
