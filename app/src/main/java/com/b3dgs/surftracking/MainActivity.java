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

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

public final class MainActivity extends AppCompatActivity
{
    private static final int ACTIVITY_CHOOSE_FILE = 3;

    private final Collection<SessionsFolderListener> listeners = new ArrayList<>();
    private Consumer<Integer> tabChanger;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        setSupportActionBar(findViewById(R.id.toolbar));

        final ViewPager viewPager = findViewById(R.id.container);
        final ArrayList<String> detailItems = new ArrayList<>();

        final SessionsView sessionsView = new SessionsView(viewPager::setCurrentItem, detailItems);
        listeners.add(sessionsView);

        final DetailAdapter detailAdapter = new DetailAdapter(this::startActivity, viewPager.getContext(), detailItems);
        sessionsView.addListener(detailAdapter);
        listeners.add(detailAdapter);

        fireSessionsFolderSelected(Constant.DEFAULT_SESSIONS_FOLDER);

        final GalleryView galleryView = new GalleryView(getFragmentManager(), detailAdapter);
        sessionsView.addListener(galleryView);

        viewPager.setAdapter(new SectionsPager(getSupportFragmentManager(),
                                               sessionsView,
                                               galleryView,
                                               this::getString));

        final TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabChanger = viewPager::setCurrentItem;
        tabChanger.accept(Constant.ID_SESSIONS_VIEW);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        listeners.clear();
        tabChanger = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        switch (keyCode)
        {
            case KeyEvent.KEYCODE_BACK:
                tabChanger.accept(Constant.ID_SESSIONS_VIEW);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_settings)
        {
            final Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("*/*");
            chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
            final Intent intent = Intent.createChooser(chooseFile, getString(R.string.sessions_file));
            startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == ACTIVITY_CHOOSE_FILE)
        {
            Optional.ofNullable(data.getData()).ifPresent(uri -> fireSessionsFolderSelected(uri.getPath()));
        }
    }

    private void fireSessionsFolderSelected(String path)
    {
        listeners.forEach(l -> l.onSessionsFolderSelected(path));
    }
}
