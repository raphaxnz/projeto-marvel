package com.aula.marvel.ui.common;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.aula.marvel.R;
import com.aula.marvel.data.MockData;
import com.aula.marvel.databinding.ItemHeroCircleBinding;
import com.bumptech.glide.Glide;
import java.util.List;

/**
 * Bolinha na cor do herói com a arte sobressaindo (Home "Heróis em destaque" e "Personagens" do arco).
 * Sem arte recortada local, mostra a foto da API recortada em círculo dentro da bolinha.
 */
public final class HeroCircleAdapter extends RecyclerView.Adapter<HeroCircleAdapter.Holder> {
    public interface Listener { void onHeroClick(MockData.Hero hero); }

    private final List<MockData.Hero> heroes;
    private final Listener listener;

    public HeroCircleAdapter(List<MockData.Hero> heroes, Listener listener) { this.heroes = heroes; this.listener = listener; }

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemHeroCircleBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override public void onBindViewHolder(@NonNull Holder h, int position) {
        MockData.Hero hero = heroes.get(position);
        int color = ContextCompat.getColor(h.itemView.getContext(), hero.color != 0 ? hero.color : R.color.marvel_red);
        h.b.vCircle.setBackgroundTintList(ColorStateList.valueOf(color));
        Glide.with(h.b.ivArt).clear(h.b.ivArt);
        if (hero.art != 0) {
            h.b.ivArt.setPadding(0, 0, 0, 0);
            h.b.ivArt.setImageResource(hero.art);
        } else if (hero.imageUrl != null) {
            // foto de 72dp centralizada na bolinha de 80dp (anel na cor do herói)
            float d = h.itemView.getResources().getDisplayMetrics().density;
            h.b.ivArt.setPadding(Math.round(8.5f * d), Math.round(27 * d), Math.round(9.5f * d), Math.round(15 * d));
            Glide.with(h.b.ivArt).load(hero.imageUrl).circleCrop().into(h.b.ivArt);
        } else {
            h.b.ivArt.setImageDrawable(null);
        }
        h.b.ivArt.setContentDescription(hero.name);
        h.b.getRoot().setOnClickListener(v -> listener.onHeroClick(hero));
    }

    @Override public int getItemCount() { return heroes.size(); }

    static final class Holder extends RecyclerView.ViewHolder {
        final ItemHeroCircleBinding b;
        Holder(ItemHeroCircleBinding b) { super(b.getRoot()); this.b = b; }
    }
}
