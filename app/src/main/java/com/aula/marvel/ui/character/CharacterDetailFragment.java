package com.aula.marvel.ui.character;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import com.aula.marvel.R;
import com.aula.marvel.databinding.FragmentCharacterDetailBinding;

public final class CharacterDetailFragment extends Fragment {
    private FragmentCharacterDetailBinding binding;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCharacterDetailBinding.inflate(inflater, container, false);
        binding.detailCanvas.post(() -> {
            if (binding == null || binding.detailCanvas.getWidth() == 0) return;
            ViewGroup.LayoutParams params = binding.detailCanvas.getLayoutParams();
            params.height = Math.round(binding.detailCanvas.getWidth() * 2304f / 824f);
            binding.detailCanvas.setLayoutParams(params);
        });
        binding.tapBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());
        binding.tapHome.setOnClickListener(v -> go(R.id.homeFragment));
        binding.tapSearch.setOnClickListener(v -> go(R.id.searchFragment));
        binding.tapTimeline.setOnClickListener(v -> go(R.id.timelineFragment));
        return binding.getRoot();
    }

    private void go(int destination) {
        NavOptions options = new NavOptions.Builder().setPopUpTo(R.id.homeFragment, false).build();
        NavHostFragment.findNavController(this).navigate(destination, null, options);
    }

    @Override public void onDestroyView() { binding = null; super.onDestroyView(); }
}
