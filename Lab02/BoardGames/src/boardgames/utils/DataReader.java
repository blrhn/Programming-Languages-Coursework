package boardgames.utils;

import boardgames.festival.Game;
import boardgames.festival.Player;
import boardgames.festival.Table;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public final class DataReader {
    public static List<Game> readGames(String path) {
        List<Game> games = new ArrayList<>();

        File file = new File(path);
        try {
            Scanner readFile = new Scanner(file);
            readFile.nextLine();

            while (readFile.hasNextLine()) {
                String[] line = readFile.nextLine().split("; ");

                int id = Integer.parseInt(line[0]);
                int quantity = Integer.parseInt(line[1]);
                int minPlayers = Integer.parseInt(line[2]);
                int maxPlayers = Integer.parseInt(line[3]);

                for (int i = 0; i < quantity; i++) {
                    games.add(new Game(id, minPlayers, maxPlayers));
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("Nie znaleziono pliku");
            return null;
        }

        return games;
    }

    public static List<Table> readTables(String path) {
        List<Table> tables = new ArrayList<>();

        File file = new File(path);
        try {
            Scanner readFile = new Scanner(file);
            readFile.nextLine();

            while (readFile.hasNextLine()) {
                String[] line = readFile.nextLine().split("; ");

                int id = Integer.parseInt(line[0]);
                int seats = Integer.parseInt(line[1]);

                tables.add(new Table(id, seats));
            }

        } catch (FileNotFoundException e) {
            System.out.println("Nie znaleziono pliku");
            return null;
        }

        return tables;
    }

    public static List<Player> readPreferences(String path) {
        List<Player> players = new ArrayList<>();

        File file = new File(path);
        try {
            Scanner readFile = new Scanner(file);
            readFile.nextLine();

            while (readFile.hasNextLine()) {
                String[] line = readFile.nextLine().split("; ");
                String[] preferenceList = line[1].split(", ");

                List<Integer> preferredGames = new ArrayList<>();

                for (String s : preferenceList) {
                    preferredGames.add(Integer.parseInt(s));
                }
                int id = Integer.parseInt(line[0]);

                players.add(new Player(id, preferredGames));
            }

        } catch (FileNotFoundException e) {
            System.out.println("Nie znaleziono pliku");
            return null;
        }

        return players;
    }
}
