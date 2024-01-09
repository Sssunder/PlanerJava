import java.util.ArrayList;
import java.util.List;

public class DayPlanner {
    private List<Task> tasks;

    public DayPlanner() {
        tasks = new ArrayList<>();
    }

    public void addTask(Task task) {
        tasks.add(task);
    }

    public void markTaskCompleted(int index) {
        if (index >= 0 && index < tasks.size()) {
            tasks.get(index).markCompleted();
        }
    }

    public List<Task> getTasks() {
        return tasks;
    }
}
