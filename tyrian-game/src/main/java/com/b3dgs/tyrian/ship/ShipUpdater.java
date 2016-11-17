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
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.core.InputDevicePointer;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Sequencable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.Alterable;
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
import com.b3dgs.lionengine.game.feature.identifiable.Identifiable;
import com.b3dgs.lionengine.game.feature.launchable.Launchable;
import com.b3dgs.lionengine.game.feature.launchable.LaunchableListener;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.launchable.LauncherListener;
import com.b3dgs.lionengine.game.feature.layerable.Layerable;
import com.b3dgs.lionengine.game.feature.refreshable.Refreshable;
import com.b3dgs.lionengine.game.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.handler.Handler;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.util.UtilRandom;
import com.b3dgs.tyrian.Constant;
import com.b3dgs.tyrian.Sfx;
import com.b3dgs.tyrian.bonus.action.Action;
import com.b3dgs.tyrian.effect.Explode;
import com.b3dgs.tyrian.effect.Explode.PostAction;
import com.b3dgs.tyrian.entity.EntityModel;
import com.b3dgs.tyrian.weapon.Weapon;
import com.b3dgs.tyrian.weapon.WeaponModel;
import com.b3dgs.tyrian.weapon.WeaponUpdater;

/**
 * Ship updater implementation.
 */
public final class ShipUpdater extends FeatureModel implements Refreshable, CollidableListener
{
    /** Shield increment delay in milliseconds. */
    private static final long SHIELD_INC_DELAY = 1000L;
    /** Speed. */
    private static final Direction SPEED = new Force(0.0, 1.0);
    /** Default energy. */
    private static final int ENERGY = 13;
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
    private final Timing shieldIncTiming = new Timing();
    private Alterable shield;
    private Alterable armor;
    private Alterable energy;
    private SpriteTiled surface;
    private WeaponUpdater front;
    private WeaponUpdater rear;

    @Service private Layerable layerable;
    @Service private Transformable transformable;
    @Service private Collidable collidable;
    @Service private ShipModel model;
    @Service private ShipController controller;

    @Service private Sequencable sequence;
    @Service private InputDevicePointer pointer;
    @Service private Factory factory;
    @Service private Handler handler;
    @Service private Camera camera;

    /**
     * Create a ship renderer.
     */
    ShipUpdater()
    {
        super();

        shieldIncTiming.start();
    }

    /**
     * Perform a fire.
     */
    public void fire()
    {
        if (energy.isEnough(ENERGY * 2))
        {
            front.fire(transformable, SPEED);
            rear.fire(transformable, SPEED);
        }
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

    /**
     * Ignore weapon projectiles collision.
     * 
     * @param launcher The launcher to ignore.
     */
    private void ignoreProjectileCollision(Launcher launcher)
    {
        launcher.addListener(new LauncherListener()
        {
            @Override
            public void notifyFired()
            {
                energy.decrease(ENERGY);
            }
        });
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
     * Called on hit.
     * 
     * @param dir The hit direction force value.
     */
    private void onHit(double dir)
    {
        force.setDirection(0.0, dir * 2 - SPEED.getDirectionVertical() * 2);
        if (getFeature(ShipRenderer.class).showHit(transformable))
        {
            onHurt();
        }
    }

    /**
     * Update hurt case.
     */
    private void onHurt()
    {
        if (shield.decrease(1) == 0)
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
                explode.start(Geom.createRectangle(transformable.getX() - 10, transformable.getY() + 10, 50, 50),
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
    public void prepare(FeatureProvider provider, Services services)
    {
        super.prepare(provider, services);

        shield = model.getShield();
        armor = model.getArmor();
        energy = model.getEnergy();
        surface = model.getSurface();
        layerable.setLayer(Constant.LAYER_SHIP_UPDATE, Constant.LAYER_SHIP);
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

        energy.increase((int) Math.ceil(1 * extrp));
        if (!shield.isFull() && shieldIncTiming.elapsed(SHIELD_INC_DELAY))
        {
            shieldIncTiming.restart();
            shield.increase(1);
            energy.decrease(ENERGY * 2);
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
        if (collidable.hasFeature(Action.class))
        {
            collidable.getFeature(Action.class).action(this);
        }
    }
}
