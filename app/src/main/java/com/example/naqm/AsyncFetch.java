package com.example.naqm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AsyncFetch extends AsyncTask<String, Void, List<Air>> {

    public AsyncFetch(Context context) {
        this.context = context;
    }

    private Context context;
    private onResponse onResponse;

    public onResponse getOnResponse() {
        return onResponse;
    }

    public void setOnResponse(onResponse onResponse) {
        this.onResponse = onResponse;
    }

    @Override
    protected List<Air> doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        List<Air> jsonList = new ArrayList<Air>();
        try {
            URL url = new URL(params[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            int responseCode = urlConnection.getResponseCode();
            String responseMessage = urlConnection.getResponseMessage();
            if(responseCode == HttpURLConnection.HTTP_OK){
                String responseString = readStream(urlConnection.getInputStream());
                Log.e("doInBackground", responseString);
                jsonList = parseData(responseString);
            }else{
                Log.e("doInBackground", "Response code:"+ responseCode);
                Log.e("doInBackground", "Response message:"+ responseMessage);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }
        return jsonList;
    }
    @SuppressLint("SimpleDateFormat")
    private List<Air> parseData(String responseString){
        List<Air> myList = new ArrayList<Air>();
        try {
            JSONArray jsonArr = new JSONArray(responseString);
            int numberOfPoints = jsonArr.length();
            Log.e("parseData", "Found objects: "+numberOfPoints);
            for (int i = 0; i < numberOfPoints; i++) {
                JSONObject jsonObj = jsonArr.getJSONObject(i);
                Air data = new Air();
                data.setId(Integer.parseInt(jsonObj.getString("id")));
                String s = jsonObj.getString("timestamp");
                List<String> dateAndTime = Arrays.asList(s.split(" "));
                Log.e("parseData", "Id made: "+ dateAndTime.get(0)+ " " +dateAndTime.get(1));
//                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//                SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm:ss");
                data.setDate(dateAndTime.get(0));
                data.setTime(dateAndTime.get(1));
                data.setNh3(Double.parseDouble(jsonObj.getString("nh3")));
                data.setCo(Double.parseDouble(jsonObj.getString("co")));
                data.setNo2(Double.parseDouble(jsonObj.getString("no2")));
                data.setSo2(Integer.parseInt(jsonObj.getString("so2")));
                data.setDust(Integer.parseInt(jsonObj.getString("dust")));
                data.setCh4(Double.parseDouble(jsonObj.getString("ch4")));
                data.setTemperature(Integer.parseInt(jsonObj.getString("temperature")));
                data.setHumidity(Integer.parseInt(jsonObj.getString("humidity")));
                data.setNode_id(Integer.parseInt(jsonObj.getString("node_id")));
                myList.add(data);
            }
        } catch (JSONException e) {
            Log.e("parseData", "String not found "+ e);
            e.printStackTrace();
        }
        Log.e("parseData", "List made: "+myList);
        return  myList;
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    @Override
    protected void onPostExecute(List<Air> result) {
        super.onPostExecute(result);
        this.onResponse.onResponse(result);
    }

    public interface onResponse {
        public void onResponse(List<Air> object);
    }
}