package com.aula.marvel.ui.search;

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
import com.aula.marvel.databinding.FragmentSearchBinding;

public final class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        binding.tapFirstResult.setOnClickListener(v -> go(R.id.characterDetailFragment, false));
        binding.tapHome.setOnClickListener(v -> go(R.id.homeFragment, true));
        binding.tapTimeline.setOnClickListener(v -> go(R.id.timelineFragment, true));
        return binding.getRoot();
    }

    private void go(int destination, boolean root) {
        NavOptions options = root
                ? new NavOptions.Builder().setPopUpTo(R.id.homeFragment, false).build()
                : null;
        NavHostFragment.findNavController(this).navigate(destination, null, options);
    }

    @Override public void onDestroyView() { binding = null; super.onDestroyView(); }
}
