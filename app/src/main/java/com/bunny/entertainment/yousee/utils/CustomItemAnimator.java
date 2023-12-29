package com.bunny.entertainment.yousee.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.bunny.entertainment.yousee.R;

public class CustomItemAnimator extends DefaultItemAnimator {

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder holder) {
        // Implement the add animation
        setAddAnimations(holder.itemView);
        return super.animateAdd(holder);
    }

    @Override
    public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder, @NonNull RecyclerView.ViewHolder newHolder, @Nullable ItemHolderInfo preInfo, @Nullable ItemHolderInfo postInfo) {
        // Implement the change animation
        // ... (if needed)
        assert preInfo != null;
        assert postInfo != null;
        return super.animateChange(oldHolder, newHolder, preInfo, postInfo);
    }

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder holder) {
        // Implement the remove animation
        setRemoveAnimations(holder.itemView);
        return super.animateRemove(holder);
    }

    private void setAddAnimations(View view) {
        Animation animation = AnimationUtils.loadAnimation(view.getContext(), R.anim.recyclerviewscroll_top_bottom);
        view.startAnimation(animation);
    }

    private void setRemoveAnimations(View view) {
        // Implement remove animation if needed
    }

    // Implement other necessary methods if needed
}

