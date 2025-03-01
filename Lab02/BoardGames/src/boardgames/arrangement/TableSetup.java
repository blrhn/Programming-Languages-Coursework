package boardgames.arrangement;

import boardgames.festival.Game;
import boardgames.festival.Player;
import boardgames.festival.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableSetup {
    private Table table;
    private List<Game> games;
    private final List<Player> players;
    // stores how many players got assigned to a certain game
    private final Map<Game, Integer> playersStatus;

    public TableSetup() {
        this.games = new ArrayList<>();
        this.players = new ArrayList<>();
        this.playersStatus = new HashMap<>();
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    public void addPlayers(Player player) {
        this.players.add(player);
    }

    public void removePlayers(List<Player> invalidPlayers) {
        players.removeAll(invalidPlayers);
    }

    public void initializeTracking(List<Game> games) {
        for (Game game : games) {
            playersStatus.put(game, 0);
        }
    }

    public void keepTrackOfPlayers(Game game, int value) {
        playersStatus.put(game, playersStatus.get(game) + value);
    }

    public int getPlayersNumber(Game game) {
        return playersStatus.getOrDefault(game, 0);
    }

    public List<Player> getPlayers() {
        return players;
    }


    public List<Game> getGames() {
        return games;
    }

    public double sumSatisfaction() {
        double satisfaction = 0;
        for (Player player : players) {
            satisfaction += player.getSatisfaction();
        }

        return satisfaction;
    }

    public int getTableId() {
        return table.getId();
    }

}
