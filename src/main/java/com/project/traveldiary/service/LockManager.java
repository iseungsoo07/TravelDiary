package com.project.traveldiary.service;

import static com.project.traveldiary.type.ErrorCode.LOCK_ACQUSITION_FAIL;
import static com.project.traveldiary.type.ErrorCode.LOCK_ALREADY_ASSIGNED;

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

    public void lock(String prefix, String key) {
        RLock lock = redissonClient.getLock(getLockKey(prefix, key));
        log.info("Lock 획득 시도 {}", getLockKey(prefix, key));

        try {
            boolean available = lock.tryLock(1, 15, TimeUnit.SECONDS);

            if (!available) {
                log.error("Lock Acquisition Failed");
                throw new LockException(LOCK_ALREADY_ASSIGNED);
            }

        } catch (Exception e) {
            log.error("Redis lock failed", e);
            throw new LockException(LOCK_ACQUSITION_FAIL);
        }
    }

    public void unlock(String prefix, String key) {
        log.info("{} Lock 해제", getLockKey(prefix, key));
        redissonClient.getLock(getLockKey(prefix, key)).unlock();
    }

    private String getLockKey(String prefix, String diaryId) {
        return prefix + ":" + diaryId;
    }


}
