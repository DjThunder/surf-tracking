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

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public final class SectionFragment extends Fragment
{
    /** The fragment argument representing the section number for this fragment. */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private SessionsView sessionsView;
    private GalleryView galleryView;

    /**
     * Create fragment.
     */
    public SectionFragment()
    {
        super();
    }

    /**
     * Returns a new instance of this fragment for the given section number.
     *
     * @param sessionsView The sessions view.
     * @param galleryView The gallery view.
     * @param sectionNumber The section number.
     */
    public static SectionFragment newInstance(SessionsView sessionsView, GalleryView galleryView, int sectionNumber)
    {
        final SectionFragment section = new SectionFragment();
        section.sessionsView = sessionsView;
        section.galleryView = galleryView;

        final Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        section.setArguments(args);

        return section;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle bundle)
    {
        View rootView = null;
        final Bundle argument = getArguments();
        if (argument != null)
        {
            final int tabId = argument.getInt(ARG_SECTION_NUMBER);
            switch (tabId)
            {
                case Constant.ID_SESSIONS_VIEW:
                    if (sessionsView != null)
                    {
                        rootView = sessionsView.create(inflater, container);
                    }
                    break;
                case Constant.ID_GALLERY_VIEW:
                    if (galleryView != null)
                    {
                        rootView = galleryView.create(inflater, container, this);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Unhandled tab " + tabId);
            }
        }
        return rootView;
    }
}
