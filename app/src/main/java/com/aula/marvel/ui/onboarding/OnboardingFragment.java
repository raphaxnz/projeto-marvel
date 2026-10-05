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
import com.aula.marvel.data.MockData;
import com.aula.marvel.databinding.FragmentOnboardingBinding;
import com.aula.marvel.ui.common.Toolbar;
import java.util.List;

public final class OnboardingFragment extends Fragment {
    private FragmentOnboardingBinding binding;
    private List<MockData.OnboardingPage> pages;

    private final ViewPager2.OnPageChangeCallback pageCallback = new ViewPager2.OnPageChangeCallback() {
        @Override public void onPageSelected(int position) { render(position); }
    };

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentOnboardingBinding.inflate(inflater, container, false);
        pages = MockData.onboarding();

        Toolbar.logo(this, binding.toolbar);
        binding.toolbar.ivLogo.setVisibility(View.GONE);
        binding.toolbar.btnBack.setOnClickListener(v -> {
            int page = binding.vpOnboarding.getCurrentItem();
            if (page > 0) binding.vpOnboarding.setCurrentItem(page - 1, true);
        });

        binding.vpOnboarding.setAdapter(new OnboardingAdapter(pages));
        binding.vpOnboarding.setOffscreenPageLimit(3);
        binding.vpOnboarding.registerOnPageChangeCallback(pageCallback);

        binding.btnSkip.setOnClickListener(v -> complete());
        binding.btnNext.setOnClickListener(v -> {
            int page = binding.vpOnboarding.getCurrentItem();
            if (page == pages.size() - 1) complete();
            else binding.vpOnboarding.setCurrentItem(page + 1, true);
        });
        render(0);
        return binding.getRoot();
    }

    private void render(int position) {
        boolean last = position == pages.size() - 1;
        MockData.OnboardingPage page = pages.get(position);
        binding.tvPageTitle.setText(page.title);
        binding.tvPageDescription.setText(page.description);
        binding.dot1.setBackgroundResource(position == 0 ? R.drawable.bg_dot_active : R.drawable.bg_dot);
        binding.dot2.setBackgroundResource(position == 1 ? R.drawable.bg_dot_active : R.drawable.bg_dot);
        binding.dot3.setBackgroundResource(position == 2 ? R.drawable.bg_dot_active : R.drawable.bg_dot);
        binding.btnSkip.setVisibility(last ? View.INVISIBLE : View.VISIBLE);
        binding.btnNext.setText(last ? R.string.start : R.string.next);
        binding.toolbar.btnBack.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        // Na última página o botão "Começar" fica centralizado (Figma 41:1010).
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) binding.btnNext.getLayoutParams();
        androidx.constraintlayout.widget.ConstraintLayout.LayoutParams clp =
                (androidx.constraintlayout.widget.ConstraintLayout.LayoutParams) lp;
        clp.startToStart = last ? androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.PARENT_ID
                : androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.UNSET;
        binding.btnNext.setLayoutParams(clp);
    }

    private void complete() {
        NavOptions options = new NavOptions.Builder().setPopUpTo(R.id.onboardingFragment, true).build();
        NavHostFragment.findNavController(this).navigate(R.id.homeFragment, null, options);
    }

    @Override public void onDestroyView() {
        binding.vpOnboarding.unregisterOnPageChangeCallback(pageCallback);
        binding = null;
        super.onDestroyView();
    }
}
