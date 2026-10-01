package com.aula.marvel.ui.common;

import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ImageButton;
import androidx.annotation.IdRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.aula.marvel.R;
import com.aula.marvel.databinding.IncludeBottomNavBinding;

public final class BottomNav {
    private BottomNav() {}

    public static void bind(Fragment fragment, IncludeBottomNavBinding binding, @IdRes int selectedId) {
        int muted = ContextCompat.getColor(fragment.requireContext(), R.color.app_text_muted);
        int active = ContextCompat.getColor(fragment.requireContext(), R.color.app_primary);
        binding.btnNavHome.setImageTintList(ColorStateList.valueOf(selectedId == R.id.homeFragment ? active : muted));
        binding.btnNavSearch.setImageTintList(ColorStateList.valueOf(selectedId == R.id.searchFragment ? active : muted));
        binding.btnNavTimeline.setImageTintList(ColorStateList.valueOf(selectedId == R.id.timelineFragment ? active : muted));
        binding.btnNavHome.setOnClickListener(v -> go(fragment, R.id.homeFragment));
        binding.btnNavSearch.setOnClickListener(v -> go(fragment, R.id.searchFragment));
        binding.btnNavTimeline.setOnClickListener(v -> go(fragment, R.id.timelineFragment));
    }

    private static void go(Fragment fragment, @IdRes int destination) {
        NavController nav = NavHostFragment.findNavController(fragment);
        if (nav.getCurrentDestination() != null && nav.getCurrentDestination().getId() == destination) return;
        nav.navigate(destination);
    }
}
