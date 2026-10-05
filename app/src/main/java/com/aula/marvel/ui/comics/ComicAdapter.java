package com.aula.marvel.ui.comics;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.aula.marvel.R;
import com.aula.marvel.data.MockData;
import com.bumptech.glide.Glide;

/** HQ em lista horizontal (item_comic) ou grid 3 colunas (item_comic_grid). Mesmos IDs nos dois layouts. */
public final class ComicAdapter extends ListAdapter<MockData.Comic, ComicAdapter.Holder> {
    private final boolean grid;

    public ComicAdapter(boolean grid) { super(DIFF); this.grid = grid; }

    private static final DiffUtil.ItemCallback<MockData.Comic> DIFF = new DiffUtil.ItemCallback<MockData.Comic>() {
        @Override public boolean areItemsTheSame(@NonNull MockData.Comic a, @NonNull MockData.Comic b) { return a.id == b.id; }
        @Override public boolean areContentsTheSame(@NonNull MockData.Comic a, @NonNull MockData.Comic b) {
            return a.title.equals(b.title) && a.issue.equals(b.issue);
        }
    };

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = grid ? R.layout.item_comic_grid : R.layout.item_comic;
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(layout, parent, false));
    }

    @Override public void onBindViewHolder(@NonNull Holder h, int position) {
        MockData.Comic c = getItem(position);
        Glide.with(h.cover).clear(h.cover);
        if (c.coverUrl != null) {
            Glide.with(h.cover).load(c.coverUrl).centerCrop().into(h.cover);
        } else if (c.cover != 0) {
            h.cover.setImageResource(c.cover);
        } else {
            h.cover.setImageDrawable(null);
        }
        h.title.setText(c.title);
        h.issue.setText(c.issue);
    }

    static final class Holder extends RecyclerView.ViewHolder {
        final ImageView cover; final TextView title, issue;
        Holder(@NonNull View v) {
            super(v);
            cover = v.findViewById(R.id.ivCover);
            title = v.findViewById(R.id.tvTitle);
            issue = v.findViewById(R.id.tvIssue);
        }
    }
}
