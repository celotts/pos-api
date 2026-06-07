package com.posapi.application.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface UserStore {
    boolean containsKey(String key);
    void put(String key, Object value);
    Map<String, Object> values();
}