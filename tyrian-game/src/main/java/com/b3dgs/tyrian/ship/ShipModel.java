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

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.SizeConfig;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.launchable.Launcher;
import com.b3dgs.lionengine.game.feature.launchable.LauncherListener;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.Sprite;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;
import com.b3dgs.tyrian.Constant;
import com.b3dgs.tyrian.weapon.Weapon;
import com.b3dgs.tyrian.weapon.WeaponModel;
import com.b3dgs.tyrian.weapon.WeaponUpdater;

/**
 * Ship model implementation.
 */
public final class ShipModel extends FeatureModel
{
    /** Default energy. */
    static final int ENERGY = 8;
    /** Speed. */
    static final Direction SPEED = new Force(0.0, 1.0);
    private static final int OFFSET_Y = 8;

    private final Alterable shield = new Alterable(15);
    private final Alterable armor = new Alterable(10);
    private final Alterable energy = new Alterable(200);
    private final Force hitForce = new Force(0.0, 0.0, 1, 0.1);
    private final Tick hitTick = new Tick();
    private final SpriteTiled surface;
    private final SpriteAnimated hit;
    private final Factory factory;
    private final Handler handler;

    private WeaponUpdater front;
    private WeaponUpdater rear;

    @FeatureGet private Transformable transformable;

    /**
     * Create a ship.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    ShipModel(Services services, Setup setup)
    {
        super();

        factory = services.get(Factory.class);
        handler = services.get(Handler.class);

        final SizeConfig config = SizeConfig.imports(setup);
        surface = Drawable.loadSpriteTiled(setup.getSurface(), config.getWidth(), config.getHeight());
        surface.setOrigin(Origin.MIDDLE);

        hit = Drawable.loadSpriteAnimated(Medias.create(Constant.FOLDER_EFFECT, "Hit.png"), 1, 1);
        hit.load();
        hit.prepare();
        hit.setOrigin(Origin.CENTER_BOTTOM);
        hit.setFrameOffsets(1, OFFSET_Y);

        shield.fill();
        armor.fill();
        energy.fill();

        front = createWeapon(Weapon.MISSILE_LAUNCHER);
        rear = createWeapon(Weapon.SONIC_WAVE);
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
    private WeaponUpdater createWeapon(Media media)
    {
        final Weapon weapon = factory.create(media);
        handler.add(weapon);
        ignoreProjectileCollision(weapon.getFeature(Launcher.class));
        return weapon.getFeature(WeaponModel.class).take(false);
    }

    /**
     * Get the surface representation.
     * 
     * @return The surface representation.
     */
    public SpriteTiled getSurface()
    {
        return surface;
    }

    /**
     * Get the hit sprite.
     * 
     * @return The hit sprite.
     */
    public Sprite getHit()
    {
        return hit;
    }

    /**
     * Get the hit tick.
     * 
     * @return The hit tick.
     */
    public Tick getHitTick()
    {
        return hitTick;
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
    public WeaponUpdater getFront()
    {
        return front;
    }

    /**
     * Get the weapon rear.
     * 
     * @return The rear weapon.
     */
    public WeaponUpdater getRear()
    {
        return rear;
    }
}
