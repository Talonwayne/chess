package handlers.Responses;

import model.GameData;

import java.util.HashSet;

public record ListGamesResponse (HashSet<GameData> games){}
