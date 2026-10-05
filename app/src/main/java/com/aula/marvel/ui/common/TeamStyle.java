package com.aula.marvel.ui.common;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import com.aula.marvel.R;
import com.aula.marvel.data.MockData;

/** Cor de cada equipe: X-Men amarelo, Vingadores vermelho, Quarteto Fantástico azul. */
public final class TeamStyle {
    private TeamStyle() {}

    /** Cor de destaque para textos e ícones (varia com o tema, para manter o contraste). */
    @ColorRes public static int accent(String teamKey) {
        if (MockData.TEAM_XMEN.equals(teamKey)) return R.color.team_xmen_accent;
        if (MockData.TEAM_FANTASTIC.equals(teamKey)) return R.color.team_fantastic_accent;
        return R.color.team_avengers_accent;
    }

    /** Cor do nome da equipe sobre o banner (foto escura nos dois temas): tons claros. */
    @ColorInt public static int onBanner(String teamKey) {
        if (MockData.TEAM_XMEN.equals(teamKey)) return 0xFFDBBA15;
        if (MockData.TEAM_FANTASTIC.equals(teamKey)) return 0xFF4A86E8;
        return 0xFFE23636;
    }

    /** Tom escuro da cor da equipe (chip selecionado da linha do tempo). */
    @ColorInt public static int deep(String teamKey) {
        if (MockData.TEAM_XMEN.equals(teamKey)) return 0xFF6B5A08;
        if (MockData.TEAM_FANTASTIC.equals(teamKey)) return 0xFF011F69;
        return 0xFF430503;
    }

    /** Arte dos integrantes no botão "Membros". */
    @DrawableRes public static int membersThumb(String teamKey) {
        if (MockData.TEAM_AVENGERS.equals(teamKey)) return R.drawable.img_members_avengers;
        if (MockData.TEAM_FANTASTIC.equals(teamKey)) return R.drawable.img_members_ff;
        return R.drawable.img_members_thumb;
    }

    /** Fundo do botão "Membros": degradê horizontal na cor da equipe, cantos de 20dp. */
    public static Drawable membersButton(Context context, String teamKey) {
        int[] colors;
        if (MockData.TEAM_XMEN.equals(teamKey)) colors = new int[]{0xFFDBBA15, 0xFF6B5A08};
        else if (MockData.TEAM_FANTASTIC.equals(teamKey)) colors = new int[]{0xFF022D99, 0xFF01154A};
        else colors = new int[]{0xFFA90C08, 0xFF5A0604};
        GradientDrawable shape = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
        shape.setCornerRadius(20 * context.getResources().getDisplayMetrics().density);
        return new RippleDrawable(ColorStateList.valueOf(0x33FFFFFF), shape, null);
    }
}
