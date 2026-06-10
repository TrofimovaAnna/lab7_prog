package server;

import shared.model.Ticket;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class CollectionManager {
    private final List<Ticket> collection = new LinkedList<>();
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final DatabaseManager db;

    public CollectionManager(DatabaseManager db) {
        this.db = db;
        loadFromDb();
    }

    private void loadFromDb() {
        rwLock.writeLock().lock();
        try {
            collection.addAll(db.loadAllTickets());
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public boolean add(Ticket ticket, String owner) {
        try {
            long dbId = db.saveTicket(ticket, owner);
            if (dbId == -1) return false;

            ticket.setId(dbId);
            ticket.setOwnerUsername(owner);

            rwLock.writeLock().lock();
            try { collection.add(ticket); }
            finally { rwLock.writeLock().unlock(); }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void clear(String owner) {
        rwLock.writeLock().lock();
        try {
            db.clearByOwner(owner);
            collection.removeIf(t -> owner.equals(t.getOwnerUsername()));
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public boolean removeFirst(String owner) {
        rwLock.writeLock().lock();
        try {
            if (!collection.isEmpty() && db.isOwner(collection.get(0).getId(), owner)) {
                collection.remove(0);
                return true;
            }
            return false;
        } finally { rwLock.writeLock().unlock(); }
    }

    public List<Ticket> getCollection() {
        rwLock.readLock().lock();
        try { return new ArrayList<>(collection); }
        finally { rwLock.readLock().unlock(); }
    }

    public String getInfo() {
        rwLock.readLock().lock();
        try {
            return "Тип: " + collection.getClass().getSimpleName() +
                    "\nКоличество элементов: " + collection.size();
        } finally { rwLock.readLock().unlock(); }
    }

    public Ticket getMaxByPrice() {
        rwLock.readLock().lock();
        try {
            return collection.stream().max(Comparator.comparing(Ticket::getPrice)).orElse(null);
        } finally { rwLock.readLock().unlock(); }
    }

    public Ticket getMinByVenue() {
        rwLock.readLock().lock();
        try {
            return collection.stream()
                    .filter(t -> t.getVenue() != null)
                    .min(Comparator.comparing(t -> t.getVenue().getName()))
                    .orElse(null);
        } finally { rwLock.readLock().unlock(); }
    }

    public boolean update(long id, Ticket newTicket, String owner) {
        // сначала проверка прав и потом пытаемся обновить бд
        if (!db.isOwner(id, owner)) return false;

        try {
            if (!db.updateTicket(id, newTicket)) return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // при успехе обновляем объект в памяти
        rwLock.writeLock().lock();
        try {
            for (int i = 0; i < collection.size(); i++) {
                if (collection.get(i).getId() == id) {
                    collection.set(i, newTicket);
                    return true;
                }
            }
            return false;
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    public boolean removeById(long id, String owner) {
        // проверка прав и удаление из бд
        if (!db.isOwner(id, owner)) return false;

        try {
            if (!db.removeTicket(id)) return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // только при успехе сохранения в бд удаляем из памяти
        rwLock.writeLock().lock();
        try {
            return collection.removeIf(t -> t.getId() == id);
        } finally {
            rwLock.writeLock().unlock();
        }
    }
}