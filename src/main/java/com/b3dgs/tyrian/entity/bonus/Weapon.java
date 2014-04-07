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

import com.b3dgs.tyrian.Sfx;
import com.b3dgs.tyrian.entity.ship.Ship;
import com.b3dgs.tyrian.weapon.FactoryWeapon;
import com.b3dgs.tyrian.weapon.WeaponType;

/**
 * Weapon bonus.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
abstract class Weapon
        extends Bonus
{
    /** Type. */
    private final EntityBonusType type;
    /** Factory weapon. */
    private final FactoryWeapon factoryWeapon;
    /** Front. */
    private final boolean front;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     * @param front <code>true</code> if front weapon, <code>false</code> if rear.
     */
    protected Weapon(SetupEntityBonus setup, boolean front)
    {
        super(setup);
        type = setup.type;
        factoryWeapon = setup.factoryWeapon;
        this.front = front;
    }

    @Override
    protected void onDestroyed()
    {
        Sfx.POWER_UP.play();
    }

    @Override
    protected void onHit(Ship ship)
    {
        super.onHit(ship);
        if (front)
        {
            ship.setWeaponFront(factoryWeapon.create(WeaponType.valueOf(type.name())));
        }
        else
        {
            ship.setWeaponRear(factoryWeapon.create(WeaponType.valueOf(type.name())));
        }
    }
}
