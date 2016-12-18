/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sdb;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Objects;

/**
 *
 * @author gwinilts
 */
public class Table {

    private RandomAccessFile meta;
    private RandomAccessFile strings;
    private RandomAccessFile data;
    private String tableName;
    private RowDescriptor[] rows;
    private int recordSize ;

    public static enum FieldType {
        String (1),
        Integer (2),
        Float (3),
        Blob (4);

        public int value;

        FieldType(int value) {
            this.value = value;
        }

        static FieldType get(int i) {
            return FieldType.values()[i];
        }
    };

    public static class RowDescriptor {
        public String name;
        public FieldType type;

        public RowDescriptor(String name, FieldType type) {
            this.name = name;
            this.type = type;
        };

        @Override
        public String toString() {
          return name + " (" + this.type.name() + ")";
        }

        public RowDescriptor() {

        }
    };

    public static class TableExistsException extends Exception {
        public TableExistsException(String ex) {
            super(ex);
        }
    }

    public static class TablePermissionsException extends Exception {
        public TablePermissionsException(String ex) {
            super(ex);
        }
    }

    public static class TableNotExistException extends Exception {
        public TableNotExistException(String ex) {
            super(ex);
        }
    }


    // A constructor for generating a new table

    public Table(String name, RowDescriptor... rows) throws Exception {
        // 1: check if a meta file with the same name exists
        File hMeta = new File(name + ".tmeta");

        if (hMeta.exists()) throw new TableExistsException("Table " + name + " already seems to exist.");

        hMeta.createNewFile();

        if (!hMeta.canWrite()) throw new TablePermissionsException("Table " + name + " could not be created. (permissions)");

        // create the files for this table

        File hString = new File(name  + ".tstring");
        File hData = new File(name + ".tdata");



        meta = new RandomAccessFile(hMeta, "rwd");
        strings = new RandomAccessFile(hString, "rwd");
        data = new RandomAccessFile(hData, "rwd");


        meta.writeLong(putString(name));
        meta.writeInt(rows.length);

        for (RowDescriptor r: rows) {
          meta.writeLong(putString(r.name));
          meta.writeInt(r.type.value);
        }

        recordSize = rows.length * 8;

        this.rows = rows;
        this.tableName = name;
    };

    public Table(String name) throws Exception{
        this.tableName = name;

        File hMeta = new File(name + ".tmeta");
        File hString = new File(name + ".tstring");
        File hData = new File(name + ".tdata");

        if (!hMeta.exists()) throw new TableNotExistException ("This table (" + name + ") doesn't seem to exist.");

        meta = new RandomAccessFile(hMeta, "rwd");
        strings = new RandomAccessFile(hString, "rwd");
        data = new RandomAccessFile(hData, "rwd");

        meta.seek(8);

        this.rows = new RowDescriptor[meta.readInt()];
        recordSize = rows.length * 8;

        for (int i = 0; i < rows.length; i++) {
            rows[i] = new RowDescriptor();

            meta.seek(12 * (i + 1));
            rows[i].name = getString(meta.readLong());
            meta.seek((12 * (i + 1)) + 8);
            rows[i].type = FieldType.get(meta.readInt());
        }
    }

    public void putRecord(String... values) throws Exception {
        try {
            long seek = data.length();
            int skip = 0;
            for (RowDescriptor row: rows) {
                data.seek(seek + skip * 8);
                switch (row.type) {
                    case Integer:
                        data.writeLong(Integer.parseInt(values[skip]));
                        break;
                    case String:
                        data.writeLong(putString(values[skip]));
                        break;
                    case Blob:
                        throw new Exception("Blob feature not implemented yet.");
                    case Float:
                        throw new Exception("Blob feature not implemented yet.");
                    default:
                        throw new Exception("No thanks!");
                }
                skip++;
            }
        } catch (IOException ex) {

        }
    }

    public void getRecord(int index) throws Exception {
        try {

            long seek = (index * (rows.length * 8));
            long unit = 0;

            for (int i = 0; i < rows.length; i++) {
                data.seek((i * 8) + seek);
                if (rows[i].type == FieldType.Integer) {
                    System.out.println(unit = data.readLong());
                } else {
                    System.out.println(getString(unit = data.readLong()));
                }
            }

        } catch (IOException e) {

        }
    }

    private long putString(String string) {
        try {
            long desc = (string.length());
            desc <<= 32;
            desc |= strings.length();

            strings.seek(strings.length());

            strings.write(string.getBytes());

            return desc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    };

    private String getString(long desc) {
        int addr = (int) desc;
        int len = (int) (desc >> 32);
        byte[] string = new byte[len];

        try {

            for (int i = 0; i < len; i++) {
                strings.seek(addr + i);
                string[i] = strings.readByte();
            }

            return new String(string);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * equals
     * @param e some object
     * @return true if the tables have the same name and row descriptors. False if the former is untrue, or the other object is not an instance of Table.
     */

    @Override
    public boolean equals(Object e) {

        if (!(e instanceof Table)) return false;
        Table other = Table.class.cast(e);
        return this.hashCode() == other.hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.tableName);
        hash = 97 * hash + Arrays.deepHashCode(this.rows);
        return hash;

    }


}
