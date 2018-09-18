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

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Utility class.
 */
final class Util
{
    /**
     * Get folder content.
     *
     * @param folder The folder parent (filter hidden file).
     * @return The folder content.
     */
    public static List<File> list(File folder)
    {
        if (folder != null && folder.isDirectory())
        {
            final File[] content = folder.listFiles();
            if (content != null)
            {
                return Arrays.stream(content).filter(f -> !f.isHidden()).collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    /**
     * Get the session folder path from session name.
     *
     * @param session The session name.
     * @return The session folder path.
     */
    public static String getSessionFolder(String session)
    {
        final String[] split = session.split("-");
        if (split.length == 3)
        {
            return new File(new File(split[0], split[1]), split[2]).getPath();
        }
        return new File(new File(split[0], split[1]), split[2] + "-" + split[3]).getPath();
    }

    /**
     * Read thumbnail file first line.
     *
     * @param folder The session folder reference.
     * @return The thumb file name from first line.
     */
    public static Optional<String> readThumbnailFile(File folder)
    {
        final File thumb = new File(folder, Constant.THUMBNAIL_FILE);
        if (thumb.isFile())
        {
            try (BufferedReader reader = new BufferedReader(new FileReader(thumb)))
            {
                return Optional.of(new File(folder, reader.readLine()).getAbsolutePath());
            }
            catch (IOException e)
            {
                Log.e(Constant.LOG, "Unable to read thumb file !");
            }
        }
        return Optional.empty();
    }

    /**
     * Get first image from folder.
     *
     * @param folder The session folder.
     * @return The first image found.
     */
    public static Optional<String> getFirstImage(File folder)
    {
        return Optional.ofNullable(folder.listFiles()).flatMap(Util::getFirstImage);
    }

    /**
     * Get first image from folder.
     *
     * @param files The files in folder.
     * @return The first image found.
     */
    private static Optional<String> getFirstImage(File[] files)
    {
        Arrays.sort(files);
        for (File file : files)
        {
            final String name = file.getName().toLowerCase(Locale.ENGLISH);
            if (name.endsWith(".jpg") || name.endsWith(".mp4"))
            {
                return Optional.of(file.getAbsolutePath());
            }
        }
        return Optional.empty();
    }

    /**
     * Get resource direction from code.
     *
     * @param code The direction code.
     * @return The associated resource.
     */
    public static int getDirection(String code)
    {
        switch (code)
        {
            case "_N":
                return R.drawable.direction_nord;
            case "NO":
                return R.drawable.direction_nord_west;
            case "NE":
                return R.drawable.direction_nord_east;
            case "_S":
                return R.drawable.direction_south;
            case "SO":
                return R.drawable.direction_south_west;
            case "SE":
                return R.drawable.direction_south_east;
            case "_E":
                return R.drawable.direction_east;
            case "_O":
                return R.drawable.direction_west;
            default:
                throw new IllegalStateException("Unknown wind " + code);
        }
    }

    /**
     * Log error.
     *
     * @param message The error message.
     */
    public static void log(String message)
    {
        Log.i(Constant.LOG, message);
    }

    /**
     * Log error.
     *
     * @param message The error message.
     * @param throwable The stack trace.
     */
    public static void log(String message, Throwable throwable)
    {
        Log.e(Constant.LOG, message, throwable);
    }

    private Util()
    {
        // Private
    }
}
