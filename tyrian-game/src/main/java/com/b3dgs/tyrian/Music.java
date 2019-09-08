/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.tyrian;

import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;

/**
 * List of available musics.
 */
public enum Music
{
    /** Asteroid 2 music. */
    ASTEROID_2("asteroid2.lds");

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
     * @param name The music name.
     */
    Music(String name)
    {
        audio = AudioFactory.loadAudio(Medias.create(Constant.FOLDER_MUSIC, name));
        audio.setVolume(50);
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
