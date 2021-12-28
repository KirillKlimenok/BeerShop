package modsen.com.service.fileReader;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public interface FileReaderService {
    ArrayList<String> read(String path) throws FileNotFoundException;
}
