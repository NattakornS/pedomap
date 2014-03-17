package com.mobile.pedomap;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class TrackDS {
	private PedoSQLiteHelper dbHelper;
	private SQLiteDatabase db;

	private String[] allDateColumns = { PedoSQLiteHelper.COLUMN_DateId,
			PedoSQLiteHelper.COLUMN_TotalTime,
			PedoSQLiteHelper.COLUMN_TotalStep,
			PedoSQLiteHelper.COLUMN_Calories,
			PedoSQLiteHelper.COLUMN_RecordDate };
	private String[] allLocationColumns = { PedoSQLiteHelper.COLUMN_LocationId,
			PedoSQLiteHelper.COLUMN_DateId, PedoSQLiteHelper.COLUMN_RoundNo,
			PedoSQLiteHelper.COLUMN_Latitude,
			PedoSQLiteHelper.COLUMN_Longtitude };
	private String[] allRecordDetailColumns = {
			PedoSQLiteHelper.COLUMN_RecordId,
			PedoSQLiteHelper.COLUMN_LocationId,
			PedoSQLiteHelper.COLUMN_MinuteTime,
			PedoSQLiteHelper.COLUMN_Calories, PedoSQLiteHelper.COLUMN_Speed,
			PedoSQLiteHelper.COLUMN_Pace, PedoSQLiteHelper.COLUMN_Step,
			PedoSQLiteHelper.COLUMN_Distance };

	public TrackDS(Context ct) {
		dbHelper = new PedoSQLiteHelper(ct);
	}

	public void open() throws SQLException {
		db = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public DateData createDate(long time) {
		ContentValues values = new ContentValues();
		values.put(PedoSQLiteHelper.COLUMN_RecordDate, time);
		long insertId = db.insert(PedoSQLiteHelper.TABLE_DateSet, null, values);
		Cursor cursor = db.query(PedoSQLiteHelper.TABLE_DateSet,
				allDateColumns,
				PedoSQLiteHelper.COLUMN_DateId + "=" + insertId, null, null,
				null, null);
		cursor.moveToFirst();
		DateData newTrack = cursorToDateData(cursor);
		cursor.close();
		// id = new Integer(newTrack.getDateId());
		return newTrack;
	}

	public DateData updateDate(DateData data) {
		ContentValues values = new ContentValues();
		values.put(PedoSQLiteHelper.COLUMN_TotalTime, data.getTotalTime());
		values.put(PedoSQLiteHelper.COLUMN_TotalStep, data.getTotalStep());
		values.put(PedoSQLiteHelper.COLUMN_Calories, data.getCalories());
		long rowUpdated = db.update(PedoSQLiteHelper.TABLE_DateSet, values, PedoSQLiteHelper.COLUMN_DateId+"=?", new String[] { String.valueOf(data.getDateId()) });
		System.out.println("DateData "+rowUpdated+" row(s) updated");
		//long insertId = db.insert(PedoSQLiteHelper.TABLE_DateSet, null, values);
		Cursor cursor = db.query(PedoSQLiteHelper.TABLE_DateSet,
				allDateColumns,
				PedoSQLiteHelper.COLUMN_DateId + "=" + data.getDateId(), null, null,
				null, null);
		cursor.moveToFirst();
		DateData newTrack = cursorToDateData(cursor);
		cursor.close();
		// id = new Integer(newTrack.getDateId());
		return newTrack;
	}

	//
	//
	// public void createLocation(ArrayList<LocationData> locationDatas,
	// DateData dateData) {
	// if (locationDatas == null || dateData == null) {
	// return;
	// }
	// System.out.println("LocationData to DB");
	// for (int i = 0; i < locationDatas.size(); i++) {
	// LocationData locationData = locationDatas.get(i);
	// ContentValues values = new ContentValues();
	// // System.out.println(locationData.getLatitude());
	// // values.put(PedoSQLiteHelper.COLUMN_LocationId,
	// locationData.getLocationId());
	// values.put(PedoSQLiteHelper.COLUMN_DateId, dateData.getDateId());
	// values.put(PedoSQLiteHelper.COLUMN_RoundNo, locationData.getRoundNo());
	// values.put(PedoSQLiteHelper.COLUMN_Latitude, locationData.getLatitude());
	// values.put(PedoSQLiteHelper.COLUMN_Longtitude,
	// locationData.getLongtitude());
	// // long insertId = db.insert(PedoSQLiteHelper.TABLE_Location,
	// null,values);
	// db.insert(PedoSQLiteHelper.TABLE_Location, null,values);
	// }
	//
	// }

	public LocationData createLocation(DateData dateData,LocationData lDat) {
		if (dateData == null || lDat==null) {
			return null;
		}
		System.out.println("LocationData to DB");
		LocationData locationData = lDat;
		ContentValues values = new ContentValues();
		// System.out.println(locationData.getLatitude());
		// values.put(PedoSQLiteHelper.COLUMN_LocationId,
		// locationData.getLocationId());
		values.put(PedoSQLiteHelper.COLUMN_DateId, dateData.getDateId());
		values.put(PedoSQLiteHelper.COLUMN_RoundNo, locationData.getRoundNo());
		values.put(PedoSQLiteHelper.COLUMN_Latitude, locationData.getLatitude());
		values.put(PedoSQLiteHelper.COLUMN_Longtitude,
				locationData.getLongtitude());
		// long insertId = db.insert(PedoSQLiteHelper.TABLE_Location,
		// null,values);
		long insertId = db
				.insert(PedoSQLiteHelper.TABLE_Location, null, values);
		Cursor cursor = db.query(PedoSQLiteHelper.TABLE_Location,
				allLocationColumns, PedoSQLiteHelper.COLUMN_LocationId + "="
						+ insertId, null, null, null, null);
		cursor.moveToFirst();
		LocationData newTrack = cursorToLocationData(cursor);
		cursor.close();
		return newTrack;
	}

	public RecordData createRecord(LocationData locationData,RecordData rDat) {
		if (locationData == null||rDat==null) {
			return null;
		}
		System.out.println("Record to DB");

		RecordData recordData = rDat;
		ContentValues values = new ContentValues();
		// System.out.println(locationData.getLatitude());
		// values.put(PedoSQLiteHelper.COLUMN_RecordId,
		// recordData.getRecordId());
		values.put(PedoSQLiteHelper.COLUMN_LocationId,
				locationData.getLocationId());
		values.put(PedoSQLiteHelper.COLUMN_MinuteTime,
				recordData.getMinuteTime());
		values.put(PedoSQLiteHelper.COLUMN_Calories, recordData.getCalories());
		values.put(PedoSQLiteHelper.COLUMN_Speed, recordData.getSpeed());
		values.put(PedoSQLiteHelper.COLUMN_Pace, recordData.getPaces());
		values.put(PedoSQLiteHelper.COLUMN_Step, recordData.getStep());
		values.put(PedoSQLiteHelper.COLUMN_Distance, recordData.getDistance());

		// long insertId = db.insert(PedoSQLiteHelper.TABLE_RecordDetail,
		// null,values);
		long insertId = db.insert(PedoSQLiteHelper.TABLE_RecordDetail, null, values);

		Cursor cursor = db.query(PedoSQLiteHelper.TABLE_RecordDetail,
				allRecordDetailColumns, PedoSQLiteHelper.COLUMN_RecordId + "="
						+ insertId, null, null, null, null);
		cursor.moveToFirst();
		RecordData newTrack = cursorToRecordData(cursor);
		cursor.close();
		return newTrack;
	}

	// public void createRecord(ArrayList<RecordData> recordDatas,
	// LocationData locationData) {
	// if (recordDatas == null || locationData == null) {
	// return;
	// }
	// System.out.println("LocationData to DB");
	// for (int i = 0; i < recordDatas.size(); i++) {
	// RecordData recordData = recordDatas.get(i);
	// ContentValues values = new ContentValues();
	// // System.out.println(locationData.getLatitude());
	// // values.put(PedoSQLiteHelper.COLUMN_RecordId,
	// // recordData.getRecordId());
	// values.put(PedoSQLiteHelper.COLUMN_LocationId,
	// locationData.getLocationId());
	// values.put(PedoSQLiteHelper.COLUMN_MinuteTime,
	// recordData.getMinuteTime());
	// values.put(PedoSQLiteHelper.COLUMN_Calories,
	// recordData.getCalories());
	// values.put(PedoSQLiteHelper.COLUMN_Speed, recordData.getSpeed());
	// values.put(PedoSQLiteHelper.COLUMN_Pace, recordData.getPaces());
	// values.put(PedoSQLiteHelper.COLUMN_Step, recordData.getStep());
	// values.put(PedoSQLiteHelper.COLUMN_Distance,
	// recordData.getDistance());
	//
	// // long insertId = db.insert(PedoSQLiteHelper.TABLE_RecordDetail,
	// // null,values);
	// db.insert(PedoSQLiteHelper.TABLE_RecordDetail, null, values);
	// }
	// }

	public ArrayList<RecordData> getAllRecordByLocationId(int id) {
		ArrayList<RecordData> datas = new ArrayList<RecordData>();
		Cursor cursor = db.query(PedoSQLiteHelper.TABLE_RecordDetail,
				allRecordDetailColumns, PedoSQLiteHelper.COLUMN_LocationId
						+ "=?", new String[] { String.valueOf(id) }, null,
				null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			RecordData data = cursorToRecordData(cursor);
			datas.add(data);
			cursor.moveToNext();
		}
		cursor.close();
		return datas;
	}
	

//	public int getTotalTimeByDateId(int id) {
////		ArrayList<RecordData> datas = new ArrayList<RecordData>();
//		Cursor cursor = db.query(PedoSQLiteHelper.TABLE_DateSet,
//				allRecordDetailColumns, PedoSQLiteHelper.COLUMN_DateId
//						+ "=?", new String[] { String.valueOf(id) }, null,
//				null, null);
//		cursor.moveToFirst();
//			DateData data = cursorToDateData(cursor);
//		
//		cursor.close();
//		return data.getTotalTime();
//	}
	

//	public int getTotalStepByDateId(int id) {
////		ArrayList<RecordData> datas = new ArrayList<RecordData>();
//		Cursor cursor = db.query(PedoSQLiteHelper.TABLE_DateSet,
//				allRecordDetailColumns, PedoSQLiteHelper.COLUMN_DateId
//						+ "=?", new String[] { String.valueOf(id) }, null,
//				null, null);
//		cursor.moveToFirst();
//			DateData data = cursorToDateData(cursor);
//		
//		cursor.close();
//		return data.getTotalTime();
//	}

	public ArrayList<LocationData> getAllLocationByDateId(int id) {
		ArrayList<LocationData> datas = new ArrayList<LocationData>();
		Cursor cursor = db.query(PedoSQLiteHelper.TABLE_Location,
				allLocationColumns, PedoSQLiteHelper.COLUMN_DateId + "=?",
				new String[] { String.valueOf(id) }, null, null, null);
		cursor.moveToFirst();

		while (!cursor.isAfterLast()) {
			LocationData data = cursorToLocationData(cursor);
			datas.add(data);
			cursor.moveToNext();
		}
		cursor.close();
		return datas;
	}

	public ArrayList<DateData> getAllDate() {
		ArrayList<DateData> recordDatas = new ArrayList<DateData>();
		Cursor cursor = db.query(PedoSQLiteHelper.TABLE_DateSet,
				allDateColumns, null, null, null, null, null);
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			DateData dateData = cursorToDateData(cursor);
			recordDatas.add(dateData);
			cursor.moveToNext();
		}
		cursor.close();
		return recordDatas;
	}
	

	public DateData getDateDataById(int id) {
		Cursor cursor = db.query(PedoSQLiteHelper.TABLE_DateSet,
				allDateColumns, PedoSQLiteHelper.COLUMN_DateId
						+ "=?", new String[] { String.valueOf(id) }, null,
				null, null);
		cursor.moveToFirst();
		DateData data = cursorToDateData(cursor);
		cursor.close();
		return data;
	}
	
	public ArrayList<RecordData> getRecordsByDateId(int dateId) {
//		String sql = new String();
//		sql+=" SELECT * ";
//		sql+=" FROM "+PedoSQLiteHelper.TABLE_RecordDetail;
//		sql+=" WHERE "+PedoSQLiteHelper.COLUMN_LocationId;
//		sql+=" IN (";
//		sql+=" SELECT "+PedoSQLiteHelper.COLUMN_LocationId;
//		sql+=" FROM " + PedoSQLiteHelper.TABLE_Location;
//		sql+=" WHERE "+PedoSQLiteHelper.COLUMN_DateId +" = "+PedoSQLiteHelper.COLUMN_DateId+")";
		ArrayList<LocationData> locDatas = getAllLocationByDateId(dateId);
		ArrayList<RecordData> recDatas = new ArrayList<RecordData>();
		for (LocationData locationData : locDatas) {
			recDatas.addAll(getAllRecordByLocationId(locationData.getLocationId()));
		}
		return recDatas;
	}

	public void copyDB() {
		dbHelper.copyDbToExternal();
	}

	private DateData cursorToDateData(Cursor cursor) {
		DateData data = new DateData();
		data.setDateId(cursor.getInt(0));
		data.setTotalTime(cursor.getInt(1));
		data.setStep(cursor.getInt(2));
		data.setCalories(cursor.getInt(3));
		data.setTime(cursor.getLong(4));
		return data;
	}

	private LocationData cursorToLocationData(Cursor cursor) {
		LocationData data = new LocationData();
		data.setLocationId(cursor.getInt(0));
		data.setDateId(cursor.getInt(1));
		data.setRoundNo(cursor.getInt(2));
		data.setLatitude(cursor.getDouble(3));
		data.setLongtitude(cursor.getDouble(4));
		return data;
	}

	private RecordData cursorToRecordData(Cursor cursor) {
		RecordData data = new RecordData();
		data.setRecordId(cursor.getInt(0));
		data.setLocationId(cursor.getInt(1));
		data.setMinuteTime(cursor.getInt(2));
		data.setCalories(cursor.getInt(3));
		data.setSpeed(cursor.getDouble(4));
		data.setPaces(cursor.getInt(5));
		data.setStep(cursor.getInt(6));
		data.setDistance(cursor.getDouble(7));
		return data;
	}
}
