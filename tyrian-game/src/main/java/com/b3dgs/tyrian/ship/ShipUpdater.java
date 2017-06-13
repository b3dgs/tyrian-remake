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
package com.b3dgs.tyrian.ship;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.sequence.Sequencer;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.Camera;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.FeatureGet;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.Setup;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Layerable;
import com.b3dgs.lionengine.game.feature.Refreshable;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.launchable.Launchable;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.SpriteTiled;
import com.b3dgs.tyrian.Constant;
import com.b3dgs.tyrian.Sfx;
import com.b3dgs.tyrian.bonus.action.Action;
import com.b3dgs.tyrian.effect.Explode;
import com.b3dgs.tyrian.effect.Explode.PostAction;
import com.b3dgs.tyrian.entity.EntityModel;

/**
 * Ship updater implementation.
 */
public final class ShipUpdater extends FeatureModel implements Refreshable, CollidableListener
{
    /** Shield increment delay in milliseconds. */
    private static final long SHIELD_INC_DELAY = 1000L;
    /** Turning max. */
    private static final double TURNING_HIGH = 1.5;
    /** Turning max. */
    private static final double TURNING_LOW = 0.5;
    /** Turning high left. */
    private static final int TURNING_HIGH_LEFT = 0;
    /** Turning high left. */
    private static final int TURNING_LOW_LEFT = 1;
    /** Not turning. */
    private static final int TURNING_NOT = 2;
    /** Turning high left. */
    private static final int TURNING_LOW_RIGHT = 3;
    /** Turning high left. */
    private static final int TURNING_HIGH_RIGHT = 4;

    /**
     * Get the associated frame depending of the force.
     * 
     * @param force The force difference.
     * @return The associated tile.
     */
    private static int getTile(double force)
    {
        final int tile;
        if (force < -TURNING_HIGH)
        {
            tile = TURNING_HIGH_LEFT;
        }
        else if (force < -TURNING_LOW)
        {
            tile = TURNING_LOW_LEFT;
        }
        else if (force > TURNING_HIGH)
        {
            tile = TURNING_HIGH_RIGHT;
        }
        else if (force > TURNING_LOW)
        {
            tile = TURNING_LOW_RIGHT;
        }
        else
        {
            tile = TURNING_NOT;
        }
        return tile;
    }

    private final Tick shieldIncTick = new Tick();
    private Alterable shield;
    private Alterable armor;
    private Alterable energy;
    private SpriteTiled surface;
    private Force hitForce;
    private Tick hitTick;

    private final Context context;
    private final Sequencer sequence;
    private final Factory factory;
    private final Handler handler;
    private final Camera camera;

    @FeatureGet private Layerable layerable;
    @FeatureGet private Transformable transformable;
    @FeatureGet private Collidable collidable;
    @FeatureGet private ShipModel model;
    @FeatureGet private ShipController controller;

    /**
     * Create a ship renderer.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    ShipUpdater(Services services, Setup setup)
    {
        super();

        context = services.get(Context.class);
        sequence = services.get(Sequencer.class);
        factory = services.get(Factory.class);
        handler = services.get(Handler.class);
        camera = services.get(Camera.class);

        shieldIncTick.start();
    }

    /**
     * Get the speed.
     * 
     * @return The speed.
     */
    public Direction getSpeed()
    {
        return ShipModel.SPEED;
    }

    /**
     * Called on hit.
     * 
     * @param dir The hit direction force value.
     */
    private void onHit(double dir)
    {
        hitForce.setDirection(0.0, dir * 2 - ShipModel.SPEED.getDirectionVertical() * 2);
        if (getFeature(ShipRenderer.class).showHit(transformable))
        {
            onHurt(1);
        }
    }

    /**
     * Update hurt case.
     * 
     * @param damages The damages value
     */
    private void onHurt(int damages)
    {
        if (shield.decrease(damages) == 0)
        {
            if (armor.decrease(1) == 0)
            {
                final Explode explode = factory.create(Medias.create(Constant.FOLDER_EFFECT, "explode_big.xml"));
                final PostAction action = new PostAction()
                {
                    @Override
                    public void execute()
                    {
                        sequence.end();
                    }
                };
                explode.start(new Rectangle(transformable.getX()
                                            - transformable.getWidth() / 2,
                                            transformable.getY() + -transformable.getHeight() / 2,
                                            50,
                                            50),
                              action);
                handler.add(explode);
                energy.set(0);
                getFeature(Identifiable.class).destroy();
            }
            Sfx.PLAYER_LIFE.play();
        }
        else
        {
            Sfx.PLAYER_SHIELD.play();
        }
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        shield = model.getShield();
        armor = model.getArmor();
        energy = model.getEnergy();
        surface = model.getSurface();
        hitForce = model.getHitForce();
        layerable.setLayer(Constant.LAYER_SHIP_UPDATE, Constant.LAYER_SHIP);
        collidable.setGroup(Constant.COLLISION_GROUP_SHIP);
        collidable.setOrigin(Origin.MIDDLE);
        collidable.addAccept(Constant.COLLISION_GROUP_BONUS);
        collidable.addAccept(Constant.COLLISION_GROUP_ENTITIES);
        collidable.addAccept(Constant.COLLISION_GROUP_PROJECTILES_ENTITIES);
        collidable.setCollisionVisibility(false);
        hitTick = model.getHitTick();

        final double startX = (camera.getWidth() + Constant.MARGIN_H + transformable.getWidth()) / 2;
        final double startY = camera.getY() + camera.getHeight() / 4 - transformable.getHeight();
        transformable.teleport(startX, startY);
    }

    @Override
    public void update(double extrp)
    {
        controller.update(extrp);

        hitForce.update(extrp);

        camera.setLocation(transformable.getX()
                           / (camera.getWidth() / Constant.MARGIN_H),
                           camera.getY() + ShipModel.SPEED.getDirectionVertical() * extrp);

        surface.setTile(getTile(transformable.getX() - transformable.getOldX()));
        surface.setLocation(camera, transformable);

        energy.increase(extrp, 1);
        shieldIncTick.update(extrp);
        hitTick.update(extrp);

        if (!shield.isFull() && shieldIncTick.elapsedTime(context, SHIELD_INC_DELAY))
        {
            shieldIncTick.restart();
            shield.increase(1);
            energy.decrease(ShipModel.ENERGY * 2);
        }
    }

    @Override
    public void notifyCollided(Collidable collidable)
    {
        if (collidable.hasFeature(EntityModel.class))
        {
            final double dir = collidable.getFeature(EntityModel.class).getDirection().getDirectionVertical();
            if (dir < 0)
            {
                onHit(dir);
            }
        }
        if (collidable.hasFeature(Launchable.class))
        {
            if (getFeature(ShipRenderer.class).showHit(transformable))
            {
                onHurt(3);
            }
            collidable.getFeature(Identifiable.class).destroy();
        }
        if (collidable.hasFeature(Action.class))
        {
            collidable.getFeature(Action.class).action(model);
        }
    }
}
