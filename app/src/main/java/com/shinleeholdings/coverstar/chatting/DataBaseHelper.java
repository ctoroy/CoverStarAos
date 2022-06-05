package com.shinleeholdings.coverstar.chatting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "HubtalkDB";
	public static final String TAG = "DataBaseHelper";

	private static final int DATABASE_VERSION = 1;

	private volatile static DataBaseHelper instance;
	private SQLiteDatabase db;

	public static DataBaseHelper getSingleton(Context context) {
		if (instance == null) {
			instance = new DataBaseHelper(context);
		}
		return instance;
	}

	private DataBaseHelper(Context context) {
		super(context, DB_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	/**
	 * Database 를 쿼리한다. SQLiteDatabase query() 참고
	 * 
	 * @param tableName
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @param groupBy
	 * @param having
	 * @param orderBy
	 * @return
	 */
	public Cursor query(String tableName, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
		db = getWritableDatabase();
		if (db == null) {
			return null;
		}

		Cursor queryCursor = null;

		try {
			queryCursor = db.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
		} catch (Exception e) {
		}

		return queryCursor;
	}

	/**
	 * 테이블을 생성한다. query 자체에 있으면 생성하지 않는 부분이 있기때문에 따로 체크하지 않아도 됨
	 * 
	 * @param sqlQuery
	 */
	public boolean createTable(String sqlQuery) {
		try {
			db = getWritableDatabase();
			db.execSQL(sqlQuery);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 테이블을 지운다.
	 * 
	 * @param tableName
	 *            delete할 테이블명
	 * @return
	 */
	public boolean deleteTable(String tableName) {
		if (db == null) {
			return false;
		}

		try {
			db.delete(tableName, null, null);
		} catch (Exception e) {
		}
		return true;
	}

	/**
	 * method for deleting rows in the database.
	 * 
	 * @param table
	 *            the table to delete from
	 * @param whereClause
	 *            the optional WHERE clause to apply when deleting. Passing null will delete all rows.
	 * @param whereArgs
	 * @return the number of rows affected if a whereClause is passed in, 0 otherwise. To remove all rows and get a count pass "1" as the whereClause.
	 */
	public int deleteQuery(String table, String whereClause, String[] whereArgs) {
		db = getWritableDatabase();
		if (db == null) {
			return -1;
		}

		int queryResult = -1;

		try {
			queryResult = db.delete(table, whereClause, whereArgs);
		} catch (Exception e) {
		}

		return queryResult;
	}

	public int insertQuery(String table, String hasItemQuery, String selection, ContentValues values) {
		db = getWritableDatabase();
		if (db == null) {
			return -1;
		}

		long queryResult = -1;
		try {
			Cursor cursor = db.rawQuery(hasItemQuery, null);
			if (cursor == null) {
				return (int) queryResult;
			}

			cursor.moveToFirst();

			if (cursor.getInt(0) <= 0) {
				queryResult = db.insert(table, null, values);
			} else {
				queryResult = db.update(table, values, selection, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return (int) queryResult;
	}

	/**
	 * deleteQuery DB에 있으면 delete하고 없으면 처리하지 않는다.
	 * 
	 * @param table
	 * @param selection
	 * @return
	 */
	public int deleteQuery(String table, String selection) {
		db = getWritableDatabase();
		if (db == null) {
			return -1;
		}

		long queryResult = -1;
		try {
			queryResult = db.delete(table, selection, null);
		} catch (Exception e) {
		}

		return (int) queryResult;
	}
}
