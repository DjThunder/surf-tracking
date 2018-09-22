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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents the calendar with an item per session by date.
 */
final class CalendarAdapter extends BaseAdapter
{
    /** Sessions list. */
    private final List<String> sessionsName = new ArrayList<>();
    /** Sessions date. */
    private final List<String> sessionsDate = new ArrayList<>();
    private final List<String> days = new ArrayList<>();
    /** Sessions on same day. */
    private final Map<String, List<String>> multipleSessionSameDay = new HashMap<>();
    /** Count for sessions on same day. */
    private final Map<String, Integer> multipleSessionsSameDayCount = new HashMap<>();
    /** Main sessions folder. */
    private final String sessionsFolder;
    private final Context context;
    private final Calendar monthCalendar;
    private final InfoProvider detailProvider;

    /**
     * Create calendar.
     *
     * @param context The context reference.
     * @param monthCalendar The month calendar.
     * @param sessionsFolder The sessions folder.
     * @param detailProvider The detail provider.
     */
    CalendarAdapter(Context context,
                    Calendar monthCalendar,
                    String sessionsFolder,
                    InfoProvider detailProvider)
    {
        super();

        this.context = context;
        this.monthCalendar = monthCalendar;
        this.sessionsFolder = sessionsFolder;
        this.detailProvider = detailProvider;
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1);
    }

    /**
     * Set sessions to map to calendar.
     *
     * @param sessions The sessions set.
     */
    public void setSessions(Collection<String> sessions)
    {
        sessionsName.clear();
        sessionsDate.clear();
        multipleSessionsSameDayCount.clear();

        sessions.forEach(this::handleSession);
    }

    private void handleSession(String session)
    {
        sessionsName.add(session);

        String day = session.substring(session.indexOf('-', session.indexOf('-') + 1) + 1);
        if (day.length() < 2) // Ensure day has two chars
        {
            day = "0" + day;
            sessionsDate.add(session);
        }
        else if (day.length() > 2) // Multiple session a day
        {
            day = day.substring(0, day.indexOf('-'));

            multipleSessionSameDay.computeIfAbsent(day, d -> new ArrayList<>()).add(session);
            multipleSessionsSameDayCount.put(day, 1);
            sessionsDate.add(session.substring(0, session.lastIndexOf('-')));
        }
        else // Date with two chars
        {
            sessionsDate.add(session);
        }
        days.add(day);
    }

    @Override
    public int getCount()
    {
        return days.size();
    }

    @Override
    public Object getItem(int position)
    {
        return days.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final View view = getView(convertView, parent);
        final TextView dayView = view.findViewById(R.id.date);

        // create day string for comparison
        String day = days.get(days.size() - 1 - position);

        // disable empty days from the beginning
        if (day.isEmpty())
        {
            dayView.setClickable(false);
            dayView.setFocusable(false);
        }
        dayView.setText(day);

        if (day.length() == 1)
        {
            day = "0" + day;
        }

        final String sessionDate = monthCalendar.get(Calendar.YEAR) + "-" + getMonth() + "-" + day;

        if (sessionDate.length() > 0 && sessionsName != null && sessionsDate.contains(sessionDate))
        {
            final String session = getSession(day, sessionDate);
            updateSession(view, session, dayView);
            updateScore(view, session);
            updateWaveHeight(view, session);
        }
        else
        {
            view.findViewById(R.id.date_icon).setVisibility(View.INVISIBLE);
        }
        return view;
    }

    private View getView(View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            // if it's not recycled, initialize some attributes
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inflater != null)
            {
                return inflater.inflate(R.layout.calendar_item, parent, false);
            }
        }
        return convertView;
    }

    /**
     * Get month from calendar formatted with two chars.
     *
     * @return The month with two chars.
     */
    private String getMonth()
    {
        final String month = String.valueOf(monthCalendar.get(Calendar.MONTH) + 1);
        if (month.length() == 1)
        {
            return "0" + month;
        }
        return month;
    }

    /**
     * Get session name from date. Handles multiple sessions a session automatically.
     *
     * @param sessionDate The session date.
     * @return The session full name.
     */
    private String getSession(String day, String sessionDate)
    {
        if (multipleSessionSameDay.containsKey(day))
        {
            final List<String> multiDays = multipleSessionSameDay.get(day);
            final String sessionMultiDay = multiDays.get(multipleSessionsSameDayCount.get(day));
            final int id;
            if (multipleSessionsSameDayCount.get(day) > 0)
            {
                id = multipleSessionsSameDayCount.get(day) - 1;
            }
            else
            {
                id = 0;
            }
            multipleSessionsSameDayCount.put(day, id);
            return sessionMultiDay;
        }
        return sessionsName.get(sessionsDate.indexOf(sessionDate));
    }

    /**
     * Update session day view with image thumbnail.
     *
     * @param view The view reference.
     * @param session The session name.
     * @param dayView The day text.
     */
    private void updateSession(View view, String session, TextView dayView)
    {
        ((TextView) view.findViewById(R.id.name)).setText(session);

        final ImageView sessionImage = view.findViewById(R.id.date_icon);
        sessionImage.setVisibility(View.VISIBLE);
        dayView.setTextColor(sessionImage.getContext().getColor(android.R.color.white));

        getSessionThumbnail(session).ifPresent(image -> Glide.with(sessionImage.getContext())
                                                             .load(image)
                                                             .thumbnail(0.25f)
                                                             .into(sessionImage));
    }

    /**
     * Get session thumbnail (first image or the one defined in thumb.txt).
     *
     * @param session The session name.
     * @return The image thumbnail.
     */
    private Optional<String> getSessionThumbnail(String session)
    {
        final File folder = new File(sessionsFolder, Util.getSessionFolder(session));
        final Optional<String> thumbnail = Util.readThumbnailFile(folder);
        if (thumbnail.isPresent())
        {
            return thumbnail;
        }
        return Util.getFirstImage(folder);
    }

    /**
     * Update session score (star visible if top score).
     *
     * @param view The view reference.
     * @param session The session name.
     */
    private void updateScore(View view, String session)
    {
        final int score = Integer.parseInt(detailProvider.getInfo(session).getS());
        if (score > 1)
        {
            setVisible(view, R.id.score);
        }
    }

    /**
     * Update session height bar.
     *
     * @param view The view reference.
     * @param session The session name.
     */
    private void updateWaveHeight(View view, String session)
    {
        final double height = Double.parseDouble(detailProvider.getInfo(session).getHmax());
        if (height > 0.0)
        {
            setVisible(view, R.id.height1);
        }
        if (height > 0.5)
        {
            setVisible(view, R.id.height2);
        }
        if (height > 0.9)
        {
            setVisible(view, R.id.height3);
        }
        if (height > 1.1)
        {
            setVisible(view, R.id.height4);
        }
        if (height > 1.3)
        {
            setVisible(view, R.id.height5);
        }
    }
    
    private static void setVisible(View view, int r)
    {
        view.findViewById(r).setVisibility(View.VISIBLE);
    }
}
