package com.chiyuan.va.proxy;

import android.content.BroadcastReceiver;
import com.chiyuan.va.utils.Slog;
import android.content.Context;
import com.chiyuan.va.utils.Slog;
import android.content.Intent;
import com.chiyuan.va.utils.Slog;
import android.os.RemoteException;
import com.chiyuan.va.utils.Slog;

import com.chiyuan.va.ChiyuanVACore;
import com.chiyuan.va.utils.Slog;
import com.chiyuan.va.entity.am.PendingResultData;
import com.chiyuan.va.utils.Slog;
import com.chiyuan.va.proxy.record.ProxyBroadcastRecord;
import com.chiyuan.va.utils.Slog;


public class ProxyBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = "ER";

    @Override
    public void onReceive(Context context, Intent intent) {
        intent.setExtrasClassLoader(context.getClassLoader());
        ProxyBroadcastRecord record = ProxyBroadcastRecord.create(intent);
        if (record.mIntent == null) {
            return;
        }
        PendingResult pendingResult = goAsync();
        try {
            ChiyuanVACore.getBActivityManager().scheduleBroadcastReceiver(record.mIntent, new PendingResultData(pendingResult), record.mUserId);
        } catch (RemoteException e) {
            pendingResult.finish();
        }
    }
}