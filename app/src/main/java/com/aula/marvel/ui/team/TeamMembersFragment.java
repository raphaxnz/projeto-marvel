package com.aula.marvel.ui.team;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;
import com.aula.marvel.R;
import com.aula.marvel.data.MockData;
import com.aula.marvel.data.api.MarvelRepository;
import com.aula.marvel.databinding.FragmentTeamMembersBinding;
import com.aula.marvel.ui.common.Toolbar;
import java.util.List;
import java.util.Locale;

public final class TeamMembersFragment extends Fragment {
    private FragmentTeamMembersBinding binding;
    private List<MockData.Hero> members;

    private final ViewPager2.OnPageChangeCallback pageCallback = new ViewPager2.OnPageChangeCallback() {
        @Override public void onPageSelected(int position) { updateArrows(position); }
    };

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentTeamMembersBinding.inflate(inflater, container, false);
        long teamId = getArguments() == null ? 1L : getArguments().getLong("teamId", 1L);
        MarvelRepository repo = MarvelRepository.get(requireContext());

        Toolbar.title(this, binding.toolbar, repo.keyForTeam(teamId).toUpperCase(Locale.ROOT));
        binding.vpMembers.setOffscreenPageLimit(1);
        binding.vpMembers.registerOnPageChangeCallback(pageCallback);
        binding.btnPrev.setVisibility(View.INVISIBLE);
        binding.btnNext.setVisibility(View.INVISIBLE);

        binding.btnNext.setOnClickListener(v -> {
            if (members == null || members.isEmpty()) return;
            binding.vpMembers.setCurrentItem((binding.vpMembers.getCurrentItem() + 1) % members.size(), true);
        });
        binding.btnPrev.setOnClickListener(v -> {
            int current = binding.vpMembers.getCurrentItem();
            if (current > 0) binding.vpMembers.setCurrentItem(current - 1, true);
        });

        List<MockData.Hero> cached = repo.cachedMembers(teamId);
        if (cached != null) setMembers(cached);
        else repo.teamMembers(teamId, this::setMembers);
        return binding.getRoot();
    }

    private void setMembers(List<MockData.Hero> list) {
        if (binding == null || list == null || list.isEmpty() || list == members) return;
        members = list;
        binding.vpMembers.setAdapter(new MemberAdapter(list, hero -> {
            Bundle args = new Bundle();
            args.putLong("heroId", hero.id);
            NavHostFragment.findNavController(this).navigate(R.id.characterDetailFragment, args);
        }));
        updateArrows(binding.vpMembers.getCurrentItem());
    }

    private void updateArrows(int position) {
        if (binding == null || members == null) return;
        binding.btnPrev.setVisibility(position > 0 ? View.VISIBLE : View.INVISIBLE);
        binding.btnNext.setVisibility(members.size() > 1 ? View.VISIBLE : View.INVISIBLE);
    }

    @Override public void onDestroyView() {
        binding.vpMembers.unregisterOnPageChangeCallback(pageCallback);
        binding = null;
        members = null;
        super.onDestroyView();
    }
}
