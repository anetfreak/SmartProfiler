package edu.sjsu.smartprofiler;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

public class MainActivity extends FragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks, 
	GooglePlayServicesClient.OnConnectionFailedListener {

	LocationClient mLocationClient;
	private TextView addressLabel;
	private Button btnAddLocation;
	private TextView storedAddresses;
	Intent intentService;
	PendingIntent pendingIntent;
	boolean serviceCreated = false;
	LocationRequest locationRequest;
	
	@Override
	protected void onStart() {
		super.onStart();
		mLocationClient.connect();
	}
	
	@Override
	protected void onStop() {
		mLocationClient.disconnect();
		super.onStop();
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mLocationClient = new LocationClient(this, this, this);
		
		addressLabel = (TextView) findViewById(R.id.addressLabel);
		storedAddresses = (TextView) findViewById(R.id.storedAddresses);
		btnAddLocation = (Button) findViewById(R.id.btnAddLocation);
		populateAddressLabel();

		btnAddLocation.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				openLocationActivity(view);
			}
		});
		
	}
	
	public void openLocationActivity(View v){
		Intent intent = new Intent(this, LocationActivity.class);
    	startActivity(intent);
	}
	
	@Override
	public void onConnected(Bundle dataBundle) {
		displayCurrentLocation();
		
		if(!serviceCreated) {
			intentService = new Intent(this, LocationService.class);
			pendingIntent = PendingIntent.getService(this, 1, intentService, 0);
			locationRequest = LocationRequest.create();
			locationRequest.setInterval(30000);
			mLocationClient.requestLocationUpdates(locationRequest, pendingIntent);
			serviceCreated = true;
		}
	}
	
	@Override
	public void onDisconnected() {
//		Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Toast.makeText(this, "Connection Failure : " + connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
	}
	
	public void displayCurrentLocation() {
		// Get the current location's latitude & longitude
		if(mLocationClient != null && mLocationClient.isConnected()) {
			Location currentLocation = mLocationClient.getLastLocation();
			if(currentLocation != null) {
				//Displays the address in the UI..
				(new GetAddressTask(this)).execute(currentLocation);
			} else {
				Toast.makeText(this, "Error retreiving current location.. Trying again..", Toast.LENGTH_SHORT).show();
			}
		}
	}

	
	public void populateAddressLabel(){
		DBHandler handler = new DBHandler(this, null, null, 1);
		List<String> addresses = handler.getSavedLocations();
		storedAddresses.setText("");
		for(String add : addresses) {
			storedAddresses.append(add);
			storedAddresses.append("\n\n");
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
	
	//Does the task of fetching the location based on the current position..
	private class GetAddressTask extends AsyncTask<Location, Void, String>{
		Context mContext;
		public GetAddressTask(Context context) {
			super();
			mContext = context;
		}

		@Override
		protected void onPostExecute(String address) {
			addressLabel.setText(address);
		}
		@Override
		protected String doInBackground(Location... params) {
			Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
			Location loc = params[0];

			// Create a list to contain the result address
			List<Address> addresses = null;
			try {
				addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
			} catch (IOException e1) {
				Log.e("MainActivity", "IO Exception in getFromLocation()");
				e1.printStackTrace();
				return ("IO Exception trying to get address");
			} catch (IllegalArgumentException e2) {
				String errorString = "Illegal arguments " + Double.toString(loc.getLatitude()) + " , " + Double.toString(loc.getLongitude()) + " passed to address service";
				Log.e("MainActivity", errorString);
				e2.printStackTrace();
				return errorString;
			}

			if (addresses != null && addresses.size() > 0) {
				Address address = addresses.get(0);
				String addressText = String.format("%s, %s, %s", address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",
						address.getLocality(), address.getCountryName());
				return addressText;
			} else {
				return "Address not found";
			}
		}
	}

}