package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {

    private List<Task> tasks;

    public TaskManager() {
        tasks = new ArrayList<>();
    }

    public void addTask(String description) {
        Task task = new Task(description);
        tasks.add(task);
    }

    public void toggleTaskCompletion(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.get(index).markCompleted(!tasks.get(index).isCompleted());
        }
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> newTasks) {
        tasks = newTasks;
    }


    public void removeTask(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.remove(index);
        }
    }
    public void saveTasksToFile(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Task task : tasks) {
                writer.write(task.getDescription() + "," + task.isCompleted());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadTasksFromFile(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String description = parts[0];
                boolean completed = Boolean.parseBoolean(parts[1]);
                Task task = new Task(description);
                task.markCompleted(completed);
                tasks.add(task);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
