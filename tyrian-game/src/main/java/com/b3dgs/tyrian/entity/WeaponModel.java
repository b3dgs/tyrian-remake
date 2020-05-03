/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.tyrian.entity;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.launchable.LauncherListener;
import com.b3dgs.tyrian.Constant;
import com.b3dgs.tyrian.Sfx;

/**
 * Weapon model implementation.
 */
@FeatureInterface
public class WeaponModel extends FeatureModel implements Routine
{
    /** Fire node name. */
    private static final String NODE_FIRE = "fire";

    private final Audio sfxFire;
    private final boolean front;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Launcher launcher;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public WeaponModel(Services services, Setup setup)
    {
        super(services, setup);

        if (setup.hasNode(NODE_FIRE))
        {
            final String sfxFile = setup.getText(NODE_FIRE);
            sfxFire = AudioFactory.loadAudio(Medias.create(Constant.FOLDER_SFX,
                                                           UtilFile.normalizeExtension(sfxFile,
                                                                                       Sfx.AUDIO_FILE_EXTENSION)));
        }
        else
        {
            sfxFire = null;
        }

        front = setup.getMedia().getPath().contains(Constant.FOLDER_FRONT);
    }

    /**
     * Take the weapon.
     * 
     * @param sfx <code>true</code> to play sound, <code>false</code> else.
     * @return The weapon taken.
     */
    public WeaponModel take(boolean sfx)
    {
        if (sfx)
        {
            Sfx.POWER_UP.play();
        }
        return this;
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

    /**
     * Fire weapon.
     * 
     * @param from The fire source.
     * @param initial The fire initial speed.
     */
    public void fire(Localizable from, Direction initial)
    {
        transformable.setLocation(from.getX(), from.getY());
        launcher.fire(initial);
    }

    /**
     * Fire weapon.
     * 
     * @param from The fire source.
     * @param target The fire target.
     */
    public void fire(Localizable from, Localizable target)
    {
        transformable.setLocation(from.getX(), from.getY());
        launcher.fire(target);
    }

    /**
     * Increase weapon level.
     */
    public void increaseLevel()
    {
        launcher.setLevel(UtilMath.clamp(launcher.getLevel() + 1, 0, Constant.WEAPON_LEVEL_MAX));
    }

    /**
     * Get the weapon level.
     * 
     * @return The weapon level.
     */
    public int getLevel()
    {
        return launcher.getLevel();
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        launcher.addListener((LauncherListener) () ->
        {
            if (sfxFire != null && Sfx.isEnabled())
            {
                sfxFire.play();
            }
        });
    }

    @Override
    public void update(double extrp)
    {
        launcher.update(extrp);
    }
}
