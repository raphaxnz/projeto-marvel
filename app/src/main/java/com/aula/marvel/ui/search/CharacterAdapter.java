package com.aula.marvel.ui.search;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.aula.marvel.R;
import com.aula.marvel.data.FavoritesStore;
import com.aula.marvel.data.MockData;
import com.aula.marvel.databinding.ItemCharacterResultBinding;
import com.bumptech.glide.Glide;

final class CharacterAdapter extends ListAdapter<MockData.Hero, CharacterAdapter.Holder> {
    interface Listener { void onHeroClick(MockData.Hero hero); }

    private final Listener listener;
    private final FavoritesStore favorites;

    CharacterAdapter(FavoritesStore favorites, Listener listener) {
        super(DIFF);
        this.favorites = favorites;
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<MockData.Hero> DIFF = new DiffUtil.ItemCallback<MockData.Hero>() {
        @Override public boolean areItemsTheSame(@NonNull MockData.Hero a, @NonNull MockData.Hero b) { return a.id == b.id; }
        @Override public boolean areContentsTheSame(@NonNull MockData.Hero a, @NonNull MockData.Hero b) {
            return a.name.equals(b.name) && a.realName.equals(b.realName)
                    && (a.imageUrl == null ? b.imageUrl == null : a.imageUrl.equals(b.imageUrl));
        }
    };

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemCharacterResultBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override public void onBindViewHolder(@NonNull Holder h, int position) {
        MockData.Hero hero = getItem(position);
        int color = ContextCompat.getColor(h.itemView.getContext(), hero.color != 0 ? hero.color : R.color.marvel_red);

        h.b.tvName.setText(hero.name);
        h.b.tvRealName.setText(hero.realName);

        // Avatar: foto da API; sem foto, avatar local; fundo na cor do personagem.
        h.b.ivAvatar.setBackgroundTintList(ColorStateList.valueOf(color));
        Glide.with(h.b.ivAvatar).clear(h.b.ivAvatar);
        if (hero.imageUrl != null) {
            Glide.with(h.b.ivAvatar).load(hero.imageUrl).centerCrop().into(h.b.ivAvatar);
        } else if (hero.avatar != 0) {
            h.b.ivAvatar.setImageResource(hero.avatar);
        } else {
            h.b.ivAvatar.setImageDrawable(null);
        }

        // Tag da equipe (Figma: X-Men = vermelho com texto amarelo)
        boolean hasTeam = hero.team != null && !"—".equals(hero.team);
        h.b.tvTeam.setVisibility(hasTeam ? View.VISIBLE : View.GONE);
        if (hasTeam) {
            h.b.tvTeam.setText(hero.team);
            boolean xmen = MockData.TEAM_XMEN.equals(hero.team);
            boolean ff = MockData.TEAM_FANTASTIC.equals(hero.team);
            h.b.tvTeam.setBackgroundResource(ff ? R.drawable.bg_tag_blue : R.drawable.bg_tag_red);
            h.b.tvTeam.setTextColor(ContextCompat.getColor(h.itemView.getContext(), xmen ? R.color.marvel_yellow : R.color.white));
        }

        h.b.btnFavorite.setImageTintList(ColorStateList.valueOf(color));
        renderFavorite(h, hero);
        h.b.btnFavorite.setOnClickListener(v -> {
            favorites.toggle(hero.id);
            renderFavorite(h, hero);
        });
        h.b.getRoot().setOnClickListener(v -> listener.onHeroClick(hero));
    }

    private void renderFavorite(Holder h, MockData.Hero hero) {
        h.b.btnFavorite.setImageResource(favorites.isFavorite(hero.id) ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline);
    }

    static final class Holder extends RecyclerView.ViewHolder {
        final ItemCharacterResultBinding b;
        Holder(ItemCharacterResultBinding b) { super(b.getRoot()); this.b = b; }
    }
}
