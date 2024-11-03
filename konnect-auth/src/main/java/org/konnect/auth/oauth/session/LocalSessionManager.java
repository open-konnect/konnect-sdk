package org.konnect.auth.oauth.session;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class LocalSessionManager<T> implements SessionManager<T> {

    private final Map<String, T> sessionData = new ConcurrentHashMap<>();

    @Override
    public void save(String sessionKey, T data) {
        sessionData.put(sessionKey, data);
    }

    @Override
    public Optional<T> fetch(String sessionKey) {
        return Optional.ofNullable(sessionData.get(sessionKey));
    }

    @Override
    public void clear(String sessionKey) {
        sessionData.remove(sessionKey);
    }
}
