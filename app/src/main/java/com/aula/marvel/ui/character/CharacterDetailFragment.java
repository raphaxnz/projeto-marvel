package com.aula.marvel.ui.character;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import com.aula.marvel.R;
import com.aula.marvel.data.MockData;
import com.aula.marvel.data.api.MarvelRepository;
import com.aula.marvel.databinding.FragmentCharacterDetailBinding;
import com.aula.marvel.databinding.ItemPowerChipBinding;
import com.aula.marvel.ui.comics.ComicAdapter;
import com.aula.marvel.ui.common.BottomNav;
import com.aula.marvel.ui.common.TeamBadge;
import com.aula.marvel.ui.common.Toolbar;
import com.bumptech.glide.Glide;
import java.util.List;

public final class CharacterDetailFragment extends Fragment {
    private FragmentCharacterDetailBinding binding;
    private ComicAdapter comics;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentCharacterDetailBinding.inflate(inflater, container, false);
        long heroId = getArguments() == null ? 1L : getArguments().getLong("heroId", 1L);
        MarvelRepository repo = MarvelRepository.get(requireContext());

        Toolbar.title(this, binding.toolbar, "");
        BottomNav.bind(this, binding.bottomNav, R.id.searchFragment);
        comics = new ComicAdapter(false);
        binding.rvComics.setAdapter(comics);

        MockData.Hero cached = repo.cachedHero(heroId);
        if (cached != null) render(cached);
        repo.character(heroId, this::render);
        repo.characterComics(heroId, this::renderComics);
        return binding.getRoot();
    }

    private void render(MockData.Hero hero) {
        if (binding == null) return;
        Toolbar.title(this, binding.toolbar, hero.name);
        int color = ContextCompat.getColor(requireContext(), hero.color != 0 ? hero.color : R.color.marvel_blue);

        // Header na cor do personagem (Figma: tom mais escuro)
        binding.header.setBackgroundColor(ColorUtils.blendARGB(color, 0xFF000000, 0.3f));
        Glide.with(this).clear(binding.ivArt);
        if (hero.art != 0) {
            binding.ivArt.setImageResource(hero.art);
        } else if (hero.imageUrl != null) {
            Glide.with(this).load(hero.imageUrl).fitCenter().into(binding.ivArt);
        } else {
            binding.ivArt.setImageDrawable(null);
        }
        binding.tvHeaderName.setText(hero.name);
        binding.tvName.setText(hero.name);
        binding.tvName.setTextColor(ColorUtils.blendARGB(color, 0xFFFFFFFF, 0.35f));
        binding.tvRealName.setText(hero.realName);

        TeamBadge.bind(binding.ivTeamBadge, hero.team, ContextCompat.getColor(requireContext(), R.color.app_text_primary));

        String about = hero.apiBio != null && !hero.apiBio.isEmpty() ? hero.apiBio : hero.bio;
        binding.tvAbout.setText(about == null || about.isEmpty() ? "—" : about);
        binding.tvFirstAppearance.setText(hero.firstAppearance);
        binding.tvBirthday.setText(hero.birth);

        // Chips de poder na cor do personagem
        binding.chipsPowers.removeAllViews();
        int shown = 0;
        for (String power : hero.powers) {
            if (shown++ >= 8) break;
            TextView chip = ItemPowerChipBinding.inflate(getLayoutInflater(), binding.chipsPowers, false).getRoot();
            chip.setText(power);
            chip.setBackgroundTintList(ColorStateList.valueOf(color));
            binding.chipsPowers.addView(chip);
        }
        boolean hasPowers = !hero.powers.isEmpty();
        binding.tvPowersTitle.setVisibility(hasPowers ? View.VISIBLE : View.GONE);
        binding.chipsPowers.setVisibility(hasPowers ? View.VISIBLE : View.GONE);
    }

    private void renderComics(List<MockData.Comic> list) {
        if (binding == null) return;
        comics.submitList(list.size() > 10 ? list.subList(0, 10) : list);
        boolean has = !list.isEmpty();
        binding.tvComicsTitle.setVisibility(has ? View.VISIBLE : View.GONE);
        binding.rvComics.setVisibility(has ? View.VISIBLE : View.GONE);
    }

    @Override public void onDestroyView() { binding = null; super.onDestroyView(); }
}
