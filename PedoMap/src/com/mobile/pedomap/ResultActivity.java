package com.mobile.pedomap;

import java.util.ArrayList;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.internal.gr;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class ResultActivity extends Activity {

	private GoogleMap map;
	private int position;
	private TextView txtTime;
	private TextView txtCalories;
	private TextView txtStep;
	private ArrayList<LocationData> locDatas;
	private TrackDS ds;
	private DateData dateData;
	private ArrayList<RecordData> records;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);
		Bundle extra = getIntent().getExtras();
		position = extra.getInt("position");
		System.out.println("List position : " + position);
		map = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.mapResultFragment)).getMap();
		txtTime = (TextView) findViewById(R.id.txttime);
		txtStep = (TextView) findViewById(R.id.txtstep);
		txtCalories = (TextView) findViewById(R.id.txtcalories);
		ds = new TrackDS(this);
		ds.open();
		System.out.println("Position : " + position);
		dateData = ds.getDateDataById(position);

		txtTime.setText("" + (int) dateData.getTotalTime());
		txtStep.setText("" + (int) dateData.getTotalStep());
		txtCalories.setText("" + (int) dateData.getCalories());

		locDatas = ds.getAllLocationByDateId(position);
		System.out.println("locDatas : " + locDatas.size());
		for (int i = 0; i < locDatas.size(); i++) {

			LatLng ll = new LatLng(locDatas.get(i).getLatitude(), locDatas.get(
					i).getLongtitude());
			map.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 15.0f));
			map.addMarker(new MarkerOptions()
					.title("" + (i + 1))
					.position(ll)
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.ic_launcher)));
		}

		map.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {
				// TODO Auto-generated method stub
				System.out.println(marker.getTitle());
				System.out.println(locDatas.get(Integer.valueOf(marker.getTitle())-1).getLocationId());
				initGraphView(locDatas.get(Integer.valueOf(marker.getTitle())-1).getLocationId());
				return false;
			}
		});
		initGraphView();

	}

	private void initGraphView() {
		records = ds.getRecordsByDateId(position);
		ArrayList<GraphViewData> paceDatas = new ArrayList<GraphView.GraphViewData>();
		for (int i = 0; i < records.size(); i++) {
			if (records.get(i) == null) {
				break;
			}
			paceDatas.add(new GraphViewData(i + 1, records.get(i).getPaces()));
		}
		System.out.println("Pace size : " + paceDatas.size());
		GraphViewSeries paceSeries = new GraphViewSeries(
				paceDatas.toArray(new GraphViewData[paceDatas.size()]));
		GraphView graphView = new LineGraphView(this,
				"Pace per Minute / Time(Minute)");
		graphView.addSeries(paceSeries);
		LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
		layout.addView(graphView);
	}
	private void initGraphView(int id) {
		records = ds.getAllRecordByLocationId(id);
		ArrayList<GraphViewData> paceDatas = new ArrayList<GraphView.GraphViewData>();
		for (int i = 0; i < records.size(); i++) {
			if (records.get(i) == null) {
				break;
			}
			paceDatas.add(new GraphViewData(i + 1, records.get(i).getPaces()));
		}
		System.out.println("Pace size : " + paceDatas.size());
		GraphViewSeries paceSeries = new GraphViewSeries(
				paceDatas.toArray(new GraphViewData[paceDatas.size()]));
		GraphView graphView = new LineGraphView(this,
				"Pace per Minute / Time(Minute)");
		graphView.addSeries(paceSeries);
		LinearLayout layout = (LinearLayout) findViewById(R.id.graph1);
		layout.removeAllViewsInLayout();
		if (paceDatas.size()!=0) {
			layout.addView(graphView);
		}
	}

	@Override
	protected void onResume() {
		if (ds != null)
			ds.open();
		
		super.onResume();
	}

	@Override
	protected void onPause() {
		if (ds != null)
			ds.close();
		super.onPause();
	}

}
