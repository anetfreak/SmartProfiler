package edu.sjsu.smartprofiler;

public class Profile {
	
	private int id;
	private String name;
	private Ringer_Mode ringerMode;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Ringer_Mode getRingerMode() {
		return ringerMode;
	}
	public void setRingerMode(Ringer_Mode ringerMode) {
		this.ringerMode = ringerMode;
	}
	
}
