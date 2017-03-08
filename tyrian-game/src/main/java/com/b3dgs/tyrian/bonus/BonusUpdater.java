/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.tyrian.bonus;

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Service;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Refreshable;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.graphic.SpriteAnimated;
import com.b3dgs.tyrian.Constant;

/**
 * Bonus updater implementation.
 */
public class BonusUpdater extends FeatureModel implements Refreshable
{
    private static final double FALLING_SPEED = -1.0;

    private final SpriteAnimated surface;

    @Service private Factory factory;
    @Service private Handler handler;
    @Service private Camera camera;
    @Service private Transformable transformable;
    @Service private Collidable collidable;

    /**
     * Create a bonus updater.
     * 
     * @param model The model reference.
     */
    BonusUpdater(BonusModel model)
    {
        super();

        surface = model.getSurface();
    }

    @Override
    public void prepare(FeatureProvider provider, Services services)
    {
        super.prepare(provider, services);

        collidable.setOrigin(Origin.MIDDLE);
        collidable.setGroup(Constant.COLLISION_GROUP_BONUS);
    }

    @Override
    public void update(double extrp)
    {
        transformable.moveLocation(extrp, 0.0, FALLING_SPEED);
        surface.setLocation(camera, transformable);
        surface.update(extrp);

        if (transformable.getY() < -transformable.getHeight())
        {
            getFeature(Identifiable.class).destroy();
        }
    }
}
