package com.example.java_based_client;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static java.net.HttpURLConnection.HTTP_OK;

public class GetAccessTokenAsyncTask extends AsyncTask<String, String, String> {

    private TaskParams mParams;
    private static final String LOG_TAG = "GetAccessTokenAsyncTask";

    public void setTaskParams(TaskParams params) {
        this.mParams = params;
    }

    public class TaskParams {
        public String mClientId;
        public String mClientSecret;
        public String mTenantId;
        public IAccessTokenListener mAccessTokenListener;

        public TaskParams(String clientId, String clientSecret, String tenantId, IAccessTokenListener accessTokenListener) {
            this.mClientId = clientId;
            this.mClientSecret = clientSecret;
            this.mTenantId = tenantId;
            this.mAccessTokenListener = accessTokenListener;
        }
    }

    public interface IAccessTokenListener {
        void onAccessTokenObtained(String accessToken);
    }

    @Override
    protected String doInBackground(String[] objects) {

        String accessToken = null;

        try {
            StringBuilder urlStringBuilder = new StringBuilder();
            urlStringBuilder.append("https://login.windows.net/");
            urlStringBuilder.append(mParams.mTenantId);
            urlStringBuilder.append("/oauth2/token");
            URL tokenUrl = new URL(urlStringBuilder.toString());


            StringBuilder formStringBuilder = new StringBuilder();
            formStringBuilder.append("grant_type=client_credentials&resource=https://cognitiveservices.azure.com/&client_id=");
            formStringBuilder.append(mParams.mClientId);
            formStringBuilder.append("&client_secret=");
            formStringBuilder.append(mParams.mClientSecret);
            String form = formStringBuilder.toString();

            HttpURLConnection httpURLConnection = (HttpURLConnection) tokenUrl.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            httpURLConnection.setDoOutput(true);

            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.writeBytes(form);
            dataOutputStream.flush();
            dataOutputStream.close();

            int responseCode = httpURLConnection.getResponseCode();

            if (responseCode == HTTP_OK) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuffer response = new StringBuffer();


                String line = bufferedReader.readLine();
                while (!TextUtils.isEmpty(line)) {
                    response.append(line);
                    line = bufferedReader.readLine();
                }

                bufferedReader.close();

                JSONObject accessTokenJson = new JSONObject(response.toString());
                accessToken = accessTokenJson.getString("access_token");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i(LOG_TAG, "Accesstoken: " + accessToken);
        return accessToken;
    }

    @Override
    protected void onPostExecute(String accessToken) {
        super.onPostExecute(accessToken);
        if (mParams.mAccessTokenListener != null) {
            mParams.mAccessTokenListener.onAccessTokenObtained(accessToken);
        }
    }
}
