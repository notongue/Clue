/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clue.ai;

import clue.GameController;
import clue.card.Card;
import clue.card.RoomCard;
import clue.tile.Room;
import clue.tile.Tile;
import java.util.ArrayList;
import java.util.LinkedList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author steve
 */
public class AiAdvancedTest {
    
    private static GameController gc;
    public AiAdvancedTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        gc = new GameController(1,1,"testCsv/tiles1.csv", "testCsv/doors1.csv");
    
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of onUpdate method, of class AiAdvanced.
     */
    @Test
    public void testOnUpdate() {
        System.out.println("onUpdate");
        AiAdvanced instance = null;
        instance.onUpdate();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of makeLists method, of class AiAdvanced.
     */
    @Test
    public void testMakeLists() {
        System.out.println("makeLists");
        AiAdvanced instance = null;
        instance.makeLists();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of moveToRoom method, of class AiAdvanced.
     */
    @Test
    public void testMoveToRoom() {
        System.out.println("moveToRoom");
        AiAdvanced instance = null;
        instance.moveToRoom();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getLists method, of class AiAdvanced.
     */
    @Test
    public void testGetLists() {
        System.out.println("getLists");
        AiAdvanced instance = null;
        ArrayList<ArrayList<Card>> expResult = null;
        ArrayList<ArrayList<Card>> result = instance.getLists();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getPathToRoom method, of class AiAdvanced.
     */
    @Test
    public void testGetPathToRoom() {
        System.out.println("getPathToRoom");
        AiAdvanced instance = null;
        LinkedList<Tile> expResult = null;
        LinkedList<Tile> result = instance.getPathToRoom();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
    
    /**
     * Test of BFS method, of class AiAdvanced.
     */
    @Test
    public void testBFS() throws InterruptedException{//Trying to find a room.
        System.out.println("BFS (one valid path to room)");
        //--Layout of map--
        // t1 t2 t3
        // t4    t6
        //       r1
        
        LinkedList<Tile> predictedPath = new LinkedList<>();
        
        Tile t1 = new Tile(0, 0);
        Tile t2 = new Tile(1, 0);
        Tile t3 = new Tile(2, 0);
        Tile t4 = new Tile(0, 1);
        Room r1 = new Room(new RoomCard(1));
        Tile t6 = new Tile(2, 1);
   
        t1.addAdjacentBoth(t2);
        t1.addAdjacentBoth(t4);
        t2.addAdjacentBoth(t3);
        t3.addAdjacentBoth(t6);
        t6.addAdjacentBoth(r1);
        
        assertTrue(t1.isAdjacent(t2));
        assertTrue(t2.isAdjacent(t3));
        assertTrue(t3.isAdjacent(t6));
        assertTrue(t6.isAdjacent(r1));
        
        LinkedList<Tile> expectedPath = new LinkedList<>();
        expectedPath.add(t1);
        expectedPath.add(t2);
        expectedPath.add(t3);
        expectedPath.add(t6);
        expectedPath.add(r1);
        
        
        AiAdvanced p1 = new AiAdvanced(1, gc, 3, 3);
        
        p1.setPosition(t1);
        
        LinkedList<Tile> solutionPath;
        solutionPath = p1.BFS();
        
        //System.out.println("solution path");
        //for(Tile tile: solutionPath){
        //    System.out.println(tile.getX()+ " " + tile.getY());
        //}
        
        assertEquals(1 ,((Room) p1.BFS().getLast()).getId());
        assertEquals(expectedPath, solutionPath);
        
    }
    

    /**
     * Test of makeLists method, of class AiAdvanced.
     */
    @Test
    public void testBFS2() throws InterruptedException{//Testing it on a board with no room.
        System.out.println("BFS (no room)");
        //Board Layout
        //t1 t2 t3 t4 
        //t5 t6 t7 t8
        
        Tile t1 = new Tile(0, 0);
        Tile t2 = new Tile(1, 0);
        Tile t3 = new Tile(2, 0);
        Tile t4 = new Tile(3, 0);
        Tile t5 = new Tile(0, 1);
        Tile t6 = new Tile(1, 1);
        Tile t7 = new Tile(2, 1);
        Tile t8 = new Tile(3, 1);
        
        t1.addAdjacentBoth(t2);
        t1.addAdjacentBoth(t5);
        t2.addAdjacentBoth(t3);
        t2.addAdjacentBoth(t6);
        t3.addAdjacentBoth(t4);
        t3.addAdjacentBoth(t7);
        t4.addAdjacentBoth(t8);
        t8.addAdjacentBoth(t7);
        t7.addAdjacentBoth(t6);
        t6.addAdjacentBoth(t5);
        
        
        
        AiAdvanced testPlayer = new AiAdvanced(1, gc, 4, 2);
        testPlayer.setPosition(t1);
        
        LinkedList<Tile> resultPath = testPlayer.BFS();
        
        assertTrue(resultPath.isEmpty());
    }    
    
    

    /**
     * Test of moveToRoom method, of class AiAdvanced.
     */
    @Test
    public void testBFS3() throws InterruptedException{//testing bfs with players as obstacles w/valid route
        System.out.println("BFS (with room, with valid path, with players)");
        //Board Layout
        //t1 t2 t3 t4
        //t5 t6 t7 r8
        
        Tile t1 = new Tile(0, 0);
        Tile t2 = new Tile(1, 0);
        Tile t3 = new Tile(2, 0);
        Tile t4 = new Tile(3, 0);
        Tile t5 = new Tile(0, 1);
        Tile t6 = new Tile(1, 1);
        Tile t7 = new Tile(2, 1);
        Tile r8 = new Room();
        
        t1.addAdjacentBoth(t2);
        t1.addAdjacentBoth(t5);
        t2.addAdjacentBoth(t3);
        t2.addAdjacentBoth(t6);
        t3.addAdjacentBoth(t4);
        t3.addAdjacentBoth(t7);
        t4.addAdjacentBoth(r8);
        r8.addAdjacentBoth(t7);
        t7.addAdjacentBoth(t6);
        t6.addAdjacentBoth(t5);
        
        AiAdvanced testPlayer = new AiAdvanced(1, gc, 4, 2);
        testPlayer.setPosition(t1);
        
        AiAdvanced obstacle1 = new AiAdvanced(2, gc, 4, 2);
        
        obstacle1.setPosition(t2);
         
        LinkedList<Tile> expectedPath = new LinkedList<>();
        
        expectedPath.add(t1);
        expectedPath.add(t5);
        expectedPath.add(t6);
        expectedPath.add(t7);
        expectedPath.add(r8);
          
        LinkedList<Tile> resultPath = testPlayer.BFS();
        
        assertEquals(resultPath, expectedPath);
    }   

    

    /**
     * Test of getLists method, of class AiAdvanced.
     */
    @Test
    public void testBFS4() throws InterruptedException{//testing bfs with players as obstacles W/O Valid route
        System.out.println("BFS (with room, with players)");
        //Board Layout
        //t1 t2 t3 r4
        Tile t1 = new Tile(0, 0);
        Tile t2 = new Tile(1, 0);
        Tile t3 = new Tile(2, 0);
        Tile r4 = new Room();
        
        t1.addAdjacentBoth(t2);
        t2.addAdjacentBoth(t3);
        t3.addAdjacentBoth(r4);
        
        AiAdvanced p1 = new AiAdvanced(1, gc, 4, 1);
        p1.setPosition(t1);
        
        AiAdvanced p2 = new AiAdvanced(2, gc, 4, 1);
        p2.setPosition(t3);
        
        LinkedList<Tile> resultPath = p1.BFS();
        
        assertTrue(resultPath.isEmpty());
    }


    /**
     * Test of getPathToRoom method, of class AiAdvanced.
     */
    @Test
    public void testBFS5() throws InterruptedException{//testing bfs with multiple rooms
        System.out.println("BFS (many rooms)");
        //Board Layout 
        //t1 t2 t3 r4
        //t5
        //r6
        
        Tile t1 = new Tile(0, 0);
        Tile t2 = new Tile(1, 0);
        Tile t3 = new Tile(2, 0);
        Tile r4 = new Room();
        Tile t5 = new Tile(0, 1);
        Tile r6 = new Room();
        
        t1.addAdjacentBoth(t2);
        t1.addAdjacentBoth(t5);
        t2.addAdjacentBoth(t3);
        t3.addAdjacentBoth(r4);
        t5.addAdjacentBoth(r6);
        
        LinkedList<Tile> expectedPath = new LinkedList<>();
        
        expectedPath.add(t1);
        expectedPath.add(t5);
        expectedPath.add(r6);
        
        AiAdvanced p1 = new AiAdvanced(1, gc, 4, 3);
        p1.setPosition(t1);
        
        LinkedList<Tile> resultPath = p1.BFS();
        
        assertEquals(expectedPath, resultPath);  
        
    }

    
}
