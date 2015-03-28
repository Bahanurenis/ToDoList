package com.shenkar.data;

import com.shenkar.common.Task;

import java.sql.SQLException;
import java.util.List;

public interface TasksIDAO {
    void open() throws SQLException;
    void close();
    List<Task> getNotCompletedTasks(String category);
    List<Task> getCompletedTasks();
    void removeTask(Task task);
    Task addTask(Task task, String category);
    void editTask(Task task);
    void markTaskAsDone(Task task);
    void getTaskBack(Task task);
}
