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
package com.b3dgs.tyrian.entity;

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Service;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.Setup;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Refreshable;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.launchable.Launchable;
import com.b3dgs.lionengine.game.feature.launchable.LaunchableListener;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.tyrian.Constant;
import com.b3dgs.tyrian.ship.ShipUpdater;
import com.b3dgs.tyrian.weapon.WeaponUpdater;

/**
 * Shooter implementation.
 */
public class Shooter extends FeatureModel implements Refreshable
{
    private WeaponUpdater weapon;
    private Localizable target;

    @Service private Transformable transformable;

    @Service private Factory factory;
    @Service private ShipUpdater ship;

    /**
     * Create a shooter.
     * 
     * @param setup The setup reference.
     */
    Shooter(Setup setup)
    {
        super();
    }

    @Override
    public void prepare(FeatureProvider provider, Services services)
    {
        super.prepare(provider, services);

        weapon = factory.create(Medias.create(Constant.FOLDER_WEAPON, "pulser.xml")).getFeature(WeaponUpdater.class);

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

        weapon.getFeature(Launcher.class).addListener(new LaunchableListener()
        {
            @Override
            public void notifyFired(Launchable launchable)
            {
                launchable.getFeature(Collidable.class).setGroup(Constant.COLLISION_GROUP_PROJECTILES_ENTITIES);
            }
        });
    }

    @Override
    public void update(double extrp)
    {
        weapon.fire(transformable, target);
    }
}
