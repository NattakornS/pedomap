package com.mobile.pedomap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class PedoSQLiteHelper extends SQLiteOpenHelper {

	public static final String DATABASE_NAME = "Pedometer.db";
	private static final int DATABASE_VERSION = 1;

	public static final String TABLE_DateSet = "TbDateSet";
	public static final String TABLE_Location = "TbLocation";
	public static final String TABLE_RecordDetail = "TbRecordDetail";

	public static final String COLUMN_DateId = "DateId";
	public static final String COLUMN_TotalTime = "TotalTime";
	public static final String COLUMN_TotalStep = "TotalStep";
	public static final String COLUMN_RecordDate = "RecordDate";
	public static final String COLUMN_LocationId = "LocationId";
	public static final String COLUMN_RoundNo = "RoundNo";
	public static final String COLUMN_Latitude = "Latitude";
	public static final String COLUMN_Longtitude = "Longtitude";
	public static final String COLUMN_RecordId = "RecordId";
	public static final String COLUMN_MinuteTime = "MinuteTime";
	public static final String COLUMN_Calories = "Calories";
	public static final String COLUMN_Speed = "Speed";
	public static final String COLUMN_Pace = "Pace";
	public static final String COLUMN_Step = "Step";
	public static final String COLUMN_Distance = "Distance";

	private static final String DateSet_DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_DateSet
			+ " ("
			+ COLUMN_DateId
			+ " integer PRIMARY KEY AUTOINCREMENT NOT NULL, "
//			+ COLUMN_Calories
//			+ " int NULL,"
			+ COLUMN_TotalTime
			+ " integer NULL ,"
					+ COLUMN_TotalStep
					+ " integer NULL ,"
							+ COLUMN_Calories
							+ " integer NULL ,"
			+ COLUMN_RecordDate
			+ " date NOT NULL )";
//	
	private static final String Location_DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_Location
			+ " ("
					+ COLUMN_LocationId
					+ " integer PRIMARY KEY AUTOINCREMENT  NOT NULL ,"
							+ COLUMN_DateId
							+ " integer NOT NULL ,"
									+ COLUMN_RoundNo
									+ " integer NULL ,"
											+ COLUMN_Latitude
											+ " double NULL ,"
													+ COLUMN_Longtitude
													+ " double NULL ,"
			+"FOREIGN KEY ("+COLUMN_DateId+") REFERENCES "+TABLE_DateSet+"("+COLUMN_DateId+"))";
			
	
	private static final String RecordDetail_DATABASE_CREATE = "CREATE TABLE IF NOT EXISTS "
			+ TABLE_RecordDetail
			+ " ("
					+ COLUMN_RecordId
					+ " integer PRIMARY KEY AUTOINCREMENT  NOT NULL ,"
							+ COLUMN_LocationId
							+ " integer NOT NULL ,"
									+ COLUMN_MinuteTime
									+ " integer NULL ,"
											+ COLUMN_Calories
											+ " integer NULL ,"
													+ COLUMN_Speed
													+ " double NULL ,"
															+ COLUMN_Pace
															+ " integer NULL ,"
																	+ COLUMN_Step
																	+ " integer NULL ,"
																			+ COLUMN_Distance
																			+ " double NULL ,"
			+"FOREIGN KEY ("+COLUMN_LocationId+") REFERENCES "+TABLE_Location+"("+COLUMN_LocationId+"))";
					
	private static String DB_PATH = Environment.getExternalStorageState() + "/"
			+ DATABASE_NAME;
	private Context context;
	

	public PedoSQLiteHelper() {
		super(null, DATABASE_NAME, null, DATABASE_VERSION);
	}
	public PedoSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub

		db.execSQL(DateSet_DATABASE_CREATE);
		db.execSQL(Location_DATABASE_CREATE);
		db.execSQL(RecordDetail_DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(PedoSQLiteHelper.class.getName(),

		"Upgrading database from version " + oldVersion + " to "

		+ newVersion + ", which will destroy all old data");

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_DateSet);

		onCreate(db);

	}
	
	public void copyDbToExternal() {
	    try {
	        File sd = Environment.getExternalStorageDirectory();
	        File data = Environment.getDataDirectory();

	        if (sd.canWrite()) {
	            String currentDBPath = "//data//" + context.getApplicationContext().getPackageName() + "//databases//"
	                                    + DATABASE_NAME;
	            String backupDBPath = DATABASE_NAME;
	            File currentDB = new File(data, currentDBPath);
	            File backupDB = new File(sd, backupDBPath);
	            FileInputStream fis = new FileInputStream(currentDB);
	            FileOutputStream fos = new FileOutputStream(backupDB);
	            FileChannel src = fis.getChannel();
	            FileChannel dst = fos.getChannel();
	            dst.transferFrom(src, 0, src.size());
	            src.close();
	            dst.close();
	            fis.close();
	            fos.close();
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}
