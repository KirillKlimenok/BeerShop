package modsen.com.service.property;

import lombok.SneakyThrows;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyFileReaderService implements ReaderPropertyFile {


    @Override
    public String read(String path, String key) throws IOException {
        Properties properties = new Properties();
        FileInputStream fileInputStream = new FileInputStream(path);

        properties.load(fileInputStream);
        return properties.getProperty(key);
    }
}
