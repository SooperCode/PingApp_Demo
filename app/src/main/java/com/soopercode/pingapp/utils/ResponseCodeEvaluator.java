package com.soopercode.pingapp.utils;

import android.content.Context;

import com.soopercode.pingapp.R;

import java.util.Random;

/**
 * Evaluates the given HTTP status code and generates
 * random response messages corresponding to it.
 *
 * @author  Ria
 */
public class ResponseCodeEvaluator {

    private Context context;

    /**
     * Creates a {@code ResponseCodeEvaluator} with the Context
     * needed to retrieve data from the string resources of this App.
     *
     * @param context   The current Context
     */
    public ResponseCodeEvaluator(Context context){
        this.context = context;
    }

    /**
     * Evaluates the given Response Code based on the officially defined
     * HyperText Transfer Protocol status code categories and generates
     * corresponding text messages.
     *
     * @param responseCode  A HTTP status code
     * @return              The generated String resource
     */
    public String generateResponse(int responseCode){
        if(100 <= responseCode && responseCode <= 199){
            return getResponse(10, 4);
        }else if(200 <= responseCode && responseCode <= 299) {
            return getResponse(20, 5);
        }else if(300 <= responseCode && responseCode <= 399){
            return getResponse(30, 4);
        }else if(400 <= responseCode && responseCode <= 499){
            return getResponse(40, 6);
        }else if(500 <= responseCode && responseCode <= 599){
            return getResponse(50, 5);
        }else{
            return getResponse(0, 5);
        }
    }

    /**
     * Fetches a random {@code String} from the App's String resources,
     * based on the specified category. The categories and their content
     * correspond to the various HTTP status categories.
     *
     * @param category      The category from which to retrieve the String
     * @param number        Number of available Strings to choose from
     * @return              The randomly chosen {@code String}
     */
    private String getResponse(int category, int number){
        String response = "";
        Random randy = new Random();
        int choice = category + randy.nextInt(number);

        switch (choice){
            case 10: response = context.getString(R.string.rc_1xx); break;
            case 11: response = context.getString(R.string.rc_1xx_2); break;
            case 12: response = context.getString(R.string.rc_1xx_3); break;
            case 13: response = context.getString(R.string.rc_1xx_4); break;

            case 20: response = context.getString(R.string.rc_2xx); break;
            case 21: response = context.getString(R.string.rc_2xx_2); break;
            case 22: response = context.getString(R.string.rc_2xx_3); break;
            case 23: response = context.getString(R.string.rc_2xx_4); break;
            case 24: response = context.getString(R.string.rc_2xx_5); break;

            case 30: response = context.getString(R.string.rc_3xx); break;
            case 31: response = context.getString(R.string.rc_3xx_2); break;
            case 32: response = context.getString(R.string.rc_3xx_3); break;
            case 33: response = context.getString(R.string.rc_3xx_4); break;

            case 40: response = context.getString(R.string.rc_4xx); break;
            case 41: response = context.getString(R.string.rc_4xx_2); break;
            case 42: response = context.getString(R.string.rc_4xx_3); break;
            case 43: response = context.getString(R.string.rc_4xx_4); break;
            case 44: response = context.getString(R.string.rc_4xx_5); break;
            case 45: response = context.getString(R.string.rc_4xx_6); break;

            case 50: response = context.getString(R.string.rc_5xx); break;
            case 51: response = context.getString(R.string.rc_5xx_2); break;
            case 52: response = context.getString(R.string.rc_5xx_3); break;
            case 53: response = context.getString(R.string.rc_5xx_4); break;
            case 54: response = context.getString(R.string.rc_5xx_5); break;

            case 0: response = context.getString(R.string.rc_nix); break;
            case 1: response = context.getString(R.string.rc_nix_2); break;
            case 2: response = context.getString(R.string.rc_nix_3); break;
            case 3: response = context.getString(R.string.rc_nix_4); break;
            case 4: response = context.getString(R.string.rc_nix_5); break;
        }
        return response;
    }
}
