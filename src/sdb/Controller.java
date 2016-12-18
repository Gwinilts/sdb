/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sdb;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gwinilts
 */
public class Controller extends Thread {
    private Table master;

    public class AccessRequest {
        public Table target;
        public Thread requestor;
        public long requestTime;

        public AccessRequest(Table target, Thread requestor) {
            this.target = target;
            this.requestor = requestor;
            this.requestTime = System.currentTimeMillis();
        }

        @Override
        public boolean equals(Object e) {
            if (!(e instanceof AccessRequest)) return false;
            AccessRequest r = AccessRequest.class.cast(e);
            return this.target.equals(r.target) && this.requestor.equals(r.requestor);
        }
    }

    private volatile ConcurrentHashMap<String, Table> tables;
    private volatile ConcurrentHashMap<Table, LinkedList<AccessRequest>> accessQueue;

    public Controller(String instanceName) {
        try {
            try {
                master = new Table(instanceName);
            } catch (Table.TableNotExistException e) {
                master = new Table(instanceName, new Table.RowDescriptor("ChildTable", Table.FieldType.String));
            }
            //accessQueue = new LinkedList<AccessRequest>();
            tables = new ConcurrentHashMap<String, Table>();
        } catch (Exception e) {

        }
    }

    public boolean hasAccess(Thread t, Table m) {
      return false;
    }

    /**
      arg should be formatted like this:
        name String, age Integer, address String
    **/

    public void addTable(String tableName, String arg) {
      String[] argExp = arg.replaceAll(", ", ",").split(",");
      String[] argDet;
      Table.RowDescriptor[] tArgs = new Table.RowDescriptor[argExp.length];

      for (int i = 0; i < argExp.length; i++) {
        argDet = argExp[i].split(" ");
        tArgs[i] = new Table.RowDescriptor(argDet[0], Table.FieldType.valueOf(argDet[1]));
        System.out.println(tArgs[i].toString());
      }

    }

    /*

    public void openTable(String tableName) {
        Table theTable;
        try {
            //theTable = new Table(tableName);
        } catch (Table.TableNotExistException e) {
            //theTable = new Table(tableName, )
        } catch (Exception ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    */




}
