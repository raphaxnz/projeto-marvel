package com.aula.marvel.ui.common;

import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public final class ArtworkPagerAdapter extends RecyclerView.Adapter<ArtworkPagerAdapter.Holder> {
    public interface Listener { void onArtworkClick(int position); }

    private final int[] drawables;
    private final Listener listener;

    public ArtworkPagerAdapter(int... drawables) {
        this(null, drawables);
    }

    public ArtworkPagerAdapter(@Nullable Listener listener, int... drawables) {
        this.listener = listener;
        this.drawables = drawables;
    }

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView image = new ImageView(parent.getContext());
        image.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        image.setAdjustViewBounds(false);
        return new Holder(image);
    }

    @Override public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.image.setImageResource(drawables[position]);
        holder.image.setContentDescription(null);
        holder.image.setOnClickListener(listener == null ? null : v -> {
            int current = holder.getBindingAdapterPosition();
            if (current != RecyclerView.NO_POSITION) listener.onArtworkClick(current);
        });
    }

    @Override public int getItemCount() { return drawables.length; }

    static final class Holder extends RecyclerView.ViewHolder {
        final ImageView image;
        Holder(ImageView image) { super(image); this.image = image; }
    }
}
