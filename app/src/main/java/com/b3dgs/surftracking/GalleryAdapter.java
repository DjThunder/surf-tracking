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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Represents the gallery inside a session.
 */
final class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.MyViewHolder>
{
    private final ArrayList<String> images = new ArrayList<>();
    private final Context context;

    static class MyViewHolder extends RecyclerView.ViewHolder
    {
        final ImageView thumbnail;
        final ImageView play;

        MyViewHolder(View view)
        {
            super(view);

            thumbnail = view.findViewById(R.id.thumbnail);
            play = view.findViewById(R.id.play);
        }
    }

    /**
     * Create adapter.
     *
     * @param context The context reference.
     */
    GalleryAdapter(Context context)
    {
        super();

        this.context = context;
    }

    /**
     * Set images.
     *
     * @param data The new data.
     */
    void setImages(List<String> data)
    {
        images.clear();
        images.addAll(data);
    }

    /**
     * Get current data.
     *
     * @return The current data.
     */
    ArrayList<String> getImages()
    {
        return images;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_thumbnail, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        final String image = images.get(position);
        Glide.with(context).load(image).thumbnail(1.0f).into(holder.thumbnail);

        if (image.toLowerCase(Locale.ENGLISH).endsWith(GalleryView.EXTENSION_VIDEO))
        {
            holder.play.setVisibility(View.VISIBLE);
        }
        else
        {
            holder.play.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount()
    {
        return images.size();
    }
}
