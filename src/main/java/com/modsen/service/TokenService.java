package com.modsen.service;

import java.util.UUID;

public class TokenService {
    public UUID generateNewToken() {
        return UUID.randomUUID();
    }
}
