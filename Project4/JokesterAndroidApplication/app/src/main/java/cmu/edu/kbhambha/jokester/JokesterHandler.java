/**
 * Name: Kanishka Bhambhani
 * Andrew Id: kbhambha
 */
package cmu.edu.kbhambha.jokester;

import android.os.AsyncTask;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

/**
 * class JokesterHandler
 * The class is used to connect to the heroku api
 * Send the post call to the api and get the clean response back
 */
public class JokesterHandler {
    MainActivity ma = null;

    /**
     * function getJokes()
     * The function get jokes is used to get the jokes from the API and it is asynchronously called in the background
     * @param request
     * @param ma
     */
    public void getJokes(JSONObject request, MainActivity ma) {
        this.ma = ma;
        new AsyncGetJokes().execute(request);
    }

    /**
     * class AsyncGetJokes
     * The class is created to implement asynchronization
     */
    private class AsyncGetJokes extends AsyncTask<JSONObject, Void, JSONArray> {

        /**
         * function doInBackground
         * The function is internal to the asyncTask class, to process the functionality in the bg
         * @param jsonObjects
         * @return
         */
        @Override
        protected JSONArray doInBackground(JSONObject... jsonObjects) {
            return getJokes(jsonObjects[0]);
        }

        /**
         * function onPostExecute
         * The function is internal to AsyncTask to execute and call main activity after the job is completed
         * @param response
         */
        protected void onPostExecute(JSONArray response) {
            ma.sendJokes(response);
        }

        /**
         * function getJokes
         * The function uses okHttp library and does a post request to the heroku api
         * The response is received in the response body
         * @param jsonObject the parameter data sent by the user
         * @return the array of jokes
         */
        private JSONArray getJokes(JSONObject jsonObject) {
            //Buiding the httpClient
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .build();
            //Setting the request body with the user data
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString()); // new
            //Post call to the api
            Request request = new Request.Builder()
                    .url("https://still-falls-98039.herokuapp.com/jokes")
                    .post(body)
                    .build();
            String responseText = "";
            try {
                //Getting the response after the client is called
                Response response = client.newCall(request).execute();
                //setting the data to string and returning as jaon array
                responseText = response.body().string();
                return new JSONArray(responseText);
            } catch (SocketTimeoutException | JSONException e) { //If any exception due to IO or JSON
                e.printStackTrace();
                System.out.println("Error: " + responseText);
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
