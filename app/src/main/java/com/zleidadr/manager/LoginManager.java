package com.zleidadr.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.zleidadr.entity.Operator;
import com.zleidadr.sugarDb.DbManager;

/**
 * Created by xiaoxuli on 16/1/4.
 */
public class LoginManager {

    private static final String OPERATOR_ID = "operatorId";
    private static final String OPERATOR_NAME = "operatorName";
    private static final String TOKEN = "token";


    private static final String LOGIN_ZLEIDA_FILE = "login_zleida";
    private static LoginManager mLoginManager;
    private Operator mOperator;

    private LoginManager() {
    }

    public static synchronized LoginManager getInstance() {
        if (mLoginManager == null) {
            mLoginManager = new LoginManager();
        }
        return mLoginManager;
    }

    public boolean isLogin(Context context) {
        if (this.mOperator != null && verifyOperator(mOperator)) {
            return true;
        }
        SharedPreferences sp =
                context.getSharedPreferences(LOGIN_ZLEIDA_FILE, Context.MODE_PRIVATE);
        this.mOperator = new Operator();
        this.mOperator.setOperatorId(sp.getString(OPERATOR_ID, null));
        this.mOperator.setOperatorName(sp.getString(OPERATOR_NAME, null));
        this.mOperator.setToken(sp.getString(TOKEN, null));
        if (verifyOperator(mOperator)) {
            return true;
        }

        return false;
    }

    public boolean login(Context context, Operator operator) {
        SharedPreferences sp =
                context.getSharedPreferences(LOGIN_ZLEIDA_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (!sp.getString(OPERATOR_ID, "").equals(operator.getOperatorId())) {
            DbManager.clearDbData();
        }
        editor.putString(OPERATOR_ID, operator.getOperatorId());
        editor.putString(OPERATOR_NAME, operator.getOperatorName());
        editor.putString(TOKEN, operator.getToken());
        editor.apply();
        this.mOperator = operator;
        return true;
    }

    public void logout(Context context) {
        this.mOperator = null;
        SharedPreferences sp =
                context.getSharedPreferences(LOGIN_ZLEIDA_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
//        editor.putString(OPERATOR_ID, "");
        editor.putString(OPERATOR_NAME, "");
//        editor.putString(TOKEN, "");
        editor.apply();
    }

    public boolean verifyOperator(Operator operator) {
        if (TextUtils.isEmpty(operator.getOperatorId()) ||
                TextUtils.isEmpty(operator.getOperatorName()) ||
                TextUtils.isEmpty(operator.getToken())) {
            return false;
        }
        return true;
    }

    public Operator getOperator(Context context) {
        if (mOperator == null) {
            isLogin(context);
        }
        return mOperator;
    }
}
