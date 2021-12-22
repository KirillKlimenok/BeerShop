package modsen.com.service.PropertysService;

import lombok.SneakyThrows;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyFileReaderService implements ReaderPropertyFile {

    @Override
    @SneakyThrows
    public String read(String path, String key) {
        Properties properties = new Properties();
        FileInputStream fileInputStream = new FileInputStream(path);

        properties.load(fileInputStream);
        return properties.getProperty(key);
    }
}
