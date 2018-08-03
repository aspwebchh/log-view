package logView;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class RangeQueryControlItem {
    public final String name;
    public final int value;

    public RangeQueryControlItem(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String toString() {
        return name;
    }

    public static ObservableList<RangeQueryControlItem> data() {
        List<RangeQueryControlItem> data = new ArrayList<>();
        data.add( new RangeQueryControlItem("1日",1));
        data.add(new RangeQueryControlItem("3日",3));
        data.add(new RangeQueryControlItem("7日",7));
        data.add(new RangeQueryControlItem("15日",15));
        data.add(new RangeQueryControlItem("一个月",30));
        data.add(new RangeQueryControlItem("半年",180));
        data.add(new RangeQueryControlItem("1年",365));
        return  FXCollections.observableArrayList(data);
    }
}
