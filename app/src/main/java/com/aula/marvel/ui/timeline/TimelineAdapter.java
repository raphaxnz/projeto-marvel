package com.aula.marvel.ui.timeline;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.aula.marvel.data.MockData;
import com.aula.marvel.databinding.ItemTimelineBinding;
import com.bumptech.glide.Glide;

final class TimelineAdapter extends ListAdapter<MockData.TimelineEntry, TimelineAdapter.Holder> {
    interface Listener { void onEventClick(MockData.TimelineEntry entry); }

    private final Listener listener;
    /** Cor da equipe selecionada: linha vertical e contorno das bolinhas. */
    @ColorInt private int accent = 0xFFA90C08;

    TimelineAdapter(Listener listener) { super(DIFF); this.listener = listener; }

    void setAccent(@ColorInt int color) {
        if (color == accent) return;
        accent = color;
        notifyItemRangeChanged(0, getItemCount());
    }

    private static final DiffUtil.ItemCallback<MockData.TimelineEntry> DIFF = new DiffUtil.ItemCallback<MockData.TimelineEntry>() {
        @Override public boolean areItemsTheSame(@NonNull MockData.TimelineEntry a, @NonNull MockData.TimelineEntry b) { return a.id == b.id; }
        @Override public boolean areContentsTheSame(@NonNull MockData.TimelineEntry a, @NonNull MockData.TimelineEntry b) {
            return a.title.equals(b.title) && a.year.equals(b.year);
        }
    };

    @NonNull @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(ItemTimelineBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override public void onBindViewHolder(@NonNull Holder h, int pos) {
        MockData.TimelineEntry e = getItem(pos);
        h.b.tvYear.setText(e.year);
        h.b.tvTitle.setText(e.title);
        h.b.tvDescription.setText(e.description);
        h.b.tvIssue.setText(e.issue);
        h.b.vLineTop.setBackgroundColor(accent);
        h.b.vLineBottom.setBackgroundColor(accent);
        if (h.b.vDot.getBackground() instanceof GradientDrawable) {
            float density = h.itemView.getResources().getDisplayMetrics().density;
            ((GradientDrawable) h.b.vDot.getBackground().mutate()).setStroke(Math.round(3 * density), accent);
        }
        Glide.with(h.b.ivCover).clear(h.b.ivCover);
        if (e.coverUrl != null) {
            Glide.with(h.b.ivCover).load(e.coverUrl).centerCrop().into(h.b.ivCover);
        } else if (e.cover != 0) {
            h.b.ivCover.setImageResource(e.cover);
        } else {
            h.b.ivCover.setImageDrawable(null);
        }
        // Linha contínua entre as bolinhas; some nas pontas (Figma).
        h.b.vLineTop.setVisibility(pos == 0 ? View.INVISIBLE : View.VISIBLE);
        h.b.vLineBottom.setVisibility(pos == getItemCount() - 1 ? View.INVISIBLE : View.VISIBLE);
        h.b.card.setOnClickListener(v -> listener.onEventClick(e));
    }

    static final class Holder extends RecyclerView.ViewHolder {
        final ItemTimelineBinding b;
        Holder(ItemTimelineBinding b) { super(b.getRoot()); this.b = b; }
    }
}
