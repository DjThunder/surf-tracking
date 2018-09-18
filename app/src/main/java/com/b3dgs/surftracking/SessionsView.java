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
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Represents the sessions view.
 */
final class SessionsView implements SessionsFolderListener, SessionsLoader
{
    private final Collection<SessionSelectionListener> listeners = new ArrayList<>();
    private final Map<String, SessionData> sessionsData = new HashMap<>();
    private final Map<String, Integer> multiDetail = new HashMap<>();
    private final Map<String, Integer> multiDetailAdded = new HashMap<>();
    private final Consumer<Integer> tabChanger;
    private final ArrayList<String> detailItems;

    private View rootView;
    private LinearLayout monthsLayout;
    private String sessionsFolder;

    /**
     * Create session view.
     *
     * @param tabChanger The function to change tab.
     * @param detailItems The items data.
     */
    SessionsView(Consumer<Integer> tabChanger, ArrayList<String> detailItems)
    {
        super();

        this.tabChanger = tabChanger;
        this.detailItems = detailItems;
    }

    /**
     * Get the sessions data.
     *
     * @return The sessions data.
     */
    public Map<String, SessionData> getData()
    {
        return sessionsData;
    }

    /**
     * Add a session selection listener.
     *
     * @param listener The listener to add.
     */
    public void addListener(SessionSelectionListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Create view.
     *
     * @param inflater The inflater reference.
     * @param container The container reference.
     * @return The created view.
     */
    public View create(LayoutInflater inflater, ViewGroup container)
    {
        if (rootView != null)
        {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_sessions, container, false);
        multiDetail.clear();

        final File folder = new File(sessionsFolder);
        monthsLayout = rootView.findViewById(R.id.months);
        LoadSessionsTask task = new LoadSessionsTask(folder, inflater, this);
        task.execute();

        return rootView;
    }

    /**
     * Create view.
     *
     * @param inflater The inflater reference.
     */
    void create(LayoutInflater inflater,
                LinearLayout parent,
                NavigableMap<String, NavigableMap<String, NavigableSet<String>>> sessions)
    {
        for (final String year : sessions.descendingKeySet())
        {
            final NavigableMap<String, NavigableSet<String>> months = sessions.get(year);
            for (final String month : months.descendingKeySet())
            {
                final Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, Integer.parseInt(year));
                calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);

                parent.addView(createMonth(months.get(month), inflater, calendar));
            }
        }
    }

    @Override
    public ArrayList<View> loadSessions(File folder, LayoutInflater inflater)
    {
        final ArrayList<View> monthsView = new ArrayList<>();
        sessionsData.clear();
        multiDetailAdded.clear();

        final File fileData = new File(sessionsFolder, Constant.FILE_SESSIONS);
        try (InputStream input = new FileInputStream(fileData))
        {
            final NodeList sessionsPerYears = getSessionsPerYears(input);
            final int yearsNumber = sessionsPerYears.getLength();
            for (int y = 0; y < yearsNumber; y++)
            {
                final Node yearsNode = sessionsPerYears.item(y);
                if (yearsNode instanceof Element)
                {
                    final String year = yearsNode.getNodeName().replace("_", "");

                    NavigableSet<String> sessions = new TreeSet<>();
                    String lastMonth = null;
                    final NodeList sessionsNode = yearsNode.getChildNodes();
                    final int sessionsNumber = sessionsNode.getLength();
                    for (int s = sessionsNumber - 1; s >= 0; s--)
                    {
                        final Node sessionNode = sessionsNode.item(s);
                        if (sessionNode instanceof Element)
                        {
                            final String monthDay = ((Element) sessionNode).getAttribute("date");
                            final String month = monthDay.substring(0, monthDay.indexOf('-'));
                            final String sessionName = year + "-" + monthDay;

                            final SessionData data = new SessionData((Element) sessionNode);
                            if (multiDetail.containsKey(sessionName)) // Detected as multi session a day
                            {
                                if (multiDetail.get(sessionName) == 0) // First multi session
                                {
                                    sessionsData.put(sessionName + "-1", sessionsData.get(sessionName));
                                    sessionsData.remove(sessionName);
                                    multiDetail.put(sessionName, 1);
                                    sessions.add(sessionName + "-1");
                                    sessions.remove(sessionName);
                                }
                                if (multiDetailAdded.containsKey(sessionName)) // Next multi session count increase
                                {
                                    multiDetailAdded.put(sessionName, multiDetailAdded.get(sessionName) + 1);
                                }
                                else
                                {
                                    multiDetailAdded.put(sessionName, 2);
                                }

                                final String sessionMultiName = sessionName + "-" + multiDetailAdded.get(sessionName);
                                sessionsData.put(sessionMultiName, data);

                                if (month.equals(lastMonth))
                                {
                                    sessions.add(sessionMultiName);
                                }
                            }
                            else // Single session
                            {
                                sessionsData.put(sessionName, data);
                                multiDetail.put(sessionName, 0);
                                if (lastMonth == null || month.equals(lastMonth))
                                {
                                    sessions.add(sessionName);
                                }
                            }

                            // Add to calendar on month change
                            if (lastMonth != null && !month.equals(lastMonth))
                            {
                                createMonth(year, lastMonth, sessions, inflater, monthsView);

                                sessions = new TreeSet<>();
                                lastMonth = month;

                                sessionsData.put(sessionName, data);
                                multiDetail.put(sessionName, 0);
                                sessions.add(sessionName);
                            }
                            if (lastMonth == null)
                            {
                                lastMonth = month;
                            }
                        }
                    }
                    // Add to calendar on year change
                    createMonth(year, lastMonth, sessions, inflater, monthsView);
                }
            }
        }
        catch (IOException | ParserConfigurationException | SAXException e)
        {
            throw new IllegalArgumentException("XML error", e);
        }

        return monthsView;
    }

    private void createMonth(String year,
                             String month,
                             NavigableSet<String> sessions,
                             LayoutInflater inflater,
                             ArrayList<View> monthsView)
    {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(year));
        calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);

        final View view = createMonth(sessions, inflater, calendar);
        monthsView.add(view);
    }

    @Override
    public void onSessionsLoaded(ArrayList<View> views)
    {
        final Handler handler = new Handler(Looper.getMainLooper());
        final Runnable task = () ->
        {
            AtomicInteger i = new AtomicInteger();
            while (true)
            {
                try
                {
                    final int v = i.getAndIncrement();
                    if (v >= views.size())
                    {
                        break;
                    }
                    handler.post(() -> monthsLayout.addView(views.get(v)));
                    Thread.sleep(100L);
                }
                catch (InterruptedException e)
                {
                    Util.log("Interrupted !", e);
                }
            }
        };
        new Thread(task, "Add views").start();
    }

    /**
     * Create session month view.
     *
     * @param sessions The month sessions.
     * @param inflater The inflater reference.
     * @param month The month calendar.
     * @return The created month view.
     */
    private View createMonth(NavigableSet<String> sessions, LayoutInflater inflater, Calendar month)
    {
        final Context context = inflater.getContext();
        final CalendarAdapter adapter = new CalendarAdapter(context, month, sessionsFolder, this::getInfo);
        adapter.setSessions(sessions);

        final View calendarMonth = inflater.inflate(R.layout.calendar_month, null);

        final GridView gridView = calendarMonth.findViewById(R.id.grid);
        gridView.setAdapter(adapter);

        final TextView title = calendarMonth.findViewById(R.id.title);
        title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));

        final AdapterView.OnItemClickListener detailListener = createDetailListener();
        gridView.setOnItemClickListener(detailListener);

        final AtomicInteger oldOrientation = new AtomicInteger(-1);
        final SensorEventListener rotateListener = new SensorEventListener()
        {
            @Override
            public void onSensorChanged(SensorEvent event)
            {
                final int o = context.getResources().getConfiguration().orientation;
                if (oldOrientation.get() != o)
                {
                    resizeGridView(gridView, sessions.size(), o == Configuration.ORIENTATION_PORTRAIT ? 182 : 320);
                }
                oldOrientation.set(o);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy)
            {
                // Nothing to do
            }
        };

        final SensorManager sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        if (sm != null)
        {
            sm.registerListener(rotateListener,
                                sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                                SensorManager.SENSOR_DELAY_NORMAL);
        }

        return calendarMonth;
    }

    private SessionData getInfo(String session)
    {
        if (!sessionsData.containsKey(session))
        {
            throw new IllegalArgumentException("Unknown session: " + session);
        }
        return sessionsData.get(session);
    }

    private void resizeGridView(GridView gridView, int items, int height)
    {
        final ViewGroup.LayoutParams params = gridView.getLayoutParams();
        int rows = (int) Math.ceil(items / (double) 4);
        params.height = height * rows;
        gridView.setLayoutParams(params);
    }

    private AdapterView.OnItemClickListener createDetailListener()
    {
        return (parent, v, position, id) ->
        {
            final TextView name = v.findViewById(R.id.name);
            if (name.getText().length() > 0)
            {
                final String session = name.getText().toString();
                detailItems.clear();

                final SessionData data = sessionsData.get(session);
                detailItems.add(data.getStart());
                detailItems.add(data.getTime());
                detailItems.add(data.getMd() + " m" + System.lineSeparator() + data.getMf() + " m");
                detailItems.add(data.getCoef());

                detailItems.add(data.getKj() + " Kj");
                detailItems.add(session);
                detailItems.add(data.getHmin() + " m" + System.lineSeparator() + data.getHmax() + " m");
                detailItems.add(data.getP() + " sec");
                detailItems.add(String.valueOf(Util.getDirection(data.getHdir())));
                detailItems.add(String.valueOf(Util.getDirection(data.getVdir())) + "_" + data.getBft() + " bft");
                detailItems.add(data.getS());
                detailItems.add(data.getVp());

                listeners.forEach(l -> l.onSessionSelected(sessionsFolder, session));
                tabChanger.accept(Constant.ID_GALLERY_VIEW);
            }
        };
    }

    private static NodeList getSessionsPerYears(InputStream input)
            throws ParserConfigurationException, SAXException, IOException
    {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder db = factory.newDocumentBuilder();
        final Document doc = db.parse(input);
        final Element root = doc.getDocumentElement();
        return root.getChildNodes();
    }

    @Override
    public void onSessionsFolderSelected(String sessionsFolder)
    {
        this.sessionsFolder = sessionsFolder;
    }
}
