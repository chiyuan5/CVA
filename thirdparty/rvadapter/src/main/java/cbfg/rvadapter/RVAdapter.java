package cbfg.rvadapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RVAdapter<T> extends RecyclerView.Adapter<RVHolder<?>> {
    public interface OnItemClickListener<T> {
        void onItemClick(View view, T item, int position);
    }

    public interface OnItemLongClickListener<T> {
        void onItemLongClick(View view, T item, int position);
    }

    private final Context context;
    private final RVHolderFactory holderFactory;
    private final List<T> items = new ArrayList<>();
    private OnItemClickListener<T> itemClickListener;
    private OnItemLongClickListener<T> itemLongClickListener;

    public RVAdapter(Context context, RVHolderFactory holderFactory) {
        this.context = context;
        this.holderFactory = holderFactory;
    }

    public RVAdapter<T> bind(RecyclerView recyclerView) {
        recyclerView.setAdapter(this);
        return this;
    }

    public RVAdapter<T> bindRecyclerView(RecyclerView recyclerView) {
        return bind(recyclerView);
    }

    public RVAdapter<T> setItemClickListener(OnItemClickListener<T> listener) {
        this.itemClickListener = listener;
        return this;
    }

    public RVAdapter<T> setItemLongClickListener(OnItemLongClickListener<T> listener) {
        this.itemLongClickListener = listener;
        return this;
    }

    public RVAdapter<T> setItems(List<T> newItems) {
        return setItems(newItems, true, true);
    }

    public RVAdapter<T> setItems(List<T> newItems, boolean clearSelections, boolean needNotify) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
        if (needNotify) {
            notifyDataSetChanged();
        }
        return this;
    }

    public RVAdapter<T> replaceAt(int position, T item) {
        if (position >= 0 && position < items.size()) {
            items.set(position, item);
            notifyItemChanged(position);
        }
        return this;
    }

    public List<T> getItems() {
        return items;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public RVHolder<?> onCreateViewHolder(@NonNull android.view.ViewGroup parent, int viewType) {
        Object item = items.isEmpty() ? new Object() : items.get(Math.max(0, Math.min(viewType, items.size() - 1)));
        return holderFactory.createViewHolder(parent, viewType, item);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void onBindViewHolder(@NonNull RVHolder<?> holder, int position) {
        final T item = items.get(position);
        RVHolder rawHolder = (RVHolder) holder;
        rawHolder.setContent(item, false, null);
        holder.setListeners(
                v -> {
                    if (itemClickListener != null) {
                        int adapterPosition = holder.getBindingAdapterPosition();
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            itemClickListener.onItemClick(v, item, adapterPosition);
                        }
                    }
                },
                v -> {
                    if (itemLongClickListener != null) {
                        int adapterPosition = holder.getBindingAdapterPosition();
                        if (adapterPosition != RecyclerView.NO_POSITION) {
                            itemLongClickListener.onItemLongClick(v, item, adapterPosition);
                        }
                        return true;
                    }
                    return false;
                }
        );
    }
}
