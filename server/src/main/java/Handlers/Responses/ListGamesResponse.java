package handlers.Responses;

import model.GameData;

import java.util.HashSet;

record ListGamesResponse (HashSet<GameData> games){}
