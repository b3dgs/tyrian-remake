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

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Service;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.io.Mouse;

/**
 * Ship control implementation.
 */
public final class ShipControllerPc extends FeatureModel implements ShipController
{
    private static final double SENSIBILITY = 1.0;

    private final Cursor cursor = new Cursor();
    private double oldX;
    private double oldY;

    private int count;

    @Service private Transformable transformable;
    @Service private ShipUpdater updater;

    @Service private Context context;
    @Service private Mouse mouse;
    @Service private Camera camera;

    /**
     * Create a PC ship controller.
     */
    public ShipControllerPc()
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
        if (count > 1)
        {
            transformable.moveLocation(extrp,
                                       cursor.getX() - oldX,
                                       cursor.getY() - oldY + updater.getHitForce().getDirectionVertical());
        }
        else
        {
            count++;
        }
        final double width = transformable.getWidth() / 2.0;
        final double maxX = camera.getWidth() + camera.getWidth() / 2 - width * 2;
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
    public void prepare(FeatureProvider provider, Services services)
    {
        super.prepare(provider, services);

        mouse.setCenter(context.getX()
                        + context.getConfig().getOutput().getWidth() / 2,
                        context.getY() + context.getConfig().getOutput().getHeight() / 2);

        cursor.setInputDevice(mouse);
        cursor.setSyncMode(false);
        cursor.setViewer(camera);
        cursor.setSensibility(SENSIBILITY, SENSIBILITY);
    }

    @Override
    public void update(double extrp)
    {
        oldX = cursor.getX();
        oldY = cursor.getY();
        cursor.update(extrp);
        // mouse.lock();
        updatePosition(extrp);
        if (mouse.getClick() > 0)
        {
            updater.fire();
        }
    }
}
