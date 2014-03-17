package com.mobile.pedomap;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import name.bagi.levente.pedometer.PedometerSettings;
import name.bagi.levente.pedometer.StepService;
import name.bagi.levente.pedometer.Utils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements LocationListener,
		OnClickListener {
	private static final String TAG = "MainActivity";
	private GoogleMap map;
	private LocationManager myLocationManager;

	private Handler myHandler = new Handler();
	private long startTime = 0L;
	protected long timeInMillies = 0L;
	protected long timeSwap = 0L;
	protected long finalTime = 0L;

	/**
	 * True, when service is running.
	 */
	private boolean mIsRunning = false;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	protected TextView mSpeedValueView;
	protected TextView mCaloriesValueView;
	private TextView mStepValueView;
	private TextView mPaceValueView;
	private TextView mDistanceValueView;
	private Button startBtn;
	private Button stopBtn;
	private TextView txt_current_time;
	private SharedPreferences mSettings;
	private PedometerSettings mPedometerSettings;
	private boolean mIsMetric;
	private SQLiteDatabase db;
	private Utils mUtils;
	private TrackDS datasource;
	private DateData dateData;
	private LocationData locationData;
	private ArrayList<LocationData> locationDatas;
	private ArrayList<RecordData> recordDatas;
	private boolean recordBl = false;
	protected long timeOutInMillies;
	private boolean mQuitting = false; // Set when user selected Quit from menu,
	private Location currentLoc;
	private float accelationSquareRoot;

	private boolean locChk = false;
	private int oldTimeRecord;
	private int oldTimeLocation;
	private ActionBarDrawerToggle mDrawerToggle;

	// can be used by onPause, onStop,
	// onDestroy

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// locChk=false;
		setContentView(R.layout.activity_main);
		initLayout();
		initDataBase();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_launcher, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {

				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {

				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		
		initDrawerLayout();
		map = ((MapFragment) getFragmentManager().findFragmentById(
				R.id.mapFragment)).getMap();
		map.getUiSettings().setZoomControlsEnabled(false);
		myLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		enableGPSListener();
		mUtils = Utils.getInstance();
		// initDataBase();
	}

	private void initDataBase() {
		datasource = new TrackDS(this);
		datasource.open();
	}

	private void initLayout() {
		// Layout
		mSpeedValueView = (TextView) findViewById(R.id.txt_curent_speed);
		mDistanceValueView = (TextView) findViewById(R.id.txt_curent_distance);
		mStepValueView = (TextView) findViewById(R.id.txt_step);
		txt_current_time = (TextView) findViewById(R.id.txt_curent_time);
		mPaceValueView = (TextView) findViewById(R.id.txt_pace);
		mCaloriesValueView = (TextView) findViewById(R.id.txt_calories);
		startBtn = (Button) findViewById(R.id.btn_start);
		stopBtn = (Button) findViewById(R.id.btn_stop_track);
		startBtn.setOnClickListener(this);
		stopBtn.setOnClickListener(this);

	}

	private void initDrawerLayout() {
		ArrayList<DateData> datas = datasource.getAllDate();
		for (int i = 0; i < datas.size(); i++) {
			System.out.println("Date : " + datas.get(i).getDateId());
		}
		ListAdapter adapter = new DateListAdapter(this, datas, getResources());
		mDrawerList.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		mSettings = PreferenceManager.getDefaultSharedPreferences(this);
		mPedometerSettings = new PedometerSettings(mSettings);
		mUtils.setSpeak(mSettings.getBoolean("speak", false));

		// Read from preferences if the service was running on the last onPause
		// mIsRunning = mPedometerSettings.isServiceRunning();

		// Start the service if this is considered to be an application start
		// (last onPause was long ago)
		if (mIsRunning) {
			startStepService();
			bindStepService();
		} else if (!mIsRunning) {
			// unbindStepService();
			stopStepService();
		}
		mPedometerSettings.clearServiceRunning();
		mIsMetric = mPedometerSettings.isMetric();

		((TextView) findViewById(R.id.txt_distance_unit))
				.setText(getString(mIsMetric ? R.string.kilometers
						: R.string.miles));
		((TextView) findViewById(R.id.txt_speed_unit))
				.setText(getString(mIsMetric ? R.string.kilometers_per_hour
						: R.string.miles_per_hour));
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.i(TAG, "[ACTIVITY] onPause");
		if (mIsRunning) {
			unbindStepService();
		}
		if (mQuitting) {
			mPedometerSettings.saveServiceRunningWithNullTimestamp(mIsRunning);
		} else {
			mPedometerSettings.saveServiceRunningWithTimestamp(mIsRunning);
		}

		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.i(TAG, "[ACTIVITY] onStop");
		super.onStop();
	}

	protected void onDestroy() {
		Log.i(TAG, "[ACTIVITY] onDestroy");
		super.onDestroy();
	}

	protected void onRestart() {
		Log.i(TAG, "[ACTIVITY] onRestart");
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.clear();
		menu.add(0, 0, 0, R.string.settings)
				.setIcon(android.R.drawable.ic_menu_preferences)
				.setShortcut('8', 's')
				.setIntent(
						new Intent(this,
								name.bagi.levente.pedometer.Settings.class));
		return true;
	}

	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// switch (item.getItemId()) {
	// case 0:
	// Intent intent = new Intent(this,
	// name.bagi.levente.pedometer.Settings.class);
	// startActivity(intent);
	// return true;
	// }
	// return false;
	// }
	private void enableGPSListener() {
		if (!isGpsEnable()) {
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(intent);
						break;
					case DialogInterface.BUTTON_NEGATIVE:
						// No button clicked
						break;
					}
				}
			};
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("Setting GPS?")
					.setPositiveButton("Yes", dialogClickListener)
					.setNegativeButton("No", dialogClickListener).show();
		} else {

			myLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 1500, 10, this);
			Toast.makeText(getApplicationContext(), "Track data",
					Toast.LENGTH_SHORT).show();
		}

	}

	public boolean isGpsEnable() {
		boolean isgpsenable = false;
		// String provider = Settings.Secure.getString(getContentResolver(),
		// Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
		// if (!provider.equals("")) { // GPS is Enabled
		// isgpsenable = true;
		// }
		if (myLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			isgpsenable = true;
		} else {
			isgpsenable = false;
		}
		return isgpsenable;
	}

	@Override
	public void onLocationChanged(Location location) {
		// receive location
		// if (location == null || locationDatas == null)
		// return;
		currentLoc = location;
		// LatLng currentLoc = new
		// LatLng(location.getLatitude(),location.getLongitude());
		//
		// // LocationData data = new LocationData();
		// // data.setLatitude(currentLoc.getLatitude());
		// // data.setLongtitude(currentLoc.getLongitude());
		// //// data.setForce(accelationSquareRoot);
		// //// data.setTime(currentLoc.getTime());
		// // locationDatas.add(data);
		// // System.out.println("Data record");
		// LatLng ll = new LatLng(currentLoc.getLatitude(),
		// currentLoc.getLongitude());
		// map.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 15.0f));
		// map.addMarker(new MarkerOptions()
		// .title("Me")
		// .position(ll)
		// .icon(BitmapDescriptorFactory
		// .fromResource(R.drawable.ic_launcher)));
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	private StepService mService;

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = ((StepService.StepBinder) service).getService();

			mService.registerCallback(mCallback);
			mService.reloadSettings();

		}

		public void onServiceDisconnected(ComponentName className) {
			mService = null;
		}
	};

	private void startStepService() {
		if (!mIsRunning) {
			Log.i(TAG, "[SERVICE] Start");
			mIsRunning = true;
			startService(new Intent(MainActivity.this, StepService.class));
		}
	}

	private void bindStepService() {
		Log.i(TAG, "[SERVICE] Bind");
		bindService(new Intent(MainActivity.this, StepService.class),
				mConnection, Context.BIND_AUTO_CREATE
						+ Context.BIND_DEBUG_UNBIND);
	}

	private void unbindStepService() {
		Log.i(TAG, "[SERVICE] Unbind");
		unbindService(mConnection);
	}

	private void stopStepService() {
		Log.i(TAG, "[SERVICE] Stop");
		if (mService != null) {
			Log.i(TAG, "[SERVICE] stopService");
			stopService(new Intent(MainActivity.this, StepService.class));
		}
		mIsRunning = false;
	}

	private void resetValues(boolean updateDisplay) {
		if (mService != null && mIsRunning) {
			mService.resetValues();
		} else {
			mStepValueView.setText("00");
			mPaceValueView.setText("00");
			mDistanceValueView.setText("00");
			mSpeedValueView.setText("00");
			mCaloriesValueView.setText("00");
			txt_current_time.setText("00:00");
			SharedPreferences state = getSharedPreferences("state", 0);
			SharedPreferences.Editor stateEditor = state.edit();
			if (updateDisplay) {
				stateEditor.putInt("steps", 0);
				stateEditor.putInt("pace", 0);
				stateEditor.putFloat("distance", 0);
				stateEditor.putFloat("speed", 0);
				stateEditor.putFloat("calories", 0);
				stateEditor.commit();
			}
		}
	}

	@Override
	public void onClick(View v) {
		if (startBtn.equals(v)) {
			if (startBtn.getText().equals("Start")) {
				startBtn.setText("Stop");
				map.clear();
				locChk = true;
				// mStepValue = 0;
				// mPaceValue = 0;
				oldTimeRecord = -1;
				oldTimeLocation = -1;
				recordBl = true;
				Date date = new Date();
				long time = date.getTime();
				dateData = datasource.createDate(time);

				System.out.println("RID : " + dateData.getDateId()
						+ "\nTime : " + dateData.getTime());
				locationDatas = new ArrayList<LocationData>();
				recordDatas = new ArrayList<RecordData>();

				startTime = SystemClock.uptimeMillis();
				myHandler.postDelayed(updateTimerMethod, 0);
				Toast.makeText(this, "Start", 1000).show();
				startStepService();
				bindStepService();
				return;
			} else if (startBtn.getText().equals("Stop") && mIsRunning) {
				startBtn.setText("Start");
				recordBl = false;
				locChk = false;

				dateData.setCalories(mCaloriesValue);
				dateData.setStep(mStepValue);

				long totalTime = SystemClock.uptimeMillis() - startTime;
				totalTime = timeSwap + timeInMillies;
				dateData.setTotalTime((int) Math
						.ceil((totalTime / 1000 / 60.0)));
				dateData = datasource.updateDate(dateData);

				File dbFile = getDatabasePath(PedoSQLiteHelper.DATABASE_NAME);
				Log.i("DB path", dbFile.getAbsolutePath());
				// datasource.createLocation(locationDatas, dateData);
				datasource.copyDB();
				initDrawerLayout();

				myHandler.removeCallbacks(updateTimerMethod);
				resetValues(true);
				Toast.makeText(this, "Stop", 1000).show();
				unbindStepService();
				stopStepService();
				return;
			}

		} else if (stopBtn.equals(v)) {
			mDrawerLayout.openDrawer(mDrawerList);
		}
		// switch (v.getId()) {
		// case 0:
		//
		// case 1:

		// case MENU_RESET:
		// resetValues(true);
		// return true;
		// case MENU_QUIT:
		// resetValues(false);
		// unbindStepService();
		// stopStepService();
		// mQuitting = true;
		// finish();
		// return true;
		// }
	}

	// TODO: unite all into 1 type of message
	private StepService.ICallback mCallback = new StepService.ICallback() {
		public void stepsChanged(int value) {
			mHandler.sendMessage(mHandler.obtainMessage(STEPS_MSG, value, 0));
		}

		public void paceChanged(int value) {
			mHandler.sendMessage(mHandler.obtainMessage(PACE_MSG, value, 0));
		}

		public void distanceChanged(float value) {
			mHandler.sendMessage(mHandler.obtainMessage(DISTANCE_MSG,
					(int) (value * 1000), 0));
		}

		public void speedChanged(float value) {
			mHandler.sendMessage(mHandler.obtainMessage(SPEED_MSG,
					(int) (value * 1000), 0));
		}

		public void caloriesChanged(float value) {
			mHandler.sendMessage(mHandler.obtainMessage(CALORIES_MSG,
					(int) (value), 0));
		}
	};
	protected int mCaloriesValue;
	protected float mSpeedValue;
	protected int mPaceValue;
	protected int mStepValue;
	protected float mDistanceValue;

	protected int imCaloriesValue = 0;
	protected float imSpeedValue = 0;
	protected int imPaceValue = 0;
	protected int imStepValue = 0;
	protected float imDistanceValue = 0;

	private static final int STEPS_MSG = 1;
	private static final int PACE_MSG = 2;
	private static final int DISTANCE_MSG = 3;
	private static final int SPEED_MSG = 4;
	private static final int CALORIES_MSG = 5;

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			System.out.println(msg.arg1);
			switch (msg.what) {
			case STEPS_MSG:
				mStepValue = (int) msg.arg1;
				// imStepValue = (int) msg.arg1;
				mStepValueView.setText("" + mStepValue);
				break;
			case PACE_MSG:
				mPaceValue = msg.arg1;
				imPaceValue += mPaceValue;
				if (mPaceValue <= 0) {
					mPaceValueView.setText("0");
				} else {
					mPaceValueView.setText("" + (int) mPaceValue);
				}
				break;
			case DISTANCE_MSG:
				mDistanceValue = ((int) msg.arg1) / 1000f;
				// imDistanceValue = ((int) msg.arg1) / 1000f;
				if (mDistanceValue <= 0) {
					mDistanceValueView.setText("0");
				} else {
					mDistanceValueView
							.setText(("" + (mDistanceValue + 0.000001f))
									.substring(0, 5));
				}
				break;
			case SPEED_MSG:
				mSpeedValue = ((int) msg.arg1) / 1000f;
				imSpeedValue += mSpeedValue;
				if (mSpeedValue <= 0) {
					mSpeedValueView.setText("0");
				} else {
					mSpeedValueView.setText(("" + (mSpeedValue + 0.000001f))
							.substring(0, 4));
				}
				break;
			case CALORIES_MSG:
				mCaloriesValue = msg.arg1;
				// imCaloriesValue = msg.arg1;
				if (mCaloriesValue <= 0) {
					mCaloriesValueView.setText("0");
				} else {
					mCaloriesValueView.setText("" + (int) mCaloriesValue);
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}

	};
	private long lastUpdate;

	public void onBackPressed() {
		// resetValues(false);
		// unbindStepService();
		stopStepService();
		myHandler.removeCallbacks(updateTimerMethod);
		mQuitting = true;
		finish();

	};

	// private void getAccelerometer(SensorEvent event) {
	// float[] values = event.values;
	// // Movement
	// float x = values[0];
	// float y = values[1];
	// float z = values[2];
	//
	// accelationSquareRoot = (x * x + y * y + z * z)
	// / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
	// long actualTime = System.currentTimeMillis();
	// if (accelationSquareRoot >= 2) //
	// {
	// System.out.println("accelero value : " + accelationSquareRoot);
	// if (actualTime - lastUpdate < 200) {
	//
	// return;
	// }
	// lastUpdate = actualTime;
	// Toast.makeText(this, "Device was shuffed", Toast.LENGTH_SHORT)
	// .show();
	//
	// }
	// }

	// Timer Thread
	private Runnable updateTimerMethod = new Runnable() {

		public void run() {
			timeInMillies = SystemClock.uptimeMillis() - startTime;
			finalTime = timeSwap + timeInMillies;

			int seconds = (int) (finalTime / 1000);

			seconds = seconds % 60;
			// seconds*=12;
			// seconds=seconds>=59?59:seconds;
			int minutes = (int) (finalTime / 60000);
			minutes = minutes % 60;
			// minutes*=12;
			// minutes=minutes>=59?0:minutes;
			int hours = (int) (finalTime / 3600000);
			// System.out.println("s : "+seconds+" m : "+minutes+" h : "+hours);

			// int milliseconds = (int) (finalTime % 1000);
			txt_current_time.setText("" + String.format("%02d", hours) + ":"
					+ String.format("%02d", minutes) + ":"
					+ String.format("%02d", seconds));
			myHandler.postDelayed(this, 0);
			// System.out.println(currentLoc);
			// System.out.println(locChk);

			if (currentLoc != null && locChk) {
				locChk = false;
				locationData = new LocationData();
				locationData.setDateId(dateData.getDateId());
				locationData.setLatitude(currentLoc.getLatitude());
				locationData.setLongtitude(currentLoc.getLongitude());
				locationData.setRoundNo((int) hours);
				// System.out.println(locationData);
				locationData = datasource
						.createLocation(dateData, locationData);

				// LocationData data = new LocationData();
				// data.setLatitude(currentLoc.getLatitude());
				// data.setLongtitude(currentLoc.getLongitude());
				// // data.setForce(accelationSquareRoot);
				// // data.setTime(currentLoc.getTime());
				// locationDatas.add(data);
				// System.out.println("Data record");
				LatLng ll = new LatLng(currentLoc.getLatitude(),
						currentLoc.getLongitude());
				map.animateCamera(CameraUpdateFactory.newLatLngZoom(ll, 15.0f));
				map.addMarker(new MarkerOptions()
						.title("Me")
						.position(ll)
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.ic_launcher)));
			}
			int recordModulus = 5;
			if (seconds % recordModulus == 4 && oldTimeRecord != seconds) {
				oldTimeRecord = seconds;
				RecordData record = new RecordData();
				record.setMinuteTime((int) minutes);
				record.setCalories(mCaloriesValue - imCaloriesValue);
				imCaloriesValue = mCaloriesValue;
				record.setSpeed(mSpeedValue / (double) recordModulus);
				imSpeedValue = 0;
				record.setStep(mStepValue - imStepValue);
				imStepValue = mStepValue;
				record.setPaces(mPaceValue / recordModulus);
				imPaceValue = 0;
				record.setDistance(mDistanceValue - imDistanceValue);
				imDistanceValue = mDistanceValue;
				record = datasource.createRecord(locationData, record);
			}
			if (seconds % 20 == 19 && oldTimeLocation != seconds) {
				locChk = true;
				oldTimeLocation = seconds;
			}

		}
	};

	public void onItemClick(int mPosition) {
		Intent intent = new Intent(this, ResultActivity.class);
		intent.putExtra("position", mPosition + 1);
		startActivity(intent);
	}
}
