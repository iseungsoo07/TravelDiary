package com.project.traveldiary.repository;

import com.project.traveldiary.entity.Follow;
import com.project.traveldiary.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowing(User follower, User following);

    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    Page<Follow> findByFollowingOrderByFollowDateDesc(User following, Pageable pageable);

    Page<Follow> findByFollowerOrderByFollowDateDesc(User follower, Pageable pageable);

    long countByFollower(User follower);

    long countByFollowing(User following);


}
