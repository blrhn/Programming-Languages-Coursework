package boardgames;

import boardgames.festival.Game;
import boardgames.festival.Player;
import boardgames.festival.Table;
import boardgames.arrangement.GamesArrangement;
import boardgames.arrangement.TableSetup;
import boardgames.utils.DataReader;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // gathering the necessary data from files
        String gamesPath = "resources/games.txt";
        String preferencesPath = "resources/preferences.txt";
        String tablesPath = "resources/tables.txt";

        List<Game> games = DataReader.readGames(gamesPath);
        List<Table> tables = DataReader.readTables(tablesPath);
        List<Player> players = DataReader.readPreferences(preferencesPath);

        if (games == null || tables == null || players == null) {
            System.out.println("Nie mozna bylo poprawnie wczytac gier i/lub stolikow i/lub preferencji graczy");
            return;
        }

        // reading the weights for the satisfaction criteria
        System.out.println("Podaj wagi w1, w2, w3 do kryterium: ");
        Scanner in = new Scanner(System.in);
        int w1 = in.nextInt();
        int w2 = in.nextInt();
        int w3 = in.nextInt();

        GamesArrangement gamesArrangement = new GamesArrangement(games, tables, players, w1, w2, w3);
        List<TableSetup> preparedGameFestival = gamesArrangement.arrange();

        // showing the results of the arrangement
        System.out.println("Wynik przydzialu: " + gamesArrangement.calculateScore());

        for (TableSetup setup : preparedGameFestival) {
            System.out.print("Przy stoliku: " + setup.getTableId() + " graja gracze: ");
            for (Player player : setup.getPlayers()) {
                System.out.print(player.getId() + " ");
            }
            System.out.println();
            System.out.print("W gre/gry: ");
            for (Game game : setup.getGames()) {
                System.out.print(game.getId() + " ");
            }
            System.out.println();
        }
    }
}
