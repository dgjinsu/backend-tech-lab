package com.mirero.pwm.sse.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Repository
public class EmitterRepository {
    private final Map<String, SseEmitter> clients = new ConcurrentHashMap<>();
    private final Set<String> userList = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void addClient(String userId, SseEmitter emitter) {
        clients.put(userId, emitter);
        userList.add(userId);
    }

    public void removeClient(String userId) {
        clients.remove(userId);
        userList.remove(userId);
    }

    public Optional<SseEmitter> getClient(String userId) {
        return Optional.ofNullable(clients.get(userId));
    }

    public List<String> getAllUsers() {
        return new ArrayList<>(userList);
    }
}