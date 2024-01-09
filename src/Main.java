import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

public class Main {
    private static final String FILE_PATH = "tasks.txt";

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Простой Планер");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            DayPlanner dayPlanner = loadTasksFromFile();

            JList<String> taskList = new JList<>(new DefaultListModel<>());
            updateTaskList(taskList, dayPlanner);

            JButton addButton = new JButton("Добавить задачу");
            JButton markCompletedButton = new JButton("Пометить как выполненную");

            addButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String taskDescription = JOptionPane.showInputDialog("Введите описание задачи:");
                    Task newTask = new Task(taskDescription);
                    dayPlanner.addTask(newTask);
                    saveTasksToFile(dayPlanner);
                    updateTaskList(taskList, dayPlanner);
                }
            });

            markCompletedButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int selectedIndex = taskList.getSelectedIndex();
                    if (selectedIndex != -1) {
                        dayPlanner.markTaskCompleted(selectedIndex);
                        saveTasksToFile(dayPlanner);
                        updateTaskList(taskList, dayPlanner);
                    } else {
                        JOptionPane.showMessageDialog(null, "Выберите задачу для пометки выполненной.");
                    }
                }
            });

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JScrollPane(taskList), BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new GridLayout(1,2,10,10));
            buttonPanel.add(addButton);
            buttonPanel.add(markCompletedButton);

            panel.add(buttonPanel, BorderLayout.SOUTH);

            frame.getContentPane().add(panel);
            frame.setSize(500, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static DayPlanner loadTasksFromFile() {
        DayPlanner dayPlanner = new DayPlanner();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Формат строки: "Описание задачи;true/false"
                String[] parts = line.split(";");
                if (parts.length == 2) {
                    Task task = new Task(parts[0]);
                    if (Boolean.parseBoolean(parts[1])) {
                        task.markCompleted();
                    }
                    dayPlanner.addTask(task);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dayPlanner;
    }

    private static void saveTasksToFile(DayPlanner dayPlanner) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (Task task : dayPlanner.getTasks()) {
                // Сохраняем описание задачи и её состояние выполнения
                writer.println(task.getDescription() + ";" + task.isCompleted());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void updateTaskList(JList<String> taskList, DayPlanner dayPlanner) {
        DefaultListModel<String> model = (DefaultListModel<String>) taskList.getModel();
        model.clear();
        for (Task task : dayPlanner.getTasks()) {
            model.addElement(task.toString());
        }
    }
}
