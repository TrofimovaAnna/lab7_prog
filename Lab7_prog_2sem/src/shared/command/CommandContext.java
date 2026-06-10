package shared.command;
import server.CollectionManager;
import server.HistoryManager;

public class CommandContext {
    private final CollectionManager collectionManager;
    private final HistoryManager historyManager;
    private String currentUser;

    public CommandContext(CollectionManager cm, HistoryManager hm) {
        this.collectionManager = cm;
        this.historyManager = hm;
    }

    public CollectionManager getCollectionManager() { return collectionManager; }
    public HistoryManager getHistoryManager() { return historyManager; }
    public String getCurrentUser() { return currentUser; }
    public void setCurrentUser(String currentUser) { this.currentUser = currentUser; }
}