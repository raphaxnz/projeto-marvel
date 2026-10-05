package com.aula.marvel.ui.team;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.aula.marvel.R;
import com.aula.marvel.data.MockData;
import com.aula.marvel.databinding.ItemMemberBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import java.util.List;
import java.util.Locale;

/**
 * Membros (Figma 8:14/9:38/10:58): painel na cor do personagem; faixas laterais com a cor
 * do anterior (esquerda) e do próximo (direita). Painel em x=0 na 1ª página, x=29 nas do meio
 * e x=42 na última.
 */
final class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.Holder> {
    interface Listener { void onMemberClick(MockData.Hero hero); }

    private final List<MockData.Hero> members;
    private final Listener listener;

    MemberAdapter(List<MockData.Hero> members, Listener listener) { this.members = members; this.listener = listener; }

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemMemberBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override public void onBindViewHolder(@NonNull Holder h, int pos) {
        MockData.Hero hero = members.get(pos);
        h.b.cardPanel.setCardBackgroundColor(color(h, hero));

        Glide.with(h.b.ivArt).clear(h.b.ivArt);
        if (hero.art != 0) {
            h.b.ivArt.setPadding(0, 0, 0, 0);
            h.b.ivArt.setImageResource(hero.art);
        } else if (hero.imageUrl != null) {
            // Sem arte recortada: foto da API enquadrada sobre o painel, com cantos arredondados.
            h.b.ivArt.setPadding(dp(h, 34), dp(h, 40), dp(h, 76), dp(h, 30));
            Glide.with(h.b.ivArt).load(hero.imageUrl)
                    .transform(new FitCenter(), new RoundedCorners(dp(h, 24)))
                    .into(h.b.ivArt);
        } else {
            h.b.ivArt.setImageDrawable(null);
        }
        String name = hero.name.toLowerCase(Locale.ROOT);
        // Nomes longos vão para 2 linhas; com hífen ficam em 1 (o Android em pt quebraria "homem / -aranha").
        h.b.tvName.setMaxLines(name.contains("-") ? 1 : 2);
        h.b.tvName.setText(name);

        int last = members.size() - 1;
        // Folga lateral = 10% da largura; no Figma (412dp) o painel fica em x=0, 29 e 42dp.
        ConstraintLayout.LayoutParams lp = (ConstraintLayout.LayoutParams) h.b.cardPanel.getLayoutParams();
        lp.horizontalBias = pos == 0 ? 0f : pos == last ? 1f : 0.69f;
        h.b.cardPanel.setLayoutParams(lp);

        boolean hasPrev = pos > 0;
        boolean hasNext = pos < last;
        h.b.vSidePrev.setVisibility(hasPrev ? View.VISIBLE : View.INVISIBLE);
        h.b.vSideNext.setVisibility(hasNext ? View.VISIBLE : View.INVISIBLE);
        if (hasPrev) h.b.vSidePrev.setBackgroundColor(color(h, members.get(pos - 1)));
        if (hasNext) h.b.vSideNext.setBackgroundColor(color(h, members.get(pos + 1)));

        h.b.cardPanel.setOnClickListener(v -> listener.onMemberClick(hero));
        h.b.ivArt.setOnClickListener(v -> listener.onMemberClick(hero));
    }

    private static int dp(Holder h, int v) {
        return Math.round(v * h.itemView.getResources().getDisplayMetrics().density);
    }

    private static int color(Holder h, MockData.Hero hero) {
        return ContextCompat.getColor(h.itemView.getContext(), hero.color != 0 ? hero.color : R.color.marvel_red);
    }

    @Override public int getItemCount() { return members.size(); }

    static final class Holder extends RecyclerView.ViewHolder {
        final ItemMemberBinding b;
        Holder(ItemMemberBinding b) { super(b.getRoot()); this.b = b; }
    }
}
