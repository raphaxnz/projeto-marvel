package com.aula.marvel.ui.common;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;
import com.aula.marvel.R;
import com.aula.marvel.data.MockData;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

public final class UiFactory {
    private UiFactory() {}
    public static int dp(Context c, int value) { return Math.round(value * c.getResources().getDisplayMetrics().density); }

    public static TextView heroBubble(Context context, MockData.Hero hero, View.OnClickListener click) {
        TextView v = circle(context, hero.initials, hero.color, 72);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(dp(context,72),dp(context,72));
        p.setMarginEnd(dp(context,14)); v.setLayoutParams(p); v.setOnClickListener(click); v.setContentDescription(hero.name); return v;
    }

    public static MaterialCardView teamCard(Context context, String name, String mark, View.OnClickListener click) {
        MaterialCardView card = new MaterialCardView(context);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(dp(context,170),dp(context,100)); p.setMarginEnd(dp(context,12)); card.setLayoutParams(p);
        card.setCardBackgroundColor(ContextCompat.getColor(context,R.color.app_surface)); card.setRadius(dp(context,16)); card.setOnClickListener(click);
        LinearLayout box = new LinearLayout(context); box.setOrientation(LinearLayout.VERTICAL); box.setGravity(Gravity.CENTER_VERTICAL); box.setPadding(dp(context,16),dp(context,12),dp(context,16),dp(context,12));
        TextView m = new TextView(context); m.setText(mark); m.setTextSize(30); m.setTypeface(Typeface.DEFAULT,Typeface.BOLD); m.setTextColor(ContextCompat.getColor(context,R.color.app_primary));
        TextView t = new TextView(context); t.setText(name); t.setTextSize(16); t.setTypeface(Typeface.DEFAULT,Typeface.BOLD); t.setTextColor(Color.WHITE); box.addView(m); box.addView(t); card.addView(box); return card;
    }

    public static LinearLayout comicPreview(Context context, MockData.Comic comic) {
        LinearLayout box = new LinearLayout(context); box.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(context,108),LinearLayout.LayoutParams.WRAP_CONTENT); params.setMarginEnd(dp(context,12)); box.setLayoutParams(params);
        TextView cover = new TextView(context); cover.setText(comic.mark); cover.setGravity(Gravity.CENTER); cover.setTextColor(Color.WHITE); cover.setTextSize(25); cover.setTypeface(Typeface.DEFAULT,Typeface.BOLD); cover.setBackgroundResource(R.drawable.bg_banner_red); cover.setLayoutParams(new LinearLayout.LayoutParams(-1,dp(context,154)));
        TextView title = new TextView(context); title.setText(comic.title); title.setTextColor(Color.WHITE); title.setTextSize(12); title.setMaxLines(2); title.setPadding(0,dp(context,6),0,0);
        TextView issue = new TextView(context); issue.setText(comic.issue); issue.setTextColor(ContextCompat.getColor(context,R.color.app_text_secondary)); issue.setTextSize(11); box.addView(cover); box.addView(title); box.addView(issue); return box;
    }

    public static TextView circle(Context context, String text, @ColorRes int color, int sizeDp) {
        TextView v = new TextView(context); v.setText(text); v.setGravity(Gravity.CENTER); v.setTextColor(Color.WHITE); v.setTextSize(18); v.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        GradientDrawable bg = new GradientDrawable(); bg.setShape(GradientDrawable.OVAL); bg.setColor(ContextCompat.getColor(context,color)); v.setBackground(bg); v.setLayoutParams(new LinearLayout.LayoutParams(dp(context,sizeDp),dp(context,sizeDp))); return v;
    }

    public static Chip powerChip(Context context, String text) {
        Chip chip = new Chip(context); chip.setText(text); chip.setTextColor(Color.WHITE); chip.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.app_chip_blue))); chip.setChipStrokeWidth(0); chip.setClickable(false); return chip;
    }
}
