package com.softbankrobotics.pepperapptemplate.Utils;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Debug;

import androidx.annotation.RequiresApi;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java8.util.stream.StreamSupport;
import java8.util.stream.IntStreams;
import java9.util.concurrent.*;

public class TranslatorTask extends AsyncTask<String, String, String> {

    private static String subscriptionKey = ""; //TODO: add your own subscription key!
    private static String location = "";    //TODO: add your location!
    public String translatedString = "";

    HttpUrl url;
    OkHttpClient client;

    public TranslatorTask()
    {
        super();
    }

    @Override
    protected void onPreExecute() {
        url = new HttpUrl.Builder()
                .scheme("https")
                .host("api.cognitive.microsofttranslator.com")
                .addPathSegment("/translate")
                .addQueryParameter("api-version", "3.0")
                .addQueryParameter("from", "en")
                .addQueryParameter("to", "de")
                .addQueryParameter("to", "it")
                .build();

        client = new OkHttpClient();
    }

    @Override
    protected void onProgressUpdate(String ...progress) {
        // do nothing here
    }

    @Override
    protected String doInBackground(String... textToTranslate){
        // Only use the first argument - multiple words can be posted at once
        //return getResponse(textToTranslate[0]);

        try {
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType,
                    "[{\"Text\": \"Hello World!\"}]");
            Request request = new Request.Builder().url(url).post(body)
                    .addHeader("Ocp-Apim-Subscription-Key", subscriptionKey)
                    .addHeader("Ocp-Apim-Subscription-Region", location)
                    .addHeader("Content-type", "application/json")
                    .build();

            //CallbackFuture future = new CallbackFuture();
            //client.newCall(request).enqueue(future);
            //Response response = future.get();
            Response response = client.newCall(request).execute();
            return response.body().string();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return "aaa";
    }

    protected void onPostExecute(String translatedText)
    {
        translatedString = translatedText;
    }

    public String getResponse(String textToTranslate) {
        try {
            String response = this.Post(textToTranslate);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    protected String Post(String textToTranslate) throws ExecutionException, InterruptedException, IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType,
                "[{\"Text\": \"Hello World!\"}]");
        Request request = new Request.Builder().url(url).post(body)
                .addHeader("Ocp-Apim-Subscription-Key", subscriptionKey)
                .addHeader("Ocp-Apim-Subscription-Region", location)
                .addHeader("Content-type", "application/json")
                .build();

        CallbackFuture future = new CallbackFuture();
        client.newCall(request).enqueue(future);
        Response response = future.get();

        //Response response = client.newCall(request).execute();
        return response.body().string();
    }

}


class CallbackFuture extends CompletableFuture<Response> implements Callback {
    public void onResponse(Call call, Response response) {
        super.complete(response);
    }
    public void onFailure(Call call, IOException e){
        super.completeExceptionally(e);
    }

    @Override
    public void onFailure(Request request, IOException e) {
        e.printStackTrace();
    }
    @Override
    public void onResponse(Response response) throws IOException {
    }
}
