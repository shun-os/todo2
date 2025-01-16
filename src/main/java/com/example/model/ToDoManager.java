package com.example.model;

import java.time.LocalDate;

import com.example.model.ToDo.Priority;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ToDoManager {

    private ObservableList<ToDo> toDoList;

    public ToDoManager() {
        this.toDoList = FXCollections.observableArrayList();
    }

    public ObservableList<ToDo> getToDoList() {
        return toDoList;
    }

    public void add(ToDo task) {
        toDoList.add(task);
    }


    private ListProperty<ToDo> todos = new SimpleListProperty<>(FXCollections.observableArrayList());

    public ListProperty<ToDo> todosProperty() {
        return todos;
    }

    public void remove(ToDo todo) {
        todos.remove(todo);
        System.out.println("Removed #" + todo.getId());
    }

    private void addListener(ToDo todo) {
        todo.titleProperty().addListener((observable, oldValue, newValue) ->
                System.out.println("Title changed #" + todo.getId() + " : " + newValue));

        todo.dateProperty().addListener((observable, oldValue, newValue) ->
                System.out.println("Date changed #" + todo.getId() + " : " + newValue));

        todo.completedProperty().addListener((observable, oldValue, newValue) ->
                System.out.println("Completed changed #" + todo.getId() + " : " + newValue));

        todo.priorityProperty().addListener((observable, oldValue, newValue) ->
                System.out.println("Priority changed #" + todo.getId() + " : " + newValue));
    }

    
    private void addNewToDo(int id, String title, String description, LocalDate date, boolean completed, Priority priority) { 
        var todo = new ToDo(id, title, description, date, completed, priority);
        addListener(todo);
        todos.add(todo);
    }

    public void create(String title, String description, LocalDate date, boolean completed, Priority priority){
        int newId = todos.isEmpty() ? 0 : todos.stream().mapToInt(ToDo::getId).max().getAsInt() + 1;
        addNewToDo(newId, title, description, date, completed, priority);
        System.out.println("Added #" + newId);
    }

    

    public void loadInitialData() {
        addNewToDo(0, "Design", "Design description", LocalDate.parse("2022-12-01"), true, Priority.MEDIUM);  // StringからPriorityに変更
        addNewToDo(1, "Implementation", "Implementation description", LocalDate.parse("2022-12-07"), false, Priority.HIGH);   // StringからPriorityに変更
    }
}
