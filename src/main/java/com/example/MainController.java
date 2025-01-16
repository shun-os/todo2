package com.example;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.example.model.ToDo;
import com.example.model.ToDoManager;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
public class MainController {
    private final String TODO_ID_PREFIX = "todo-";
    @FXML
    private Label clockLabel;
    @FXML
    private Label selectedTimeLabel;  // 選択された時間を表示するラベル
    @FXML
    private Button nowButton;
    @FXML
    private Button fiveMinutesButton;
    @FXML
    private Button tenMinutesButton;
    @FXML
    private MenuItem menuItemAbout;
    @FXML
    private MenuItem menuItemClose;
    @FXML
    private Button addBtn;
    @FXML
    private DatePicker headerDatePicker;
    @FXML
    private TextField headerTitleField;
    @FXML
    private TextField descriptionField;
//    @FXML
//    private VBox todoListVBox;
 
    @FXML
    private ListView<ToDo> taskListView;
    
    private ToDoManager toDoManager;
    
    private ToDoManager model;
    private LocalTime selectedTime;  // ユーザーが選択した時刻を保持
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private void showInfo(String txt) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("アプリの情報");
        alert.setHeaderText(null);
        alert.setContentText(txt);
        alert.showAndWait();
    }
    private HBox createToDoHBox(ToDo todo) {
        // チェックリスト
        var completedCheckBox = new CheckBox();
        completedCheckBox.setSelected(todo.isCompleted());
        completedCheckBox.getStyleClass().add("todo-completed");
        // タイトル
        var titleLabel = new Label(todo.getTitle());
        titleLabel.getStyleClass().add("todo-title");
        // 重要度
        var priorityLabel = new Label("優先度: " + todo.getPriority()); // 日本語の名称を追加
        priorityLabel.getStyleClass().add("todo-priority");
        // 時間
        var timeLabel = new Label(todo.getNowTimestamp().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        timeLabel.getStyleClass().add("todo-time");
        // 日付
        var dateLabel = new Label(todo.getDate().toString());
        dateLabel.getStyleClass().add("todo-date");
        // 削除ボタン
        var deleteBtn = new Button("削除");
        deleteBtn.getStyleClass().add("todo-delete");
        // HBoxを作成して各要素を追加
        HBox todoItem = new HBox(10); // 10 pxのスペースを設定
        todoItem.getChildren().addAll(completedCheckBox, titleLabel, priorityLabel, timeLabel, dateLabel, deleteBtn);
        todoItem.getStyleClass().add("todo-item");
        todoItem.setId(TODO_ID_PREFIX + todo.getId());
        // Event Handlerを追加
        completedCheckBox.selectedProperty().bindBidirectional(todo.completedProperty());
        deleteBtn.setOnAction(e -> {
            model.remove(todo);
//            todoListVBox.getChildren().remove(todoItem); // Remove from view
        });
        return todoItem; // HBoxを直接返す
    }
    public void initModel(ToDoManager manager) {
        if (this.toDoManager != null) {
            throw new IllegalStateException("Model can only be initialized once");
        }
        this.toDoManager = manager;
        toDoManager.loadInitialData();
        toDoManager.todosProperty().addListener((ListChangeListener<ToDo>) c -> {
            Platform.runLater(() -> taskListView.setItems(toDoManager.getToDoList()));
        });
        taskListView.setItems(toDoManager.getToDoList()); // 初期表示

        addBtn.setOnAction(e -> {
            toDoManager.create(headerTitleField.getText(), descriptionField.getText(), headerDatePicker.getValue(), false, ToDo.Priority.MEDIUM);
            headerTitleField.clear();
            descriptionField.clear();
            headerDatePicker.setValue(LocalDate.now());
        });
    }
    private void loadTaskList() {
        taskListView.getItems().clear();
        for (ToDo todo : model.todosProperty()) {
            taskListView.getItems().add(todo); // ToDoを直接リストに追加
        }
        taskListView.setCellFactory(param -> new javafx.scene.control.ListCell<ToDo>() {
            @Override
            protected void updateItem(ToDo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%s - %s - %s", 
                        item.getDate(), 
                        item.getNowTimestamp().toLocalTime(),
                        item.getPriority()));
                }
            }
        });
    }

    public void initialize() {
        headerDatePicker.setValue(LocalDate.now());
        updateClock(); // 時計の初期化
        startClockUpdate(); // 時計の更新を開始
        menuItemAbout.setOnAction(e -> showInfo("ToDo App"));
        menuItemClose.setOnAction(e -> Platform.exit());
        // ボタンのイベントハンドラ設定
        nowButton.setOnAction(e -> setCurrentTime());
        fiveMinutesButton.setOnAction(e -> setFutureTime(5));
        tenMinutesButton.setOnAction(e -> setFutureTime(10));
    }
    private void updateClock() {
        LocalTime now = LocalTime.now();
        clockLabel.setText(now.format(timeFormatter));
    }
    private void startClockUpdate() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000); // 1秒ごとに更新
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                Platform.runLater(this::updateClock);
            }
        }).start();
    }
    private void setCurrentTime() {
        selectedTime = LocalTime.now();
        updateSelectedTimeLabel();
    }
    private void setFutureTime(int minutes) {
        if (selectedTime == null) {
            selectedTime = LocalTime.now();
        }
        selectedTime = selectedTime.plusMinutes(minutes);
        updateSelectedTimeLabel();
    }
    private void updateSelectedTimeLabel() {
        if (selectedTime != null) {
            clockLabel.setText(selectedTime.format(timeFormatter));
            selectedTimeLabel.setText("選択された時刻: " + selectedTime.format(timeFormatter));
            selectedTimeLabel.setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
        }
    }
    @FXML
    private void handleDeleteButtonAction() {
        // 選択されたアイテムを取得
        ToDo selectedTask = taskListView.getSelectionModel().getSelectedItem();

        if (selectedTask == null) {
            // アイテムが選択されていない場合 - 警告を表示
            Alert alert = new Alert(Alert.AlertType.WARNING, "削除するアイテムを選択してください。", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        // 削除確認ダイアログを表示
        Alert confirmationAlert = new Alert(
            Alert.AlertType.CONFIRMATION,
            "このアイテムを削除しますか？: " + selectedTask.getTaskName(),
            ButtonType.YES, ButtonType.NO
        );

        ButtonType result = confirmationAlert.showAndWait().orElse(ButtonType.NO);
        if (result == ButtonType.YES) {
            // ToDoManagerから削除
            toDoManager.remove(selectedTask);

            // ListViewを更新
            taskListView.setItems(toDoManager.getToDoList());
        }
    }
}