package com.bemetoy.bp.persistence.db.base;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by albieliang on 16/2/22.
 */
public interface DatabaseInit {

    SQLiteDatabase openOrCreateDatabase(String name, int version);

	SQLiteDatabase openOrCreateDatabase(String path, String name, int version);

    String getDatabasePath();
}