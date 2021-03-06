/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clue;

import clue.tile.NoSuchRoomException;
import clue.tile.NoSuchTileException;
import clue.card.RoomCard;
import clue.tile.Room;
import clue.tile.SpecialTile;
import clue.tile.Tile;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.regex.Pattern;

/**
 * Used to build the board from csv files. Also maps x y coordinates to tiles, allowing other classes to get a tile from x y coordinate
 * Build up adjacency lists for tiles from the csv for adjacent standard tiles. Doors enable an adjacency to be added between a standard tile and a room (they also allow room to room for shortcuts)
 * @author Malter
 */
public final class BoardMappings {
    
    private Tile[][] mappings;
    private PriorityQueue<StartingTile> startingTilesPQ;
    private LinkedList<Tile> startTiles;
    private Room[] rooms;
    private int boardWidth;
    private int boardHeight;
    
    class StartingTile implements Comparable<StartingTile>{

        
        public Tile t;
        public int i;
        
        /**
         * Creates a starting tile
         * @param t the tile 
         * @param i the id of the starting tile
         */
        StartingTile(Tile t, int i){
            this.t = t;
            this.i = i;
        }

        @Override
        public int compareTo(StartingTile t) {
            if (this.i > t.i){
                return 1;
            }
            else if (this.i < t.i){
                return -1;
            }    
            else {
                return 0;
            }    
        }
    }

    /**
     * BoardMappings will create the board (the Tiles and there adjacencies) from csv files and provide mappings from (int x, y) coordinates to tiles/rooms.
     * Required key for csv files is as follows: 
     *      tile csv: each cell in the csv represents one tile at the same location
     *          0 = basic tile
     *          -1 = no tile
     *          S = starting tile (optionally it can be followed by a number greater than 0)
     *          int N greater than 0 = room with id N-1
     *          I = intrigue tile
     *
     *      door csv: each row represents one door
     *          index 0: the room id that the door leads to
     *          index 1: the x coordinate of the tile that leads to that room
     *          index 2: the y coordinate of the tile that leads to that room
     *          index 3: the direction the player has to take to go from the room to the outside tile (U or D or L or R)
     *          *Note, doors cannot exist between two rooms, instead addShortcut should be used
     * 
     * @param tileRoomLayoutPath the path of the csv file that contains the tile information
     * @param doorLocationsPath the path of the csv file that contains the door information
     * @throws NoSuchRoomException thrown when the map failed to generate because it expected a room but it did not exist (due to being provided a bad csv pair)
     * @throws NoSuchTileException thrown when the map failed to generate because it expected a tile but it did not exist (due to being provided a bad csv pair)
     * @throws clue.MissingRoomDuringCreationException thrown when room with id N was found, but a no id for some id less than N was found
     */
    public BoardMappings(String tileRoomLayoutPath, String doorLocationsPath) throws NoSuchRoomException, NoSuchTileException, MissingRoomDuringCreationException{

        startTiles = new LinkedList<>();
        startingTilesPQ = new PriorityQueue<>();
        
        ArrayList<ArrayList<String>> tiles = loadCsv2D(tileRoomLayoutPath);
        calculateBoardWidthHeight(tiles);//sets the values of boardWidth and boardHeight
        paddWithEmpty(tiles);//pads the 2d string representation of the tile csv
        int roomCount = getRoomCount(tiles);
        
        //System.out.println(boardWidth+","+boardHeight);
        rooms = loadRooms(roomCount);
        mappings = createTileMappings(tiles, roomCount);

        List<Door> doorLocations = loadCsvDoors(doorLocationsPath);
        //System.out.println(this);
        addDoorsToTileAdjacencies(doorLocations);
        
        if (tileRoomLayoutPath.equals("Maps/archersAvenue/archersAvenueTiles.csv")){
            addShortcut(0,8);
            addShortcut(6,2);
        }
        


        
        //----------------debugging prints below--------------------------//
        //for (Tile[] tArr : mappings){        
        //    for (Tile t : tArr){
        //        if (t != null){
        //            System.out.print((t.getX()+1)+","+(t.getY()+1)+": ");
        //        }
        //        else{continue;}
        //        for (Tile aj : t.getAdjacent()){
        //            if (aj != null){   
        //                System.out.print((aj.getX()+1)+","+(aj.getY()+1)+","); 
        //            }
        //        }   
        //        System.out.println();
        //    }
        //}
        
        //System.out.println("Rooms");
        //for (Tile t : rooms){        
        //    System.out.print("id_"+(t.getY())+": ");
        //    for (Tile aj : t.getAdjacent()){
        //        System.out.print((aj.getX())+","+(aj.getY()));
        //    }
        //    System.out.println();
        //}  
    }    
    
    /**
     * Calculates the width and the height of the board from the loaded csv tile data
     * @param tiles the 2d representation of the tiles csv
     * 
     */
    public void calculateBoardWidthHeight(ArrayList<ArrayList<String>> tiles){
        
        int maxWidth = -1;
        int lastRowWithValue = -1;
        
        int rowWidth = 0;
        for (int i = 0; i < tiles.size(); i++){
            ArrayList<String> row = tiles.get(i);
            rowWidth = row.size();
            for (int j = row.size() - 1; j >= 0; j--){
                //reduce the width of the row by the number of empty cells after the first non empty cell
                if (row.get(j).equals("")){
                    rowWidth--;
                }
                else{//stop reducing count, non empty cell found
                    lastRowWithValue = i;//value was found so update last row with value indicator
                    break;
                }
            }
            if (maxWidth < rowWidth){
                maxWidth = rowWidth;
            }    
        
        }

        
        
        
        boardWidth = maxWidth;
        boardHeight = lastRowWithValue+1;
    
    }
    
    /**
     * Pads the 2d representation of the tile csv with empty tile representation
     * @param tiles the 2d representation of the tiles csv
     */
    public void paddWithEmpty(ArrayList<ArrayList<String>> tiles){
        for (int i = 0; i < tiles.size(); i++){
            ArrayList<String> row = tiles.get(i);
            while (row.size() < boardWidth){
                row.add("-1");
            }
        } 
    }
        
        
    
    
    /**
     * loads the data from the csv file for the tiles maintaining the 2d structure of the csv
     * @param path the path of the csv file
     * @return 2d list of strings which represents the data which was in the csv file
     */
    public ArrayList<ArrayList<String>> loadCsv2D(String path){
        
        ArrayList<ArrayList<String>> csvData = new ArrayList<>();
        try{
            
            BufferedReader br = new BufferedReader(new FileReader(path));//open file      
            ArrayList<String[]> lines = new ArrayList<>();            
            String thisLine;
            
            while((thisLine = br.readLine()) != null){//read all lines from csv
                lines.add(thisLine.split(","));
            }
            
            ArrayList<String> rowBuffer;
            for (String[] row : lines){//store ourput from csv into 2d arraylist
                rowBuffer = new ArrayList<>();
                for (String cell : row){  
                    //System.out.println("b"+cell);
                    cell = cell.replaceAll("[^0-9A-Z-]+", "");
                    //System.out.println("a"+cell);
                    rowBuffer.add(cell);
                }
                csvData.add(rowBuffer);
            }    
        }
        catch(FileNotFoundException e){
            System.out.println(e);
        } 
        catch (IOException ex) { 
            System.out.println(ex);
        }
        return csvData;
    }
    
    /**
     * Gets all of the rooms
     * @return the rooms
     */
    public Room[] getRooms(){
        return rooms;
    }
    
    
    /**
     * 
     * @param tiles the string cell values loaded from the tiles csv
     * @return the max room id in the tiles 2d list
     * @throws MissingRoomDuringCreationException thrown when room with id N was found, but a no id for some id less than N was found
     */
    public int getRoomCount(ArrayList<ArrayList<String>> tiles) throws MissingRoomDuringCreationException{
        int roomId;
        int maxRoomId = -1;
        ArrayList<Integer> roomIds = new ArrayList<>();
        for (ArrayList<String> row : tiles){
            for (String tile : row){
            
                try {
                    
                    roomId = Integer.parseInt(tile);
                    if (roomId > 0 && !roomIds.contains(roomId)){
                    //then it is a room id and not a standard tile / special tile
                    // do not add the id if it is allready in the list
                        roomIds.add(new Integer(roomId));
                        if (roomId > maxRoomId){
                            maxRoomId = roomId;
                        }
                    }
                    
                    
                }
                catch (NumberFormatException e){
                
                }
            }
        }
        //System.out.println(roomIds);
        for (int i = 1; i < roomIds.size(); i++){
            if (!roomIds.contains(i)){
                throw new MissingRoomDuringCreationException(maxRoomId+" room id was found, but there is a missing room number: "+i);
            }
        }
        return maxRoomId;
    }
    /**
     * gets Room object of room given the room id
     * @param roomId the id of the room to be fetched
     * @return room object that matches the given id
     * @throws NoSuchRoomException when the room id does not match a valid room
     */
    public Room getRoom(int roomId) throws NoSuchRoomException{

        //"trying to get roomid: "+roomId);
        if (roomId < rooms.length && roomId >= 0){
            return rooms[roomId];
        }
        throw new NoSuchRoomException("roomId: "+roomId);

    }
    /**
     * gets the tile that is associated with the given x y coordinate
     * this method can be used to get a room tile if you give it the Room.x (-1) and Room.y (roomId) values
     * @param x the x coordinate of the tile
     * @param y the y coordinate of the tile
     * @return the tile at the given x y coordinate
     * @throws NoSuchRoomException this is thrown when trying to get a room tile that doesn't exist
     */
    public final Tile getTile(int x, int y) throws NoSuchRoomException, ArrayIndexOutOfBoundsException{
        if (x >= 0 && x < mappings[0].length && y >= 0 && y < mappings.length){//trying to get a non room tile when x >=0
            
            for (Room room : rooms){//try to pair the x y coord with a room location
                for (int[] loc : room.getLocations()){
                    if ( x == loc[0] && y == loc[1]){
                        return room;
                    }          
                }
            }
            
            
            return mappings[y][x];
        }
        else if ( x == -1 && y >= 0){//trying to get a room tile using room id
            return getRoom(y);  
        }
        else{
            throw new ArrayIndexOutOfBoundsException("Tile.getTile was called with an illegal x,y: "+x+","+y);
        }
    }

    /**
     * loads the Door objects from the csv file
     * @param path the path of the csv file
     * @return list of Door objects 
     * @throws clue.tile.NoSuchTileException thrown when a door in the csv refers to a tile that was not found
     * @throws clue.tile.NoSuchRoomException thrown when a door in the csv refers to a room that was not found
     */
    public ArrayList<Door> loadCsvDoors(String path) throws NoSuchTileException, NoSuchRoomException {
        ArrayList<Door> doors = new ArrayList<>();
        ArrayList<ArrayList<String>> csvData = loadCsv2D(path);
        Door door;
        for (ArrayList<String> row : csvData){
            if (row.get(0).equals("")){
                continue;
            }
            try{
                if (Integer.parseInt(row.get(0).replaceAll("[^0-9]+", ""))-1 < getRooms().length){
                    door = new Door(
                            Integer.parseInt(row.get(0).replaceAll("[^0-9]+", ""))-1, 
                            Integer.parseInt(row.get(1).replaceAll("[^0-9]+", "")), 
                            Integer.parseInt(row.get(2).replaceAll("[^0-9]+", "")),
                            row.get(3).replaceAll("[^UDLR]", ""));
                    doors.add(door);
                }
                else{
                    throw new NoSuchRoomException("Attempted to create door to invalid room with csv value: "+Integer.parseInt(row.get(0).replaceAll("[^0-9]+", "")));
                }
                
            }
            catch (NumberFormatException ex){
                throw new NoSuchTileException("Attempted to create door (from door csv) with: roomid = "+row.get(0) + "tile.x = "+row.get(1)+" tile.y = "+row.get(2));
            }
            
        }
        return doors;
    }

    /**
     * creates the Room objects for the board
     * @param roomCount number of rooms to create
     * @return Room[] list of rooms created 
     */
    public Room[] loadRooms(int roomCount) {
        Room [] loadedRooms = new Room[roomCount];
        for (int i = 0; i < roomCount; i++){
            loadedRooms[i] = new Room(new RoomCard(i));
        }
        return loadedRooms;
    }

    /**
     * Creates the mappings for non room / non empty tiles, maps int coordinates to the index of the array
     * @param tiles the 2d representation of the board (outputted by loadCsv)
     * @param roomCount the number of rooms
     * @return Tile[][] the tile mappings of the board, rooms and empty tiles will be null in this structure
     * @throws NoSuchRoomException thrown when it tried to fetch a room that didn't exist
     */
    public Tile[][] createTileMappings(ArrayList<ArrayList<String>> tiles, int roomCount) throws NoSuchRoomException {
        
        
        Tile[][] localMappings = new Tile[boardHeight][boardWidth];      
        String cell;      
        for (int y = 0; y < boardHeight; y++){//create Tile objects in mappings, object may be tile,room,special or null depending on what the csv cell was
            for (int x = 0; x < boardWidth; x++){
                

                cell = tiles.get(y).get(x);
                switch (cell) {
                    case "":
                        localMappings[y][x] = new Tile(-1,-1);
                        break;
                    case "-1":
                        localMappings[y][x] = new Tile(-1,-1);
                        break;
                    case "0":
                        localMappings[y][x] = new Tile(x,y);
                        break;
                    case "I":
                        localMappings[y][x] = new SpecialTile(x,y);
                        //tiles.get(y).set(x, "0");
                        break;
                    case "S":
                        localMappings[y][x] = new Tile(x,y);
                        startingTilesPQ.add( new StartingTile(localMappings[y][x],100));
                        break;
                    default:

                        if (cell.startsWith("S")){
                        
                            cell = cell.substring(1);
                            
                            try{
                                int startingId = Integer.parseInt(cell);
                                if (startingId < 0 || startingId > 99){
                                    throw new NumberFormatException("values in tiles csv must be -1,0,I,Sn (where n is a number between 0 and 99), or a number that is less than the total room count, found: "+cell+"\n");
                            
                                }
                                localMappings[y][x] = new Tile(x,y);
                                startingTilesPQ.add(new StartingTile(localMappings[y][x], startingId));
                            }
                            catch (NumberFormatException ex){
                                throw new NumberFormatException("values in tiles csv must be -1,0,I,Sn (where n is a number between 0 and 99), or a number that is less than the total room count, found: "+cell+"\n");
                            
                            }
                        }
                        else{
                            cell = cell.replaceAll("[^0-9]+", "");
                            localMappings[y][x] = new Tile(-1,-1);

                            try{
                                if (Integer.parseInt(cell) > roomCount){
                                    throw new NoSuchRoomException();
                                }
                                else{
                                    getRoom(Integer.parseInt(cell)-1).addLocation(x,y);
                                }
                            }
                            catch (NumberFormatException ex){
                                throw new NumberFormatException("values in tiles csv must be -1,0,I,Sn (where n is a number between 0 and 99), or a number that is less than the total room count, found: "+cell+"\n");
                            }
                            //break;
                        }

                }
   
            }
        }
        
        Tile currentTile;
        String nonRoomTilePattern = "S|0|I|S[0-9]";
        for (int y = 0; y < boardHeight; y++){
            for (int x = 0; x < boardWidth; x++){
                
                

                //if (currentTile != null){
                    //System.out.println(currentTile.getX()+","+currentTile.getY()+","+x+","+y);
                //}
                if (Pattern.matches(nonRoomTilePattern, tiles.get(y).get(x))){//if current tile is a standard tile
                    currentTile = localMappings[y][x];
                    if (currentTile.getY() != -1 || currentTile.getX() != -1){//should allways be non null
                        //System.out.println(currentTile != null);
                        if (x>0){//there is a left tile
                            if (Pattern.matches(nonRoomTilePattern, tiles.get(y).get(x-1))){ //if tile to the left is also a standard tile
                                currentTile.addAdjacentBoth(localMappings[y][x-1]);
                            }
                        } 
                        
                       
                        
                        
                        
                        //if (x<boardWidth-1){//if there is a right tile
                        //    if (Pattern.matches(nonRoomTilePattern, tiles.get(y).get(x+1))){ ///if tile to the right is also a standard tile
                        //        currentTile.addAdjacent(localMappings[y][x+1]);
                        //    }
                        //}
                        //if (y>0){//if there is a above tile
                        //    if (Pattern.matches(nonRoomTilePattern, tiles.get(y-1).get(x))){ //if tile above is also a standard tile
                        //        currentTile.addAdjacent(localMappings[y-1][x]);
                        //    }
                        //}
                        if (y<boardHeight-1){//if there is a tile below
                            if (Pattern.matches(nonRoomTilePattern, tiles.get(y+1).get(x))){ //if tile below is also a standard tile
                                currentTile.addAdjacentBoth(localMappings[y+1][x]);
                            }
                        }    
                    }
                    else{
                        //(currentTile.getY() == -1 || currentTile.getX() == -1)+",,,,,,,,,,,"+currentTile.getX()+","+currentTile.getY());
                        throw new NullPointerException("[BoardMappings.createTileMappings]@mw434 FATAL ERROR CODED WRONG");
                    
                    }
 
                }   
            }
        }
        
        
        return localMappings;
    }

    /**
     * each Door object will add an equivalent adjacency to the tiles associated for that Door to allow movement between a basic Tile and a Room
     * @param doorLocations
     * @throws NoSuchRoomException
     * @throws NoSuchTileException 
     */
    private void addDoorsToTileAdjacencies(List<Door> doorLocations) throws NoSuchRoomException, NoSuchTileException {
        Tile outside;
        Tile room;
        int[] loc = new int[3];
        for (Door door: doorLocations){
            
            try {
                room = getTile(-1, door.getRoomY());
                //System.out.println("R"+room);
                outside = getTile(door.getTileX(), door.getTileY());
                //System.out.println("O"+outside);
                room.addAdjacentBoth(outside);
                
                switch (door.getDir()) {
                    
                    case "U"://the tile is above the room
                        loc[0] = outside.getX();
                        loc[1] = outside.getY()+1;
                        loc[2] = 1;
                        
                        break;
                    case "R":
                        loc[0] = outside.getX()-1;
                        loc[1] = outside.getY();
                        loc[2] = 2;
                        break;
                    case "D"://the tile is below the room
                        loc[0] = outside.getX();
                        loc[1] = outside.getY()-1;
                        loc[2] = 3;
                        break;   
                        
                    case "L":
                        loc[0] = outside.getX()+1;
                        loc[1] = outside.getY();
                        loc[2] = 4;
                        break;    

                    default:
                        loc[0] = -1;
                        loc[1] = -1;
                        loc[2] = -1;
                        break;
                }
                ((Room)room).addDoorLocation(loc);
                
                
                
            } catch (NoSuchRoomException ex) {
                throw ex;
            }
            catch (NullPointerException ex){
                throw new NoSuchTileException("door csv file contains an illegal door, the outside tile index (1,2) must be the x y coordinates of a base tile or special tile");
            }
            catch (ArrayIndexOutOfBoundsException ex){
                throw new NoSuchTileException(ex.toString());
            }
        }
        
        for (Room r : rooms){
            r.removeDoorLocationsFromDrawingLocations();
        }
    }
     /**
      * gets the starting locations of the board
      * @return the list of starting tiles on the board
      */
    public LinkedList<Tile> getStartingTiles(){
        if (startTiles.isEmpty()){
            while (!startingTilesPQ.isEmpty()){
                startTiles.add(startingTilesPQ.poll().t);
            }
        }
        return startTiles;
    }  
    
    /**
     * adds a shortcut between two rooms
     * @param r1 the id of one of the rooms in the shortcut
     * @param r2 the id of the other of the rooms in the shortcut
     * @throws NoSuchRoomException thrown when room with a given id cannot be found
     */
    public void addShortcut(int r1, int r2) throws NoSuchRoomException{
        Room room1 = (Room)getTile(-1,r1);
        Room room2 = (Room)getTile(-1,r2);
        
        room1.addAdjacentBoth(room2);
    }
    
    @Override
    public String toString(){
        String res = "";
        for (int y = 0; y < boardHeight; y++){
            for (int x = 0; x < boardWidth; x++){
                res+= mappings[y][x].getY()+" ";
            }
            res+= "\n";
        }
        return res;
    
    }
    
    public int getBoardWidth(){
        return boardWidth;
    }
    
    public int getBoardHeight(){
        return boardHeight;
    }
}
