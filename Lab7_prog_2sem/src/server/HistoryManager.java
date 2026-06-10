package server;

import java.util.LinkedList;
import java.util.List;

public class HistoryManager {
    private final LinkedList<String> history = new LinkedList<>();
    private static final int MAX_SIZE = 13;

    public void addCommand(String cmd) {
        history.addLast(cmd);
        if (history.size() > MAX_SIZE) history.removeFirst();
    }

    public List<String> getHistory() {
        return new LinkedList<>(history);
    }
}