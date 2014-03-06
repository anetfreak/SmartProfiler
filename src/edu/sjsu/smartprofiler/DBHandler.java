package edu.sjsu.smartprofiler;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHandler extends SQLiteOpenHelper {
	
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "profilerdb";
	private static final String TABLE_PROFILES = "profiles";
	private static final String TABLE_LOCATIONS = "locations";
	
	public DBHandler(Context context, String name, CursorFactory factory, int version) {
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_TABLE = "CREATE TABLE " + TABLE_PROFILES + "( id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, ringer_mode TEXT)";
	    db.execSQL(CREATE_TABLE);
	    
	    CREATE_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + "( id INTEGER PRIMARY KEY AUTOINCREMENT, profile_id INTEGER, name TEXT, address TEXT, latitude REAL, longitude REAL, FOREIGN KEY(profile_id) REFERENCES "+TABLE_PROFILES+"(id))";
	    db.execSQL(CREATE_TABLE);
	    
	    //Populate Profiles table
	    ContentValues values = new ContentValues();
        values.put("id", 1);
        values.put("name", "SILENT");
        values.put("ringer_mode", "SILENT");
        db.insert(TABLE_PROFILES, null, values);
        values.put("id", 2);
        values.put("name", "VIBRATE");
        values.put("ringer_mode", "VIBRATE");
        db.insert(TABLE_PROFILES, null, values);
        values.put("id", 3);
        values.put("name", "NORMAL");
        values.put("ringer_mode", "NORMAL");
        db.insert(TABLE_PROFILES, null, values);
//        db.close();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILES);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
	    onCreate(db);
	}
	
	public void addLocation(MyLocation location) {

        ContentValues values = new ContentValues();
        values.put("name", location.getName());
        values.put("profile_id", location.getProfileId());
        values.put("latitude", location.getLatitude());
        values.put("longitude", location.getLongitude());
        values.put("address", location.getAddress());
 
        SQLiteDatabase db = this.getWritableDatabase();
        
        db.insert(TABLE_LOCATIONS, null, values);
        db.close();
	}
	
	public List<String> getProfiles() {
		String query = "SELECT ringer_mode FROM " + TABLE_PROFILES;
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		List<String> profileNames = new ArrayList<String>();
		
		if (cursor.moveToFirst()) {
			cursor.moveToFirst();
			profileNames.add(cursor.getString(0));
			cursor.close();
		}
	    db.close();
		return profileNames;
	}
	
	public Integer getProfileId(String profileName) {
		String query = "SELECT id FROM " + TABLE_PROFILES + " where LOWER(ringer_mode) = LOWER(?)";
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, new String[]{profileName});
		Integer profileId = null;
		
		if (cursor.moveToFirst()) {
			cursor.moveToFirst();
			profileId = cursor.getInt(0);
			cursor.close();
		}
	    db.close();
		return profileId;
	}
	
	public List<String> getSavedLocations() {
		String query = "SELECT address FROM " + TABLE_LOCATIONS;
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		List<String> addresses = new ArrayList<String>();
		
		if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
                String add = cursor.getString(cursor.getColumnIndex("address"));
                addresses.add(add);
                cursor.moveToNext();
            }
        }		
		cursor.close();
	    db.close();
		return addresses;
	}
	
	public List<MyLocation> getAllLocations() {
		String query = "SELECT locations.id, locations.address, locations.latitude, locations.longitude, profiles.ringer_mode FROM " + TABLE_LOCATIONS + " join " + TABLE_PROFILES + " on profiles.id = locations.profile_id";
		
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		List<MyLocation> locations = new ArrayList<MyLocation>();
		
		if (cursor.moveToFirst()) {
            while (cursor.isAfterLast() == false) {
            	MyLocation loc = new MyLocation();
            	loc.setId(cursor.getInt(0));
            	loc.setAddress(cursor.getString(1));
            	loc.setLatitude(cursor.getDouble(2));
            	loc.setLongitude(cursor.getDouble(3));
            	loc.setRingerMode(cursor.getString(4));
            	locations.add(loc);
                cursor.moveToNext();
            }
        }		
		cursor.close();
	    db.close();
		return locations;
	}

}
