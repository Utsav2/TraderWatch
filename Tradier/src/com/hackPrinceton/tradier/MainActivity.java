package com.hackPrinceton.tradier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import retrofit.RestAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	private static final int ICON_KEY = 0;
	private static final int TEMP_KEY = 1;
	private static final UUID TRADIER_UUID = UUID
			.fromString("ca0a8c62-b790-11e3-9f3b-425861b86ab66");
	final String WATCHLISTS = "https://api.tradier.com/v1/watchlists";
	final String SPECIFIC_WATCHLIST = "https://api.tradier.com/v1/watchlists/my-watchlist";
	final int MAX_WATCHLISTS = 8;
	final int MAX_STOCKS = 20;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent i = new Intent(this, StockUpdate.class);
		PendingIntent pintent = PendingIntent.getService(this, 0, i, 0);
		AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				5 * 1000, pintent);


		this.startService(i);

	}
	

}	
