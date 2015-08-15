package com.shequcun.farm.db;

public class DBRecordField {

	public static final int TYPE_STRING = 1;

	int type = TYPE_STRING;
	String key;
	Object value;

	public DBRecordField(int type) {
		this.type = type;
	}

	public byte[] getData() throws Exception {
		java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
		bout.write(type);
		bout.write(Convert.get2BString(key));
		switch (type) {

		case TYPE_STRING: {
			if (value == null)
				value = "";
			bout.write(Convert.get2BString((String) value));
		}
			break;

		}
		byte[] data = bout.toByteArray();
		bout.close();
		return data;
	}

}
