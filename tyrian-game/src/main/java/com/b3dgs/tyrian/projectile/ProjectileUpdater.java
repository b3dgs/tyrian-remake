/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.Service;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Refreshable;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.launchable.Launchable;
import com.b3dgs.lionengine.game.feature.launchable.LaunchableListener;
import com.b3dgs.lionengine.graphic.SpriteAnimated;
import com.b3dgs.tyrian.effect.Effect;

/**
 * Projectile updater implementation.
 */
final class ProjectileUpdater extends FeatureModel implements Refreshable
{
    private final Tick tick = new Tick();
    private final Force force = new Force();
    private final SpriteAnimated surface;
    private final long effectRate;
    private final Media effectMedia;
    private final Direction acceleration;

    @Service private Transformable transformable;
    @Service private Launchable launchable;
    @Service private Collidable collidable;

    @Service private Context context;
    @Service private Factory factory;
    @Service private Handler handler;
    @Service private Viewer viewer;

    /**
     * Create a projectile updater.
     * 
     * @param model The model reference.
     */
    ProjectileUpdater(ProjectileModel model)
    {
        super();

        surface = model.getSurface();
        effectRate = model.getEffectRate();
        effectMedia = model.getEffectMedia();
        acceleration = model.getAcceleration();
    }

    @Override
    public void prepare(FeatureProvider provider, Services services)
    {
        super.prepare(provider, services);

        collidable.setOrigin(Origin.MIDDLE);
        launchable.addListener(new LaunchableListener()
        {
            @Override
            public void notifyFired(Launchable launchable)
            {
                if (effectMedia != null)
                {
                    startEffect(transformable);
                }
            }
        });
    }

    @Override
    public void update(double extrp)
    {
        transformable.moveLocation(extrp, force);
        force.addDirection(extrp, acceleration);

        launchable.update(extrp);

        surface.setLocation(viewer, transformable);

        tick.update(extrp);
        if (tick.elapsedTime(context, effectRate))
        {
            startEffect(transformable);
        }
        if (!viewer.isViewable(transformable, 0, 0))
        {
            getFeature(Identifiable.class).destroy();
        }
    }

    /**
     * Start effect.
     * 
     * @param localizable The localizable reference.
     */
    private void startEffect(Localizable localizable)
    {
        final Effect effect = factory.create(effectMedia);
        handler.add(effect);
        effect.start(localizable);
        tick.restart();
    }
}
