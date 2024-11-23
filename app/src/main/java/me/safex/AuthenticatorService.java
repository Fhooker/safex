package me.safex;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AuthenticatorService extends Service {
    public AuthenticatorService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        MyAuthenticatorCVE201713288 authenticator = new MyAuthenticatorCVE201713288(this);
        return authenticator.getIBinder();
    }
}