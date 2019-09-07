/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.tyrian.entity;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.Routines;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.collidable.CollidableModel;

/**
 * Entity base implementation.
 */
public class Entity extends FeaturableModel
{
    /**
     * Create entity.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Entity(Services services, Setup setup)
    {
        super(services, setup);

        addFeature(new TransformableModel(services, setup));
        addFeatureAndGet(new CollidableModel(services, setup)).setOrigin(Origin.MIDDLE);
        addFeature(new LayerableModel(services, setup));
        addFeature(new EntityModel(services, setup));
    }

    @Override
    public void addAfter(Services services, Setup setup)
    {
        addFeature(new Routines(services, setup));
        addFeature(new EntityUpdater(services, setup));
        addFeature(new EntityRenderer(services, setup));
    }
}
