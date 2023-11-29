package com.project.traveldiary.aop;

import com.project.traveldiary.service.LockManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
@Slf4j
public class LockAopAspect {

    private final LockManager lockManager;

    @Around("@annotation(com.project.traveldiary.aop.DistributedLock) && args(id, userId)")
    public Object aroundMethod(ProceedingJoinPoint joinPoint, Long id, String userId) throws Throwable {
        log.info("aop 수행");

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        DistributedLock lock = signature.getMethod().getAnnotation(DistributedLock.class);

        lockManager.lock(lock.prefix(), String.valueOf(id));

        try {
            return joinPoint.proceed();
        } finally {
            lockManager.unlock(lock.prefix(), String.valueOf(id));
        }
    }
}
