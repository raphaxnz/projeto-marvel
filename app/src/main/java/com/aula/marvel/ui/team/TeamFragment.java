package com.aula.marvel.ui.team;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.aula.marvel.R;
import com.aula.marvel.data.MockData;
import com.aula.marvel.data.api.MarvelRepository;
import com.aula.marvel.databinding.FragmentTeamBinding;
import com.aula.marvel.ui.comics.ComicAdapter;
import com.aula.marvel.ui.common.BottomNav;
import com.aula.marvel.ui.common.TeamBadge;
import com.aula.marvel.ui.common.TeamStyle;
import com.aula.marvel.ui.common.Toolbar;
import java.util.Locale;

public final class TeamFragment extends Fragment {
    private FragmentTeamBinding binding;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTeamBinding.inflate(inflater, container, false);
        long teamId = getArguments() == null ? 1L : getArguments().getLong("teamId", 1L);
        MarvelRepository repo = MarvelRepository.get(requireContext());
        String key = repo.keyForTeam(teamId);

        Toolbar.title(this, binding.toolbar, key.toUpperCase(Locale.ROOT));
        BottomNav.bind(this, binding.bottomNav, R.id.homeFragment);
        binding.ivHeader.setImageResource(MockData.headerFor(key));
        binding.tvTeamName.setText(key);
        applyTeamStyle(key);

        Bundle args = new Bundle();
        args.putLong("teamId", teamId);
        binding.btnMembers.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(R.id.teamMembersFragment, args));
        binding.btnViewAll.setOnClickListener(v -> NavHostFragment.findNavController(this).navigate(R.id.comicsFragment, args));

        ComicAdapter comics = new ComicAdapter(false);
        binding.rvComics.setAdapter(comics);
        repo.teamComics(teamId, list -> {
            if (binding == null) return;
            comics.submitList(list.size() > 6 ? list.subList(0, 6) : list);
        });

        MockData.Team cached = repo.cachedTeam(teamId);
        if (cached != null) render(cached);
        repo.team(teamId, this::render);
        return binding.getRoot();
    }

    private void render(MockData.Team team) {
        if (binding == null) return;
        String key = team.key();
        binding.ivHeader.setImageResource(MockData.headerFor(key));
        binding.tvTeamName.setText(key);
        TeamBadge.bind(binding.ivBadge, key, Color.WHITE);
        binding.infoCard.tvInfoValue1.setText(team.firstAppearance);
        binding.infoCard.tvInfoValue2.setText(team.membersCount);
    }

    /** Nome da equipe, "Ver todas" e botão "Membros" na cor da equipe. */
    private void applyTeamStyle(String key) {
        int accent = ContextCompat.getColor(requireContext(), TeamStyle.accent(key));
        binding.tvTeamName.setTextColor(TeamStyle.onBanner(key));
        binding.tvViewAll.setTextColor(accent);
        binding.ivViewAll.setImageTintList(ColorStateList.valueOf(accent));
        binding.btnMembers.setBackground(TeamStyle.membersButton(requireContext(), key));
        binding.ivMembersThumb.setImageResource(TeamStyle.membersThumb(key));
        // A arte dos X-Men (Figma) preenche o espaço; as fotos de grupo são mais largas e
        // mostram todos os integrantes alinhados embaixo, sem cortar as pontas.
        binding.ivMembersThumb.setScaleType(MockData.TEAM_XMEN.equals(key)
                ? ImageView.ScaleType.CENTER_CROP : ImageView.ScaleType.FIT_END);
    }

    @Override public void onDestroyView() { binding = null; super.onDestroyView(); }
}
