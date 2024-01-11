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
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.RoutineUpdate;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.tyrian.Constant;

/**
 * Shooter implementation.
 */
@FeatureInterface
public final class Shooter extends FeatureModel implements RoutineUpdate
{
    private final Factory factory = services.get(Factory.class);
    private final ShipModel ship = services.get(ShipModel.class);

    private final Transformable transformable;

    private final WeaponModel weapon = factory.create(Medias.create(Constant.FOLDER_WEAPON, "pulser.xml"))
                                              .getFeature(WeaponModel.class);
    private Localizable target;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param transformable The transformable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public Shooter(Services services, Setup setup, Transformable transformable)
    {
        super(services, setup);

        this.transformable = transformable;

        final Transformable other = ship.getFeature(Transformable.class);
        target = new Localizable()
        {
            @Override
            public double getX()
            {
                return other.getX() + ship.getSpeed().getDirectionHorizontal();
            }

            @Override
            public double getY()
            {
                return other.getY() + ship.getSpeed().getDirectionVertical();
            }
        };

        weapon.getFeature(Launcher.class)
              .addListener(launchable -> launchable.getFeature(Collidable.class)
                                                   .setGroup(Constant.COLLISION_GROUP_PROJECTILES_ENTITIES));
    }

    @Override
    public void update(double extrp)
    {
        weapon.fire(transformable, target);
        weapon.update(extrp);
    }
}
