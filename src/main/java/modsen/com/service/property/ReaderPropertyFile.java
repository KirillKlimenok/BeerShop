package modsen.com.service.property;

import java.io.IOException;

public interface ReaderPropertyFile {
    String read(String path, String key) throws IOException;
}
