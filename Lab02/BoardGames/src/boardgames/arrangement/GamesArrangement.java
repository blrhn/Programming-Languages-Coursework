package boardgames.arrangement;

import boardgames.festival.Game;
import boardgames.festival.Player;
import boardgames.festival.Table;
import boardgames.utils.PlayerGamePair;
import boardgames.utils.TableGamePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class GamesArrangement {
    private final List<Game> games;
    private final List<Table> tables;
    private final List<Player> players;
    private final int w1;
    private final int w2;
    private final int w3;
    private final List<TableSetup> bestAssignment;

    public GamesArrangement(List<Game> games, List<Table> tables, List<Player> players, int w1, int w2, int w3) {
        this.games = games;
        this.tables = tables;
        this.players = players;
        this.w1 = w1;
        this.w2 = w2;
        this.w3 = w3;
        this.bestAssignment = new ArrayList<>();
    }

    // arranging board games and players to tables based on seating capacity, number of players and preferences
    public List<TableSetup> arrange() {
        /*
        sort the setup collections, so that:
        - games with higher player counts are first to be arranged, increasing their chances of being played at
        tables with enough seats
        - players with fewer preferred games are first to be arranged, increasing their chances of being assigned
        to any game
        */
        games.sort(Comparator.comparingInt(Game::getMaxPlayers).reversed());
        tables.sort(Comparator.comparingInt(Table::getSeats).reversed());
        players.sort(Comparator.comparingInt(pref -> pref.getPreferredGames().size()));

        for (Table table : tables) {
            // list of games that can be played on the table (there are enough available seats)
            List<Game> gamesForTable = games.stream().filter(g -> (
                    table.getSeats() >= g.getMinPlayers() && g.getMaxPlayers() <= table.getSeats())).toList();

            TableGamePair tableGames = matchTable(table, gamesForTable);

            if (tableGames == null) {
                continue;
            }

            // preparing the collection to store players assigned to each game at the table
            TableSetup tableSetup = new TableSetup();
            List<Game> gamesTemp = tableGames.gameList();

            tableSetup.setGames(gamesTemp);
            tableSetup.initializeTracking(gamesTemp);
            tableSetup.setTable(table);

            // assigning players to each game at the table
            for (Game game : gamesTemp) {
                List<Player> playersForGame = players.stream()
                        .filter(p -> p.getPreferredGames().contains(game.getId()) && !p.isAssigned()).toList();

                for (Player player : playersForGame) {
                    tryToAssign(player, gamesForTable, table, gamesTemp, tableSetup);
                }
            }

            List<Player> invalidPlayers = collectInvalidPlayers(tableSetup, table);
            tableSetup.removePlayers(invalidPlayers);

            bestAssignment.add(tableSetup);
        }

        return bestAssignment;
    }

    /*
    returns the table with all matched games that can be played simultaneously;
    returns null if no games are matched
    */
    private TableGamePair matchTable(Table table, List<Game> gamesForTable) {
        List<Game> matchedGames = new ArrayList<>();

        for (Game game : gamesForTable) {
            if (!game.isTaken() && table.getRemainingSeats() >= game.getMinPlayers()
                    && game.getMaxPlayers() <= table.getRemainingSeats()) {

                int players = Math.min(table.getRemainingSeats(), game.getMaxPlayers());
                table.takeSeatByGame(players);
                game.takeGame();
                matchedGames.add(game);
            }
        }

        if (!matchedGames.isEmpty()) {
            games.removeAll(matchedGames);
            return new TableGamePair(table, matchedGames);
        }

        return null;
    }

    // returns a player game pair with the highest satisfaction score
    private PlayerGamePair matchPlayer(Player player, List<Game> gamesForTable) {
        PlayerGamePair playerGame = null;
        double highestScore = 0;

        for (Game game : gamesForTable) {
            double score = player.calculateSatisfaction(game.getId());

            if (score > highestScore) {
                highestScore = score;
                player.setSatisfaction(score);

                playerGame = new PlayerGamePair(player, game);
            }
        }

        return playerGame;
    }

    /*
    returns a list of players who don't meet the game's requirements
    (e.g. of invalid players: two players are assigned for a three player game)
    */
    private List<Player> collectInvalidPlayers(TableSetup tableSetup, Table table) {
        List<Player> invalidPlayers = new ArrayList<>();

        for (Game game : tableSetup.getGames()) {
            if (tableSetup.getPlayersNumber(game) < game.getMinPlayers()) {

                for (Player player : tableSetup.getPlayers()) {
                    if (player.getActualID() == game.getId()) {
                        tableSetup.keepTrackOfPlayers(game, -1);
                        player.unAssign();
                        player.resetSatisfaction();
                        player.resetActualID();
                        table.addSeats();
                        invalidPlayers.add(player);
                    }
                }
            }
       }

        return invalidPlayers;
    }

    // updates helper variables if a player can play one game available on the table
    private void tryToAssign(Player player, List<Game> gamesForTable, Table table,
                             List<Game> gamesTemp, TableSetup tableSetup) {

        PlayerGamePair playerGame = matchPlayer(player, gamesForTable);
        if (playerGame != null) {
            Game assignedGame = playerGame.game();
            Player assignedPlayer = playerGame.player();
            int currentCount = tableSetup.getPlayersNumber(assignedGame);

            if (table.getSeats() > 0 && gamesTemp.contains(assignedGame)
                    && currentCount < assignedGame.getMaxPlayers() ) {

                table.takeSeats();
                assignedPlayer.assign();
                assignedPlayer.setActualID(assignedGame.getId());
                tableSetup.addPlayers(assignedPlayer);
                tableSetup.keepTrackOfPlayers(assignedGame, 1);
            }
        }
    }

   // returns calculated overall optimisation score based on given criteria
   public double calculateScore() {
       int totalPlayers = 0;
       int totalGames = 0;
       int penalty = 0;
       double totalSatisfaction = 0;
       boolean hasMultipleGames = false;

       for (TableSetup festival : bestAssignment) {
           totalPlayers += festival.getPlayers().size();
           totalSatisfaction += festival.sumSatisfaction();
           int gameCount = festival.getGames().size();

           totalGames += gameCount;

           if (gameCount > 0) {
               hasMultipleGames = true;
           }
       }

       if (hasMultipleGames) {
           penalty = tables.size() - totalGames;
       }

       return w1 * totalPlayers + w2 * totalSatisfaction + w3 * penalty;
   }
}