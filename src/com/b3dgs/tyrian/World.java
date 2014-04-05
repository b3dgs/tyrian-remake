/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.tyrian;

import java.io.IOException;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.UtilityRandom;
import com.b3dgs.lionengine.core.DeviceType;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Mouse;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.UtilityMedia;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.file.File;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.WorldGame;
import com.b3dgs.lionengine.utility.LevelRipConverter;
import com.b3dgs.tyrian.background.Background;
import com.b3dgs.tyrian.effect.FactoryEffect;
import com.b3dgs.tyrian.effect.HandlerEffect;
import com.b3dgs.tyrian.entity.EntityOpponent;
import com.b3dgs.tyrian.entity.HandlerEntity;
import com.b3dgs.tyrian.entity.bonus.EntityBonusType;
import com.b3dgs.tyrian.entity.bonus.FactoryEntityBonus;
import com.b3dgs.tyrian.entity.dynamic.EntityDynamicType;
import com.b3dgs.tyrian.entity.dynamic.FactoryEntityDynamic;
import com.b3dgs.tyrian.entity.scenery.FactoryEntityScenery;
import com.b3dgs.tyrian.entity.ship.FactoryShip;
import com.b3dgs.tyrian.entity.ship.Ship;
import com.b3dgs.tyrian.entity.ship.ShipType;
import com.b3dgs.tyrian.map.Map;
import com.b3dgs.tyrian.map.Tile;
import com.b3dgs.tyrian.projectile.FactoryProjectile;
import com.b3dgs.tyrian.projectile.HandlerProjectile;
import com.b3dgs.tyrian.weapon.FactoryWeapon;

/**
 * World implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class World
        extends WorldGame
{
    /**
     * Create a level from a level rip.
     * 
     * @param map The map reference.
     * @param levelrip The level rip image.
     * @param tilesheet The tilesheet image.
     * @param output The output level saved.
     */
    private static void ripLevel(Map map, Media levelrip, Media tilesheet, Media output)
    {
        if (output.getFile().exists())
        {
            try (final FileReading file = File.createFileReading(output);)
            {
                map.load(file);
            }
            catch (final IOException exception)
            {
                Verbose.exception(World.class, "ripLevel", exception, "Error on loading map !");
            }
        }
        else
        {
            final LevelRipConverter<Tile> rip = new LevelRipConverter<>();
            rip.start(levelrip, map, tilesheet);
            try (final FileWriting file = File.createFileWriting(output);)
            {
                map.save(file);
            }
            catch (final IOException exception)
            {
                Verbose.exception(World.class, "ripLevel", exception, "Error on saving map !");
            }
        }
    }

    /** Mouse. */
    private final Mouse mouse;
    /** Hud reference. */
    private final Hud hud;
    /** Map reference. */
    private final Map map;
    /** Background reference. */
    private final Background background;
    /** Camera reference. */
    private final CameraGame camera;
    /** Factory effect. */
    private final FactoryEffect factoryEffect;
    /** Handler effect. */
    private final HandlerEffect handlerEffect;
    /** Handler entity scenery. */
    private final HandlerEntity handlerEntityScenery;
    /** Handler entity dynamic. */
    private final HandlerEntity handlerEntityDynamic;
    /** Handler entity bonus. */
    private final HandlerEntity handlerEntityBonus;
    /** Factory projectile. */
    private final FactoryProjectile factoryProjectile;
    /** Handler projectile. */
    private final HandlerProjectile handlerProjectile;
    /** Weapon factory. */
    private final FactoryWeapon factoryWeapon;
    /** Factory entity scenery. */
    private final FactoryEntityScenery factoryEntityScenery;
    /** Factory entity dynamic. */
    private final FactoryEntityDynamic factoryEntityDynamic;
    /** Factory entity bonus. */
    private final FactoryEntityBonus factoryEntityBonus;
    /** Factory ship. */
    private final FactoryShip factoryShip;
    /** Ship reference. */
    private final Ship ship;
    /** Meteor timer. */
    private final Timing timerMeteor;
    /** Bonus timer. */
    private final Timing timerBonus;

    /**
     * @see WorldGame#WorldGame(Sequence)
     */
    World(Sequence sequence)
    {
        super(sequence);
        mouse = sequence.getInputDevice(DeviceType.MOUSE);
        hud = new Hud();
        map = new Map();
        background = new Background();
        camera = new CameraGame();
        timerMeteor = new Timing();
        timerBonus = new Timing();

        factoryEffect = new FactoryEffect();
        handlerEffect = new HandlerEffect(camera);
        handlerEntityScenery = new HandlerEntity(camera);
        handlerEntityDynamic = new HandlerEntity(camera);
        handlerEntityBonus = new HandlerEntity(camera);

        factoryProjectile = new FactoryProjectile(factoryEffect, handlerEffect);
        final HandlerEntity[] handlers = new HandlerEntity[]
        {
                handlerEntityScenery, handlerEntityDynamic
        };
        handlerProjectile = new HandlerProjectile(camera, handlers);
        factoryWeapon = new FactoryWeapon(factoryProjectile, handlerProjectile);

        factoryEntityScenery = new FactoryEntityScenery(factoryEffect, handlerEffect, factoryWeapon);
        factoryEntityDynamic = new FactoryEntityDynamic(factoryEffect, handlerEffect, factoryWeapon);
        factoryEntityBonus = new FactoryEntityBonus(factoryEffect, handlerEffect, factoryWeapon);

        factoryShip = new FactoryShip(factoryEffect, handlerEffect, factoryWeapon);
        ship = factoryShip.create(ShipType.GENCORE_PHOENIX);
        handlerEntityScenery.setShip(ship);
        handlerEntityDynamic.setShip(ship);
        handlerEntityBonus.setShip(ship);

        // Rip a level and store data in the map
        for (int i = 0; i < 21; i++)
        {
            final int index = UtilityRandom.getRandomInteger(20);
            if (i == 0)
            {
                World.ripLevel(map, UtilityMedia.get("levels", "images", index + ".png"),
                        UtilityMedia.get("tiles", "level1"), UtilityMedia.get("levels", "0.map"));
            }
            else
            {
                final Map newMap = new Map();
                World.ripLevel(newMap, UtilityMedia.get("levels", "images", index + ".png"),
                        UtilityMedia.get("tiles", "level1"), UtilityMedia.get("levels", i + ".map"));
                map.append(newMap, 0, newMap.getHeightInTile() * i);
            }
        }
        map.spawnEntityStatic(factoryEntityScenery, handlerEntityScenery);
        hud.setShip(ship);
        ship.init(mouse, camera, height);
        camera.setView(0, 0, 263, 184);
        timerMeteor.start();
        timerBonus.start();
    }

    /*
     * WorldGame
     */

    @Override
    public void update(double extrp)
    {
        camera.moveLocation(extrp, 0.0, 1.0);
        camera.setLocationX(ship.getLocationX() / 12);

        ship.update(extrp, mouse, camera, height);

        handlerProjectile.update(extrp);
        handlerEntityScenery.update(extrp);
        handlerEntityDynamic.update(extrp);
        handlerEntityBonus.update(extrp);
        handlerEffect.update(extrp);

        background.update(extrp);
        hud.update(extrp);

        if (timerMeteor.elapsed(500))
        {
            if (UtilityRandom.getRandomInteger(50) == 0)
            {
                final EntityOpponent entity = factoryEntityDynamic.create(EntityDynamicType.METEOR_BIG);
                entity.teleport(UtilityRandom.getRandomInteger(camera.getViewWidth()) - entity.getWidth() / 2,
                        camera.getLocationY() + camera.getViewHeight() + entity.getHeight());
                handlerEntityDynamic.add(entity);
                timerMeteor.start();
            }
        }
        if (timerBonus.elapsed(2000))
        {
            final EntityBonusType bonus;
            switch (UtilityRandom.getRandomInteger(30))
            {
                case 0:
                    bonus = EntityBonusType.PULSE_CANNON;
                    break;
                case 1:
                    bonus = EntityBonusType.HYPER_PULSE;
                    break;
                case 2:
                    bonus = EntityBonusType.MACHINE_GUN;
                    break;
                case 3:
                    bonus = EntityBonusType.MISSILE_LAUNCHER_FRONT;
                    break;
                case 4:
                    bonus = EntityBonusType.MISSILE_LAUNCHER_REAR;
                    break;
                case 5:
                    bonus = EntityBonusType.WAVE_CANNON_REAR;
                    break;
                default:
                    bonus = EntityBonusType.POWER_UP;
                    break;

            }
            final EntityOpponent entity = factoryEntityBonus.create(bonus);
            entity.teleport(UtilityRandom.getRandomInteger(camera.getViewWidth()) - entity.getWidth() / 2,
                    camera.getLocationY() + camera.getViewHeight() + entity.getHeight());
            handlerEntityBonus.add(entity);
            timerBonus.restart();
        }
    }

    @Override
    public void render(Graphic g)
    {
        background.render(g, camera);
        map.render(g, camera);

        handlerEntityScenery.render(g);
        handlerEntityDynamic.render(g);
        handlerEntityBonus.render(g);

        ship.render(g, camera);

        handlerProjectile.render(g);
        handlerEffect.render(g);

        hud.render(g);
    }

    @Override
    protected void saving(FileWriting file) throws IOException
    {
        // Nothing to do
    }

    @Override
    protected void loading(FileReading file) throws IOException
    {
        // Nothing to do
    }
}
