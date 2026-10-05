package com.aula.marvel.ui.common;

import android.view.View;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.aula.marvel.R;
import com.aula.marvel.databinding.IncludeToolbarBinding;

/** Configura o include_toolbar: modo logo (Home/Busca/Timeline) ou modo título (detalhes). */
public final class Toolbar {
    private Toolbar() {}

    /** Logo "MARVEL STUDIOS" à esquerda + toggle de tema. */
    public static void logo(Fragment fragment, IncludeToolbarBinding b) {
        b.ivLogo.setVisibility(View.VISIBLE);
        b.btnBack.setVisibility(View.GONE);
        b.tvTitle.setVisibility(View.GONE);
        bindTheme(fragment, b);
    }

    /** Botão voltar + título centralizado + toggle de tema. */
    public static void title(Fragment fragment, IncludeToolbarBinding b, @Nullable CharSequence title) {
        b.ivLogo.setVisibility(View.GONE);
        b.btnBack.setVisibility(View.VISIBLE);
        b.tvTitle.setVisibility(View.VISIBLE);
        b.tvTitle.setText(title);
        b.btnBack.setOnClickListener(v -> NavHostFragment.findNavController(fragment).popBackStack());
        bindTheme(fragment, b);
    }

    private static void bindTheme(Fragment fragment, IncludeToolbarBinding b) {
        boolean dark = ThemeManager.isDark(fragment.requireContext());
        b.btnTheme.setImageResource(dark ? R.drawable.ic_sun : R.drawable.ic_moon);
        b.btnTheme.setOnClickListener(v -> ThemeManager.toggle(fragment.requireContext()));
    }
}
