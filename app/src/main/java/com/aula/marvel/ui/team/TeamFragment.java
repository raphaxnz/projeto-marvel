package com.aula.marvel.ui.team;

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
import com.aula.marvel.databinding.FragmentTeamBinding;

public final class TeamFragment extends Fragment {
    private FragmentTeamBinding binding;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTeamBinding.inflate(inflater, container, false);
        binding.tapBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());
        binding.tapMembers.setOnClickListener(v -> go(R.id.teamMembersFragment, false));
        binding.tapHome.setOnClickListener(v -> go(R.id.homeFragment, true));
        binding.tapSearch.setOnClickListener(v -> go(R.id.searchFragment, true));
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
