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

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestOptions;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Represents the fullscreen gallery view.
 */
public final class SlideshowDialogFragment extends DialogFragment
{
    private ArrayList<String> images;
    private ViewPager viewPager;
    private TextView lblCount;
    private int selectedPosition;

    static SlideshowDialogFragment newInstance()
    {
        SlideshowDialogFragment fragment = new SlideshowDialogFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View view = inflater.inflate(R.layout.fragment_image_slider, container, false);
        viewPager = view.findViewById(R.id.viewpager);
        lblCount = view.findViewById(R.id.lbl_count);

        images = (ArrayList<String>) getArguments().getSerializable(GalleryView.ARG_IMAGES);
        selectedPosition = getArguments().getInt(GalleryView.ARG_POSITION);

        final MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        setCurrentItem(selectedPosition);

        return view;
    }

    private void setCurrentItem(int position)
    {
        viewPager.setCurrentItem(position, false);
        displayMetaInfo(selectedPosition);
    }

    //	page change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener()
    {
        @Override
        public void onPageSelected(int position)
        {
            displayMetaInfo(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2)
        {
        }

        @Override
        public void onPageScrollStateChanged(int arg0)
        {
        }
    };

    private void displayMetaInfo(int position)
    {
        lblCount.setText(String.format(Locale.ENGLISH, "%d / %d", position + 1, images.size()));
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        super.onCreate(savedInstanceState);
    }

    private final class MyViewPagerAdapter extends PagerAdapter
    {
        private LayoutInflater layoutInflater;

        MyViewPagerAdapter()
        {
            super();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position)
        {
            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View view = layoutInflater.inflate(R.layout.image_fullscreen_preview, container, false);
            final String file = images.get(position);
            final ImageView imageViewPreview = view.findViewById(R.id.image_preview);
            imageViewPreview.setOnClickListener(v ->
                                                {
                                                    if (file.toLowerCase(Locale.ENGLISH)
                                                            .endsWith(GalleryView.EXTENSION_VIDEO))
                                                    {
                                                        final Intent intent = new Intent(Intent.ACTION_VIEW);
                                                        intent.setDataAndType(Uri.parse(file),
                                                                              "video/" +
                                                                              GalleryView.EXTENSION_VIDEO.substring(1));
                                                        startActivity(intent);
                                                    }
                                                });

            Glide.with(getActivity())
                 .load(file)
                 .apply(RequestOptions.bitmapTransform(new RotateTransformation(90f)))
                 .into(imageViewPreview);

            final ImageView play = view.findViewById(R.id.full_play);
            if (file.toLowerCase(Locale.ENGLISH).endsWith(".mp4"))
            {
                play.setVisibility(View.VISIBLE);
            }
            else
            {
                play.setVisibility(View.INVISIBLE);
            }

            container.addView(view);

            return view;
        }

        @Override
        public int getCount()
        {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object obj)
        {
            return view == obj;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object)
        {
            container.removeView((View) object);
        }
    }

    private static final class RotateTransformation extends BitmapTransformation
    {
        private float rotateRotationAngle;

        RotateTransformation(float rotateRotationAngle)
        {
            super();

            this.rotateRotationAngle = rotateRotationAngle;
        }

        @Override
        protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap dest, int outWidth, int outHeight)
        {
            final Matrix matrix = new Matrix();
            matrix.postRotate(rotateRotationAngle);

            return Bitmap.createBitmap(dest, 0, 0, dest.getWidth(), dest.getHeight(), matrix, true);
        }

        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest)
        {
            messageDigest.update(("rotate" + rotateRotationAngle).getBytes());
        }
    }
}
