package com.shequcun.farm.db;

public class DBRecordItem {

	int id;
	java.util.Hashtable<String, DBRecordField> fields = new java.util.Hashtable<String, DBRecordField>();

	public void setStringValue(String key, String avalue) {
		DBRecordField aField = new DBRecordField(DBRecordField.TYPE_STRING);
		aField.key = key;
		aField.value = avalue;
		fields.put(key, aField);
	}

	public byte[] getData() throws Exception {
		java.util.Enumeration<DBRecordField> eles = fields.elements();
		java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
		bout.write(Convert.convertInt(id));
		while (eles.hasMoreElements()) {
			bout.write(eles.nextElement().getData());
		}
		byte[] data = bout.toByteArray();
		bout.close();
		return data;
	}

	public String getStringValue(String key, String defaultValue) {
		DBRecordField obj = fields.get(key);
		if (obj == null)
			return defaultValue;
		return (String) obj.value;
	}

	public int getId() {
		return id;
	}

}
