package com.bluetouch.util;

import android.content.Context;

/**
 * Created by User on 2016-03-15.
 */
public class DeleteTouch {

    public void deleteTouch(Context context, int position, String strid) {
        try {
            if (position == 0) {
                SharedPrefsUtils.setStringPreference(context, strid + "T" + 1, SharedPrefsUtils.getStringPreference(context, strid + "T" + 2));
                SharedPrefsUtils.setStringPreference(context,"A" + 1,  SharedPrefsUtils.getStringPreference(context,"A" + 2));

                SharedPrefsUtils.setStringPreference(context, strid + "T" + 2, SharedPrefsUtils.getStringPreference(context, strid + "T" + 3));
                SharedPrefsUtils.setStringPreference(context,"A" + 2,  SharedPrefsUtils.getStringPreference(context,"A" + 3));
                SharedPrefsUtils.setStringPreference(context, strid + "T" + 3, SharedPrefsUtils.getStringPreference(context, strid + "T" + 4));
                SharedPrefsUtils.setStringPreference(context,"A" + 3,  SharedPrefsUtils.getStringPreference(context,"A" + 4));
                SharedPrefsUtils.setStringPreference(context, strid + "T" + 4, SharedPrefsUtils.getStringPreference(context, strid + "T" + 5));
                SharedPrefsUtils.setStringPreference(context,"A" + 4,  SharedPrefsUtils.getStringPreference(context,"A" + 5));
            } else if (position == 1) {
                SharedPrefsUtils.setStringPreference(context,strid + "T" + 2,SharedPrefsUtils.getStringPreference(context, strid + "T" + 3));
                SharedPrefsUtils.setStringPreference(context,"A" + 2,  SharedPrefsUtils.getStringPreference(context,"A" + 3));
                SharedPrefsUtils.setStringPreference(context,strid + "T" + 3,SharedPrefsUtils.getStringPreference(context, strid + "T" + 4));
                SharedPrefsUtils.setStringPreference(context,"A" + 3,  SharedPrefsUtils.getStringPreference(context,"A" + 4));
                SharedPrefsUtils.setStringPreference(context,strid + "T" + 4,SharedPrefsUtils.getStringPreference(context, strid + "T" + 5));
                SharedPrefsUtils.setStringPreference(context,"A" + 4,  SharedPrefsUtils.getStringPreference(context,"A" + 5));
            } else if (position == 2) {
                SharedPrefsUtils.setStringPreference(context,strid + "T" + 3,SharedPrefsUtils.getStringPreference(context, strid + "T" + 4));
                SharedPrefsUtils.setStringPreference(context,"A" + 3,  SharedPrefsUtils.getStringPreference(context,"A" + 4));
                SharedPrefsUtils.setStringPreference(context,strid + "T" + 4,SharedPrefsUtils.getStringPreference(context, strid + "T" + 5));
                SharedPrefsUtils.setStringPreference(context,"A" + 4,  SharedPrefsUtils.getStringPreference(context,"A" + 5));
            } else if (position == 3) {
                SharedPrefsUtils.setStringPreference(context,strid + "T" + 4,SharedPrefsUtils.getStringPreference(context, strid + "T" + 5));
                SharedPrefsUtils.setStringPreference(context,"A" + 4,  SharedPrefsUtils.getStringPreference(context, "A" + 5));
            } else if (position == 4) {
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}
