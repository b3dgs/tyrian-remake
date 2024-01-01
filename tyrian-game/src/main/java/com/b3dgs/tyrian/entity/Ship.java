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
package com.b3dgs.tyrian.entity;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;

/**
 * Ship entity feature, shooting player.
 */
@FeatureInterface
public final class Ship extends FeatureModel implements Routine
{
    private static final int RANDOM_Y = 64;

    private final Viewer viewer = services.get(Viewer.class);
    private final Transformable player = services.get(ShipModel.class).getFeature(Transformable.class);

    private final Transformable transformable;

    private final boolean follow = UtilRandom.getRandomBoolean();
    private final int y = UtilRandom.getRandomInteger(RANDOM_Y);

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param transformable The transformable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public Ship(Services services, Setup setup, Transformable transformable)
    {
        super(services, setup);

        this.transformable = transformable;
    }

    @Override
    public void update(double extrp)
    {
        final int dy = (int) (viewer.getY() + viewer.getHeight() - transformable.getHeight() - y);
        if (transformable.getY() <= dy)
        {
            if (follow)
            {
                transformable.moveLocation(extrp,
                                           Force.fromVector(transformable.getX(),
                                                            transformable.getY(),
                                                            player.getX(),
                                                            player.getY()));
            }
            transformable.teleportY(dy);
        }
    }
}
