package edu.sjsu.smartprofiler;

import java.util.List;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.AudioManager;
import android.widget.Toast;

import com.google.android.gms.location.LocationClient;

public class LocationService extends IntentService {

	public LocationService() {
		super("Fused Location Service");
	}
	
	public LocationService(String name) {
		super("Fused Location Service");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		DBHandler handler = new DBHandler(this, null, null, 1);
		Location location = intent.getParcelableExtra(LocationClient.KEY_LOCATION_CHANGED);
		if(location != null) {
			
			//Fetch all location from databases and compare here
			List<MyLocation> locations = handler.getAllLocations();
			for(MyLocation loc : locations) {
			
				Location androidLocation = new Location("User");
				androidLocation.setLatitude(loc.getLatitude());
				androidLocation.setLongitude(loc.getLongitude());
				
				float distance = androidLocation.distanceTo(location);
				if(distance < 150) {
					changeProfile(loc.getRingerMode());
				}
			}
		}
	}
	
	public void changeProfile(String profile) {
		//Change the Ringer Mode to Silent/Loud
		final AudioManager mode = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
		if(profile.equalsIgnoreCase("Silent")) {	
			mode.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			Toast.makeText(this, "Phone switched to Silent Mode", Toast.LENGTH_LONG).show();
		} else if(profile.equalsIgnoreCase("Vibrate")) {
			mode.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			Toast.makeText(this, "Phone switched to Vibrate Mode", Toast.LENGTH_LONG).show();
		} else {
			mode.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
			Toast.makeText(this, "Phone switched to Loud Mode", Toast.LENGTH_LONG).show();
		}
	}

}