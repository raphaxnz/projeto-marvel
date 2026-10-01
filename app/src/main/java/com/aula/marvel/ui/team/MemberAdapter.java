package com.aula.marvel.ui.team;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.aula.marvel.data.MockData;
import com.aula.marvel.databinding.ItemMemberBinding;
import java.util.List;

final class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.Holder>{
    private final List<MockData.Member> members; MemberAdapter(List<MockData.Member> members){this.members=members;}
    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup p,int type){return new Holder(ItemMemberBinding.inflate(LayoutInflater.from(p.getContext()),p,false));}
    @Override public void onBindViewHolder(@NonNull Holder h,int pos){MockData.Member m=members.get(pos);int color=ContextCompat.getColor(h.itemView.getContext(),m.color);h.b.memberPanel.setBackgroundTintList(ColorStateList.valueOf(color));h.b.tvMemberArt.setText(m.mark);h.b.tvMemberName.setText(m.name);}
    @Override public int getItemCount(){return members.size();}
    static final class Holder extends RecyclerView.ViewHolder{final ItemMemberBinding b;Holder(ItemMemberBinding b){super(b.getRoot());this.b=b;}}
}
