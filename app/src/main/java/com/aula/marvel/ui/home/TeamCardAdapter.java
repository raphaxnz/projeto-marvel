package com.aula.marvel.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aula.marvel.data.MockData;
import com.aula.marvel.databinding.ItemTeamCardBinding;
import java.util.List;

/** Card de equipe da Home: arte do protótipo (Figma) para cada uma das três equipes. */
final class TeamCardAdapter extends RecyclerView.Adapter<TeamCardAdapter.Holder> {
    interface Listener { void onTeamClick(MockData.Team team); }

    private final List<MockData.Team> teams;
    private final Listener listener;

    TeamCardAdapter(List<MockData.Team> teams, Listener listener) { this.teams = teams; this.listener = listener; }

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemTeamCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override public void onBindViewHolder(@NonNull Holder h, int position) {
        MockData.Team team = teams.get(position);
        h.b.tvName.setText(team.key());
        h.b.ivCover.setImageResource(MockData.coverFor(team.key()));
        h.b.getRoot().setOnClickListener(v -> listener.onTeamClick(team));
    }

    @Override public int getItemCount() { return teams.size(); }

    static final class Holder extends RecyclerView.ViewHolder {
        final ItemTeamCardBinding b;
        Holder(ItemTeamCardBinding b) { super(b.getRoot()); this.b = b; }
    }
}
