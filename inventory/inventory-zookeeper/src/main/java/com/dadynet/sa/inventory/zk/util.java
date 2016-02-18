package com.dadynet.sa.inventory.zk;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

public class util {
	
	
    public static Map toMap(String jsonString,Map convert) throws JSONException {

        JSONObject jsonObject = new JSONObject(jsonString);
        
        Map result = new HashMap();
        Iterator iterator = jsonObject.keys();
        String key = null;
        Object value = null;
        
        while (iterator.hasNext()) {

            key = (String) iterator.next();
            value = jsonObject.get(key);
            if(convert.containsKey(key))
            {
               key = convert.get(key).toString();
            }
            result.put(key, value.toString());
        }


 

 
        return result;

    }

}
