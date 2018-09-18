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

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

/**
 * Represents the gallery view inside the session.
 */
final class GalleryView implements SessionSelectionListener
{
    public static final String ARG_IMAGES = "images";
    public static final String ARG_POSITION = "position";
    public static final String EXTENSION_IMAGE = ".jpg";
    public static final String EXTENSION_VIDEO = ".mp4";

    private final FragmentManager fragmentManager;
    private final DetailAdapter detailAdapter;

    private GalleryAdapter adapter;
    private View rootView;
    private TextView detailDate;

    /**
     * Create detail view.
     *
     * @param fragmentManager The fragment manager reference.
     * @param detailAdapter The detail adapter reference.
     */
    GalleryView(FragmentManager fragmentManager, DetailAdapter detailAdapter)
    {
        super();

        this.fragmentManager = fragmentManager;
        this.detailAdapter = detailAdapter;
    }

    /**
     * Create view.
     *
     * @param inflater The inflater reference.
     * @param container The container reference.
     * @param parent The parent reference.
     * @return The created view.
     */
    public View create(LayoutInflater inflater, ViewGroup container, Fragment parent)
    {
        if (rootView != null)
        {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_gallery, container, false);
        adapter = new GalleryAdapter(rootView.getContext());
        detailDate = rootView.findViewById(R.id.detail_date);

        final RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(inflater.getContext(), 3));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        final ClickListener listener = new ClickListener()
        {
            @Override
            public void onClick(View view, int position)
            {
                onClicked(parent, position);
            }

            @Override
            public void onLongClick(View view, int position)
            {
                // Nothing to do
            }
        };
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(inflater.getContext(), recyclerView, listener));

        final GridView detailGrid = rootView.findViewById(R.id.detailarea);
        detailGrid.setAdapter(detailAdapter);

        return rootView;
    }

    private void onClicked(Fragment parent, int position)
    {
        final ArrayList<String> images = adapter.getImages();
        final String file = images.get(position);
        if (file.toLowerCase(Locale.ENGLISH).endsWith(EXTENSION_IMAGE))
        {
            openImage(position);
        }
        else if (file.toLowerCase(Locale.ENGLISH).endsWith(EXTENSION_VIDEO))
        {
            openVideo(parent, file);
        }
    }

    private void openImage(int position)
    {
        final FragmentTransaction ft = fragmentManager.beginTransaction();
        final SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
        final Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_IMAGES, adapter.getImages());
        bundle.putInt(ARG_POSITION, position);
        newFragment.setArguments(bundle);
        newFragment.show(ft, "slideshow");
    }

    private static void openVideo(Fragment parent, String file)
    {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(file), "video/" + EXTENSION_VIDEO.substring(1));
        parent.startActivity(intent);
    }

    @Override
    public void onSessionSelected(String root, String session)
    {
        if (session != null)
        {
            detailDate.setText(session);

            final ArrayList<String> imageFiles = new ArrayList<>();
            final File folder = new File(root, Util.getSessionFolder(session));
            final File[] files = folder.listFiles();
            if (files != null)
            {
                Arrays.sort(files);
                for (File file : files)
                {
                    final String name = file.getName().toLowerCase(Locale.ENGLISH);
                    if (name.endsWith(EXTENSION_IMAGE) || name.endsWith(EXTENSION_VIDEO))
                    {
                        imageFiles.add(file.getAbsolutePath());
                    }
                }
                adapter.setImages(imageFiles);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
