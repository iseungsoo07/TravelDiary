package com.project.traveldiary.aop;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.project.traveldiary.exception.LockException;
import com.project.traveldiary.service.LockManager;
import com.project.traveldiary.type.ErrorCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LockAopAspectTest {

    @Mock
    LockManager lockManager;

    @Mock
    ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    MethodSignature methodSignature;

    @Mock
    DistributedLock distributedLock;

    @InjectMocks
    private LockAopAspect lockAopAspect;

    @Test
    @DisplayName("락 획득 및 해제 성공")
    void lockAndUnlock() throws Throwable {
        // given

        given(proceedingJoinPoint.getSignature())
            .willReturn(methodSignature);

        given(methodSignature.getMethod())
            .willReturn(getClass().getMethod("exampleMethod", Long.class, String.class));

        lockAopAspect.aroundMethod(proceedingJoinPoint, 123L, "abc");

        // then
        verify(lockManager, times(1)).lock(eq("like_diary"), anyString());
        verify(proceedingJoinPoint, times(1)).proceed();
        verify(lockManager, times(1)).unlock(eq("like_diary"), anyString());
    }

    @Test
    @DisplayName("예외가 발생해도 락 획득 및 해제 성공")
    void lockAndUnlock_evenIfException() throws Throwable {
        // given
        given(proceedingJoinPoint.getSignature())
            .willReturn(methodSignature);

        given(methodSignature.getMethod())
            .willReturn(getClass().getMethod("exampleMethod", Long.class, String.class));

        given(proceedingJoinPoint.proceed())
            .willThrow(new LockException(ErrorCode.LOCK_ACQUSITION_FAIL));

        Assertions.assertThrows(LockException.class,
            () -> lockAopAspect.aroundMethod(proceedingJoinPoint, 123L, "abc"));

        // then
        verify(lockManager, times(1)).lock(eq("like_diary"), anyString());
        verify(proceedingJoinPoint, times(1)).proceed();
        verify(lockManager, times(1)).unlock(eq("like_diary"), anyString());

    }

    @DistributedLock(prefix = "like_diary")
    public String exampleMethod(Long id, String userId) {
        return "result";
    }

}