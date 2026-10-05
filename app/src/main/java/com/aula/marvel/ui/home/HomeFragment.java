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
import com.aula.marvel.data.MockData;
import com.aula.marvel.data.api.MarvelRepository;
import com.aula.marvel.databinding.FragmentHomeBinding;
import com.aula.marvel.ui.common.BottomNav;
import com.aula.marvel.ui.common.HeroCircleAdapter;
import com.aula.marvel.ui.common.Toolbar;

public final class HomeFragment extends Fragment {
    private static final long BANNER_INTERVAL_MS = 4000;
    private FragmentHomeBinding binding;
    private final Handler bannerHandler = new Handler(Looper.getMainLooper());
    private final Runnable advanceBanner = () -> {
        if (binding == null) return;
        int count = binding.vpBanner.getAdapter() == null ? 0 : binding.vpBanner.getAdapter().getItemCount();
        if (count > 0) binding.vpBanner.setCurrentItem((binding.vpBanner.getCurrentItem() + 1) % count, true);
    };
    private final ViewPager2.OnPageChangeCallback bannerCallback = new ViewPager2.OnPageChangeCallback() {
        @Override public void onPageSelected(int position) { renderDots(position); }
        @Override public void onPageScrollStateChanged(int state) {
            bannerHandler.removeCallbacks(advanceBanner);
            if (state == ViewPager2.SCROLL_STATE_IDLE) bannerHandler.postDelayed(advanceBanner, BANNER_INTERVAL_MS);
        }
    };

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        Toolbar.logo(this, binding.toolbar);
        BottomNav.bind(this, binding.bottomNav, R.id.homeFragment);

        binding.vpBanner.setAdapter(new BannerAdapter(position -> go(R.id.searchFragment, null)));
        binding.vpBanner.registerOnPageChangeCallback(bannerCallback);

        MarvelRepository.get(requireContext()).featuredHeroes(apiHeroes -> {
            if (binding == null) return;
            binding.rvHeroes.setAdapter(new HeroCircleAdapter(apiHeroes, hero -> {
            Bundle args = new Bundle();
            args.putLong("heroId", hero.id);
            go(R.id.characterDetailFragment, args);
            }));
        });

        MarvelRepository.get(requireContext()).teams(apiTeams -> {
            if (binding == null) return;
            binding.rvTeams.setAdapter(new TeamCardAdapter(apiTeams, team -> {
            Bundle args = new Bundle();
            args.putLong("teamId", team.id);
            go(R.id.teamFragment, args);
            }));
        });
        return binding.getRoot();
    }

    private void renderDots(int position) {
        binding.bannerDot1.setBackgroundResource(position == 0 ? R.drawable.bg_dot_active : R.drawable.bg_dot_banner);
        binding.bannerDot2.setBackgroundResource(position == 1 ? R.drawable.bg_dot_active : R.drawable.bg_dot_banner);
        binding.bannerDot3.setBackgroundResource(position == 2 ? R.drawable.bg_dot_active : R.drawable.bg_dot_banner);
    }

    private void go(int destination, @Nullable Bundle args) {
        NavHostFragment.findNavController(this).navigate(destination, args);
    }

    @Override public void onResume() {
        super.onResume();
        bannerHandler.removeCallbacks(advanceBanner);
        bannerHandler.postDelayed(advanceBanner, BANNER_INTERVAL_MS);
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
