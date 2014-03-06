package edu.sjsu.smartprofiler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

public class LocationActivity extends FragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
	
	LocationClient locationClient;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);
		locationClient = new LocationClient(this, this, this);
		
		Button addLocation = (Button) findViewById(R.id.btnAdd);
		addLocation.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				saveLocation();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.location, menu);
		return true;
	}
	
	public void saveLocation() {
		DBHandler handler = new DBHandler(this, null, null, 1);
		MyLocation location = new MyLocation();
		Context mContext = this;
		List<Address> addresses = new ArrayList<Address>();
		Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
		
		EditText locationName = (EditText) findViewById(R.id.txtLocationName);
		EditText txtAddress = (EditText) findViewById(R.id.txtAddress);
		Spinner profile = (Spinner) findViewById(R.id.spinnerProfile);
		
		try {
			addresses = geocoder.getFromLocationName(txtAddress.getText().toString(), 1);
			location.setName(locationName.getText().toString());
			location.setProfileId(handler.getProfileId(profile.getSelectedItem().toString()));
			location.setAddress(txtAddress.getText().toString());
			location.setLatitude(addresses.get(0).getLatitude());
			location.setLongitude(addresses.get(0).getLongitude());
			handler.addLocation(location);
			
		} catch (IOException e) {
			Toast.makeText(this, "Invalid Address. Please re-enter", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		locationClient.connect();
	}
	
	@Override
	protected void onStop() {
		locationClient.disconnect();
		super.onStop();
	}
	
	@Override
	public void onConnected(Bundle dataBundle) {
	}
	
	@Override
	public void onDisconnected() {
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Toast.makeText(this, "Connection Failure : " + connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
	}

}
