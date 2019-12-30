package logView;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import sun.net.www.content.text.Generic;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public class LogView {

    @FXML
    private ComboBox dir, type;
    @FXML
    private TableView log_table;
    @FXML
    private GridPane log_body;
    @FXML
    private TextField keyword, except_keyword, regex;
    @FXML
    private Label log_count;
    @FXML
    private Label msg;
    @FXML
    private FlowPane msg_pane;
    @FXML
    private DatePicker beginDate;
    @FXML
    private DatePicker endDate;

    private int status = 0;

    private void initDirQueryControl() {
        List<String> pathNames = LogUtil.getPathNames();
        ObservableList<String> obList = FXCollections.observableArrayList(pathNames);
        dir.setItems(obList);
    }

    private void initTypeQueryControl() {
        List<String> types = LogUtil.getLogTypes();
        ObservableList<String> obList = FXCollections.observableArrayList(types);
        type.setItems(obList);
        type.setValue(obList.get(0));
    }

    private void initDatePicker() {
        LocalDate end = LocalDate.now();//(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        endDate.setValue(end);

        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_MONTH, -15);
        LocalDate begin = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
        beginDate.setValue(begin);
    }

    private void initTable() {
        log_table.prefWidthProperty().bind(log_body.widthProperty());
        log_table.prefHeightProperty().bind(log_body.heightProperty().subtract(60));

        ObservableList<TableColumn<?, ?>> cols = log_table.getColumns();
        TableColumn contentCol = cols.get(2);
        contentCol.prefWidthProperty().bind(log_table.widthProperty().subtract(250));
        //contentCol.setCellFactory(TextFieldTableCell.forTableColumn());
        log_table.setRowFactory(tv -> {
            TableRow row = new TableRow();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Object rowData = row.getItem();
                    Common.showContent(rowData.toString());
                }
            });
            return row;
        });
    }

    private void initMsg() {
        msg_pane.prefWidthProperty().bind(log_table.widthProperty().subtract(100));
        //msg.setText("显示已下载日志");
    }

    @FXML
    protected void initialize() {
        initDirQueryControl();
        initTypeQueryControl();
        initTable();
        initDatePicker();
        initMsg();
    }

    @FXML
    public void click() {
        String selectDir = (String) dir.getValue();
        String selectType = (String) type.getValue();
        String sKeyword = keyword.getText().trim();
        String sExceptKeyword = except_keyword.getText().trim();
        String sRegex = regex.getText();
        LocalDate beginDateValue = beginDate.getValue();
        LocalDate endDateValue = endDate.getValue();

        if (selectDir == null) {
            Common.showAlert("未选择目录");
            return;
        }

        if (beginDateValue == null) {
            Common.showAlert("未选择开始日期");
            return;
        }

        if (endDateValue == null) {
            Common.showAlert("未选择结束日期");
            return;
        }

        List<LogItem> logItems = LogUtil.getLogContent(selectDir, selectType, sKeyword, sExceptKeyword, sRegex, beginDateValue, endDateValue);
        log_table.setItems(FXCollections.observableArrayList(logItems));
        log_count.setText(logItems.size() + "个项目");
    }


    @FXML
    public void download() {
        CopyOnWriteArrayList<LogFileItem> downloadFilePaths = new CopyOnWriteArrayList<>();

        msg.setText("服务器日志下载中...");
        AtomicInteger count = new AtomicInteger(0);
        ThreadPoolExecutor executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(50);
        ServerLogUtil.download(Config.getUrl(), executorService, logFileItem -> {
            downloadFilePaths.add(logFileItem);
            count.incrementAndGet();
        });
        executorService.shutdown();
        new Thread(() -> {
            while (executorService.getActiveCount() > 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Platform.runLater(() -> {
                msg.setText(count.get() + "个项目下载完毕");
                initDirQueryControl();
                DownloadFileList.setLogFileItemList(downloadFilePaths);
            });
        }).start();
    }


    private boolean downloadDialogIsShow = false;

    @FXML
    public void showDownloadFile() {
        if (downloadDialogIsShow) {
            return;
        }
        try {
            Parent root = FXMLLoader.load(getClass().getResource("DownloadFileList.fxml"));
            Stage stage = new Stage();
            stage.setTitle("下载日志列表");
            Scene scene = new Scene(root, 800, 500);
            stage.setScene(scene);
            stage.initOwner(Main.mainScene.getWindow());
            stage.show();
            downloadDialogIsShow = true;
            stage.setOnCloseRequest(e -> {
                downloadDialogIsShow = false;
            });

            scene.getStylesheets().add(
                    getClass().getResource("logView.css")
                            .toExternalForm());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
