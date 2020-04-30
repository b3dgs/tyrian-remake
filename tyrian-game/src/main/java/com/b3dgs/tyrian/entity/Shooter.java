/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.tyrian.Constant;
import com.b3dgs.tyrian.ship.ShipModel;
import com.b3dgs.tyrian.weapon.WeaponModel;

/**
 * Shooter implementation.
 */
@FeatureInterface
public class Shooter extends FeatureModel implements Routine
{
    private final Factory factory = services.get(Factory.class);
    private final ShipModel ship = services.get(ShipModel.class);

    private final WeaponModel weapon;
    private Localizable target;

    @FeatureGet private Transformable transformable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Shooter(Services services, Setup setup)
    {
        super(services, setup);

        weapon = factory.create(Medias.create(Constant.FOLDER_WEAPON, "pulser.xml")).getFeature(WeaponModel.class);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        final Transformable transformable = ship.getFeature(Transformable.class);
        target = new Localizable()
        {
            @Override
            public double getX()
            {
                return transformable.getX() + ship.getSpeed().getDirectionHorizontal();
            }

            @Override
            public double getY()
            {
                return transformable.getY() + ship.getSpeed().getDirectionVertical();
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
