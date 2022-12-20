package com.tying.tweather.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tying.tweather.R;
import com.tying.tweather.databinding.ActivityMainBinding;
import com.tying.tweather.databinding.ActivityWeatherBinding;
import com.tying.tweather.gson.Forecast;
import com.tying.tweather.gson.Weather;
import com.tying.tweather.util.HttpUtils;
import com.tying.tweather.util.JsonUtility;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity{

    private ActivityWeatherBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeatherBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(view);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            // 如果有缓存，直接解析天气数据
            Weather weather = JsonUtility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        } else {
            // 无缓存数据，去服务器查询
            String weatherId = getIntent().getStringExtra("weather_id");
            binding.weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }

        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(binding.bingPicImg);
        } else {
            loadBingPic();
        }
    }

    /**
     * 根据天气 id 获取对应的城市天气信息
     * @param weatherId
     */
    public void requestWeather(final String weatherId) {
        loadBingPic();

        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&a53b3bd44a644c83a62fd9a17a777d7a";
        HttpUtils.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = JsonUtility.handleWeatherResponse(responseText);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + " ℃";
        String weatherInfo = weather.now.more.info;
        binding.titleLayout.titleCity.setText(cityName);
        binding.titleLayout.titleUpdateTime.setText(updateTime);
        binding.nowLayout.degreeText.setText(degree);
        binding.nowLayout.weatherInfoText.setText(weatherInfo);
        binding.forecastLayout.forecastLayout.removeAllViews();

        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, binding.forecastLayout.forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);

            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            binding.forecastLayout.forecastLayout.addView(view);
        }

        if (weather.aqi != null) {
            binding.aqiLayout.aqiText.setText(weather.aqi.city.aqi);
            binding.aqiLayout.pm25Text.setText(weather.aqi.city.pm25);
        }

        String comfort = "舒适度: " + weather.suggestion.comfort.info;
        String carWash = "洗车指数: " + weather.suggestion.carWash.info;
        String sport = "运动建议: " + weather.suggestion.sport.info;
        binding.suggestionLayout.comfortText.setText(comfort);
        binding.suggestionLayout.carWashText.setText(carWash);
        binding.suggestionLayout.sportText.setText(sport);

        binding.weatherLayout.setVisibility(View.VISIBLE);
   }

   private void loadBingPic() {

        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtils.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(binding.bingPicImg);
                    }
                });
            }
        });
   }
}