/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clue.card;

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
public class PersonCardTest {
    
    public PersonCardTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
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
     * Test of getid method, of class PersonCard.
     */
    @Test
    public void testGetid() {
        System.out.println("getid");
        PersonCard instance = new PersonCard(0);
        int expResult = 0;
        int result = instance.getid();
        assertEquals(expResult, result);
    }
    
    @Test
    public void testSixCards(){
        
        
    }
    
}
