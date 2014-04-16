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
package com.b3dgs.tyrian.weapon.other;

import com.b3dgs.tyrian.Sfx;
import com.b3dgs.tyrian.entity.Entity;
import com.b3dgs.tyrian.projectile.Impulse;
import com.b3dgs.tyrian.weapon.SetupWeapon;
import com.b3dgs.tyrian.weapon.Weapon;

/**
 * Impulser implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Impulser
        extends Weapon
{
    /**
     * @see Weapon#Weapon(SetupWeapon)
     */
    public Impulser(SetupWeapon setup)
    {
        super(setup);
        setOffsetY(-5);
        setRate(1000);
    }

    /*
     * Weapon
     */

    @Override
    protected void launchProjectile(Entity owner, Entity target)
    {
        int dmg;
        final int speed = 2;
        Sfx.WEAPON_PULSE.play();
        dmg = 60;
        addProjectile(Impulse.class, dmg, target, speed, 0, 0);
    }
}
