package com.example.game;

import java.util.List;

public class GameData {
    public Player player;
    public Goal goal;
    public List<Obstacle> obstacles;
    public String status;
    public String alertMessage;
    public GameData(Player player, Goal goal, List<Obstacle> obstacles, String status, String alertMessage) {
        this.player = player;
        this.goal = goal;
        this.obstacles = obstacles;
        this.status = status;
        this.alertMessage = alertMessage;
    }
}
