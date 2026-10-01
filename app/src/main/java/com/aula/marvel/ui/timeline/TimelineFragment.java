package com.aula.marvel.ui.timeline;

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
import com.aula.marvel.databinding.FragmentTimelineBinding;

public final class TimelineFragment extends Fragment {
    private FragmentTimelineBinding binding;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTimelineBinding.inflate(inflater, container, false);
        binding.tapFirstEvent.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigate(R.id.timelineDetailFragment));
        binding.tapHome.setOnClickListener(v -> go(R.id.homeFragment));
        binding.tapSearch.setOnClickListener(v -> go(R.id.searchFragment));
        return binding.getRoot();
    }

    private void go(int destination) {
        NavOptions options = new NavOptions.Builder().setPopUpTo(R.id.homeFragment, false).build();
        NavHostFragment.findNavController(this).navigate(destination, null, options);
    }

    @Override public void onDestroyView() { binding = null; super.onDestroyView(); }
}
