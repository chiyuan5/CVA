package com.chiyuan.va.proxy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.chiyuan.va.ChiyuanVACore;
import com.chiyuan.va.app.BActivityThread;
import com.chiyuan.va.proxy.record.ProxyPendingRecord;
import com.chiyuan.va.utils.Slog;


public class ProxyPendingActivity extends Activity {
    public static final String TAG = "ProxyPendingActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finish();
        ProxyPendingRecord pendingActivityRecord = ProxyPendingRecord.create(getIntent());
        Slog.d(TAG, "ProxyPendingActivity: " + pendingActivityRecord);
        if (pendingActivityRecord.mTarget == null)
            return;
        pendingActivityRecord.mTarget.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        pendingActivityRecord.mTarget.setExtrasClassLoader(ChiyuanVACore.getApplication().getClassLoader());
        startActivity(pendingActivityRecord.mTarget);
    }

    public static class pwiqg extends ProxyPendingActivity {

    }

    public static class qbxqu extends ProxyPendingActivity {

    }

    public static class rgpzt extends ProxyPendingActivity {

    }

    public static class swvsl extends ProxyPendingActivity {

    }

    public static class thhvv extends ProxyPendingActivity {

    }

    public static class ujnjc extends ProxyPendingActivity {

    }

    public static class vauza extends ProxyPendingActivity {

    }

    public static class wvbrd extends ProxyPendingActivity {

    }

    public static class xyqod extends ProxyPendingActivity {

    }

    public static class ylvlo extends ProxyPendingActivity {

    }

    public static class pidck extends ProxyPendingActivity {

    }

    public static class qnxjr extends ProxyPendingActivity {

    }

    public static class riran extends ProxyPendingActivity {

    }

    public static class sclfn extends ProxyPendingActivity {

    }

    public static class tcmwr extends ProxyPendingActivity {

    }

    public static class ujjtr extends ProxyPendingActivity {

    }

    public static class vgbla extends ProxyPendingActivity {

    }

    public static class wwsyk extends ProxyPendingActivity {

    }

    public static class xshqb extends ProxyPendingActivity {

    }

    public static class yochl extends ProxyPendingActivity {

    }

    public static class ppkaq extends ProxyPendingActivity {

    }

    public static class qfkfd extends ProxyPendingActivity {

    }

    public static class ritcw extends ProxyPendingActivity {

    }

    public static class sdaxv extends ProxyPendingActivity {

    }

    public static class tmaom extends ProxyPendingActivity {

    }

    public static class unpep extends ProxyPendingActivity {

    }

    public static class vqyqr extends ProxyPendingActivity {

    }

    public static class wtwym extends ProxyPendingActivity {

    }

    public static class xhaww extends ProxyPendingActivity {

    }

    public static class ytrfa extends ProxyPendingActivity {

    }

    public static class pyzan extends ProxyPendingActivity {

    }

    public static class qpsep extends ProxyPendingActivity {

    }

    public static class rnope extends ProxyPendingActivity {

    }

    public static class scnix extends ProxyPendingActivity {

    }

    public static class tyclh extends ProxyPendingActivity {

    }

    public static class uvgpy extends ProxyPendingActivity {

    }

    public static class vyaky extends ProxyPendingActivity {

    }

    public static class wiaef extends ProxyPendingActivity {

    }

    public static class xjwkv extends ProxyPendingActivity {

    }

    public static class yoptv extends ProxyPendingActivity {

    }

    public static class pvztk extends ProxyPendingActivity {

    }

    public static class qpzqx extends ProxyPendingActivity {

    }

    public static class rlgau extends ProxyPendingActivity {

    }

    public static class sigwl extends ProxyPendingActivity {

    }

    public static class tgmpm extends ProxyPendingActivity {

    }

    public static class unggz extends ProxyPendingActivity {

    }

    public static class vvtlc extends ProxyPendingActivity {

    }

    public static class wbvrn extends ProxyPendingActivity {

    }

    public static class xqhxy extends ProxyPendingActivity {

    }

    public static class yovhq extends ProxyPendingActivity {

    }
}
