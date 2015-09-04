package com.monsteroftheheaven.whosattended;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Tasvir on 8/3/2015.
 */
public class AppPreference {
    public static final String KEY_PREFS_STUDENT_LIST = "student_list";
    private static final String APP_SHARED_PREFS = AppPreference.class.getSimpleName(); //  Name of the file -.xml
    private SharedPreferences _sharedPrefs;
    private SharedPreferences.Editor _prefsEditor;

    public AppPreference(Context context) {
        this._sharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
        this._prefsEditor = _sharedPrefs.edit();
    }


    public void saveMap(Map<String,String> inputMap){
        //SharedPreferences pSharedPref = getApplicationContext().getSharedPreferences("MyVariables", Context.MODE_PRIVATE);
        if (_sharedPrefs != null){
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = _sharedPrefs.edit();
            editor.remove(KEY_PREFS_STUDENT_LIST).commit();
            editor.putString(KEY_PREFS_STUDENT_LIST, jsonString);
            editor.commit();
        }
    }

    public Map<String,String> loadMap(){
        Map<String,String> outputMap = new HashMap<String,String>();
        //SharedPreferences pSharedPref = getApplicationContext().getSharedPreferences("MyVariables", Context.MODE_PRIVATE);
        try{
            if (_sharedPrefs!= null){
                String jsonString = _sharedPrefs.getString(KEY_PREFS_STUDENT_LIST, (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while(keysItr.hasNext()) {
                    String key = keysItr.next();
                    String value = (String)jsonObject.get(key);
                    outputMap.put(key, value);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return outputMap;
    }
}
