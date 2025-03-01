package boardgames.festival;

public class Game {
    private final int id;
    private final int minPlayers;
    private final int maxPlayers;
    private boolean isTaken;

    public Game(int id, int minPlayers, int maxPlayers) {
        this.id = id;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.isTaken = false;
    }

    public int getId() {
        return id;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public boolean isTaken() {
        return isTaken;
    }

    public void takeGame() {
        isTaken = true;
    }
}
