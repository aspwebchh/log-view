package logView;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogFileItem {
    private SimpleStringProperty _path = new SimpleStringProperty();
    private SimpleStringProperty _time = new SimpleStringProperty();
    private SimpleLongProperty _size = new SimpleLongProperty();

    public SimpleStringProperty pathProperty() {
        return _path;
    }

    public SimpleStringProperty timeProperty() {
        return _time;
    }

    public SimpleLongProperty sizeProperty() {
        return _size;
    }

    private String path;
    private String time;
    private long size;

    public LogFileItem(String path, String time, long size) {
        path = path.replaceFirst("^\\/.+?\\/", "");
        size = new Double(Math.ceil(size / 1000D)).longValue();

        this.path = path;
        this.time = time;
        this.size = size;

        this._path.set(path);
        this._time.set(time);
        this._size.set(size);
    }

    public String toString() {
        return "[" + path + ", " + time + "," + size + "]";
    }

    public String getPath() {
        return path;
    }

    public String getTime() {
        return time;
    }

    public long getSize() {
        return size;
    }

    public long getTimestamp() {
        return Common.strTime2UnixTimestamp(time);
    }

    public boolean isLogback() {
        Pattern pattern = Pattern.compile("(\\d+)-(\\d+)-(\\d+)");
        Matcher matcher = pattern.matcher(path);
        return matcher.find();
    }
}
