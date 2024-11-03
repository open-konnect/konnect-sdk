package org.konnect.auth.oauth.session;

import java.util.Optional;

public interface SessionManager<T> {

    void save(String sessionKey, T data);
    Optional<T> fetch(String sessionKey);
    void clear(String sessionKey);
}
