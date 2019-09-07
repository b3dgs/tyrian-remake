/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Refreshable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.launchable.LauncherListener;
import com.b3dgs.tyrian.Constant;
import com.b3dgs.tyrian.Sfx;

/**
 * Weapon updater implementation.
 */
@FeatureInterface
public class WeaponUpdater extends FeatureModel implements Refreshable
{
    private final WeaponModel model;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Launcher launcher;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param model The model reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public WeaponUpdater(Services services, WeaponSetup setup, WeaponModel model)
    {
        super(services, setup);

        Check.notNull(model);

        this.model = model;
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        launcher.addListener((LauncherListener) () ->
        {
            final Audio audio = model.getSfxFire();
            if (audio != null && Sfx.isEnabled())
            {
                audio.play();
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
        launcher.update(extrp);
    }
}
