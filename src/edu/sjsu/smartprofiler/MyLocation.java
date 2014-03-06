package edu.sjsu.smartprofiler;

public class MyLocation {
	
	private int id;
	private int profileId;
	private String name;
	private String address;
	private double latitude;
	private double longitude;
	private String ringerMode;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getProfileId() {
		return profileId;
	}
	public void setProfileId(int profileId) {
		this.profileId = profileId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getRingerMode() {
		return ringerMode;
	}
	public void setRingerMode(String ringerMode) {
		this.ringerMode = ringerMode;
	}
}
