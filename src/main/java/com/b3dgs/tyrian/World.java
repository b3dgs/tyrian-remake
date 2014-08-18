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

import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Mouse;
import com.b3dgs.lionengine.core.Sequence;
import com.b3dgs.lionengine.core.Verbose;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.ContextGame;
import com.b3dgs.lionengine.game.WorldGame;
import com.b3dgs.lionengine.game.map.TileGame;
import com.b3dgs.lionengine.stream.FileReading;
import com.b3dgs.lionengine.stream.FileWriting;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.utility.LevelRipConverter;
import com.b3dgs.tyrian.background.Background;
import com.b3dgs.tyrian.effect.FactoryEffect;
import com.b3dgs.tyrian.effect.HandlerEffect;
import com.b3dgs.tyrian.entity.EntityOpponent;
import com.b3dgs.tyrian.entity.HandlerEntity;
import com.b3dgs.tyrian.entity.bonus.FactoryEntityBonus;
import com.b3dgs.tyrian.entity.bonus.HyperPulse;
import com.b3dgs.tyrian.entity.bonus.MachineGun;
import com.b3dgs.tyrian.entity.bonus.MissileLauncherFront;
import com.b3dgs.tyrian.entity.bonus.MissileLauncherRear;
import com.b3dgs.tyrian.entity.bonus.PowerUp;
import com.b3dgs.tyrian.entity.bonus.PulseCannon;
import com.b3dgs.tyrian.entity.bonus.WaveCannonRear;
import com.b3dgs.tyrian.entity.dynamic.FactoryEntityDynamic;
import com.b3dgs.tyrian.entity.dynamic.MeteorBig;
import com.b3dgs.tyrian.entity.scenery.FactoryEntityScenery;
import com.b3dgs.tyrian.entity.ship.FactoryShip;
import com.b3dgs.tyrian.entity.ship.GencorePhoenix;
import com.b3dgs.tyrian.entity.ship.Ship;
import com.b3dgs.tyrian.map.Map;
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
            try (final FileReading file = Stream.createFileReading(output);)
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
            final LevelRipConverter<TileGame> rip = new LevelRipConverter<>();
            rip.start(levelrip, tilesheet, map);
            try (final FileWriting file = Stream.createFileWriting(output);)
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
        mouse = sequence.getInputDevice(Mouse.class);
        mouse.setConfig(config);
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

        factoryProjectile = new FactoryProjectile();
        final HandlerEntity[] handlers = new HandlerEntity[]
        {
                handlerEntityScenery, handlerEntityDynamic
        };
        handlerProjectile = new HandlerProjectile(camera, handlers);
        factoryWeapon = new FactoryWeapon();
        factoryEntityScenery = new FactoryEntityScenery();
        factoryEntityDynamic = new FactoryEntityDynamic();
        factoryEntityBonus = new FactoryEntityBonus();
        factoryShip = new FactoryShip();

        createContextsAndPreparators();

        ship = factoryShip.create(GencorePhoenix.MEDIA);
        handlerEntityScenery.setShip(ship);
        handlerEntityDynamic.setShip(ship);
        handlerEntityBonus.setShip(ship);

        // Rip a level and store data in the map
        for (int i = 0; i < 21; i++)
        {
            final int index = UtilRandom.getRandomInteger(20);
            if (i == 0)
            {
                World.ripLevel(map, Core.MEDIA.create(AppTyrian.LEVELS_DIR, "images", index + ".png"),
                        Core.MEDIA.create(AppTyrian.TILES_DIR, "level1"),
                        Core.MEDIA.create(AppTyrian.LEVELS_DIR, "0.map"));
            }
            else
            {
                final Map newMap = new Map();
                World.ripLevel(newMap, Core.MEDIA.create(AppTyrian.LEVELS_DIR, "images", index + ".png"),
                        Core.MEDIA.create(AppTyrian.TILES_DIR, "level1"),
                        Core.MEDIA.create(AppTyrian.LEVELS_DIR, i + ".map"));
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

    /**
     * Create the contexts and assign them.
     */
    private void createContextsAndPreparators()
    {
        final ContextGame contextEntity = new ContextGame();
        contextEntity.addService(factoryEffect);
        contextEntity.addService(handlerEffect);
        contextEntity.addService(factoryWeapon);

        final ContextGame contextProjectile = new ContextGame();
        contextProjectile.addService(factoryEffect);
        contextProjectile.addService(handlerEffect);

        final ContextGame contextWeapon = new ContextGame();
        contextWeapon.addService(factoryProjectile);
        contextWeapon.addService(handlerProjectile);

        factoryShip.setContext(contextEntity);
        factoryEntityScenery.setContext(contextEntity);
        factoryEntityDynamic.setContext(contextEntity);
        factoryEntityBonus.setContext(contextEntity);

        factoryProjectile.setContext(contextProjectile);
        factoryWeapon.setContext(contextWeapon);
    }

    /*
     * WorldGame
     */

    @Override
    public void update(double extrp)
    {
        mouse.update();
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
            if (UtilRandom.getRandomInteger(50) == 0)
            {
                final EntityOpponent entity = factoryEntityDynamic.create(MeteorBig.MEDIA);
                entity.teleport(UtilRandom.getRandomInteger(camera.getViewWidth()) - entity.getWidth() / 2,
                        camera.getLocationY() + camera.getViewHeight() + entity.getHeight());
                handlerEntityDynamic.add(entity);
                timerMeteor.start();
            }
        }
        if (timerBonus.elapsed(2000))
        {
            final Media bonus;
            switch (UtilRandom.getRandomInteger(30))
            {
                case 0:
                    bonus = PulseCannon.MEDIA;
                    break;
                case 1:
                    bonus = HyperPulse.MEDIA;
                    break;
                case 2:
                    bonus = MachineGun.MEDIA;
                    break;
                case 3:
                    bonus = MissileLauncherFront.MEDIA;
                    break;
                case 4:
                    bonus = MissileLauncherRear.MEDIA;
                    break;
                case 5:
                    bonus = WaveCannonRear.MEDIA;
                    break;
                default:
                    bonus = PowerUp.MEDIA;
                    break;

            }
            final EntityOpponent entity = factoryEntityBonus.create(bonus);
            entity.teleport(UtilRandom.getRandomInteger(camera.getViewWidth()) - entity.getWidth() / 2,
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
