package modsen.com.service.PropertysService;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertyFileReaderService implements ReaderPropertyFile {

    @Override
    public String read(String path, String key) throws IOException {
        FileInputStream fileInputStream;
        Properties properties = new Properties();
        fileInputStream = new FileInputStream(path);
        properties.load(fileInputStream);
        return properties.getProperty(key);
    }
}
