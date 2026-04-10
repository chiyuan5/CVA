package com.chiyuan.va.proxy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.chiyuan.va.ChiyuanVACore;
import com.chiyuan.va.app.BActivityThread;
import com.chiyuan.va.fake.hook.HookManager;
import com.chiyuan.va.fake.service.HCallbackProxy;
import com.chiyuan.va.proxy.record.ProxyActivityRecord;
import com.chiyuan.va.proxy.record.ProxyPendingRecord;
import com.chiyuan.va.utils.Slog;


public class ProxyActivity extends Activity {
    public static final String TAG = "ProxyActivity";

    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        finish();

        HookManager.get().checkEnv(HCallbackProxy.class);


        ProxyActivityRecord record = ProxyActivityRecord.create(getIntent());
        if (record.mTarget != null) {
            record.mTarget.setExtrasClassLoader(ChiyuanVACore.getApplication().getClassLoader());
            startActivity(record.mTarget);
            return;
        }
    }

    public static class azcx extends ProxyActivity {

    }

    public static class bbbr extends ProxyActivity {

    }

    public static class coxu extends ProxyActivity {

    }

    public static class dpss extends ProxyActivity {

    }

    public static class emre extends ProxyActivity {

    }

    public static class fmsq extends ProxyActivity {

    }

    public static class gecz extends ProxyActivity {

    }

    public static class hjnw extends ProxyActivity {

    }

    public static class iclq extends ProxyActivity {

    }

    public static class jdsj extends ProxyActivity {

    }

    public static class krav extends ProxyActivity {

    }

    public static class ljvr extends ProxyActivity {

    }

    public static class mrjd extends ProxyActivity {

    }

    public static class ncna extends ProxyActivity {

    }

    public static class okeg extends ProxyActivity {

    }

    public static class pisu extends ProxyActivity {

    }

    public static class qshg extends ProxyActivity {

    }

    public static class rbhk extends ProxyActivity {

    }

    public static class sumb extends ProxyActivity {

    }

    public static class tieq extends ProxyActivity {

    }

    public static class uwoe extends ProxyActivity {

    }

    public static class vubh extends ProxyActivity {

    }

    public static class wlch extends ProxyActivity {

    }

    public static class xsni extends ProxyActivity {

    }

    public static class yzej extends ProxyActivity {

    }

    public static class ziil extends ProxyActivity {

    }

    public static class apwo extends ProxyActivity {

    }

    public static class bwcr extends ProxyActivity {

    }

    public static class chem extends ProxyActivity {

    }

    public static class dzvs extends ProxyActivity {

    }

    public static class eyhl extends ProxyActivity {

    }

    public static class fwdv extends ProxyActivity {

    }

    public static class guqu extends ProxyActivity {

    }

    public static class hcyr extends ProxyActivity {

    }

    public static class iiol extends ProxyActivity {

    }

    public static class jsrx extends ProxyActivity {

    }

    public static class kgzi extends ProxyActivity {

    }

    public static class lpaq extends ProxyActivity {

    }

    public static class mcni extends ProxyActivity {

    }

    public static class ngfp extends ProxyActivity {

    }

    public static class ockw extends ProxyActivity {

    }

    public static class pjko extends ProxyActivity {

    }

    public static class qrgw extends ProxyActivity {

    }

    public static class rkpq extends ProxyActivity {

    }

    public static class sous extends ProxyActivity {

    }

    public static class ttjr extends ProxyActivity {

    }

    public static class uinh extends ProxyActivity {

    }

    public static class vuet extends ProxyActivity {

    }

    public static class wons extends ProxyActivity {

    }

    public static class xeqa extends ProxyActivity {

    }
}
