package logView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerLogUrlItem {
    private String parent;
    private String file;

    public ServerLogUrlItem(String parent,String file) {
        this.parent = parent;
        this.file = file;
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

    public String toString() {
        return getFullUrl() + "," + (isFile() ? "file" : "dir");
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
}
