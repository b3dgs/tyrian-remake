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
package com.b3dgs.tyrian.entity.bonus;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.ContextGame;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.tyrian.Sfx;
import com.b3dgs.tyrian.entity.ship.Ship;
import com.b3dgs.tyrian.weapon.FactoryWeapon;
import com.b3dgs.tyrian.weapon.Weapon;

/**
 * Weapon bonus.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
abstract class WeaponBonus
        extends Bonus
{
    /** Weapon target. */
    private final Media target;
    /** Front. */
    private final boolean front;

    /** Factory weapon. */
    private FactoryWeapon factoryWeapon;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     * @param target The target reference.
     * @param front <code>true</code> if front weapon, <code>false</code> if rear.
     */
    protected WeaponBonus(SetupSurfaceGame setup, Media target, boolean front)
    {
        super(setup);
        this.target = target;
        this.front = front;
    }

    /*
     * Entity
     */

    @Override
    public void prepare(ContextGame context)
    {
        super.prepare(context);
        factoryWeapon = context.getService(FactoryWeapon.class);
    }

    @Override
    protected void onDestroy()
    {
        Sfx.POWER_UP.play();
    }

    @Override
    protected void onHit(Ship ship)
    {
        super.onHit(ship);
        if (front)
        {
            final Weapon weapon = factoryWeapon.create(target);
            ship.setWeaponFront(weapon);
        }
        else
        {
            final Weapon weapon = factoryWeapon.create(target);
            ship.setWeaponRear(weapon);
        }
    }
}
