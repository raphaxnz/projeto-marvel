package com.aula.marvel.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aula.marvel.databinding.ItemBannerBinding;

final class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.Holder> {
    interface Listener { void onBannerClick(int position); }
    private final String[] titles = {"Explore os seus personagens", "Descubra as equipes", "Viaje pela linha do tempo"};
    private final String[] marks = {"SP", "X", "80"};
    private final Listener listener;
    BannerAdapter(Listener listener) { this.listener = listener; }

    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemBannerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }
    @Override public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.binding.tvBannerTitle.setText(titles[position]);
        holder.binding.tvBannerHero.setText(marks[position]);
        holder.binding.getRoot().setOnClickListener(v -> listener.onBannerClick(holder.getBindingAdapterPosition()));
    }
    @Override public int getItemCount() { return titles.length; }
    static final class Holder extends RecyclerView.ViewHolder {
        final ItemBannerBinding binding;
        Holder(ItemBannerBinding binding) { super(binding.getRoot()); this.binding = binding; }
    }
}
