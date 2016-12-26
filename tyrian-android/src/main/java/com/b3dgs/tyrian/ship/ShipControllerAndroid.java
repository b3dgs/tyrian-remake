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
package com.b3dgs.tyrian.ship;

import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.Service;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.io.Mouse;

/**
 * Ship control implementation.
 */
public final class ShipControllerAndroid extends FeatureModel implements ShipController
{
    private static final double SPEED_DIVISOR = 10.0;

    private final Force force = new Force();

    @Service private Transformable transformable;
    @Service private ShipUpdater updater;

    @Service private Mouse mouse;
    @Service private Camera camera;

    /**
     * Create an Android ship controller.
     */
    public ShipControllerAndroid()
    {
        super();
    }

    /**
     * Update the transformable position.
     * 
     * @param extrp The extrapolation value.
     */
    private void updatePosition(double extrp)
    {
        transformable.moveLocation(extrp, force, updater.getHitForce());

        final double width = transformable.getWidth() / 2.0;
        final double maxX = camera.getWidth() + camera.getWidth() - width + 4;
        if (transformable.getX() < width)
        {
            transformable.teleportX(width);
        }
        if (transformable.getX() > maxX)
        {
            transformable.teleportX(maxX);
        }
        if (transformable.getY() < camera.getY())
        {
            transformable.teleportY(camera.getY());
        }
        if (transformable.getY() > camera.getY() + camera.getHeight())
        {
            transformable.teleportY(camera.getY() + camera.getHeight());
        }
    }

    @Override
    public void update(double extrp)
    {
        if (mouse.getClick() > 0)
        {
            final Force f = Force.fromVector(transformable.getX(),
                                             transformable.getY(),
                                             mouse.getX() + camera.getX(),
                                             camera.getViewpointY(mouse.getY() - transformable.getHeight() * 1.5));

            force.setDirection(f.getDirectionHorizontal()
                               * f.getVelocity()
                               / SPEED_DIVISOR,
                               f.getDirectionVertical() * f.getVelocity() / SPEED_DIVISOR);
        }
        else
        {
            force.setDirection(0.0, 1.0);
        }

        updatePosition(extrp);
        if (mouse.getClick() > 0)
        {
            updater.fire();
        }
    }
}
