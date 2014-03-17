package com.mobile.pedomap;

public class LocationData {
	int locationId;
	int dateId;
	int roundNo;
	double latitude;
	double longtitude;
	
	public LocationData() {
		// TODO Auto-generated constructor stub
	}

	public void setLocationId(int id) {
		this.locationId=id;
	}
	public void setDateId(int id) {
		this.dateId=id;
	}
	public void setRoundNo(int rno) {
		this.roundNo=rno;
	}
	public void setLatitude(double lat) {
		this.latitude=lat;
	}
	public void setLongtitude(double lng) {
		this.longtitude=lng;
	}

	public int getLocationId() {
		return this.locationId;
	}
	public int getDateId() {
		return this.dateId;
	}
	public int getRoundNo() {
		return this.roundNo;
	}
	public double getLatitude() {
		return this.latitude;
	}
	public double getLongtitude() {
		return this.longtitude;
	}

}
