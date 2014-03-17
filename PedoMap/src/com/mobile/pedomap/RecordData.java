package com.mobile.pedomap;

public class RecordData {
 	private int recordId;
 	private int locationId;
 	private int minuteTime;
 	private double distance;
 	private double speed;
 	private int steps;
 	private int paces;
 	private int calories;

	public RecordData() {
		// TODO Auto-generated constructor stub
	}
	
	public void setRecordId(int id) {
		this.recordId=id;
	}
	public void setLocationId(int id) {
		this.locationId=id;
	}
	public void setMinuteTime(int min) {
		this.minuteTime=min;
	}
	public void setDistance(double force) {
		this.distance= force;
	}
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	public void setStep(int step) {
		this.steps=step;
	}
	public void setPaces(int pace) {
		this.paces=pace;
	}
	public void setCalories(int cal) {
		this.calories=cal;
	}
	
	public int getRecordId() {
		return this.recordId;
	}
	public int getLocationId() {
		return this.locationId;
	}
	public int getMinuteTime() {
		return this.minuteTime;
	}
	public double getDistance() {
		return this.distance;
	}
	public double getSpeed() {
		return this.speed;
	}
	public int getStep() {
		return this.steps;
	}
	public int getPaces() {
		return this.paces;
	}
	public int getCalories() {
		return this.calories;
	}
}
