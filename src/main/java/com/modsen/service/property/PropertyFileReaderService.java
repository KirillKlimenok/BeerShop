package com.modsen.service.property;

import com.modsen.exceptions.FilePropertyNotFoundException;
import lombok.extern.log4j.Log4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Log4j
public class PropertyFileReaderService implements ReaderPropertyFile {
    private static final Map<String, Properties> CACHE = new HashMap<>();

    @Override
    public String read(String path, String key) {
        Properties properties = findProperty(path);
        if (properties == null) {
            synchronized (this) {
                try (FileInputStream fileInputStream = new FileInputStream(path)) {
                    properties = new Properties();
                    properties.load(fileInputStream);
                    return properties.getProperty(key);
                } catch (IOException exception) {
                    log.error(exception.getMessage());
                    throw new FilePropertyNotFoundException(exception.getMessage());
                }
            }
        }else{
            return properties.getProperty(key);
        }
    }

    public Properties findProperty(String path) {
        return CACHE.get(path);
    }
}
