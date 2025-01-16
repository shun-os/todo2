package com.example.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ToDo {

    // 列挙型Priority
    public enum Priority {
        LOW, MEDIUM, HIGH;
    }

    // フィールド
    private final int id;
    private String description;
    private final StringProperty title = new SimpleStringProperty();          // タイトル
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>(); // 日付
    private final BooleanProperty completed = new SimpleBooleanProperty();    // 完了状態
    private final ObjectProperty<Priority> priority = new SimpleObjectProperty<>(); // 優先度

    private final ObjectProperty<LocalDateTime> nowTimestamp = new SimpleObjectProperty<>(LocalDateTime.now());
    private final ObjectProperty<LocalDateTime> addFiveTime = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> addTenTime = new SimpleObjectProperty<>();
    private final StringProperty taskName = new SimpleStringProperty(); // ここでStringPropertyとして宣言
    // コンストラクタ

    public ToDo(int id, String title, String description, LocalDate date, boolean completed, Priority priority) {
        this.id = id;
        this.title.set(title);
        this.description = description;
        this.date.set(date);
        this.completed.set(completed);
        this.priority.set(priority);
        this.taskName.set(title); // コンストラクタで初期化 (必要に応じて変更)
        updateTimeProperties();
    }

    
    // IDは読み取り専用
    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // タイトル
    public StringProperty titleProperty() {
        return title;
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    // 日付
    public ObjectProperty<LocalDate> dateProperty() {
        return date;
    }

    public LocalDate getDate() {
        return date.get();
    }

    public void setDate(LocalDate localDate) {
        this.date.set(localDate);
    }
    /*
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }


     */
    // 完了状態
    public BooleanProperty completedProperty() {
        return completed;
    }

    public boolean isCompleted() {
        return completed.get();
    }

    public void setCompleted(boolean completed) {
        this.completed.set(completed);
    }

    // 重要度
    public ObjectProperty<Priority> priorityProperty() {
        return priority;
    }

    public Priority getPriority() {
        return priority.get();
    }

    public void setPriority(Priority priority) {
        this.priority.set(priority);
    }

    // StringからPriorityへの変換メソッド (オーバーロード)
    public void setPriority(String priority) {
        try {
            this.priority.set(Priority.valueOf(priority.toUpperCase()));
        } catch (IllegalArgumentException | NullPointerException e) {
            this.priority.set(Priority.LOW);  // デフォルト値
        }
    }

    // 現在時刻
    public ObjectProperty<LocalDateTime> nowTimestampProperty() {
        return nowTimestamp;
    }

    public LocalDateTime getNowTimestamp() {
        return nowTimestamp.get();
    }

    public void updateNowTimestamp() {
        nowTimestamp.set(LocalDateTime.now());
    }

    // +5分後
    public void setAddFiveTime() {
        addFiveTime.set(getNowTimestamp().plusMinutes(5));
    }

    public ObjectProperty<LocalDateTime> addFiveTimeProperty() {
        return addFiveTime;
    }

    public LocalDateTime getAddFiveTime() {
        return addFiveTime.get();
    }

    // +10分後
    public void setAddTenTime() {
        addTenTime.set(getNowTimestamp().plusMinutes(10));
    }

    public ObjectProperty<LocalDateTime> addTenTimeProperty() {
        return addTenTime;
    }

    public LocalDateTime getAddTenTime() {
        return addTenTime.get();
    }

    // 時刻プロパティを更新
    private void updateTimeProperties() {
        nowTimestamp.addListener((obs, oldTime, newTime) -> {
            setAddFiveTime();
            setAddTenTime();
        });
        setAddFiveTime();
        setAddTenTime();
    }
    
    
    public String getTaskName() {
        return taskName.get();
    }

    public void setTaskName(String taskName) {
        this.taskName.set(taskName);
    }
    
}