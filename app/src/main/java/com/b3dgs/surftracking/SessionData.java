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

import org.w3c.dom.Element;

/**
 * Session data representation.
 */
public final class SessionData
{
    private final String start;
    private final String time;
    private final String md;
    private final String mf;
    private final String coef;
    private final String hmin;
    private final String hmax;
    private final String p;
    private final String hdir;
    private final String vdir;
    private final String bft;
    private final String kj;
    private final String s;
    private final String vp;

    /**
     * Create and load session data.
     *
     * @param e The data element.
     */
    SessionData(Element e)
    {
        super();

        start = get(e, "start");
        time = get(e, "time");
        md = String.valueOf(Double.parseDouble(get(e, "Md")));
        mf = String.valueOf(Double.parseDouble(get(e, "Mf")));
        coef = get(e, "coef");
        hmin = String.valueOf(Double.parseDouble(get(e, "Hm")));
        hmax = String.valueOf(Double.parseDouble(get(e, "HM")));
        p = get(e, "P");
        kj = String.valueOf(getKj(Double.parseDouble(hmax), Double.parseDouble(p)));
        hdir = get(e, "Hdir");
        vdir = get(e, "Vdir");
        bft = get(e, "Bft");
        s = get(e, "S");
        vp = get(e, "Vp");
    }

    /**
     * Get session start time.
     *
     * @return The session start time.
     */
    public String getStart()
    {
        return start;
    }

    /**
     * Get session duration.
     *
     * @return The session duration.
     */
    public String getTime()
    {
        return time;
    }

    /**
     * Get tide start.
     *
     * @return The tide start.
     */
    public String getMd()
    {
        return md;
    }

    /**
     * Get tide end.
     *
     * @return The tide end.
     */
    public String getMf()
    {
        return mf;
    }

    /**
     * Get tide factor.
     *
     * @return The tide factor.
     */
    public String getCoef()
    {
        return coef;
    }

    /**
     * Get minimum wave height.
     *
     * @return The minimum wave height.
     */
    public String getHmin()
    {
        return hmin;
    }

    /**
     * Get maximum wave height.
     *
     * @return The maximum wave height.
     */
    public String getHmax()
    {
        return hmax;
    }

    /**
     * Get the wave period.
     *
     * @return The wave period.
     */
    public String getP()
    {
        return p;
    }

    /**
     * Get the wave direction.
     *
     * @return The wave direction.
     */
    public String getHdir()
    {
        return hdir;
    }

    /**
     * Get the wind direction.
     *
     * @return The wind direction.
     */
    public String getVdir()
    {
        return vdir;
    }

    /**
     * Get wind strength.
     *
     * @return The wind strength.
     */
    public String getBft()
    {
        return bft;
    }

    /**
     * Get the computed wave energy.
     *
     * @return The computed wave energy.
     */
    public String getKj()
    {
        return kj;
    }

    /**
     * Get the session score.
     *
     * @return The session score.
     */
    public String getS()
    {
        return s;
    }

    /**
     * Get the taken waves count.
     *
     * @return The taken waves count.
     */
    public String getVp()
    {
        return vp;
    }

    private static String get(Element element, String attribute)
    {
        return element.getAttribute(attribute);
    }

    private static int getKj(double hm, double p)
    {
        return (int) Math.floor(hm * hm * p * 21);
    }
}
