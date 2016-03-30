package com.lynp.ui.db;

/**
 * Created by niuminguo on 16/3/30.
 */
import android.content.Context;

import java.io.File;
import java.io.IOException;

public class DBLite {

    private java.io.File dbFile;
    private int maxCursor;
    private java.util.ArrayList<DBRecordItem> records = new java.util.ArrayList<DBRecordItem>();

    public DBLite(Context context, String folder, String fileName) {
        java.io.File fileDir = context.getFilesDir();
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        if (folder != null) {
            fileDir = new java.io.File(fileDir, folder);
            if (!fileDir.exists()) {
                fileDir.mkdir();
            }
        }
        dbFile = new java.io.File(fileDir, fileName);
    }

    public int getMaxCursor() {
        return maxCursor;
    }

    public int addRecord(DBRecordItem recordItem) {
        records.add(recordItem);
        recordItem.id = maxCursor++;
        return recordItem.id;
    }

    public int insertRecord(DBRecordItem recordItem, int index) {
        records.add(index, recordItem);
        recordItem.id = maxCursor++;
        return recordItem.id;
    }

    public byte[] getData() throws Exception {
        java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();

        bout.write(Convert.convertInt(maxCursor));
        bout.write(Convert.convertInt(records.size()));
        for (int i = 0; i < records.size(); i++) {
            byte[] recordData = records.get(i).getData();
            bout.write(Convert.convertInt(recordData.length));
            bout.write(recordData);
        }
        byte[] data = bout.toByteArray();
        bout.close();
        return data;
    }

    public void saveToDisk() throws Exception {
        byte[] data = getData();
        java.io.FileOutputStream fout = new java.io.FileOutputStream(dbFile);
        fout.write(data);
        fout.close();
    }

    public void saveToDisk(byte[] data) throws Exception {
        java.io.FileOutputStream fout = new java.io.FileOutputStream(dbFile);
        fout.write(data);
        fout.close();
    }

    public byte[] getZoneData() throws IOException {
        if (!dbFile.exists())
            return null;
        java.io.FileInputStream fin = null;
        java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
        try {
            fin = new java.io.FileInputStream(dbFile);
            byte[] bufferByte = new byte[256];
            int l = -1;
            while ((l = fin.read(bufferByte)) > -1) {
                bout.write(bufferByte, 0, l);
            }
            byte[] data = bout.toByteArray();
            int dataLength = data.length;
            if (dataLength == 0)
                return null;
            return data;
        } catch (Exception e) {

        } finally {
            if (fin != null) {
                fin.close();
                fin = null;
            }

            if (bout != null) {
                bout.close();
                bout = null;
            }
        }
        return null;
    }

    public void loadData() {
        clear();
        if (!dbFile.exists())
            return;
        java.io.FileInputStream fin = null;
        java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
        try {
            fin = new java.io.FileInputStream(dbFile);
            byte[] bufferByte = new byte[256];
            int l = -1;
            while ((l = fin.read(bufferByte)) > -1) {
                bout.write(bufferByte, 0, l);
            }
            byte[] data = bout.toByteArray();
            int dataLength = data.length;
            if (dataLength == 0)
                return;

            int index = 0;
            maxCursor = Convert.getInt(data, index);
            index += 4;
            int recordsCount = Convert.getInt(data, index);
            index += 4;

            ConvertString tCS = new ConvertString();
            if (recordsCount > 0) {
                while (index < dataLength) {
                    int recordDatSize = Convert.getInt(data, index);
                    index += 4;
                    int recordEnd = index + recordDatSize;

                    DBRecordItem aRecord = new DBRecordItem();
                    aRecord.id = Convert.getInt(data, index);
                    index += 4;
                    while (index < recordEnd) {
                        int type = data[index++];
                        Convert.convert2bString(data, index, tCS);
                        index += tCS.byteLength + 2;
                        String key = tCS.value;
                        switch (type) {
                            case DBRecordField.TYPE_STRING: {
                                Convert.convert2bString(data, index, tCS);
                                index += tCS.byteLength + 2;
                                aRecord.setStringValue(key, tCS.value);
                            }
                            break;

                        }

                    }
                    records.add(aRecord);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                fin.close();
                bout.close();
            } catch (Exception ex) {
            }
        }
    }

    public void appendData(File file) {
        if (!file.exists())
            return;
        java.io.FileInputStream fin = null;
        java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
        try {
            fin = new java.io.FileInputStream(file);
            byte[] bufferByte = new byte[256];
            int l = -1;

            while ((l = fin.read(bufferByte)) > -1) {

                bout.write(bufferByte, 0, l);

            }
            byte[] data = bout.toByteArray();
            int dataLength = data.length;
            if (dataLength == 0)
                return;

            int index = 0;
            // maxCursor=Convert.getInt(data, index);
            index += 4;
            int recordsCount = Convert.getInt(data, index);
            index += 4;

            maxCursor += recordsCount;

            ConvertString tCS = new ConvertString();
            if (recordsCount > 0) {
                while (index < dataLength) {
                    int recordDatSize = Convert.getInt(data, index);
                    index += 4;
                    int recordEnd = index + recordDatSize;

                    DBRecordItem aRecord = new DBRecordItem();
                    aRecord.id = Convert.getInt(data, index);
                    index += 4;
                    while (index < recordEnd) {

                        int type = data[index++];

                        Convert.convert2bString(data, index, tCS);
                        index += tCS.byteLength + 2;

                        String key = tCS.value;
                        switch (type) {

                            case DBRecordField.TYPE_STRING: {
                                Convert.convert2bString(data, index, tCS);
                                index += tCS.byteLength + 2;
                                aRecord.setStringValue(key, tCS.value);
                            }
                            break;

                        }

                    }
                    addRecord(aRecord);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                fin.close();
                bout.close();
            } catch (Exception ex) {
            }
        }
    }

    public void deleteData() {
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    public void deleteRecord(int index) throws Exception {
        if (index > -1 && index < records.size()) {
            records.remove(index);
        }
    }

    public void deleteRecordWidthID(int id) throws Exception {
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getId() == id)
                records.remove(i);
            return;
        }
    }

    public void clear() {
        records.clear();
    }

    public java.util.ArrayList<DBRecordItem> getRecords() {
        return records;
    }

    public int getRecordSize() {
        return records.size();
    }

    public DBRecordItem getRecord(int index) {
        return records.get(index);
    }

    public DBRecordItem getRecordWidthID(int id) {
        int size = records.size();
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getId() == id)
                return records.get(i);
        }
        return null;
    }

}