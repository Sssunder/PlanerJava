package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.List;

public class ImprovedGUI extends JFrame {

    private JTabbedPane tabbedPane;
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JTable tomorrowTaskTable;
    private DefaultTableModel tomorrowTableModel;
    private TaskManager taskManager;

    public ImprovedGUI() {
        taskManager = new TaskManager();

        // Создаем вкладки
        tabbedPane = new JTabbedPane();
        JPanel todayTab = makeTab("Сегодня");
        JPanel tomorrowTab = makeTab("Завтра");

        tabbedPane.add("Сегодня", todayTab);
        tabbedPane.add("Завтра", tomorrowTab);

        // Добавляем таблицу задач для сегодня
        String[] cols = {"Описание", "Статус"};
        tableModel = new DefaultTableModel(cols, 0);
        taskTable = new JTable(tableModel);
        taskTable.setGridColor(Color.GRAY);

        // Добавляем таблицу задач для завтра
        tomorrowTableModel = new DefaultTableModel(cols, 0);
        tomorrowTaskTable = new JTable(tomorrowTableModel);
        tomorrowTaskTable.setGridColor(Color.GRAY);

        // Создаем верхнюю панель для сегодня
        JPanel topPanel = createTopPanel();

        // Создаем верхнюю панель для завтра
        JPanel tomorrowTopPanel = createTopPanel();

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);

        // Пункт меню "Открыть"
        JMenuItem openMenuItem = new JMenuItem("Открыть");
        openMenuItem.addActionListener(e -> openFile());
        fileMenu.add(openMenuItem);

        // Пункт меню "Сохранить"
        JMenuItem saveMenuItem = new JMenuItem("Сохранить");
        saveMenuItem.addActionListener(e -> saveFile());
        fileMenu.add(saveMenuItem);

        // Добавляем на вкладку "Сегодня"
        todayTab.add(topPanel, BorderLayout.NORTH);
        todayTab.add(new JScrollPane(taskTable));

        // Добавляем на вкладку "Завтра"
        tomorrowTab.add(tomorrowTopPanel, BorderLayout.NORTH);
        tomorrowTab.add(new JScrollPane(tomorrowTaskTable));

        setLayout(new BorderLayout());
        add(tabbedPane, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        // Обработчики событий для кнопок сегодня
        setButtonListeners(topPanel, taskTable, tableModel, taskManager);

        // Обработчики событий для кнопок завтра
        setButtonListeners(tomorrowTopPanel, tomorrowTaskTable, tomorrowTableModel, taskManager);
    }

    private JPanel makeTab(String name) {
        JPanel tab = new JPanel();
        tab.setLayout(new BorderLayout());
        tab.setBorder(BorderFactory.createTitledBorder(name));
        return tab;
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));

        JButton addButton = new JButton("Добавить");
        JButton markCompletedButton = new JButton("Пометить выполненной");
        JButton removeButton = new JButton("Удалить");

        topPanel.add(addButton);
        topPanel.add(markCompletedButton);
        topPanel.add(removeButton);

        return topPanel;
    }
    private void openFile() {
        FileDialog fileDialog = new FileDialog(this, "Выберите файл", FileDialog.LOAD);
        fileDialog.setVisible(true);

        String directory = fileDialog.getDirectory();
        String fileName = fileDialog.getFile();

        if (directory != null && fileName != null) {
            File selectedFile = new File(directory, fileName);
            try {
                BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    String description = parts[0];
                    boolean completed = Boolean.parseBoolean(parts[1]);
                    Task task = new Task(description);
                    task.markCompleted(completed);
                    taskManager.getTasks().add(task);
                }
                reader.close();
                updateTaskTable(taskTable, tableModel, taskManager);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Ошибка при чтении файла", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void saveFile() {
        FileDialog fileDialog = new FileDialog(this, "Сохранить файл", FileDialog.SAVE);
        fileDialog.setVisible(true);

        String directory = fileDialog.getDirectory();
        String fileName = fileDialog.getFile();

        if (directory != null && fileName != null) {
            // Добавляем расширение .txt, если его нет
            if (!fileName.toLowerCase().endsWith(".txt")) {
                fileName += ".txt";
            }

            File selectedFile = new File(directory, fileName);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile))) {
                taskManager.saveTasksToFile(selectedFile.getAbsolutePath());
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Ошибка при записи файла", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



    private void setButtonListeners(JPanel topPanel, JTable currentTable, DefaultTableModel currentModel, TaskManager currentTaskManager) {
        JButton addButton = (JButton) topPanel.getComponent(0);
        JButton markCompletedButton = (JButton) topPanel.getComponent(1);
        JButton removeButton = (JButton) topPanel.getComponent(2);

        addButton.addActionListener(e -> {
            String desc = JOptionPane.showInputDialog("Описание");
            currentTaskManager.addTask(desc);
            updateTaskTable(currentTable, currentModel, currentTaskManager);
        });

        markCompletedButton.addActionListener(e -> {
            int row = currentTable.getSelectedRow();
            currentTaskManager.toggleTaskCompletion(row);
            updateTaskTable(currentTable, currentModel, currentTaskManager);
        });

        removeButton.addActionListener(e -> {
            int row = currentTable.getSelectedRow();
            currentTaskManager.removeTask(row);
            updateTaskTable(currentTable, currentModel, currentTaskManager);
        });
    }

    private void updateTaskTable(JTable currentTable, DefaultTableModel currentModel, TaskManager currentTaskManager) {
        currentModel.setRowCount(0);

        List<Task> tasks = currentTaskManager.getTasks();

        for (Task task : tasks) {
            Object[] rowData = {task.getDescription(), task.isCompleted() ? "Выполнено" : "Не выполнено"};
            currentModel.addRow(rowData);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ImprovedGUI::new);
    }
}
