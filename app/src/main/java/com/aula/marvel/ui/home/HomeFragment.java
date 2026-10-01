package com.aula.marvel.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;
import com.aula.marvel.R;
import com.aula.marvel.databinding.FragmentHomeBinding;
import com.aula.marvel.ui.common.ArtworkPagerAdapter;

public final class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private final Handler bannerHandler = new Handler(Looper.getMainLooper());
    private final Runnable advanceBanner = () -> {
        if (binding == null) return;
        binding.vpBanner.setCurrentItem((binding.vpBanner.getCurrentItem() + 1) % 3, true);
    };
    private final ViewPager2.OnPageChangeCallback bannerCallback =
            new ViewPager2.OnPageChangeCallback() {
                @Override public void onPageScrollStateChanged(int state) {
                    bannerHandler.removeCallbacks(advanceBanner);
                    if (state == ViewPager2.SCROLL_STATE_IDLE) {
                        bannerHandler.postDelayed(advanceBanner, 4000);
                    }
                }
            };

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        binding.vpBanner.setAdapter(new ArtworkPagerAdapter(position -> go(R.id.searchFragment),
                R.drawable.figma_search_result_dark,
                R.drawable.figma_asset_01,
                R.drawable.figma_asset_02));
        binding.vpBanner.registerOnPageChangeCallback(bannerCallback);
        binding.tapFeaturedHero.setOnClickListener(v -> go(R.id.characterDetailFragment));
        binding.tapTeam.setOnClickListener(v -> go(R.id.teamFragment));
        binding.tapSearch.setOnClickListener(v -> go(R.id.searchFragment));
        binding.tapTimeline.setOnClickListener(v -> go(R.id.timelineFragment));
        return binding.getRoot();
    }

    private void go(int destination) {
        NavHostFragment.findNavController(this).navigate(destination);
    }

    @Override public void onResume() {
        super.onResume();
        bannerHandler.removeCallbacks(advanceBanner);
        bannerHandler.postDelayed(advanceBanner, 4000);
    }

    @Override public void onPause() {
        bannerHandler.removeCallbacks(advanceBanner);
        super.onPause();
    }

    @Override public void onDestroyView() {
        bannerHandler.removeCallbacks(advanceBanner);
        binding.vpBanner.unregisterOnPageChangeCallback(bannerCallback);
        binding = null;
        super.onDestroyView();
    }
}
