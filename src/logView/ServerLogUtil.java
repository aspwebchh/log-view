package logView;

import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerLogUtil {


    public static void download(String dirUrl, ExecutorService executorService, Consumer<LogFileItem> onSaved) {
        String rootUrl = dirUrl;
        List<ServerLogUrlItem> urls = handleDir(rootUrl);
        for (int i = 0; i < urls.size(); i++) {
            ServerLogUrlItem urlItem = urls.get(i);
            if (urlItem.isFile()) {
                executorService.execute(() -> handleFile(urlItem, onSaved));
            } else {
                File dir = new File(urlItem.getDir());
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                download(urlItem.getFullUrl(), executorService, onSaved);
            }
        }
    }

    //是否覆盖
    private static boolean isCover(String fileName) {
        Pattern pattern = Pattern.compile("(\\d+)-(\\d+)-(\\d+)");
        Matcher matcher = pattern.matcher(fileName);
        if (!matcher.find()) {
            return true;
        }
        int year = Integer.parseInt(matcher.group(1));
        int month = Integer.parseInt(matcher.group(2));
        int day = Integer.parseInt(matcher.group(3));
        Calendar now = Calendar.getInstance();
        if (now.get(Calendar.YEAR) == year && (now.get(Calendar.MONTH) + 1) == month && now.get(Calendar.DAY_OF_MONTH) == day) {
            return true;
        } else {
            return false;
        }
    }

    private static void handleFile(ServerLogUrlItem serverLogUrlItem, Consumer<LogFileItem> onSaved) {
        File file = new File(serverLogUrlItem.getDir());
        if (file.exists() && !isCover(file.getName())) {
            return;
        }
        //非15天内的日志不更新
        if(!serverLogUrlItem.within15day()) {
            return;
        }
        String content = Common.httpGet(serverLogUrlItem.getFullUrl());
        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "utf8");
             BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)) {
            bufferedWriter.write(content);
            bufferedWriter.flush();

            if (onSaved != null) {
                LogFileItem logFileItem = new LogFileItem(serverLogUrlItem.getDir(), serverLogUrlItem.getDateString(), file.length());
                onSaved.accept(logFileItem);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<ServerLogUrlItem> handleDir(String parentUrl) {
        List<ServerLogUrlItem> result = new ArrayList<>();
        String content = Common.httpGet(parentUrl);
        if (content == null) {
            return result;
        }
        Pattern pattern = Pattern.compile("href=\"([^\\.][^\"]+)\".+?(\\d+-\\w+-\\d+\\s+\\d+:\\d+)");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String file = matcher.group(1);
            String date = matcher.group(2);
            result.add(new ServerLogUrlItem(parentUrl, file, date));
        }
        return result;
    }
}
