package cbfg.rvadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class RVHolderFactory {
    protected View inflate(int layoutRes, ViewGroup parent) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
    }

    public abstract RVHolder<?> createViewHolder(ViewGroup parent, int viewType, Object item);
}
