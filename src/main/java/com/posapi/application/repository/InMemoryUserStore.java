package com.posapi.application.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserStore implements UserStore {
    private final Map<String, Object> store = new ConcurrentHashMap<>();

    @Override
    public boolean containsKey(String key) {
        return store.containsKey(key);
    }

    @Override
    public void put(String key, Object value) {
        store.put(key, value);
    }

    @Override
    public Map<String, Object> values() {
        return store;
    }
}