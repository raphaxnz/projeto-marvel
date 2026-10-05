package com.aula.marvel.ui.search;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.aula.marvel.R;
import com.aula.marvel.data.FavoritesStore;
import com.aula.marvel.data.MockData;
import com.aula.marvel.data.api.MarvelRepository;
import com.aula.marvel.databinding.FragmentSearchBinding;
import com.aula.marvel.ui.common.BottomNav;
import com.aula.marvel.ui.common.Toolbar;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Busca restrita aos integrantes de X-Men, Vingadores e Quarteto (carregados uma vez; filtro local). */
public final class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;
    private CharacterAdapter adapter;
    private List<MockData.Hero> all = new ArrayList<>();

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        Toolbar.logo(this, binding.toolbar);
        BottomNav.bind(this, binding.bottomNav, R.id.searchFragment);

        adapter = new CharacterAdapter(new FavoritesStore(requireContext()), hero -> {
            Bundle args = new Bundle();
            args.putLong("heroId", hero.id);
            NavHostFragment.findNavController(this).navigate(R.id.characterDetailFragment, args);
        });
        binding.rvResults.setAdapter(adapter);

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) { filter(s.toString()); }
            @Override public void afterTextChanged(Editable s) {}
        });

        binding.progress.setVisibility(View.VISIBLE);
        MarvelRepository.get(requireContext()).searchable(heroes -> {
            if (binding == null) return;
            binding.progress.setVisibility(View.GONE);
            all = heroes;
            filter(binding.etSearch.getText() == null ? "" : binding.etSearch.getText().toString());
        });
        return binding.getRoot();
    }

    private void filter(String query) {
        String q = normalize(query);
        if (q.isEmpty()) { adapter.submitList(new ArrayList<>(all)); return; }
        List<MockData.Hero> out = new ArrayList<>();
        for (MockData.Hero h : all) {
            if (normalize(h.name).contains(q) || normalize(h.apiName).contains(q)
                    || normalize(h.realName).contains(q) || normalize(h.team).contains(q)) out.add(h);
        }
        adapter.submitList(out);
    }

    private static String normalize(String s) {
        if (s == null) return "";
        return Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT).trim();
    }

    @Override public void onDestroyView() { binding = null; super.onDestroyView(); }
}
