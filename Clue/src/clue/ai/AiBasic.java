/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clue.ai;

import clue.BoardMappings;
import clue.GameController;
import clue.action.AccuseAction;
import clue.action.Action;
import clue.action.EndTurnAction;
import clue.action.ShowCardAction;
import clue.action.ShowCardsAction;
import clue.action.UnknownActionException;
import clue.card.Card;
import clue.card.PersonCard;
import clue.card.RoomCard;
import clue.card.WeaponCard;
import clue.player.Player;
import clue.tile.Tile;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zemig
 */
public class AiBasic extends Player{
    
    private int targetY;//Y value of the closest room tile.
    private int targetX;////Y value of the closest room tile
    private int x;
    private int y;
    
    private GameController gameController;
    Random rand;
    
    
    public AiBasic(int id) {
        super(id);
        
        gameController = getGameController();
        rand = new Random();
    }
    
    @Override
    public void onUpdate() {//Runs after every event.
       
        //Generating Random Cards (ids).
        PersonCard randPersonCard = new PersonCard(rand.nextInt(6) + 1);
        RoomCard randRoomCard = new RoomCard(rand.nextInt(6) + 1);
        WeaponCard randWeaponCard = new WeaponCard(rand.nextInt(9)+ 1);
        
        
        if(gameController.getPlayer().getId() == getId()){//If I need to respond.
            if(gameController.getLastAction() instanceof EndTurnAction){//If my turn, last action was end turn.
                if(this.getPosition().isRoom()){//If I'm in a room
                    try {           
                        game.accuse(randPersonCard, randRoomCard, randWeaponCard);
                    } catch (InterruptedException | UnknownActionException ex) {
                        Logger.getLogger(AiBasic.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
            } else if (gameController.getLastAction() instanceof ShowCardsAction){//If I need to show a card 

                ShowCardsAction action = (ShowCardsAction) gameController.getLastAction();
                Card card = action.getCardList().get(0);

                try {
                    gameController.showCard(card);//Show Card.
                }catch (UnknownActionException | InterruptedException ex) {
                    Logger.getLogger(AiBasic.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    /*This  method will set the 
    *
    */
    public void findNewPath(){//Finds path to closest room from players position.
        
        this.x = getPosition().getX();
        this.y = getPosition().getY();
        
        
//        List<Tile> path = BFS(tileMap);
//        
//        targetX = path.get(path.size()-1).getX();//Values of the room tile(last tile).
//        targetY = path.get(path.size()-1).getY();
        
    }
   /**
    * This method finds the closest Tile to the player that is a room.
    *    
    *@param A map of all the tiles.
    *@return The path to the closest Room.
    */
    private List<Tile> findNewRoom(BoardMappings map){//Finds closest room.
     
        //TODO
        List<Tile> path = new ArrayList<Tile>();
        
        return path;
    }
    
    
    
    
    
    
}