package com.dedy.parcguide.callbacks;

import com.dmbteam.cityguide.weather.cmn.ForecastDay;

import java.util.List;

/**
 * Created by dedy ngueko on 12/3/2020.
 */
public interface CallbackForecastLoaded {

    void doneLoadingForecast(List<ForecastDay> result);
    void showLoadingWeather();
}
