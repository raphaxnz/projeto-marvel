package com.aula.marvel.ui.comics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.aula.marvel.R;
import com.aula.marvel.data.api.MarvelRepository;
import com.aula.marvel.databinding.FragmentComicsBinding;
import com.aula.marvel.ui.common.BottomNav;
import com.aula.marvel.ui.common.Toolbar;
import java.util.Locale;

/** Todas as HQs da equipe (volume principal na Comic Vine), em grid de 3 colunas. */
public final class ComicsFragment extends Fragment {
    private FragmentComicsBinding binding;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentComicsBinding.inflate(inflater, container, false);
        long teamId = getArguments() == null ? 1L : getArguments().getLong("teamId", 1L);
        MarvelRepository repo = MarvelRepository.get(requireContext());

        Toolbar.title(this, binding.toolbar, repo.keyForTeam(teamId).toUpperCase(Locale.ROOT));
        BottomNav.bind(this, binding.bottomNav, R.id.homeFragment);

        ComicAdapter adapter = new ComicAdapter(true);
        binding.rvComics.setAdapter(adapter);
        repo.teamComics(teamId, list -> { if (binding != null) adapter.submitList(list); });
        return binding.getRoot();
    }

    @Override public void onDestroyView() { binding = null; super.onDestroyView(); }
}
