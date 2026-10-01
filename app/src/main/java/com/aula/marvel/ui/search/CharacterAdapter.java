package com.aula.marvel.ui.search;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.aula.marvel.data.MockData;
import com.aula.marvel.databinding.ItemCharacterResultBinding;

final class CharacterAdapter extends ListAdapter<MockData.Hero,CharacterAdapter.Holder> {
    interface Listener { void onHeroClick(MockData.Hero hero); }
    private final Listener listener;
    CharacterAdapter(Listener listener){super(DIFF);this.listener=listener;}
    private static final DiffUtil.ItemCallback<MockData.Hero> DIFF=new DiffUtil.ItemCallback<MockData.Hero>(){
        @Override public boolean areItemsTheSame(@NonNull MockData.Hero a,@NonNull MockData.Hero b){return a.id==b.id;}
        @Override public boolean areContentsTheSame(@NonNull MockData.Hero a,@NonNull MockData.Hero b){return a.name.equals(b.name)&&a.team.equals(b.team);}
    };
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup p,int type){return new Holder(ItemCharacterResultBinding.inflate(LayoutInflater.from(p.getContext()),p,false));}
    @Override public void onBindViewHolder(@NonNull Holder h,int position){
        MockData.Hero hero=getItem(position); h.b.tvAvatar.setText(hero.initials); h.b.tvAvatar.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(h.itemView.getContext(),hero.color))); h.b.tvName.setText(hero.name); h.b.tvRealName.setText(hero.realName); h.b.tvTeam.setText(hero.team);
        h.b.getRoot().setOnClickListener(v->{int pos=h.getBindingAdapterPosition();if(pos!=RecyclerView.NO_POSITION)listener.onHeroClick(getItem(pos));});
        h.b.btnFavorite.setOnClickListener(v->v.setSelected(!v.isSelected()));
    }
    static final class Holder extends RecyclerView.ViewHolder{final ItemCharacterResultBinding b;Holder(ItemCharacterResultBinding b){super(b.getRoot());this.b=b;}}
}
