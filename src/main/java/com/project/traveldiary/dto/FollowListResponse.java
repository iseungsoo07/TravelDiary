package com.project.traveldiary.dto;

import com.project.traveldiary.entity.Follow;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowListResponse {

    private String nickname;

    public static List<FollowListResponse> follwerList(Page<Follow> page) {
        return page.getContent().stream()
            .map(follow -> FollowListResponse.builder()
                .nickname(follow.getFollower().getNickname())
                .build())
            .collect(Collectors.toList());
    }

    public static List<FollowListResponse> followingList(Page<Follow> page) {
        return page.getContent().stream()
            .map(follow -> FollowListResponse.builder()
                .nickname(follow.getFollowing().getNickname())
                .build())
            .collect(Collectors.toList());
    }


}
