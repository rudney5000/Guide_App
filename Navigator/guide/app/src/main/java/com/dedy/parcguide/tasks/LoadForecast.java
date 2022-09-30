package com.dedy.parcguide.tasks;

import android.os.AsyncTask;

import com.dedy.parcguide.MainActivity;
import com.dedy.parcguide.callbacks.CallbackForecastLoaded;
import com.dedy.parcguide.weather.WeatherManager;
import com.dedy.parcguide.weather.cmn.ForecastDay;

import java.util.Date;
import java.util.List;


public class LoadForecast extends AsyncTask<Void, Void, Void> {

    public static final long ONE_DAY_IN_MILISEC = 1000 * 60 * 60 * 24;

    private MainActivity mMainActivity;

    private CallbackForecastLoaded mCallbackForecastLoaded;

    private List<ForecastDay> mResult;

    public LoadForecast(MainActivity mainActivity, CallbackForecastLoaded callbackForecastLoaded) {
        mMainActivity = mainActivity;
        mCallbackForecastLoaded = callbackForecastLoaded;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mCallbackForecastLoaded.showLoadingWeather();
        //mMainActivity.showLoadingDialog();

    }

    @Override
    protected Void doInBackground(Void... params) {


        if (WeatherManager.getInstance().getResult() != null) {
            mResult = WeatherManager.getInstance().getResult();
        } else {
            mResult = WeatherManager.getInstance().getForecast();
        }

        for (int i = 0; i < mResult.size(); i++) {
            mResult.get(i).setDate(new Date(new Date().getTime() + i * ONE_DAY_IN_MILISEC));
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        mCallbackForecastLoaded.doneLoadingForecast(mResult);
    }
}