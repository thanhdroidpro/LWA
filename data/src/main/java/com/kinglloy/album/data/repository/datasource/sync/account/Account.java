package com.kinglloy.album.data.repository.datasource.sync.account;

import android.accounts.AccountManager;
import android.content.Context;

import com.kinglloy.album.data.BuildConfig;
import com.kinglloy.album.data.log.LogUtil;


/**
 * YaLin 2017/1/3.
 */

public class Account {

    public static final String ACCOUNT_TYPE = BuildConfig.AUTHORITY;

    public static final String ACCOUNT_NAME = "Sync Account";

    private static final String TAG = "Account";
    private static android.accounts.Account mAccount;

    public static android.accounts.Account createSyncAccount(Context context) {
        AccountManager accountManager = (AccountManager) context
                .getSystemService(Context.ACCOUNT_SERVICE);

        android.accounts.Account account = getAccount();
        if (accountManager.addAccountExplicitly(account, null, null)) {
            return account;
        } else {
            LogUtil.D(TAG, "Unable to createLiveDataStore account");
            return null;
        }
    }

    public static android.accounts.Account getAccount() {
        if (mAccount == null) {
            mAccount = new android.accounts.Account(ACCOUNT_NAME, ACCOUNT_TYPE);
        }
        return mAccount;
    }
}
