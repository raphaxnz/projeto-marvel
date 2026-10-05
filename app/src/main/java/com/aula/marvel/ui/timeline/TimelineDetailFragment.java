package com.aula.marvel.ui.timeline;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.aula.marvel.R;
import com.aula.marvel.data.MockData;
import com.aula.marvel.data.api.MarvelRepository;
import com.aula.marvel.databinding.FragmentTimelineDetailBinding;
import com.aula.marvel.ui.common.BottomNav;
import com.aula.marvel.ui.common.HeroCircleAdapter;
import com.aula.marvel.ui.common.TeamBadge;
import com.aula.marvel.ui.common.Toolbar;
import com.bumptech.glide.Glide;
import java.util.List;

public final class TimelineDetailFragment extends Fragment {
    private FragmentTimelineDetailBinding binding;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTimelineDetailBinding.inflate(inflater, container, false);
        long entryId = getArguments() == null ? 1L : getArguments().getLong("entryId", 1L);
        MarvelRepository repo = MarvelRepository.get(requireContext());

        Toolbar.title(this, binding.toolbar, "");
        BottomNav.bind(this, binding.bottomNav, R.id.timelineFragment);
        binding.infoCard.ivInfoIcon1.setImageResource(R.drawable.ic_bookmark);
        binding.infoCard.tvInfoLabel1.setText(R.string.publication);
        binding.infoCard.ivInfoIcon2.setImageResource(R.drawable.ic_calendar);
        binding.infoCard.tvInfoLabel2.setText(R.string.year);

        MockData.TimelineEntry cached = repo.cachedArc(entryId);
        if (cached != null) render(cached);
        repo.arc(entryId, this::render);
        return binding.getRoot();
    }

    private void render(MockData.TimelineEntry entry) {
        if (binding == null) return;
        Toolbar.title(this, binding.toolbar, entry.title);

        Glide.with(this).clear(binding.ivHeader);
        Glide.with(this).clear(binding.ivCover);
        if (entry.coverUrl != null) {
            Glide.with(this).load(entry.coverUrl).centerCrop().into(binding.ivHeader);
            Glide.with(this).load(entry.coverUrl).centerCrop().into(binding.ivCover);
        } else {
            binding.ivHeader.setImageResource(entry.header != 0 ? entry.header : MockData.headerFor(entry.team));
            if (entry.cover != 0) binding.ivCover.setImageResource(entry.cover); else binding.ivCover.setImageDrawable(null);
        }

        binding.tvYear.setText(entry.year);
        binding.tvArcTitle.setText(entry.title);
        binding.tvAuthors.setText(entry.authors == null || entry.authors.isEmpty() ? "…" : entry.authors);
        binding.tvAbout.setText(entry.description == null || entry.description.isEmpty() ? "—" : entry.description);
        binding.infoCard.tvInfoValue1.setText(entry.publication == null || entry.publication.isEmpty() ? "—" : entry.publication);
        binding.infoCard.tvInfoValue2.setText(entry.year);

        TeamBadge.bind(binding.ivTeamBadge, entry.team, Color.WHITE);

        // Personagens: da 1ª edição do arco (API); no protótipo, os do Figma.
        List<MockData.Hero> cast = entry.characters != null ? entry.characters
                : entry.coverUrl == null ? MockData.arcCharacters() : null;
        boolean hasCast = cast != null && !cast.isEmpty();
        binding.tvCharactersTitle.setVisibility(hasCast ? View.VISIBLE : View.GONE);
        binding.rvCharacters.setVisibility(hasCast ? View.VISIBLE : View.GONE);
        if (hasCast) {
            binding.rvCharacters.setAdapter(new HeroCircleAdapter(cast, hero -> {
                Bundle args = new Bundle();
                args.putLong("heroId", hero.id);
                NavHostFragment.findNavController(this).navigate(R.id.characterDetailFragment, args);
            }));
        }
    }

    @Override public void onDestroyView() { binding = null; super.onDestroyView(); }
}
