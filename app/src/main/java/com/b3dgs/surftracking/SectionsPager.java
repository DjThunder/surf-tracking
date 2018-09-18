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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.function.Function;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the sections/tabs/pages.
 */
public final class SectionsPager extends FragmentPagerAdapter
{
    private final SessionsView sessionsView;
    private final GalleryView galleryView;
    private final Function<Integer, String> titleGetter;

    /**
     * Create sections pager.
     *
     * @param fragmentManager The fragment manager.
     * @param sessionsView The sessions view.
     * @param galleryView The gallery view.
     * @param titleGetter The title getter provider.
     */
    SectionsPager(FragmentManager fragmentManager,
                  SessionsView sessionsView,
                  GalleryView galleryView,
                  Function<Integer, String> titleGetter)
    {
        super(fragmentManager);

        this.sessionsView = sessionsView;
        this.galleryView = galleryView;
        this.titleGetter = titleGetter;
    }

    @Override
    public Fragment getItem(int position)
    {
        return SectionFragment.newInstance(sessionsView, galleryView, position);
    }

    @Override
    public int getCount()
    {
        return Constant.SECTIONS.length;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        if (position < 0 || position >= Constant.SECTIONS.length)
        {
            throw new IllegalArgumentException("Unhandled position " + position);
        }
        return titleGetter.apply(Constant.SECTIONS[position]);
    }
}
