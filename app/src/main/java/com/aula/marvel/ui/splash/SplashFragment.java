package com.aula.marvel.ui.splash;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import com.aula.marvel.R;
import com.aula.marvel.databinding.FragmentSplashBinding;

public final class SplashFragment extends Fragment {
    private FragmentSplashBinding binding;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable openNext = () -> {
        if (!isAdded()) return;
        // O onboarding aparece toda vez que o app é aberto.
        NavOptions options = new NavOptions.Builder().setPopUpTo(R.id.splashFragment, true).build();
        NavHostFragment.findNavController(this).navigate(R.id.onboardingFragment, null, options);
    };

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSplashBinding.inflate(inflater, container, false);
        binding.ivLogo.setAlpha(0f);
        binding.ivLogo.setScaleX(.94f);
        binding.ivLogo.setScaleY(.94f);
        binding.ivLogo.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(650).start();
        handler.postDelayed(openNext, 1700);
        return binding.getRoot();
    }

    @Override public void onDestroyView() {
        handler.removeCallbacks(openNext);
        binding = null;
        super.onDestroyView();
    }
}
