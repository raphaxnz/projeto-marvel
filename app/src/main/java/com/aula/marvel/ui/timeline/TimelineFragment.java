package com.aula.marvel.ui.timeline;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.aula.marvel.R;
import com.aula.marvel.data.MockData;
import com.aula.marvel.data.api.MarvelRepository;
import com.aula.marvel.databinding.FragmentTimelineBinding;
import com.aula.marvel.ui.common.BottomNav;
import com.aula.marvel.ui.common.TeamStyle;
import com.aula.marvel.ui.common.Toolbar;
import com.google.android.material.datepicker.MaterialDatePicker;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public final class TimelineFragment extends Fragment {
    private FragmentTimelineBinding binding;
    private TimelineAdapter adapter;
    private List<MockData.TimelineEntry> all = new ArrayList<>();

    private String team = MockData.TEAM_XMEN;
    private String era = null;          // null = todas as épocas
    private Integer year = null;        // filtro do calendário
    private boolean onlyWithDescription = false;
    private boolean onlyWithIssue = false;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTimelineBinding.inflate(inflater, container, false);
        Toolbar.logo(this, binding.toolbar);
        BottomNav.bind(this, binding.bottomNav, R.id.timelineFragment);

        adapter = new TimelineAdapter(entry -> {
            Bundle args = new Bundle();
            args.putLong("entryId", entry.id);
            NavHostFragment.findNavController(this).navigate(R.id.timelineDetailFragment, args);
        });
        binding.rvTimeline.setAdapter(adapter);

        TextView[] chips = {binding.chipXmen, binding.chipAvengers, binding.chipFantasticFour};
        String[] teamNames = {MockData.TEAM_XMEN, MockData.TEAM_AVENGERS, MockData.TEAM_FANTASTIC};
        for (int i = 0; i < chips.length; i++) {
            final String name = teamNames[i];
            // Chip selecionado no tom escuro da cor da equipe (os demais mantêm o fundo padrão).
            chips[i].setOnClickListener(v -> {
                for (int j = 0; j < chips.length; j++) {
                    chips[j].setSelected(chips[j] == v);
                    chips[j].setBackgroundTintList(chips[j] == v
                            ? ColorStateList.valueOf(TeamStyle.deep(teamNames[j])) : null);
                }
                loadTeam(name);
            });
        }
        chips[0].performClick();

        binding.btnCalendar.setOnClickListener(v -> openCalendar());
        binding.btnEra.setOnClickListener(v -> openEraMenu());
        binding.btnFilter.setOnClickListener(v -> openExtraFilters());
        return binding.getRoot();
    }

    /** Arcos da equipe selecionada (Comic Vine: story_arc_credits da equipe, ordenados pelo ano). */
    private void loadTeam(String teamKey) {
        team = teamKey;
        int accent = ContextCompat.getColor(requireContext(), TeamStyle.accent(teamKey));
        adapter.setAccent(accent);
        binding.progress.setIndeterminateTintList(ColorStateList.valueOf(accent));
        all = new ArrayList<>();
        adapter.submitList(new ArrayList<>());
        binding.tvEmpty.setVisibility(View.GONE);
        binding.progress.setVisibility(View.VISIBLE);
        MarvelRepository.get(requireContext()).timeline(teamKey, entries -> {
            if (binding == null || !teamKey.equals(team)) return;
            binding.progress.setVisibility(View.GONE);
            all = entries;
            applyFilters();
        });
    }

    private void openCalendar() {
        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(R.string.filter_by_year)
                .build();
        picker.addOnPositiveButtonClickListener(selection -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selection);
            year = calendar.get(Calendar.YEAR);
            binding.tvEra.setText(String.valueOf(year));
            applyFilters();
        });
        picker.show(getChildFragmentManager(), "year_picker");
    }

    private void openEraMenu() {
        PopupMenu menu = new PopupMenu(requireContext(), binding.btnEra);
        menu.getMenu().add(0, 0, 0, R.string.all_eras);
        String[] eras = getResources().getStringArray(R.array.timeline_eras);
        for (int i = 0; i < eras.length; i++) menu.getMenu().add(0, i + 1, i + 1, eras[i]);
        menu.setOnMenuItemClickListener(item -> {
            year = null;
            era = item.getItemId() == 0 ? null : eras[item.getItemId() - 1];
            binding.tvEra.setText(era == null ? getString(R.string.all_eras) : era);
            applyFilters();
            return true;
        });
        menu.show();
    }

    private void openExtraFilters() {
        final boolean[] desc = {onlyWithDescription};
        final boolean[] issue = {onlyWithIssue};
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle(R.string.more_filters)
                .setMultiChoiceItems(
                        new CharSequence[]{getString(R.string.filter_with_description), getString(R.string.filter_with_issue)},
                        new boolean[]{desc[0], issue[0]},
                        (dialog, which, isChecked) -> {
                            if (which == 0) desc[0] = isChecked; else issue[0] = isChecked;
                        })
                .setPositiveButton(R.string.apply, (dialog, which) -> {
                    onlyWithDescription = desc[0];
                    onlyWithIssue = issue[0];
                    applyFilters();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private void applyFilters() {
        List<MockData.TimelineEntry> filtered = new ArrayList<>();
        for (MockData.TimelineEntry entry : all) {
            if (entry.team != null && !entry.team.equals(team)) continue;
            int entryYear = parseYear(entry.year);
            if (year != null && entryYear != year) continue;
            if (era != null && !matchesEra(entryYear, era)) continue;
            if (onlyWithDescription && (entry.description == null || entry.description.trim().isEmpty())) continue;
            if (onlyWithIssue && (entry.issue == null || entry.issue.trim().isEmpty())) continue;
            filtered.add(entry);
        }
        adapter.submitList(filtered);
        binding.tvEmpty.setVisibility(filtered.isEmpty() && binding.progress.getVisibility() != View.VISIBLE
                ? View.VISIBLE : View.GONE);
    }

    private static int parseYear(String value) {
        try { return Integer.parseInt(value.replaceAll("\\D", "").substring(0, 4)); }
        catch (Exception e) { return -1; }
    }

    private static boolean matchesEra(int entryYear, String era) {
        if (entryYear < 0) return false;
        switch (era) {
            case "1960": return entryYear >= 1960 && entryYear < 1970;
            case "1970": return entryYear >= 1970 && entryYear < 1980;
            case "1980": return entryYear >= 1980 && entryYear < 1990;
            case "1990": return entryYear >= 1990 && entryYear < 2000;
            default: return true;
        }
    }

    @Override public void onDestroyView() { binding = null; super.onDestroyView(); }
}