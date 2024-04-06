package com.example.bts;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginApi {
    private static final String BASE_URL = "http://localhost:5000/api/v1";
    private RequestQueue requestQueue;
    private Context context;

    public LoginApi(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public void loginUser(String email, String password, final VolleyCallback callback) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, BASE_URL + "/login", jsonBody,
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

        requestQueue.add(jsonObjectRequest);
    }

    public interface VolleyCallback {
        void onSuccess(JSONObject response);
        void onError(VolleyError error);
    }
}
