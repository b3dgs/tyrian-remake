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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.TileConfig;
import com.b3dgs.lionengine.game.feature.tile.TileRef;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.TileSetListener;
import com.b3dgs.lionengine.game.feature.tile.map.TileSheetsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersisterModel;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.Xml;
import com.b3dgs.lionengine.util.UtilRandom;
import com.b3dgs.tyrian.entity.Entity;

/**
 * Map game representation.
 */
public final class Map
{
    private static final String FILE_ENTITIES_TABLE = "entity.xml";
    private static final int MAX_LEVELS = 14;
    private static final int MAX_LEVEL_INTERVAL_HEIGHT_IN_TILE = 6;

    /**
     * Generate a map.
     * 
     * @param services The services reference.
     * @param theme The theme name.
     * @return The generated map.
     */
    public static MapTile generate(Services services, String theme)
    {
        final MapTile map = services.create(MapTileGame.class);
        map.addFeature(new LayerableModel(Constant.LAYER_MAP));
        map.loadSheets(Medias.create(Constant.FOLDER_TILE, theme, TileSheetsConfig.FILENAME));

        final java.util.Map<TileRef, Media> entities = getEntities(theme);
        final TileSetListener listener = createListener(entities, map, services);
        map.addListener(listener);

        final List<MapTile> maps = getMaps(theme, MAX_LEVELS);

        final Collection<MapTile> levels = new ArrayList<MapTile>();
        for (int i = 0; i < MAX_LEVELS; i++)
        {
            final MapTile current = maps.get(UtilRandom.getRandomInteger(maps.size() - 1));
            levels.add(current);
        }
        map.append(levels, 0, 1, 0, MAX_LEVEL_INTERVAL_HEIGHT_IN_TILE);

        map.removeListener(listener);
        entities.clear();

        return map;
    }

    /**
     * Create the map listener to add entities over tiles.
     * 
     * @param entities The entities mapping.
     * @param map The map reference.
     * @param services The services reference.
     * @return The created listener.
     */
    private static TileSetListener createListener(final java.util.Map<TileRef, Media> entities,
                                                  final MapTile map,
                                                  Services services)
    {
        final Factory factory = services.get(Factory.class);
        final Handler handler = services.get(Handler.class);

        return new TileSetListener()
        {
            @Override
            public void onTileSet(Tile tile)
            {
                final TileRef tileRef = new TileRef(tile);
                if (entities.containsKey(tileRef))
                {
                    final Media media = entities.get(tileRef);
                    final Entity entity = factory.create(media);
                    final Transformable transformable = entity.getFeature(Transformable.class);
                    final int x = (int) (tile.getX() + transformable.getWidth() / 2);
                    final int y = (int) (tile.getY() + map.getTileHeight() - transformable.getHeight() / 2);
                    transformable.teleport(x, y);
                    handler.add(entity);
                }
            }
        };
    }

    /**
     * Get a list of maps.
     * 
     * @param theme The theme name.
     * @param count The file level count.
     * @return List of maps.
     */
    private static List<MapTile> getMaps(String theme, int count)
    {
        final List<MapTile> maps = new ArrayList<MapTile>();
        for (int i = 0; i <= count; i++)
        {
            final Media level = Medias.create(Constant.FOLDER_LEVELS, theme, i + ".map");
            final Services services = new Services();
            final MapTile map = services.create(MapTileGame.class);
            final MapTilePersister persister = map.addFeatureAndGet(new MapTilePersisterModel());
            map.prepareFeatures(services);
            try
            {
                final FileReading reading = new FileReading(level);
                persister.load(reading);
                reading.close();
            }
            catch (final IOException exception)
            {
                Verbose.exception(exception);
            }
            maps.add(map);
        }
        return maps;
    }

    /**
     * Get entities equivalence from map tile.
     * 
     * @param theme The theme used.
     * @return The equivalence table between entities and tile.
     */
    private static java.util.Map<TileRef, Media> getEntities(String theme)
    {
        final java.util.Map<TileRef, Media> entities = new HashMap<TileRef, Media>();
        final Xml config = new Xml(Medias.create(Constant.FOLDER_TILE, theme, FILE_ENTITIES_TABLE));
        for (final Xml nodeTile : config.getChildren(TileConfig.NODE_TILE))
        {
            final TileRef tile = TileConfig.create(nodeTile);
            final String file = nodeTile.getText() + Factory.FILE_DATA_DOT_EXTENSION;
            final Media entity = Medias.create(Constant.FOLDER_ENTITY, Constant.FOLDER_SCENERY, file);
            entities.put(tile, entity);
        }
        return entities;
    }

    /**
     * Create the map.
     */
    private Map()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
