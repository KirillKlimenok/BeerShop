package modsen.com.service.PropertysService;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface ReaderPropertyFile {
    String read(String path, String key) throws IOException;
}
