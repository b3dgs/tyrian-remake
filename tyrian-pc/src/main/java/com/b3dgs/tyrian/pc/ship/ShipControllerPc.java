/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.tyrian.pc.ship;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.awt.Mouse;
import com.b3dgs.lionengine.game.Cursor;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.RoutineUpdate;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.tyrian.entity.ShipModel;

/**
 * Ship control implementation.
 */
@FeatureInterface
public final class ShipControllerPc extends FeatureModel implements RoutineUpdate
{
    private static final double SENSIBILITY = 1.0;

    private final Context context = services.get(Context.class);
    private final Mouse mouse = context.getInputDevice(Mouse.class);
    private final Camera camera = services.get(Camera.class);

    private final Transformable transformable;
    private final ShipModel model;

    private final Cursor cursor = new Cursor(services);

    private double oldX;
    private double oldY;
    private int count;

    /**
     * Create a PC ship controller.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     * @param transformable The transformable feature.
     * @param model The model feature.
     */
    public ShipControllerPc(Services services, Setup setup, Transformable transformable, ShipModel model)
    {
        super(services, setup);

        this.transformable = transformable;
        this.model = model;

        mouse.setCenter(context.getX() + context.getConfig().getOutput().getWidth() / 2,
                        context.getY() + context.getConfig().getOutput().getHeight() / 2);

        cursor.setSync(mouse);
        cursor.setViewer(camera);
        cursor.setSensibility(SENSIBILITY, SENSIBILITY);
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
                                       cursor.getY() - oldY + model.getHitForce().getDirectionVertical());
        }
        else
        {
            count++;
        }
        final double width = transformable.getWidth() / 2.0;
        final double maxX = camera.getWidth() + camera.getWidth() / 2.0 - width * 2;
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
        oldX = cursor.getX();
        oldY = cursor.getY();
        cursor.update(extrp);
        // mouse.lock();
        updatePosition(extrp);

        if (mouse.isPushed())
        {
            model.fire();
        }
    }
}
