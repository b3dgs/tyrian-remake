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
package com.b3dgs.tyrian.entity;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.ForceConfig;
import com.b3dgs.lionengine.game.camera.Camera;
import com.b3dgs.lionengine.game.collision.object.Collidable;
import com.b3dgs.lionengine.game.collision.object.CollidableListener;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Service;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.identifiable.Identifiable;
import com.b3dgs.lionengine.game.feature.layerable.Layerable;
import com.b3dgs.lionengine.game.feature.refreshable.Refreshable;
import com.b3dgs.lionengine.game.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.handler.Handler;
import com.b3dgs.lionengine.stream.XmlNode;
import com.b3dgs.tyrian.Constant;
import com.b3dgs.tyrian.Sfx;
import com.b3dgs.tyrian.effect.Effect;
import com.b3dgs.tyrian.effect.Explode;
import com.b3dgs.tyrian.projectile.ProjectileModel;

/**
 * Entity updater implementation.
 */
public class EntityUpdater extends FeatureModel implements Refreshable, CollidableListener
{
    /**
     * Get the entity layer.
     * 
     * @param setup The setup reference.
     * @return The entity layer.
     */
    private static int getLayer(Setup setup)
    {
        final XmlNode root = setup.getRoot();
        if (root.hasChild(ForceConfig.NODE_FORCE))
        {
            return Constant.LAYER_ENTITIES_MOVING;
        }
        return Constant.LAYER_ENTITIES_STATIC;
    }

    private final int layer;
    private Alterable life;
    private Direction direction;
    private SpriteAnimated surface;
    private Media media;

    @Service private Factory factory;
    @Service private Handler handler;
    @Service private Camera camera;

    @Service private Layerable layerable;
    @Service private Transformable transformable;
    @Service private Collidable collidable;
    @Service private EntityModel model;

    /**
     * Create an entity updater.
     * 
     * @param setup The setup reference.
     */
    EntityUpdater(Setup setup)
    {
        super();

        layer = getLayer(setup);
    }

    /**
     * Spawn bullet hit effect.
     */
    private void spawnEffectHit()
    {
        final Effect hit = factory.create(Medias.create(Constant.FOLDER_EFFECT, "bullet_hit.xml"));
        hit.start(transformable);
        handler.add(hit);
    }

    /**
     * Spawn explode effect.
     */
    private void spawnEffectExplode()
    {
        final Explode explode = factory.create(media);
        explode.start(transformable);
        handler.add(explode);
    }

    @Override
    public void prepare(FeatureProvider provider, Services services)
    {
        super.prepare(provider, services);

        layerable.setLayer(layer);
        collidable.setOrigin(Origin.MIDDLE);
        life = model.getLife();
        direction = model.getDirection();
        surface = model.getSurface();
        media = model.getExplode();
    }

    @Override
    public void update(double extrp)
    {
        transformable.moveLocation(extrp, direction);
        collidable.update(extrp);
        surface.setLocation(camera, transformable);
        surface.update(extrp);

        if (transformable.getY() < camera.getY() - transformable.getHeight())
        {
            getFeature(Identifiable.class).destroy();
        }
    }

    @Override
    public void notifyCollided(Collidable collidable)
    {
        if (collidable.hasFeature(ProjectileModel.class))
        {
            Sfx.BULLET_HIT.play();
            spawnEffectHit();

            life.decrease(1);
            if (life.getCurrent() == 0)
            {
                spawnEffectExplode();
                getFeature(Identifiable.class).destroy();
            }

            collidable.getFeature(Identifiable.class).destroy();
        }
    }
}
