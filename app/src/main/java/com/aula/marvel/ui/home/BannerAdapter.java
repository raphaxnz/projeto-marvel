package com.aula.marvel.ui.home;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.aula.marvel.R;
import com.aula.marvel.databinding.ItemBannerBinding;

final class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.Holder> {
    interface Listener { void onBannerClick(int position); }

    private final int[] titles = {
            R.string.explore_characters,
            R.string.onboarding_title_2,
            R.string.onboarding_title_3
    };
    private final int[] arts = {
            R.drawable.img_char_spider,
            R.drawable.img_char_wolverine,
            R.drawable.img_char_captain
    };
    @ColorRes private final int[] colors = {
            R.color.marvel_red,
            R.color.marvel_yellow,
            R.color.marvel_blue
    };
    private final Listener listener;

    BannerAdapter(Listener listener) { this.listener = listener; }

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemBannerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.binding.tvBannerTitle.setText(titles[position]);
        holder.binding.ivArt.setImageResource(arts[position]);
        holder.binding.bannerBackground.setBackgroundTintList(ColorStateList.valueOf(
                ContextCompat.getColor(holder.itemView.getContext(), colors[position])));
        holder.binding.getRoot().setOnClickListener(v -> {
            int current = holder.getBindingAdapterPosition();
            if (current != RecyclerView.NO_POSITION) listener.onBannerClick(current);
        });
    }

    @Override public int getItemCount() { return titles.length; }

    static final class Holder extends RecyclerView.ViewHolder {
        final ItemBannerBinding binding;
        Holder(ItemBannerBinding binding) { super(binding.getRoot()); this.binding = binding; }
    }
}
