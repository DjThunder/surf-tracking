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

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;

import java.io.File;
import java.util.ArrayList;

/**
 * Load sessions interface.
 */
public final class LoadSessionsTask extends AsyncTask<Void, Integer, ArrayList<View>>
{
    private final File folder;
    private final LayoutInflater inflater;
    private final SessionsLoader loader;

    LoadSessionsTask(File folder, LayoutInflater inflater, SessionsLoader loader)
    {
        super();

        this.folder = folder;
        this.inflater = inflater;
        this.loader = loader;
    }

    @Override
    protected ArrayList<View> doInBackground(Void... voids)
    {
        return loader.loadSessions(folder, inflater);
    }

    @Override
    protected void onPostExecute(ArrayList<View> views)
    {
        loader.onSessionsLoaded(views);
    }
}
