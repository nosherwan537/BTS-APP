package com.example.bts;
import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterApi {
    private static final String TAG = "RegisterApi";
    private static final String BASE_URL = "http://192.168.10.4:5000/api/v1/register";

    private RequestQueue requestQueue;
    private Context context;

    public RegisterApi(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public void registerUser(String name, String email, String password, final VolleyCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", name);
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Log the request body
        Log.d(TAG, "Request Body: " + jsonBody.toString());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL, jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError(error);
            }
        });

        // Log that the request is being added to the queue
        Log.d(TAG, "Adding request to queue");
        requestQueue.add(jsonObjectRequest);
    }

    public interface VolleyCallback {
        void onSuccess(JSONObject response);
        void onError(VolleyError error);
    }
}
