package boardgames.utils;

import boardgames.festival.Game;
import boardgames.festival.Table;

import java.util.List;

public record TableGamePair(Table table, List<Game> gameList) {}