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
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Layerable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.launchable.LauncherListener;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionengine.graphic.engine.Sequencer;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.tyrian.Constant;
import com.b3dgs.tyrian.Sfx;
import com.b3dgs.tyrian.bonus.action.Action;
import com.b3dgs.tyrian.entity.Explode.PostAction;

/**
 * Ship model implementation.
 */
@FeatureInterface
public final class ShipModel extends FeatureModel implements Routine, CollidableListener
{
    /** Default energy. */
    static final int ENERGY = 8;
    /** Speed. */
    static final Direction SPEED = new Force(0.0, 1.0);
    private static final int OFFSET_Y = 8;
    private static final long HIT_TIME = 25L;
    /** Shield increment delay in milliseconds. */
    private static final long SHIELD_INC_DELAY = 1000L;
    /** Turning max. */
    private static final double TURNING_HIGH = 1.5;
    /** Turning max. */
    private static final double TURNING_LOW = 0.5;
    /** Turning high left. */
    private static final int TURNING_HIGH_LEFT = 1;
    /** Turning high left. */
    private static final int TURNING_LOW_LEFT = 2;
    /** Not turning. */
    private static final int TURNING_NOT = 3;
    /** Turning high left. */
    private static final int TURNING_LOW_RIGHT = 4;
    /** Turning high left. */
    private static final int TURNING_HIGH_RIGHT = 5;

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final Sequencer sequence = services.get(Sequencer.class);
    private final Factory factory = services.get(Factory.class);
    private final Handler handler = services.get(Handler.class);
    private final Camera camera = services.get(Camera.class);

    private final Transformable transformable;
    private final Animatable animatable;

    private final Alterable shield = new Alterable(15);
    private final Alterable armor = new Alterable(10);
    private final Alterable energy = new Alterable(200);
    private final Force hitForce = new Force(0.0, 0.0, 1, 0.1);
    private final Tick hitTick = new Tick();
    private final Tick shieldIncTick = new Tick();
    private final SpriteAnimated hit;

    private WeaponModel front;
    private WeaponModel rear;

    /**
     * Create ship.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param layerable The layerable feature.
     * @param transformable The transformable feature.
     * @param collidable The collidable feature.
     * @param animatable The animatable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public ShipModel(Services services,
                     Setup setup,
                     Layerable layerable,
                     Transformable transformable,
                     Collidable collidable,
                     Animatable animatable)
    {
        super(services, setup);

        this.transformable = transformable;
        this.animatable = animatable;

        hit = Drawable.loadSpriteAnimated(Medias.create(Constant.FOLDER_EFFECT, "Hit.png"), 1, 1);
        hit.load();
        hit.prepare();
        hit.setOrigin(Origin.CENTER_BOTTOM);
        hit.setFrameOffsets(1, OFFSET_Y);

        shield.fill();
        armor.fill();
        energy.fill();

        front = createWeapon(Medias.create(Constant.FOLDER_WEAPON, Constant.FOLDER_FRONT, "pulse_cannon.xml"));
        rear = createWeapon(Medias.create(Constant.FOLDER_WEAPON, Constant.FOLDER_REAR, "sonic_wave.xml"));

        shieldIncTick.start();

        collidable.setCollisionVisibility(false);

        final double startX = (camera.getWidth() + Constant.MARGIN_H + transformable.getWidth()) / 2;
        final double startY = camera.getY() + camera.getHeight() / 4 - transformable.getHeight();
        transformable.teleport(startX, startY);
    }

    /**
     * Show hit effect.
     * 
     * @param localizable The localizable reference.
     * @return <code>true</code> if hit effective, <code>false</code> else.
     */
    public boolean showHit(Localizable localizable)
    {
        if (!hitTick.isStarted() || hitTick.elapsedTime(source.getRate(), HIT_TIME * 2))
        {
            hit.setLocation(camera, localizable);
            if (!shield.isEmpty())
            {
                hitTick.restart();
            }
            return true;
        }
        return false;
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
            front.getFeature(Identifiable.class).destroy();
            front = weapon.take(true);
            ignoreProjectileCollision(front.getFeature(Launcher.class));
        }
        else
        {
            rear.getFeature(Identifiable.class).destroy();
            rear = weapon.take(true);
            ignoreProjectileCollision(rear.getFeature(Launcher.class));
        }
    }

    /**
     * Perform a power up for front or rear weapon (random).
     */
    public void powerUp()
    {
        final WeaponModel weapon;
        if (UtilRandom.getRandomBoolean() && front.getLevel() < Constant.WEAPON_LEVEL_MAX
            || rear.getLevel() == Constant.WEAPON_LEVEL_MAX)
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
     * Perform a fire.
     */
    public void fire()
    {
        if (energy.isEnough(ShipModel.ENERGY * 2))
        {
            front.fire(transformable, SPEED);
            rear.fire(transformable, SPEED);
        }
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
     * Get the shield.
     * 
     * @return The shield.
     */
    public Alterable getShield()
    {
        return shield;
    }

    /**
     * Get the armor.
     * 
     * @return The armor.
     */
    public Alterable getArmor()
    {
        return armor;
    }

    /**
     * Get the energy fire.
     * 
     * @return The energy fire.
     */
    public Alterable getEnergy()
    {
        return energy;
    }

    /**
     * Get the hit force.
     * 
     * @return The hit force.
     */
    public Force getHitForce()
    {
        return hitForce;
    }

    /**
     * Get the weapon front.
     * 
     * @return The front weapon.
     */
    public WeaponModel getFront()
    {
        return front;
    }

    /**
     * Get the weapon rear.
     * 
     * @return The rear weapon.
     */
    public WeaponModel getRear()
    {
        return rear;
    }

    /**
     * Ignore weapon projectiles collision.
     * 
     * @param launcher The launcher to ignore.
     */
    private void ignoreProjectileCollision(Launcher launcher)
    {
        launcher.addListener((LauncherListener) () -> energy.decrease(ENERGY));
        launcher.addListener(launchable -> launchable.getFeature(Collidable.class)
                                                     .setGroup(Constant.COLLISION_GROUP_PROJECTILES_SHIP));
    }

    /**
     * Create a weapon from its media.
     * 
     * @param media The weapon media.
     * @return The created weapon.
     */
    private WeaponModel createWeapon(Media media)
    {
        final Featurable weapon = factory.create(media);
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
        hitForce.setDirection(0.0, dir * 2 - ShipModel.SPEED.getDirectionVertical() * 2);
        if (showHit(transformable))
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
                final PostAction action = () -> sequence.end();
                explode.start(new Rectangle(transformable.getX() - transformable.getWidth() / 2,
                                            transformable.getY() - transformable.getHeight() / 2,
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

    /**
     * Get the associated frame depending of the force.
     * 
     * @return The associated tile.
     */
    private int getFrame()
    {
        final double force = transformable.getX() - transformable.getOldX();
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

    @Override
    public void update(double extrp)
    {
        hitForce.update(extrp);

        camera.setLocation(transformable.getX() / (camera.getWidth() / Constant.MARGIN_H),
                           camera.getY() + ShipModel.SPEED.getDirectionVertical() * extrp);

        front.update(extrp);
        rear.update(extrp);
        animatable.setFrame(getFrame());
        energy.increase(extrp, 1);
        shieldIncTick.update(extrp);
        hitTick.update(extrp);

        if (!shield.isFull() && shieldIncTick.elapsedTime(source.getRate(), SHIELD_INC_DELAY))
        {
            shieldIncTick.restart();
            shield.increase(1);
            energy.decrease(ShipModel.ENERGY * 2);
        }
    }

    @Override
    public void render(Graphic g)
    {
        if (hitTick.isStarted() && !hitTick.elapsedTime(source.getRate(), HIT_TIME))
        {
            hit.render(g);
        }
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        if (collidable.hasFeature(EntityModel.class))
        {
            final double dir = collidable.getFeature(EntityModel.class).getDirection().getDirectionVertical();
            if (dir < 0)
            {
                onHit(dir);
            }
        }
        if (collidable.hasFeature(ProjectileModel.class))
        {
            if (showHit(transformable))
            {
                onHurt(3);
            }
            collidable.getFeature(Identifiable.class).destroy();
        }
        if (collidable.hasFeature(Action.class))
        {
            collidable.getFeature(Action.class).action(this);
        }
    }
}
