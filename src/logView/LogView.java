package logView;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import logView.Common;
import logView.LogItem;
import logView.LogUtil;
import logView.RangeQueryControlItem;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.List;

public class LogView {

    @FXML
    private ComboBox dir, range, type;
    @FXML
    private TableView log_table;
    @FXML
    private GridPane log_body;
    @FXML
    private TextField keyword, except_keyword, regex;
    @FXML
    private Label log_count;
    @FXML
    private DatePicker date;

    private int status = 0;

    private void initDirQueryControl() {
        List<String> pathNames = LogUtil.getPathNames();
        ObservableList<String> obList = FXCollections.observableArrayList(pathNames);
        dir.setItems(obList);
    }

    private void initRangeQueryControl() {
        ObservableList<RangeQueryControlItem> datalist = RangeQueryControlItem.data();
        range.setItems(datalist);
        range.setValue(datalist.get(1));
    }

    private void initTypeQueryControl() {
        List<String> types = LogUtil.getLogTypes();
        ObservableList<String> obList = FXCollections.observableArrayList(types);
        type.setItems(obList);
        type.setValue(obList.get(0));
    }

    private void  initDatePickerAndRangeEvent() {
        date.setOnAction(e->{
            if( (this.status & 2) > 0 )  {
                return;
            }
            this.status = 1;
            range.setValue(null);
            this.status = 0;
        });

        range.setOnAction(e->{
            if( (this.status & 1) > 0 )  {
                return;
            }
            this.status = 2;
            date.setValue(null);
            this.status = 0;
        });
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

    @FXML
    protected void initialize() {
        initDirQueryControl();
        initRangeQueryControl();
        initTypeQueryControl();
        initTable();
        initDatePickerAndRangeEvent();
    }

    private int getRangeValue() {
        if( range.getValue() == null) {
            return 0;
        } else {
            RangeQueryControlItem selectRange = (RangeQueryControlItem) range.getValue();
            return selectRange.value;
        }
    }

    @FXML
    public void click() {
        String selectDir = (String) dir.getValue();
        int range = getRangeValue();
        String selectType = (String) type.getValue();
        String sKeyword = keyword.getText().trim();
        String sExceptKeyword = except_keyword.getText().trim();
        String sRegex = regex.getText();
        LocalDate dt = date.getValue();

        if (selectDir == null) {
            Common.showAlert("未选择目录");
            return;
        }
        List<LogItem> logItems = LogUtil.getLogContent(selectDir, range, selectType, sKeyword, sExceptKeyword, sRegex, dt);
        log_table.setItems(FXCollections.observableArrayList(logItems));
        log_count.setText(logItems.size() + "个项目");
    }
}
