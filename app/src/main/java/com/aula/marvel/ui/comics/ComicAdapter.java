package com.aula.marvel.ui.comics;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.aula.marvel.data.MockData;
import com.aula.marvel.databinding.ItemComicBinding;

final class ComicAdapter extends ListAdapter<MockData.Comic,ComicAdapter.Holder>{
    ComicAdapter(){super(DIFF);} private static final DiffUtil.ItemCallback<MockData.Comic> DIFF=new DiffUtil.ItemCallback<MockData.Comic>(){
        @Override public boolean areItemsTheSame(@NonNull MockData.Comic a,@NonNull MockData.Comic b){return a.id==b.id;}
        @Override public boolean areContentsTheSame(@NonNull MockData.Comic a,@NonNull MockData.Comic b){return a.title.equals(b.title)&&a.issue.equals(b.issue);}
    };
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup p,int type){return new Holder(ItemComicBinding.inflate(LayoutInflater.from(p.getContext()),p,false));}
    @Override public void onBindViewHolder(@NonNull Holder h,int pos){MockData.Comic c=getItem(pos);h.b.tvCover.setText(c.mark);h.b.tvTitle.setText(c.title);h.b.tvIssue.setText(c.issue);}
    static final class Holder extends RecyclerView.ViewHolder{final ItemComicBinding b;Holder(ItemComicBinding b){super(b.getRoot());this.b=b;}}
}
