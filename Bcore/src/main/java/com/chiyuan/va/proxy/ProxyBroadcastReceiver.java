package com.chiyuan.va.proxy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;

import com.chiyuan.va.ChiyuanVACore;
import com.chiyuan.va.entity.am.PendingResultData;
import com.chiyuan.va.proxy.record.ProxyBroadcastRecord;

public class ProxyBroadcastReceiver extends BroadcastReceiver {
    // ★ 中性 TAG
    public static final String TAG = "BR";

    @Override
    public void onReceive(Context context, Intent intent) {
        intent.setExtrasClassLoader(context.getClassLoader());
        ProxyBroadcastRecord record = ProxyBroadcastRecord.create(intent);
        if (record.mIntent == null) return;
        PendingResult pendingResult = goAsync();
        try {
            ChiyuanVACore.getBActivityManager().scheduleBroadcastReceiver(
                    record.mIntent,
                    new PendingResultData(pendingResult),
                    record.mUserId);
        } catch (RemoteException e) {
            pendingResult.finish();
        }
    }
}
