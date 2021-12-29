package modsen.com.service.fileReader;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public interface FileReaderService {
    List<String> read(String path) throws FileNotFoundException;
}
