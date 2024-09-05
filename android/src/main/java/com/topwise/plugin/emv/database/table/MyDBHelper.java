package com.topwise.plugin.emv.database.table;

import android.content.Context;
import com.topwise.plugin.emv.database.DBHelper;

public class MyDBHelper extends DBHelper {
	public static final String DBNAME = "oversea7.db";
	public static final int DBVERSION = 1;

	private static final Class<?>[] clazz = { Aid.class, Capk.class};

	public MyDBHelper(Context context) {
		super(context, DBNAME, null, DBVERSION, clazz);
	}

}
