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
package com.b3dgs.tyrian;

import java.io.IOException;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.core.Context;
import com.b3dgs.lionengine.core.InputDevicePointer;
import com.b3dgs.lionengine.game.collision.object.ComponentCollision;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.layerable.Layerable;
import com.b3dgs.lionengine.game.feature.transformable.Transformable;
import com.b3dgs.lionengine.game.handler.WorldGame;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.feature.persister.MapTilePersister;
import com.b3dgs.lionengine.game.map.feature.persister.MapTilePersisterModel;
import com.b3dgs.lionengine.game.map.feature.viewer.MapTileViewerModel;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.stream.FileReading;
import com.b3dgs.lionengine.stream.FileWriting;
import com.b3dgs.lionengine.util.UtilRandom;
import com.b3dgs.tyrian.background.Background;
import com.b3dgs.tyrian.bonus.Bonus;
import com.b3dgs.tyrian.entity.Entity;
import com.b3dgs.tyrian.ship.Ship;

/**
 * World game representation.
 */
public class World extends WorldGame
{
    private static final long SPAWN_DELAY = 1000L;
    private static final int SPAWN_BONUS_CHANCE = 10;
    private static final Media[] SPAWN_ENTITIES = new Media[]
    {
        Entity.METEOR_MEDIUM_1, Entity.METEOR_LITTLE_1
    };
    private static final Media[] SPAWN_BONUS = new Media[]
    {
        Bonus.PULSE_CANNON, Bonus.SONIC_WAVE, Bonus.POWER_UP
    };

    /**
     * Get a random media from array.
     * 
     * @param medias The medias array.
     * @return The random media.
     */
    private static Media getRandomMedia(Media[] medias)
    {
        return medias[UtilRandom.getRandomInteger(medias.length - 1)];
    }

    private final InputDevicePointer mouse = services.add(getInputDevice(InputDevicePointer.class));
    private final MapTile map = services.add(Map.generate(services, "level1"));
    private final Background background = new Background(camera);
    private final Timing timing = new Timing();

    /**
     * Create the world.
     * 
     * @param context The context reference.
     */
    public World(Context context)
    {
        super(context);

        handler.addComponent(new ComponentCollision());

        map.addFeature(new MapTileViewerModel());
        map.addFeature(new MapTilePersisterModel());
        camera.setLimits(map);

        handler.add(factory.create(Ship.STALKER));
        handler.add(map);

        timing.start();
    }

    /**
     * Spawn a random object from medias array.
     * 
     * @param medias The medias array.
     * @param layer The associated layer.
     */
    private void spawn(Media[] medias, int layer)
    {
        final Media media = getRandomMedia(medias);
        final Featurable featurable = factory.create(media);
        featurable.getFeature(Layerable.class).setLayer(layer);
        handler.add(featurable);
        final Transformable transformable = featurable.getFeature(Transformable.class);
        transformable.teleport(UtilRandom.getRandomInteger(camera.getWidth()),
                               (int) camera.getY() + camera.getHeight() + transformable.getHeight());
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

        if (timing.elapsed(SPAWN_DELAY))
        {
            spawn(SPAWN_ENTITIES, Constant.LAYER_ENTITIES_MOVING);

            if (UtilRandom.getRandomInteger(SPAWN_BONUS_CHANCE) == 0)
            {
                spawn(SPAWN_BONUS, Constant.LAYER_BONUS);
            }

            timing.restart();
        }

        super.update(extrp);
    }

    @Override
    public void render(Graphic g)
    {
        background.render(g);

        super.render(g);
    }
}
