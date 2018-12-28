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
package com.b3dgs.tyrian.projectile;

import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.Recycler;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.collidable.CollidableModel;
import com.b3dgs.lionengine.game.feature.launchable.LaunchableModel;
import com.b3dgs.tyrian.Constant;

/**
 * Projectile base implementation.
 */
// CHECKSTYLE IGNORE LINE: DataAbstractionCoupling
public class Projectile extends FeaturableModel
{
    /**
     * Create a projectile.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Projectile(Services services, Setup setup)
    {
        super(services, setup);

        addFeature(new Recycler());
        addFeature(new LayerableModel(Constant.LAYER_PROJECTILES));
        addFeature(new TransformableModel(setup));
        addFeature(new LaunchableModel());
        addFeature(new CollidableModel(services, setup));

        final ProjectileModel model = addFeatureAndGet(new ProjectileModel(setup));
        addFeature(new ProjectileUpdater(services, model));
        addFeature(new ProjectileRenderer(model));
    }
}
