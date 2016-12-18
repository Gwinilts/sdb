/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sdb;

/**
 *
 * @author gwinilts
 */
public class Sdb {

    public static long leftmask = 0xFFFF0000L;
    public static long rightmask = 0x0000FFFFL;



    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Controller myController = new Controller("TestInstance");
        System.out.println("Yeah, so it is working...");
        myController.addTable("some", "name String, name Integer, name String");
    }
}
