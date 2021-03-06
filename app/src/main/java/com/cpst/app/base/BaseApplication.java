package com.cpst.app.base;

import android.app.Application;
import android.content.Context;

import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;

/**
 * Created by wy on 2017/8/9.
 *
 */

public class BaseApplication extends Application {

    public interface MsgDisplayListener {
        void handle(String msg);
    }

    public static MsgDisplayListener msgDisplayListener = null;
    public static StringBuilder cacheMsg = new StringBuilder();


    @Override
    public void onCreate() {
        super.onCreate();
        initSophix();
    }
    private void initSophix() {
        String appVersion;
        try {
            appVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (Exception e) {
            appVersion = "1.0";
        }
        SophixManager.getInstance().setContext(this)
                .setAppVersion(appVersion)
                .setAesKey(null)
                //.setAesKey("0123456789123456")
                .setEnableDebug(true)
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        String msg = new StringBuilder("").append("Mode:").append(mode)
                                .append(" Code:").append(code)
                                .append(" Info:").append(info)
                                .append(" HandlePatchVersion:").append(handlePatchVersion).toString();
                        if (msgDisplayListener != null) {
                            msgDisplayListener.handle(msg);
                        } else {
                            cacheMsg.append("\n").append(msg);
                        }
                    }
                }).initialize();
        // queryAndLoadNewPatch不可放在attachBaseContext 中，否则无网络权限，建议放在后面任意时刻，如onCreate中
        SophixManager.getInstance().queryAndLoadNewPatch();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
