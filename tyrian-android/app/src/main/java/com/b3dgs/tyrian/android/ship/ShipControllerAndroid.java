/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
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
package com.b3dgs.tyrian.android.ship;

import com.b3dgs.lionengine.android.Mouse;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.tyrian.ship.ShipController;
import com.b3dgs.tyrian.ship.ShipModel;

/**
 * Ship control implementation.
 */
@FeatureInterface
public final class ShipControllerAndroid extends FeatureModel implements ShipController
{
    private static final double SPEED_DIVISOR = 10.0;

    private final Force force = new Force();

    private final Mouse mouse = services.get(Mouse.class);
    private final Camera camera = services.get(Camera.class);

    @FeatureGet private Transformable transformable;
    @FeatureGet private ShipModel model;

    /**
     * Create an Android ship controller.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public ShipControllerAndroid(Services services, Setup setup)
    {
        super(services, setup);
    }

    /**
     * Update the transformable position.
     * 
     * @param extrp The extrapolation value.
     */
    private void updatePosition(double extrp)
    {
        transformable.moveLocation(extrp, force, model.getHitForce());

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

            force.setDirection(f.getDirectionHorizontal() * f.getVelocity() / SPEED_DIVISOR,
                               f.getDirectionVertical() * f.getVelocity() / SPEED_DIVISOR);
        }
        else
        {
            force.setDirection(0.0, 1.0);
        }

        updatePosition(extrp);
        if (mouse.getClick() > 1)
        {
            model.fire();
        }
    }
}
