package com.aula.marvel.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;
import com.aula.marvel.R;
import com.aula.marvel.databinding.FragmentOnboardingBinding;
import com.aula.marvel.ui.common.ArtworkPagerAdapter;

public final class OnboardingFragment extends Fragment {
    private FragmentOnboardingBinding binding;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentOnboardingBinding.inflate(inflater, container, false);
        binding.vpOnboarding.setAdapter(new ArtworkPagerAdapter(
                R.drawable.figma_onboarding_1,
                R.drawable.figma_onboarding_2,
                R.drawable.figma_onboarding_3));
        binding.vpOnboarding.setOffscreenPageLimit(3);
        binding.vpOnboarding.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override public void onPageSelected(int position) {
                binding.btnSkip.setVisibility(position == 2 ? View.INVISIBLE : View.VISIBLE);
            }
        });
        binding.btnSkip.setOnClickListener(v -> complete());
        binding.btnNext.setOnClickListener(v -> {
            int page = binding.vpOnboarding.getCurrentItem();
            if (page == 2) complete();
            else binding.vpOnboarding.setCurrentItem(page + 1, true);
        });
        return binding.getRoot();
    }

    private void complete() {
        requireContext().getSharedPreferences("marvel_preferences", 0).edit()
                .putBoolean("onboarding_seen_v2", true).apply();
        NavOptions options = new NavOptions.Builder().setPopUpTo(R.id.onboardingFragment, true).build();
        NavHostFragment.findNavController(this).navigate(R.id.homeFragment, null, options);
    }

    @Override public void onDestroyView() { binding = null; super.onDestroyView(); }
}
