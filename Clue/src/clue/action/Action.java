/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clue.action;

import clue.player.Player;

/**
 * Represents a Player taking an action in the game.
 * @author slb35
 */
public abstract class Action {

    /**
     * the Player initiating the Action
     */
    public Player player;
    /**
     * Provides access to the type of action being taken.
     */
    public ActionType actionType = ActionType.DEFAULT;
    /**
     * Stores whether or not the execute() method was permitted in the game rules.
     */
    public boolean result;

    /**
     * Creates an instance of the Action
     * @param player the player taking the action
     */
    public Action(Player player) {
        this.player = player;
    }

    /**
     * gets the Player object associated with this Action
     * @return the Player taking the action
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Simulates the action being taken. The success of the action is stored in
     * result.
     */
    public void execute() {
    }
}
