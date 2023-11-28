package com.project.traveldiary.service;

import static com.project.traveldiary.type.ErrorCode.LOCK_ACQUSITION_FAIL;

import com.project.traveldiary.exception.LockException;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LockManager {

    private final RedissonClient redissonClient;

    public void lock(String key) {
        RLock lock = redissonClient.getLock(getLockKey(key));
        log.info("Lock 획득 시도 {}", getLockKey(key));

        try {
            boolean available = lock.tryLock(1, 15, TimeUnit.SECONDS);

            if (!available) {
                log.error("Lock Acquisition Failed");
                throw new LockException(LOCK_ACQUSITION_FAIL);
            }

        } catch (Exception e) {
            log.error("Redis lock failed", e);
        }
    }

    public void unlock(String key) {
        log.info("{} Lock 해제", getLockKey(key));
        redissonClient.getLock(getLockKey(key)).unlock();
    }

    private String getLockKey(String diaryId) {
        return "like_diary:" + diaryId;
    }


}
