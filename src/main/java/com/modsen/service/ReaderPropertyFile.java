package com.modsen.service;

import com.modsen.exceptions.FilePropertyNotFoundException;

public interface ReaderPropertyFile {
    String read(String path, String key) throws FilePropertyNotFoundException;
}
