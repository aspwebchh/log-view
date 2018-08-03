package logView;

import javafx.beans.property.SimpleStringProperty;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LogItem {
    private SimpleStringProperty _dateTime = new SimpleStringProperty();
    private SimpleStringProperty _type = new SimpleStringProperty();
    private SimpleStringProperty _content = new SimpleStringProperty();

    public final String dateTime;
    public final String type;
    public final String content;

    public String toString() {
        return dateTime + "|" + type + "|" + content;
    }

    public LogItem(String dateTime, String type, String content ) {
        this.dateTime = dateTime;
        this.type = type;
        this.content = content;

        _dateTime.set(dateTime);
        _type.set(type);
        _content.set(content);
    }

    public Date date() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = formatter.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
       if(date == null) {
            return new Date();
       } else {
            return date;
       }
    }

    public SimpleStringProperty dateTimeProperty() {
        return  _dateTime;
    }

    public SimpleStringProperty typeProperty() {
        return  _type;
    }

    public SimpleStringProperty contentProperty() {
        return  _content;
    }


    public static List<LogItem> asList( File logFile , String type, String keyword, String exceptKeyword, String regex) {
        String fileContent = FileUtil.readStringFromFile(logFile);
        String[] items = fileContent.split("(?=(\\d{4}-\\d+-\\d+\\s+\\d+:\\d+:\\d+))");
        List<LogItem> result = Arrays.stream(items).map( item -> str2logItem(item)).filter( item ->{
            return item != null &&
                    typeEquals(type, item) &&
                    containsKeyword(keyword, item) &&
                    notContainsKeyword(exceptKeyword, item) &&
                    regexMatch(regex, item);
        }).collect(Collectors.toList());
        return result;
    }

    private static boolean regexMatch(String regex, LogItem logItem) {
        if( Common.isNullOrEmpty(regex)) {
            return true;
        }
        try{
            Pattern pattern = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(logItem.content);
            return matcher.find();
        } catch (Throwable e) {
            Common.showAlert(e.getMessage());
            return false;
        }
    }

    private static boolean typeEquals(String type, LogItem item) {
        if( Common.isNullOrEmpty(type) ) {
            return true;
        }
        return type.trim().toUpperCase().equals(item.type.trim().toUpperCase());
    }

    private static boolean containsKeyword(String keyword, LogItem item ) {
        if( Common.isNullOrEmpty(keyword) ) {
            return true;
        }
        return item.content.toLowerCase().indexOf(keyword.toLowerCase()) != -1;
    }

    private static boolean notContainsKeyword(String keyword, LogItem item ) {
        if( Common.isNullOrEmpty(keyword) ) {
            return true;
        }
        return item.content.toLowerCase().indexOf(keyword.toLowerCase()) == -1;
    }

    private static LogItem str2logItem( String logItemString ) {
         String[] items = logItemString.split(",",3);
        if( items.length <3 ) {
            return null;
        } else {
            String dateTime = items[0];
            String type = items[1];
            String content = items[2];
            return new LogItem(dateTime,type,content);
        }
    }

}
