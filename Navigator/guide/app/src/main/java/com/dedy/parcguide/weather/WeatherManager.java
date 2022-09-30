package com.dedy.parcguide.weather;

import android.util.Log;

import com.dedy.parcguide.network.NetworkFetcher;
import com.dedy.parcguide.settings.AppSettings;
import com.dedy.parcguide.weather.cmn.ForecastDay;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class WeatherManager {

    public static String FORECAST_QUERY = "http://api.openweathermap.org/data/2.5/forecast/daily?q=%s&mode=json&units=metric&APPID=%s";

    public static final String FORECAST_DAY_ICON = "http://openweathermap.org/img/w/%s.png";

    private static WeatherManager instance;

    private List<ForecastDay> mResult;

    public static WeatherManager getInstance() {

        if (instance == null) {
            instance = new WeatherManager();
        }

        return instance;
    }

    private WeatherManager() {
    }

    public List<ForecastDay> getForecast() {

        List<ForecastDay> forecastDays = new ArrayList<>();

        try {
            String urlToExecute = getForecastUrl();

            Log.i("Weather_Data", "Execute weather url - " + urlToExecute);

            JSONObject response = NetworkFetcher.executeRequest(urlToExecute, false);

            Log.i("Weather_Data", "Response of the request - " + response.toString());

            JSONArray forecastList = response.optJSONArray("list");

            Log.i("Weather_Data", "Successfully get results with count " + forecastList.length());

            for (int i = 0; i < forecastList.length(); i++) {
                ForecastDay forecastDay = new ForecastDay(forecastList.getJSONObject(i));

                forecastDays.add(forecastDay);
            }
        } catch (Exception e) {
            Log.i("Weather_Data", "Exception requesting  weather " + e.getClass());
        }

        this.mResult = forecastDays;

        return mResult;
    }

    public List<ForecastDay> getResult() {
        return mResult;
    }

    private String getForecastUrl() {

        String realUrl = String.format(FORECAST_QUERY, AppSettings.TOWN + "," + AppSettings.COUNTRY, AppSettings.OPEN_WEATHER_MAP_KEY);

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        return "" + day + "-" + month + "-" + year + ";" + realUrl;
    }
}
