package logView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class DownloadFileList {
    private static List<LogFileItem> logFileItemList = new ArrayList<>();

    public static void setLogFileItemList(List<LogFileItem> itemList) {
        logFileItemList = itemList;
    }

    @FXML
    private Label log_count;
    @FXML
    private TableView log_table;
    @FXML
    private GridPane log_body;

    @FXML
    protected void initialize() {
        initTable();
    }

    private void initTable() {
        log_table.prefWidthProperty().bind(log_body.widthProperty());
        log_table.prefHeightProperty().bind(log_body.heightProperty().subtract(10));

        ObservableList<TableColumn<?, ?>> cols = log_table.getColumns();
        TableColumn contentCol = cols.get(0);
        contentCol.prefWidthProperty().bind(log_table.widthProperty().subtract(320));
        log_table.setRowFactory(tv -> {
            TableRow row = new TableRow();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Object rowData = row.getItem();
                    LogFileItem logFileItem = (LogFileItem) rowData;
                    String path = logFileItem.getPath();
                    try {
                        Runtime.getRuntime().exec("notepad2 " + path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            return row;
        });

        List<LogFileItem> logs = logFileItemList.stream().filter(item -> !item.isLogback()).collect(Collectors.toList());
        logs.sort((a, b) ->  (int)(Common.strTime2UnixTimestamp(b.getTime()) - Common.strTime2UnixTimestamp(a.getTime())));
        log_table.setItems(FXCollections.observableArrayList(logs));
        log_count.setText(logFileItemList.size() + "个项目");

    }

}
