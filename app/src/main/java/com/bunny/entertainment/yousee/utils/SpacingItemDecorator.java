package com.bunny.entertainment.yousee.utils;

import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class SpacingItemDecorator extends RecyclerView.ItemDecoration {
    private int padding;

    public SpacingItemDecorator(int padding) {
        this.padding = padding;
    }

    @Override
    public void getItemOffsets(
            Rect outRect,
            View view,
            RecyclerView parent,
            RecyclerView.State state
    ) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.top = padding;
        outRect.bottom = padding;
        outRect.left = padding;
        outRect.right = padding;
    }
}
