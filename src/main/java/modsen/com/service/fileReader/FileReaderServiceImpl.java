package modsen.com.service.fileReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileReaderServiceImpl implements FileReaderService {
    @Override
    public ArrayList<String> read(String path) throws FileNotFoundException {
        File file = new File(path);
        Scanner scanner = new Scanner(file);
        ArrayList<String> fileInfo = new ArrayList<>();

        while (scanner.hasNext()) {
            fileInfo.add(scanner.nextLine());
        }
        scanner.close();

        return fileInfo;
    }
}
