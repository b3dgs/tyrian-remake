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
package com.b3dgs.tyrian.weapon;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.tyrian.Constant;
import com.b3dgs.tyrian.Sfx;

/**
 * Weapon setup implementation.
 */
public class WeaponSetup extends Setup
{
    /** Fire node name. */
    private static final String NODE_FIRE = com.b3dgs.lionengine.Constant.XML_PREFIX + "fire";

    private final Audio sfxFire;

    /**
     * Create a weapon setup.
     * 
     * @param config The config media.
     * @throws LionEngineException If error when opening the media.
     */
    public WeaponSetup(Media config)
    {
        super(config);

        if (hasNode(NODE_FIRE))
        {
            final String sfxFile = getText(NODE_FIRE);
            sfxFire = AudioFactory.loadAudio(Medias.create(Constant.FOLDER_SFX,
                                                           UtilFile.normalizeExtension(sfxFile,
                                                                                       Sfx.AUDIO_FILE_EXTENSION)));
        }
        else
        {
            sfxFire = null;
        }
    }

    /**
     * Get the SFX fire.
     * 
     * @return The SFX fire.
     */
    public Audio getSfxFire()
    {
        return sfxFire;
    }
}
