package com.aula.marvel.ui.onboarding;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.aula.marvel.data.MockData;
import com.aula.marvel.databinding.ItemOnboardingBinding;
import java.util.List;

final class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.PageHolder> {
    private final List<MockData.OnboardingPage> pages;
    OnboardingAdapter(List<MockData.OnboardingPage> pages){this.pages=pages;}
    @NonNull @Override public PageHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){return new PageHolder(ItemOnboardingBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));}
    @Override public void onBindViewHolder(@NonNull PageHolder h,int position){
        MockData.OnboardingPage page=pages.get(position); h.b.tvHeroMark.setText(page.mark); h.b.tvTitle.setText(page.title); h.b.tvDescription.setText(page.description);
        h.b.dotOne.setAlpha(position==0?1f:.25f); h.b.dotTwo.setAlpha(position==1?1f:.25f); h.b.dotThree.setAlpha(position==2?1f:.25f);
    }
    @Override public int getItemCount(){return pages.size();}
    static final class PageHolder extends RecyclerView.ViewHolder { final ItemOnboardingBinding b; PageHolder(ItemOnboardingBinding b){super(b.getRoot());this.b=b;} }
}
