package com.aula.marvel.ui.timeline;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.aula.marvel.data.MockData;
import com.aula.marvel.databinding.ItemTimelineBinding;

final class TimelineAdapter extends ListAdapter<MockData.TimelineEntry,TimelineAdapter.Holder>{
    interface Listener{void onEventClick(MockData.TimelineEntry event);} private final Listener listener;
    TimelineAdapter(Listener listener){super(DIFF);this.listener=listener;}
    private static final DiffUtil.ItemCallback<MockData.TimelineEntry> DIFF=new DiffUtil.ItemCallback<MockData.TimelineEntry>(){
        @Override public boolean areItemsTheSame(@NonNull MockData.TimelineEntry a,@NonNull MockData.TimelineEntry b){return a.id==b.id;}
        @Override public boolean areContentsTheSame(@NonNull MockData.TimelineEntry a,@NonNull MockData.TimelineEntry b){return a.title.equals(b.title);}
    };
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup p,int type){return new Holder(ItemTimelineBinding.inflate(LayoutInflater.from(p.getContext()),p,false));}
    @Override public void onBindViewHolder(@NonNull Holder h,int pos){MockData.TimelineEntry e=getItem(pos);h.b.tvYear.setText(e.year);h.b.tvTitle.setText(e.title);h.b.tvDescription.setText(e.description);h.b.tvIssue.setText(e.issue);h.b.cardEvent.setOnClickListener(v->{int p=h.getBindingAdapterPosition();if(p!=RecyclerView.NO_POSITION)listener.onEventClick(getItem(p));});}
    static final class Holder extends RecyclerView.ViewHolder{final ItemTimelineBinding b;Holder(ItemTimelineBinding b){super(b.getRoot());this.b=b;}}
}
