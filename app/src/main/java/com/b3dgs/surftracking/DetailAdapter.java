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
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.media.ExifInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/**
 * Represents the session detail information.
 */
final class DetailAdapter extends ArrayAdapter<String> implements SessionsFolderListener, SessionSelectionListener
{
    private static final String APP_MAPS = "com.google.android.apps.maps";

    private final Consumer<Intent> activityStarter;
    private String sessionsFolder;

    /**
     * Create adapter.
     *
     * @param activityStarter The activity starter function.
     * @param context The context reference.
     * @param items The handled items.
     */
    DetailAdapter(Consumer<Intent> activityStarter, @NonNull Context context, List<String> items)
    {
        super(context, 0, items);

        this.activityStarter = activityStarter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        final View view = getView(convertView, parent);
        final String item = getItem(position);
        if (item != null)
        {
            final ImageView icon = view.findViewById(R.id.detail_icon);
            Glide.with(getContext()).load(getIcon(position, item)).into(icon);

            final TextView value = view.findViewById(R.id.detail_value);
            updateDetail(position, value, item, icon);
        }

        return view;
    }

    /**
     * Check if an existing view is being reused, otherwise inflate the view
     *
     * @param convertView The convert view.
     * @param parent The parent reference.
     * @return The view.
     */
    private View getView(View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            return LayoutInflater.from(getContext()).inflate(R.layout.detail_item, parent, false);
        }
        return convertView;
    }

    /**
     * Get detail icon.
     *
     * @param position The detail position.
     * @param item The detail item.
     * @return The icon.
     */
    private static int getIcon(int position, String item)
    {
        switch (position)
        {
            case 0:
                return R.drawable.session_start;
            case 1:
                return R.drawable.session_duration;
            case 2:
                return R.drawable.tide;
            case 3:
                return R.drawable.coef;
            case 4:
                return R.drawable.wave_energy;
            case 5:
                return R.drawable.gps;
            case 6:
                return R.drawable.wave_height;
            case 7:
                return R.drawable.wave_period;
            case 8:
                return Integer.parseInt(item.split("_")[0]);
            case 9:
                return Integer.parseInt(item.split("_")[0]);
            case 10:
                return R.drawable.rank;
            case 11:
                return R.drawable.wave_taken;
            default:
                throw new IllegalArgumentException("Unknown position: " + position);
        }
    }

    /**
     * Update detail info.
     *
     * @param position The detail position.
     * @param value The detail value.
     * @param item The detail item.
     * @param icon The detail icon.
     */
    private void updateDetail(int position, TextView value, String item, ImageView icon)
    {
        if (position == 9) // Wind dir
        {
            value.setText(item.split("_")[1]);
        }
        else if (position != 8) // Wave dir
        {
            value.setText(item);
        }
        if (position == 5) // GPS
        {
            value.setVisibility(View.INVISIBLE);
            icon.setOnClickListener(v -> startMaps(value.getText().toString()));
        }
    }

    /**
     * Start maps app with current GPS location.
     *
     * @param session The session name.
     */
    private void startMaps(String session)
    {
        final String gps = getGps(session);
        final Uri gmmIntentUri = Uri.parse("geo:" + gps + "?q=" + gps + "Session");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage(APP_MAPS);
        activityStarter.accept(mapIntent);
    }

    /**
     * Get session GPS location.
     *
     * @param session The session name.
     * @return The associated GPS.
     */
    private String getGps(String session)
    {
        final File folder = new File(sessionsFolder, Util.getSessionFolder(session));
        try
        {
            final File[] files = folder.listFiles();
            if (files != null)
            {
                for (File file : files)
                {
                    if (file.getName().toLowerCase(Locale.ENGLISH).endsWith(".jpg"))
                    {
                        final ExifInterface exif = new ExifInterface(file.getAbsolutePath());
                        double[] latLong = exif.getLatLong();
                        return latLong[0] + "," + latLong[1];
                    }
                }
            }
        }
        catch (IOException e)
        {
            Util.log("Unable to set GPS !", e);
        }
        return "0,0";
    }

    @Override
    public void onSessionsFolderSelected(String sessionsFolder)
    {
        this.sessionsFolder = sessionsFolder;
    }

    @Override
    public void onSessionSelected(String root, String session)
    {
        notifyDataSetChanged();
    }
}
