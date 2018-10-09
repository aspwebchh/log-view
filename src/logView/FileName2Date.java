package logView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileName2Date {
    private int year;
    private int month;
    private int day;

    private boolean isValid;

    public FileName2Date( String fileName ) {
        Pattern pattern = Pattern.compile("^(\\d+)-(\\d+)-(\\d+)");
        Matcher matcher = pattern.matcher(fileName);
        if( !matcher.find()) {
            this.isValid = false;
        } else {
            this.isValid = true;

            year = Integer.parseInt( matcher.group(1) );
            month = Integer.parseInt( matcher.group(2) );
            day = Integer.parseInt( matcher.group(3) );
        }
    }

    public boolean isValid() {
        return this.isValid;
    }

    public int getYear() {
        return this.year;
    }

    public int getMonth() {
        return this.month;
    }

    public int getDay() {
        return this.day;
    }
}
