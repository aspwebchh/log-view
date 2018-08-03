package logView;

import java.io.*;

public class FileUtil {
    public static String readStringFromFile(File file) {
        try {
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = bufferedReader.readLine();
                String result = "";
                while (lineTxt != null) {
                    result += lineTxt;
                    lineTxt =  bufferedReader.readLine();
                }
                return result;
            }
        } catch (UnsupportedEncodingException | FileNotFoundException e) {

        } catch (IOException e) {

        }
        return null;
    }
}
