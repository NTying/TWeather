package com.tying.tweather.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.tying.tweather.domain.City;
import com.tying.tweather.domain.County;
import com.tying.tweather.domain.Province;
import com.tying.tweather.gson.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtility {

    /**
     * 解析和处理服务器返回的省级数据
     * @param response：服务器响应结果
     * @return
     */
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {

            try {

                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {

                    JSONObject provinceObj = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObj.getString("name"));
                    province.setProvinceCode(provinceObj.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     * @param response：服务器响应结果
     * @param provinceId：省 Id
     * @return
     */
    public static boolean handleCityResponse(String response, int provinceId) {

        if (!TextUtils.isEmpty(response)) {

            try {

                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {

                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     * @param response：服务器响应结果
     * @param cityId： 市 id
     * @return
     */
    public static boolean handleCountyResponse(String response, int cityId) {

        if (!TextUtils.isEmpty(response)) {

            try {

                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {

                    JSONObject countyObj = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObj.getString("name"));
                    county.setWeatherId(countyObj.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 将返回的 JSON 数据解析成 Weather 实体类
     * @param response
     * @return
     */
    public static Weather handleWeatherResponse(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent, Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
