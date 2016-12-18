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
package com.b3dgs.tyrian.weapon;

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.core.Audio;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Service;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.launchable.LauncherListener;
import com.b3dgs.lionengine.game.feature.refreshable.Refreshable;
import com.b3dgs.lionengine.game.feature.transformable.Transformable;
import com.b3dgs.lionengine.graphic.Viewer;
import com.b3dgs.lionengine.util.UtilMath;
import com.b3dgs.tyrian.Constant;
import com.b3dgs.tyrian.Sfx;

/**
 * Weapon updater implementation.
 */
public class WeaponUpdater extends FeatureModel implements Refreshable
{
    private final WeaponModel model;

    @Service private Transformable transformable;
    @Service private Launcher launcher;

    @Service private Viewer viewer;

    /**
     * Create a weapon updater.
     * 
     * @param model The model reference.
     */
    public WeaponUpdater(WeaponModel model)
    {
        super();

        this.model = model;
    }

    @Override
    public void prepare(FeatureProvider provider, Services services)
    {
        super.prepare(provider, services);

        launcher.addListener(new LauncherListener()
        {
            @Override
            public void notifyFired()
            {
                final Audio audio = model.getSfxFire();
                if (audio != null && Sfx.isEnabled())
                {
                    audio.play();
                }
            }
        });
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
    public void update(double extrp)
    {
        // Nothing to do
    }
}
