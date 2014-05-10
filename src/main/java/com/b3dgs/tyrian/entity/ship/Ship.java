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
package com.b3dgs.tyrian.entity.ship;

import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.core.Mouse;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.tyrian.entity.Entity;
import com.b3dgs.tyrian.entity.SetupEntity;
import com.b3dgs.tyrian.weapon.Weapon;
import com.b3dgs.tyrian.weapon.front.PulseCannon;
import com.b3dgs.tyrian.weapon.rear.MissileLauncherRear;

/**
 * Ship base implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class Ship
        extends Entity
{
    /** Ship energy. */
    private final Alterable energy;
    /** Weapon front. */
    private Weapon weaponFront;
    /** Weapon rear. */
    private Weapon weaponRear;

    /**
     * @param setup The setup reference.
     */
    protected Ship(SetupEntity setup)
    {
        super(setup);
        energy = new Alterable(1000);
        weaponFront = setup.factoryWeapon.create(PulseCannon.class);
        weaponFront.setOwner(this);
        weaponRear = setup.factoryWeapon.create(MissileLauncherRear.class);
        weaponRear.setOwner(this);
        setSize(24, 28);
        setLocation(0, -216);
    }

    /**
     * Init the ship.
     * 
     * @param mouse The mouse reference.
     * @param screenHeight The screen height.
     * @param camera The camera reference
     */
    public void init(Mouse mouse, CameraGame camera, int screenHeight)
    {
        final double x = mouse.getX() - getWidth() / 2 + camera.getLocationIntX();
        final double y = screenHeight - mouse.getY();
        setLocation(x, y);
    }

    /**
     * Update the ship.
     * 
     * @param extrp The extrapolation value.
     * @param mouse The mouse reference.
     * @param camera The camera reference
     * @param screenHeight The screen height.
     */
    public void update(double extrp, Mouse mouse, CameraGame camera, int screenHeight)
    {
        final double camY = camera.getLocationY();
        final double destX = mouse.getX() - getWidth() / 2 + camera.getLocationX();
        final double destY = screenHeight - mouse.getY() + camY;
        final double x = UtilMath.curveValue(getLocationX(), destX, 3.0);
        final double y = UtilMath.curveValue(getLocationY(), destY, 3.0);
        final double ox = UtilMath.fixBetween(x, 0, camera.getViewWidth());
        final double oy = UtilMath.fixBetween(y, getHeight() / 2 + camY, camera.getViewHeight() + camY);

        setLocation(ox, oy);
        updateTileOffset();
        updateCollision();

        energy.increase(50);
        if (mouse.hasClicked(Mouse.LEFT))
        {
            weaponFront.launch(energy);
            weaponRear.launch(energy);
        }
    }

    /**
     * Increase the weapon level.
     * 
     * @param front <code>true</code> for the front weapon, <code>false</code> for the rear.
     */
    public void increaseWeaponLevel(boolean front)
    {
        if (front)
        {
            weaponFront.level.increase(1);
        }
        else
        {
            weaponRear.level.increase(1);
        }
    }

    /**
     * Set the front weapon.
     * 
     * @param front The front weapon.
     */
    public void setWeaponFront(Weapon front)
    {
        weaponFront = front;
        front.setOwner(this);
    }

    /**
     * Set the rear weapon.
     * 
     * @param rear The rear weapon.
     */
    public void setWeaponRear(Weapon rear)
    {
        weaponRear = rear;
        rear.setOwner(this);
    }

    /**
     * Get energy percent.
     * 
     * @return The energy percent.
     */
    public int getEnergyPercent()
    {
        return energy.getPercent();
    }

    /**
     * Update the ship sprite offset depending of its movement.
     */
    private void updateTileOffset()
    {
        final double diffX = getLocationX() - getLocationOldX();
        if (diffX >= -1 && diffX <= 1)
        {
            setTileOffset(2);
        }
        else if (diffX < -3)
        {
            setTileOffset(0);
        }
        else if (diffX >= -3 && diffX < 1)
        {
            setTileOffset(1);
        }
        else if (diffX > 1 && diffX <= 3)
        {
            setTileOffset(3);
        }
        else if (diffX > 3)
        {
            setTileOffset(4);
        }
    }
}
