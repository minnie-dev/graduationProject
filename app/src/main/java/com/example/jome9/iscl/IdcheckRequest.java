package com.example.jome9.iscl;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kch on 2018. 5. 14..
 */

public class IdcheckRequest extends StringRequest {

    final static private String URL = "http://192.168.0.180/idcheck.php";
    //final static private String URL = "http://192.168.0.6/idcheck.php";
    private Map<String, String> parameters;

    public IdcheckRequest(String userID, Response.Listener<String> listener){
        super(Method.POST, URL, listener, null);//해당 URL에 POST방식으로 파마미터들을 전송함
        parameters = new HashMap<>();
        parameters.put("userID", userID);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return parameters;
    }
}

