package Authentications;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {
    static final String PREF_USER_ID= "username";
    static final String PREF_USER_TYPE_CUSTOMER = "isCustomer";

    static SharedPreferences getSharedPreferences(Context ctx) {
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void SetUserID(Context ctx, int userId,Boolean isCustomer)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putInt(PREF_USER_ID, userId);
        editor.putBoolean(PREF_USER_TYPE_CUSTOMER, isCustomer);
        editor.commit();
    }

    public static void Logout(Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt(PREF_USER_ID,0);
        editor.commit();
    }
    public static int GetLoggedInUserID(Context ctx)
    {
        return getSharedPreferences(ctx).getInt(PREF_USER_ID, 0);
    }

    public static boolean isCustomer (Context ctx)
    {
        return getSharedPreferences(ctx).getBoolean(PREF_USER_TYPE_CUSTOMER, false);
    }



}
