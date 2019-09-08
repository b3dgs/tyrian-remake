/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.tyrian.ship;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.collidable.CollidableModel;

/**
 * Ship base implementation.
 */
public class Ship extends FeaturableModel
{
    /**
     * Create ship.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Ship(Services services, Setup setup)
    {
        super(services, setup);

        addFeature(new TransformableModel(services, setup));
        addFeature(new CollidableModel(services, setup));
        addFeature(new LayerableModel(services, setup));
        addFeature(new ShipModel(services, setup));
    }

    @Override
    public void addAfter(Services services, Setup setup)
    {
        addFeature(new ShipUpdater(services, setup));
        addFeature(new ShipRenderer(services, setup));
    }
}
