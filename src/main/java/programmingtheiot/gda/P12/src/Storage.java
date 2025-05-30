package programmingtheiot.gda.P12.src;

import java.io.FileWriter;
import java.io.IOException;

public class Storage {
        private static final String PATH = "data/latest_data.txt";

    public static void saveToFile(String data) {
        try (FileWriter fw = new FileWriter(PATH)) {
            fw.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
