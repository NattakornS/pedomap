package com.mobile.pedomap;

public class DateData {
	private int dateId;
	private int calories;
	private int totalTime;
	private int totalStep;
	private long recordDate;
	
	public DateData() {
		// TODO Auto-generated constructor stub
	}
	
	public void setDateId(int id) {
		this.dateId = id;
	}

	public void setCalories(int cal) {
		this.calories = cal;
	}

	public void setTotalTime(int tot) {
		this.totalTime = tot;
	}

	public void setTime(long time) {
		this.recordDate = time;
	}

	public void setStep(int st) {
		this.totalStep = st;
	}

	public int getDateId() {
		return this.dateId;
	}
	
	public int getCalories() {
		return this.calories;
	}

	public int getTotalTime() {
		return this.totalTime;
	}
	public int getTotalStep() {
		return this.totalStep;
	}

	public long getTime() {
		return this.recordDate;
	}

}
