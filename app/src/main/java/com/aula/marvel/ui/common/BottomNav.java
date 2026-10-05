package com.aula.marvel.ui.common;

import android.content.res.ColorStateList;
import androidx.annotation.IdRes;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import com.aula.marvel.R;
import com.aula.marvel.databinding.IncludeBottomNavBinding;

/** Liga o include_bottom_nav: tinta o ícone ativo de vermelho e move o indicador para baixo dele. */
public final class BottomNav {
    private BottomNav() {}

    public static void bind(Fragment fragment, IncludeBottomNavBinding b, @IdRes int selectedId) {
        int inactive = ContextCompat.getColor(fragment.requireContext(), R.color.app_icon_inactive);
        int active = ContextCompat.getColor(fragment.requireContext(), R.color.marvel_red);

        b.btnNavHome.setImageTintList(ColorStateList.valueOf(selectedId == R.id.homeFragment ? active : inactive));
        b.btnNavSearch.setImageTintList(ColorStateList.valueOf(selectedId == R.id.searchFragment ? active : inactive));
        b.btnNavTimeline.setImageTintList(ColorStateList.valueOf(selectedId == R.id.timelineFragment ? active : inactive));

        int anchor = selectedId == R.id.searchFragment ? R.id.btn_nav_search
                : selectedId == R.id.timelineFragment ? R.id.btn_nav_timeline
                : R.id.btn_nav_home;
        ConstraintLayout root = b.getRoot();
        ConstraintSet set = new ConstraintSet();
        set.clone(root);
        set.connect(R.id.nav_indicator, ConstraintSet.START, anchor, ConstraintSet.START);
        set.connect(R.id.nav_indicator, ConstraintSet.END, anchor, ConstraintSet.END);
        set.connect(R.id.nav_indicator, ConstraintSet.TOP, anchor, ConstraintSet.BOTTOM);
        set.applyTo(root);

        b.btnNavHome.setOnClickListener(v -> go(fragment, R.id.homeFragment));
        b.btnNavSearch.setOnClickListener(v -> go(fragment, R.id.searchFragment));
        b.btnNavTimeline.setOnClickListener(v -> go(fragment, R.id.timelineFragment));
    }

    private static void go(Fragment fragment, @IdRes int destination) {
        NavController nav = NavHostFragment.findNavController(fragment);
        if (nav.getCurrentDestination() != null && nav.getCurrentDestination().getId() == destination) return;
        NavOptions options = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(R.id.homeFragment, destination == R.id.homeFragment)
                .build();
        nav.navigate(destination, null, options);
    }
}
