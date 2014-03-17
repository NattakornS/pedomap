package com.mobile.pedomap;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DateListAdapter extends BaseAdapter implements OnClickListener {

	private Activity activity;
//	@SuppressWarnings("rawtypes")
	private ArrayList<DateData> data;
	private static LayoutInflater inflater = null;
	public Resources res;
	DateData tempValues = null;
	int i = 0;
	private Locale thLocale = null;
	private SimpleDateFormat sdf;
//	@SuppressWarnings("rawtypes")
	public DateListAdapter(Activity a, ArrayList<DateData> d, Resources resLocal) {

		activity = a;
		data = d;
		res = resLocal;

		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Locale[] l = Locale.getAvailableLocales();
		for (int i = 0; i < l.length; i++) {
			if (l[i].getDisplayName().endsWith("Thai")) {
				thLocale = l[i];
			}
		}
		if(thLocale==null){
			thLocale = Locale.getDefault();
		}
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", thLocale);

	}
	@Override
	public int getCount() {
		if (data.size() <= 0)
			return 1;
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View vi = convertView;
		ViewHolder holder;

		if (convertView == null) {

			vi = inflater.inflate(R.layout.tabitem, null);
			holder = new ViewHolder();
			holder.text = (TextView) vi.findViewById(R.id.text);
			holder.timeTxt = (TextView) vi.findViewById(R.id.timeTxt);
			vi.setTag(holder);
		} else
			holder = (ViewHolder) vi.getTag();

		if (data.size() <= 0) {
			holder.text.setText("No Data");

		} else {
			tempValues = null;
			tempValues = (DateData) data.get(position);

			holder.text.setText("Date : " + tempValues.getDateId());
			holder.timeTxt.setText("Time : "
					+ sdf.format(new Date(tempValues.getTime())));

			vi.setOnClickListener(new OnItemClickListener(position));
		}
		return vi;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		Log.v("CustomAdapter", "=====Row button clicked");

	}

	public static class ViewHolder {

		public TextView text;
		public TextView timeTxt;

	}

	private class OnItemClickListener implements OnClickListener {
		private int mPosition;

		OnItemClickListener(int position) {
			mPosition = position;
		}

		@Override
		public void onClick(View arg0) {
			MainActivity sct = (MainActivity) activity;
			sct.onItemClick(mPosition);
		}
	}
}
