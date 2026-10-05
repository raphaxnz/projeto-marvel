package com.aula.marvel.ui.common;

import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.ColorInt;
import com.aula.marvel.data.MockData;

/** Símbolo da equipe na mesma posição do protótipo (header da equipe, detalhe do personagem e do arco). */
public final class TeamBadge {
    private TeamBadge() {}

    /**
     * @param monoColor cor aplicada ao símbolo dos Vingadores, que é monocromático (preto):
     *                  branco sobre headers escuros, cor do texto do tema sobre o fundo da tela.
     */
    public static void bind(ImageView view, String teamKey, @ColorInt int monoColor) {
        int badge = MockData.badgeFor(teamKey);
        view.setVisibility(badge != 0 ? View.VISIBLE : View.GONE);
        if (badge == 0) return;
        view.setImageResource(badge);
        view.setImageTintList(MockData.TEAM_AVENGERS.equals(teamKey) ? ColorStateList.valueOf(monoColor) : null);
    }
}
