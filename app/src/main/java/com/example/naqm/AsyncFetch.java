package com.example.naqm;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class AsyncFetch extends AsyncTask<String, Void, JSONObject> {

    public AsyncFetch(Context context) {
        this.context = context;
    }

    private Context context;
    private JSONObject jsonObject;
    private onResponse onResponse;

    public onResponse getOnResponse() {
        return onResponse;
    }

    public void setOnResponse(onResponse onResponse) {
        this.onResponse = onResponse;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(params[0]);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            int responseCode = urlConnection.getResponseCode();
            String responseMessage = urlConnection.getResponseMessage();
            if(responseCode == HttpURLConnection.HTTP_OK){
                String responseString = readStream(urlConnection.getInputStream());
                Log.e("CatalogClient-Response", responseString);
//                books = parseBookData(responseString);
            }else{
                Log.e("CatalogClient", "Response code:"+ responseCode);
                Log.e("CatalogClient", "Response message:"+ responseMessage);
            }

//            jsonObject = new JSONObject(result);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();}
//        } catch (JSONException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
        finally {
            if(urlConnection != null)
                urlConnection.disconnect();
        }
        return jsonObject;
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
    protected void onPostExecute(JSONObject result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        this.onResponse.onResponse(result);
    }

    public interface onResponse {
        public void onResponse(JSONObject object);
    }
}