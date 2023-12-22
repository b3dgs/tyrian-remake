/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;

/**
 * Handle the sound effect.
 */
public enum Sfx
{
    /** Blank. */
    BLANK("blank"),
    /** Menu click. */
    CLICK("click"),
    /** Menu select. */
    SELECT("select"),
    /** Accept. */
    ACCEPT("accept"),
    /** Bullet hit. */
    BULLET_HIT("bullet_hit"),
    /** Player shield. */
    PLAYER_SHIELD("player_shield"),
    /** Player life. */
    PLAYER_LIFE("player_life"),
    /** Weapon pulse. */
    WEAPON_PULSE("weapon_pulse"),
    /** Weapon missile. */
    WEAPON_MISSILE("weapon_missile"),
    /** Weapon machine gun. */
    WEAPON_MACHINE_GUN("weapon_machine_gun"),
    /** Weapon wave. */
    WEAPON_WAVE("weapon_wave"),
    /** Explode large. */
    EXPLODE_LARGE("explode_large"),
    /** Explode little. */
    EXPLODE_LITTLE("explode_little"),
    /** Bonus. */
    BONUS("bonus"),
    /** Power up. */
    POWER_UP("power_up");

    /** Audio file extension. */
    public static final String AUDIO_FILE_EXTENSION = ".wav";

    /** Sound enabled. */
    private static boolean enabled = true;

    /**
     * Set the enabled state.
     * 
     * @param enabled <code>true</code> if enabled, <code>false</code> else.
     */
    public static void setEnabled(boolean enabled)
    {
        Sfx.enabled = enabled;
    }

    /**
     * Check if sound is enabled.
     * 
     * @return <code>true</code> if enabled, <code>false</code> else.
     */
    public static boolean isEnabled()
    {
        return enabled;
    }

    /**
     * Stop all sounds.
     */
    public static void stopAll()
    {
        if (enabled)
        {
            for (final Sfx sfx : Sfx.values())
            {
                sfx.stop();
            }
        }
    }

    /** Sounds list composing the effect. */
    private final Audio sound;

    /**
     * Constructor.
     * 
     * @param sound The sound.
     */
    Sfx(String sound)
    {
        final Media media = Medias.create(Constant.FOLDER_SFX, sound + AUDIO_FILE_EXTENSION);
        this.sound = AudioFactory.loadAudio(media);
    }

    /**
     * Play the sound effect.
     */
    public void play()
    {
        if (enabled)
        {
            sound.play();
        }
    }

    /**
     * Stop playing sound.
     */
    public void stop()
    {
        if (enabled)
        {
            sound.stop();
        }
    }
}
