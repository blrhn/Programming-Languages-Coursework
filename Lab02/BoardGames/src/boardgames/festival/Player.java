package boardgames.festival;

import java.util.List;

public class Player {
    private final int id;
    private final List<Integer> preferredGames;
    private boolean assigned;
    private double satisfaction;
    // game id
    private int actualID;

    public Player(int id, List<Integer> preferredGames) {
        this.id = id;
        this.preferredGames = preferredGames;
        this.assigned = false;
        this.satisfaction = 0;
        this.actualID = -1;
    }

    public int getId() {
        return id;
    }

    public List<Integer> getPreferredGames() {
        return preferredGames;
    }

    public double getSatisfaction() {
        return satisfaction;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public int getActualID() {
        return actualID;
    }

    public double calculateSatisfaction(int id) {
        if (!preferredGames.contains(id)) {
            return -1;
        }

        return 1.0d / (preferredGames.indexOf(id) + 1);
    }

    public void setSatisfaction(double satisfaction) {
        this.satisfaction = satisfaction;
    }

    public void resetSatisfaction() {
        this.satisfaction = 0;
    }

    public void resetActualID() {
        this.actualID = -1;
    }

    public void assign() {
        assigned = true;
    }

    public void unAssign() {
        assigned = false;
    }

    public void setActualID(int actualID) {
        this.actualID = actualID;
    }
}
