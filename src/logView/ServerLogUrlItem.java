package logView;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerLogUrlItem {
    private String parent;
    private String file;
    private String dateString;

    public ServerLogUrlItem(String parent,String file, String dateString) {
        this.parent = parent;
        this.file = file;
        this.dateString =  Common.unixTimestamp2StrTime( Date.parse(dateString) );
    }

    public String getFullUrl() {
        String url = this.parent;
        Pattern pattern = Pattern.compile("\\/$");
        if( !pattern.matcher(url).find()) {
            url +="/";
        }
        url += file;
        return url;
    }

    public String getDateString() {
        return this.dateString;
    }

    public String toString() {
        return getFullUrl() + "#" + (isFile() ? "file" : "dir") + "#" + this.getDateString();
    }

    public boolean isFile() {
        Pattern pattern = Pattern.compile(".+?\\.\\w+");
        return pattern.matcher(file).find();
    }

    public String getDir() {
        String url = getFullUrl();
        url = url.replaceFirst("http:\\/\\/[^\\/]+\\/?","");
        url = url.replaceAll("/", Matcher.quoteReplacement("\\"));
        return Config.getPath() + "\\" + url;
    }

    //15天内
    public boolean within15day() {
        long now = System.currentTimeMillis();
        long timestamp = Common.strTime2UnixTimestamp(this.dateString);
        long diff = now - timestamp;
        return diff <= 1000 * 3600 * 24 * 15;
    }
}
