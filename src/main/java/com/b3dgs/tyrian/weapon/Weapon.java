/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.ContextGame;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionengine.game.SetupGame;
import com.b3dgs.lionengine.game.projectile.LauncherProjectileGame;
import com.b3dgs.tyrian.AppTyrian;
import com.b3dgs.tyrian.entity.Entity;
import com.b3dgs.tyrian.projectile.Projectile;

/**
 * Weapon base implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class Weapon
        extends LauncherProjectileGame<Entity, Entity, Projectile>
{
    /**
     * Get a weapon configuration file.
     * 
     * @param category The category type.
     * @param type The config associated class.
     * @return The media config.
     */
    public static Media getConfig(WeaponCategory category, Class<? extends Weapon> type)
    {
        return Core.MEDIA.create(AppTyrian.WEAPONS_DIR, category.getPath(), type.getSimpleName() + "."
                + FactoryObjectGame.FILE_DATA_EXTENSION);
    }

    /** Energy to consume. */
    private int consume;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    protected Weapon(SetupGame setup)
    {
        super(setup);
        level.setMax(5);
        consume = 300;
    }

    /**
     * Start shoot.
     * 
     * @param energy The energy reference.
     */
    public void launch(Alterable energy)
    {
        if (energy.isEnough(consume))
        {
            if (launch())
            {
                energy.decrease(consume);
            }
        }
    }

    /**
     * Set the energy to consume.
     * 
     * @param consume The energy to consume.
     */
    protected void setConsume(int consume)
    {
        this.consume = consume;
    }

    /*
     * LauncherProjectileGame
     */

    @Override
    protected void prepareProjectile(ContextGame context)
    {
        // Nothing to do
    }

    @Override
    protected void launchProjectile(Entity owner)
    {
        // Nothing to do
    }

    @Override
    protected void launchProjectile(Entity owner, Entity target)
    {
        // Nothing to do
    }
}
