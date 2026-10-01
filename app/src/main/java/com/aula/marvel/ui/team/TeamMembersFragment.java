package com.aula.marvel.ui.team;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.aula.marvel.R;
import com.aula.marvel.databinding.FragmentTeamMembersBinding;
import com.aula.marvel.ui.common.ArtworkPagerAdapter;

public final class TeamMembersFragment extends Fragment {
    private FragmentTeamMembersBinding binding;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTeamMembersBinding.inflate(inflater, container, false);
        binding.vpMembers.setAdapter(new ArtworkPagerAdapter(position ->
                NavHostFragment.findNavController(this).navigate(R.id.characterDetailFragment),
                R.drawable.figma_team_members_dark,
                R.drawable.figma_character_detail_2_dark,
                R.drawable.figma_character_detail_3_dark));
        binding.vpMembers.setOffscreenPageLimit(3);
        binding.tapBack.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());
        return binding.getRoot();
    }

    @Override public void onDestroyView() { binding = null; super.onDestroyView(); }
}
