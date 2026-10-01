package com.aula.marvel.ui.timeline;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.aula.marvel.databinding.FragmentTimelineDetailBinding;

public final class TimelineDetailFragment extends Fragment {
    private FragmentTimelineDetailBinding binding;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTimelineDetailBinding.inflate(inflater, container, false);
        binding.tapBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());
        return binding.getRoot();
    }

    @Override public void onDestroyView() { binding = null; super.onDestroyView(); }
}
