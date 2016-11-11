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
package com.b3dgs.tyrian.ship;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.core.InputDevicePointer;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.camera.Camera;
import com.b3dgs.lionengine.game.collision.object.Collidable;
import com.b3dgs.lionengine.game.collision.object.CollidableListener;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Service;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.launchable.Launchable;
import com.b3dgs.lionengine.game.feature.launchable.LaunchableListener;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.refreshable.Refreshable;
import com.b3dgs.lionengine.game.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.handler.Handler;
import com.b3dgs.lionengine.util.UtilRandom;
import com.b3dgs.tyrian.Constant;
import com.b3dgs.tyrian.bonus.action.Action;
import com.b3dgs.tyrian.entity.EntityModel;
import com.b3dgs.tyrian.weapon.Weapon;
import com.b3dgs.tyrian.weapon.WeaponModel;
import com.b3dgs.tyrian.weapon.WeaponUpdater;

/**
 * Ship updater implementation.
 */
public final class ShipUpdater extends FeatureModel implements Refreshable, CollidableListener
{
    /** Speed. */
    private static final Direction SPEED = new Force(0.0, 1.0);
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

    private final Force force = new Force(0.0, 0.0, 1, 0.1);
    private final SpriteTiled surface;
    private WeaponUpdater front;
    private WeaponUpdater rear;

    @Service private Transformable transformable;
    @Service private Collidable collidable;
    @Service private ShipController controller;

    @Service private InputDevicePointer pointer;
    @Service private Factory factory;
    @Service private Handler handler;
    @Service private Camera camera;

    /**
     * Create a ship renderer.
     * 
     * @param model The model reference.
     */
    ShipUpdater(ShipModel model)
    {
        super();

        surface = model.getSurface();
    }

    /**
     * Perform a fire.
     */
    public void fire()
    {
        front.fire(transformable, SPEED);
        rear.fire(transformable, SPEED);
    }

    /**
     * Ignore weapon projectiles collision.
     * 
     * @param launcher The launcher to ignore.
     */
    private void ignoreProjectileCollision(Launcher launcher)
    {
        launcher.addListener(new LaunchableListener()
        {
            @Override
            public void notifyFired(Launchable launchable)
            {
                collidable.addIgnore(launchable.getFeature(Collidable.class));
            }
        });
    }

    /**
     * Create a weapon from its media.
     * 
     * @param media The weapon media.
     * @return The created weapon.
     */
    private WeaponUpdater createWeapon(Media media)
    {
        final Weapon weapon = factory.create(media);
        handler.add(weapon);
        ignoreProjectileCollision(weapon.getFeature(Launcher.class));
        return weapon.getFeature(WeaponModel.class).take(false);
    }

    /**
     * Take weapon.
     * 
     * @param weapon The weapon to take.
     */
    public void takeWeapon(WeaponModel weapon)
    {
        if (weapon.isFront())
        {
            front = weapon.take(true);
            ignoreProjectileCollision(front.getFeature(Launcher.class));
        }
        else
        {
            rear = weapon.take(true);
            ignoreProjectileCollision(rear.getFeature(Launcher.class));
        }
    }

    /**
     * Perform a power up for front or rear weapon (random).
     */
    public void powerUp()
    {
        final WeaponUpdater weapon;
        if (UtilRandom.getRandomBoolean())
        {
            weapon = front;
        }
        else
        {
            weapon = rear;
        }
        weapon.increaseLevel();
    }

    /**
     * Get the hit force.
     * 
     * @return The hit force.
     */
    public Force getHitForce()
    {
        return force;
    }

    @Override
    public void prepare(FeatureProvider provider, Services services)
    {
        super.prepare(provider, services);

        collidable.setOrigin(Origin.MIDDLE);
        front = createWeapon(Weapon.PULSE_CANNON);
        rear = createWeapon(Weapon.SONIC_WAVE);
    }

    @Override
    public void update(double extrp)
    {
        controller.update(extrp);
        collidable.update(extrp);

        force.update(extrp);
        camera.setLocation(transformable.getX()
                           / Constant.MARGIN_H,
                           camera.getY() + SPEED.getDirectionVertical() * extrp);

        surface.setTile(getTile(transformable.getX() - transformable.getOldX()));
        surface.setLocation(camera, transformable);
    }

    @Override
    public void notifyCollided(Collidable collidable)
    {
        if (collidable.hasFeature(EntityModel.class))
        {
            final double dir = collidable.getFeature(EntityModel.class).getDirection().getDirectionVertical();
            if (dir < 0)
            {
                force.setDirection(0.0, dir * 2 - SPEED.getDirectionVertical() * 2);
                getFeature(ShipRenderer.class).showHit(transformable);
            }
        }
        if (collidable.hasFeature(Action.class))
        {
            collidable.getFeature(Action.class).action(this);
        }
    }
}
