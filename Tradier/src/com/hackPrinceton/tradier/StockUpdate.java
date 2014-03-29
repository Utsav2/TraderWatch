package com.hackPrinceton.tradier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class StockUpdate extends Service {

	private static final int ICON_KEY = 0;
	private static final int TEMP_KEY = 1;
	private static final UUID TRADIER_UUID = UUID
			.fromString("ca0a8c62-b790-11e3-9f3b-425861b86ab6");
	final String WATCHLISTS = "https://api.tradier.com/v1/watchlists";
	final String SPECIFIC_WATCHLIST = "https://api.tradier.com/v1/watchlists/default";
	final int MAX_WATCHLISTS = 8;
	final int MAX_STOCKS = 20;
	String builder = "";
	String prev = "zero";

	JSONArray namesArray = null;
	JSONArray stocksArray = null;
	String MARKET = "https://api.tradier.com/v1/markets/quotes?symbols=";

	public void onCreate() {
		RequestQueue queue = Volley.newRequestQueue(this);
		// requestWithSomeHttpHeaders(WATCHLISTS);
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		boolean connected = PebbleKit.isWatchConnected(getApplicationContext());

		Toast.makeText(this, "HEY", Toast.LENGTH_SHORT).show();

		// getWatchlists(WATCHLISTS);
		getNames(SPECIFIC_WATCHLIST);

		return START_STICKY;

	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void getWatchlists(final String url) {
		RequestQueue queue = Volley.newRequestQueue(this);
		StringRequest postRequest = new StringRequest(Request.Method.GET, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {

							JSONObject obj = new JSONObject(response);

							JSONArray watchlists = (obj
									.getJSONObject("watchlists"))
									.getJSONArray("watchlist");

						} catch (Throwable t) {
							Log.e("My App",
									"Could not parse malformed JSON: \""
											+ response + "\"");
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.d("ERROR", "error => " + error.toString());
					}
				}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("Accept", "application/json");
				params.put("Authorization",
						"Bearer YZtE5zbjX7GGBgDMJvVzo0vhTfnt");

				return params;
			}
		};
		queue.add(postRequest);

	}

	public void getNames(final String url) {
		RequestQueue queue = Volley.newRequestQueue(this);
		StringRequest postRequest = new StringRequest(Request.Method.GET, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {

							JSONObject obj = new JSONObject(response);
							JSONArray names = obj.getJSONObject("watchlist")
									.getJSONObject("items")
									.getJSONArray("item");
							namesArray = names;
							for (int i = 0; i < names.length(); i++) {
								build(names.getJSONObject(i)
										.getString("symbol"));
							}
							clear();
						} catch (Throwable t) {
							Log.e("My App",
									"Could not parse malformed JSON: \""
											+ response + "\"");
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.d("ERROR", "error => " + error.toString());
					}
				}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("Accept", "application/json");
				params.put("Authorization",
						"Bearer YZtE5zbjX7GGBgDMJvVzo0vhTfnt");

				return params;
			}
		};
		queue.add(postRequest);

	}

	public void getStocks(final String url) {
		RequestQueue queue = Volley.newRequestQueue(this);
		StringRequest postRequest = new StringRequest(Request.Method.GET, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							JSONObject obj = new JSONObject(response);

							JSONArray stocks = obj.getJSONObject("quotes")
									.getJSONArray("quote");

							for (int i = 0; i < stocks.length(); i++) {

								send(stocks.getJSONObject(i)
										.getString("symbol")
										+ " : "
										+ stocks.getJSONObject(i).getString(
												"last")
										+ "$"
										+ compare(stocks.getJSONObject(i)
												.getString("last"),
												stocks.getJSONObject(i)
														.getString("open")));
							}

						} catch (Throwable t) {
							Log.e("My App",
									"Could not parse malformed JSON: \""
											+ response + "\"");
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// TODO Auto-generated method stub
						Log.d("ERROR", "error => " + error.toString());
					}
				}) {
			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> params = new HashMap<String, String>();
				params.put("Accept", "application/json");
				params.put("Authorization",
						"Bearer YZtE5zbjX7GGBgDMJvVzo0vhTfnt");

				return params;
			}
		};
		queue.add(postRequest);

	}

	void build(String symbol) {
		builder += (symbol + ",");
	}

	void clear() {
		builder = MARKET + builder.substring(0, builder.length() - 2);
		getStocks(builder);
		builder = "";

	}

	public void send(String data) {

		PebbleDictionary p = new PebbleDictionary();
		Log.e("DATA", data);
		p.addString(0x0, data);
		PebbleKit.sendDataToPebble(getApplicationContext(), TRADIER_UUID, p);

	}

	public String compare(String now, String dayStart) {

		double abhi = Double.parseDouble(now);
		double pehle = Double.parseDouble(dayStart);
		Double change = Math.abs((abhi - pehle) / pehle) * 100;
		if (change > 5) {

			if (abhi > pehle) {
				return " ^ " + change.toString().substring(0, 3);
			} else if (abhi < pehle) {
				return " v " + change;
			}
		}
		return "";

	}
}
