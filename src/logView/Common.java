package logView;

import javafx.scene.control.Alert;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {

    public static void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(msg);
        alert.setHeaderText("");
        alert.showAndWait();
    }

    public static String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(date);
    }

    public static boolean isNullOrEmpty( String str ) {
        return str == null || str.isEmpty();
    }
}
