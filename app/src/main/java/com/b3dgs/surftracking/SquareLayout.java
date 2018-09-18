/*
 * Copyright (C) 2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.surftracking;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Square layout.
 */
public final class SquareLayout extends RelativeLayout
{
    /**
     * Create layout.
     *
     * @param context The context reference.
     */
    public SquareLayout(Context context)
    {
        super(context);
    }

    /**
     * Create layout.
     *
     * @param context The context reference.
     * @param attrs The attributes reference.
     */
    public SquareLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    /**
     * Create layout.
     *
     * @param context The context reference.
     * @param attrs The attributes reference.
     * @param defStyleAttr The style reference.
     */
    public SquareLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Create layout.
     *
     * @param context The context reference.
     * @param attrs The attributes reference.
     * @param defStyleAttr The style reference.
     * @param defStyleRes The style reference.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SquareLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
