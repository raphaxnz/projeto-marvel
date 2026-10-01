package com.aula.marvel.ui.comics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.aula.marvel.databinding.FragmentComicsBinding;

public final class ComicsFragment extends Fragment {
    private FragmentComicsBinding binding;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentComicsBinding.inflate(inflater, container, false);
        binding.tapBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());
        return binding.getRoot();
    }

    @Override public void onDestroyView() { binding = null; super.onDestroyView(); }
}
