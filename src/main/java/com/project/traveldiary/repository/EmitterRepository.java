package com.project.traveldiary.repository;

import java.util.Map;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface EmitterRepository {

    SseEmitter save(String emitterId, SseEmitter sseEmitter);

    void saveEventCache(String eventCacheId, Object event);

    Map<String, SseEmitter> findAllEmitterStartsWithUserId(String userId);

    Map<String, Object> findAllEventCacheStartsWithUserId(String userId);

    void deleteById(String id);

    void deleteAllEmitterStartsWithUserId(String userId);

    void deleteAllEventCacheStartsWithUserId(String userId);
}
