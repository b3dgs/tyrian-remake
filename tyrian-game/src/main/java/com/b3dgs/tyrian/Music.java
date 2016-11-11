/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.tyrian;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Audio;
import com.b3dgs.lionengine.core.AudioFactory;
import com.b3dgs.lionengine.core.Medias;

/**
 * List of available musics.
 */
public enum Music
{
    /** Asteroid 2 music. */
    ASTEROID_2(Medias.create(Constant.FOLDER_MUSIC, "asteroid2.lds"));

    /** Playing music. */
    private static Audio playing;

    /**
     * Stop playing music.
     */
    public static void stop()
    {
        if (playing != null)
        {
            playing.stop();
        }
    }

    /** Music media. */
    private final Audio audio;

    /**
     * Constructor.
     * 
     * @param media The music media.
     */
    Music(Media media)
    {
        audio = AudioFactory.loadAudio(media);
    }

    /**
     * Play music.
     */
    public void play()
    {
        if (playing != null)
        {
            playing.stop();
        }
        audio.play();
        playing = audio;
    }
}
