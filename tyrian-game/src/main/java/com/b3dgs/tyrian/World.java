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
package com.b3dgs.tyrian;

import java.io.IOException;
import java.util.List;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Featurable;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Layerable;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.WorldGame;
import com.b3dgs.lionengine.game.feature.collidable.ComponentCollision;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersisterModel;
import com.b3dgs.lionengine.game.feature.tile.map.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;
import com.b3dgs.lionengine.io.InputDevicePointer;
import com.b3dgs.lionengine.util.UtilRandom;
import com.b3dgs.tyrian.background.Background;
import com.b3dgs.tyrian.ship.ShipUpdater;

/**
 * World game representation.
 */
public class World extends WorldGame
{
    /** Stalker media. */
    public static final Media STALKER = Medias.create(Constant.FOLDER_SHIP, "stalker.xml");

    private static final long SPAWN_DELAY = 1500L;
    private static final int SPAWN_BONUS_CHANCE = 1;
    private static final List<Media> SPAWN_ENTITIES = getMedias(Constant.FOLDER_ENTITY, Constant.FOLDER_DYNAMIC);
    private static final List<Media> SPAWN_BONUS = getMedias(Constant.FOLDER_ENTITY, Constant.FOLDER_BONUS);

    /**
     * Get medias in path.
     * 
     * @param path The path.
     * @return The medias found
     */
    private static List<Media> getMedias(String... path)
    {
        return Medias.getByExtension(Factory.FILE_DATA_EXTENSION, Medias.create(path));
    }

    /**
     * Get a random media from array.
     * 
     * @param medias The medias array.
     * @return The random media.
     */
    private static Media getRandomMedia(List<Media> medias)
    {
        return medias.get(UtilRandom.getRandomInteger(medias.size() - 1));
    }

    private final InputDevicePointer mouse = services.add(getInputDevice(InputDevicePointer.class));
    private final Background background = new Background(camera);
    private final Tick tick = new Tick();
    private final Hud hud;
    private final MapTile map;

    /**
     * Create the world.
     * 
     * @param context The context reference.
     * @param services The services reference.
     */
    public World(Context context, Services services)
    {
        super(context, services);

        final double underMapHeight = -camera.getHeight() * 1.5;
        camera.teleport(0, underMapHeight);

        handler.addComponent(new ComponentCollision());

        final Featurable ship = factory.create(STALKER);
        handler.add(ship);
        services.add(ship.getFeature(ShipUpdater.class));

        map = Map.generate(services, "level1");
        map.addFeature(new MapTileViewerModel());
        map.addFeature(new MapTilePersisterModel());
        handler.add(map);

        hud = new Hud(services);

        camera.setView(0,
                       0,
                       source.getWidth(),
                       source.getHeight() - hud.getHeight(),
                       source.getHeight() - hud.getHeight());

        tick.start();
    }

    /**
     * Spawn a random object from medias array.
     * 
     * @param medias The medias array.
     * @param layer The associated layer.
     */
    private void spawn(List<Media> medias, int layer)
    {
        if (!medias.isEmpty())
        {
            final Media media = getRandomMedia(medias);
            final Featurable featurable = factory.create(media);
            featurable.getFeature(Layerable.class).setLayer(layer);
            handler.add(featurable);
            final Transformable transformable = featurable.getFeature(Transformable.class);
            final double x = UtilRandom.getRandomInteger(camera.getWidth() - transformable.getWidth() * 2)
                             + transformable.getWidth();
            final double y = (int) camera.getY() + camera.getHeight() + transformable.getHeight();
            transformable.teleport(x, y);
        }
    }

    @Override
    protected void saving(FileWriting file) throws IOException
    {
        map.getFeature(MapTilePersister.class).save(file);
    }

    @Override
    protected void loading(FileReading file) throws IOException
    {
        map.getFeature(MapTilePersister.class).load(file);
    }

    @Override
    public void update(double extrp)
    {
        mouse.update(extrp);
        background.update(extrp);
        tick.update(extrp);

        if (tick.elapsedTime(context, SPAWN_DELAY))
        {
            spawn(SPAWN_ENTITIES, Constant.LAYER_ENTITIES_MOVING);

            if (UtilRandom.getRandomInteger(SPAWN_BONUS_CHANCE) == 0)
            {
                spawn(SPAWN_BONUS, Constant.LAYER_BONUS);
            }

            tick.restart();
        }

        super.update(extrp);

        hud.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        background.render(g);

        super.render(g);

        hud.render(g);
    }
}
