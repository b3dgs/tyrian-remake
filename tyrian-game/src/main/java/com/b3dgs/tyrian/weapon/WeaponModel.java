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
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.tyrian.Constant;
import com.b3dgs.tyrian.Sfx;

/**
 * Weapon model implementation.
 */
@FeatureInterface
public class WeaponModel extends FeatureModel
{
    private final Audio sfxFire;
    private final boolean front;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public WeaponModel(Services services, WeaponSetup setup)
    {
        super(services, setup);

        sfxFire = setup.getSfxFire();
        front = setup.getMedia().getPath().contains(Constant.FOLDER_FRONT);
    }

    /**
     * Take the weapon.
     * 
     * @param sfx <code>true</code> to play sound, <code>false</code> else.
     * @return The weapon taken.
     */
    public WeaponUpdater take(boolean sfx)
    {
        if (sfx)
        {
            Sfx.POWER_UP.play();
        }
        return getFeature(WeaponUpdater.class);
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

    /**
     * Check if weapon is front or rear.
     * 
     * @return <code>true</code> if front, <code>false</code> if rear.
     */
    public boolean isFront()
    {
        return front;
    }
}
