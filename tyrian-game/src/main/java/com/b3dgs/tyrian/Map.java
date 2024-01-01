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
package com.b3dgs.tyrian;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.XmlReader;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.HandlerPersister;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.tile.TileConfig;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileAppender;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.TileSetListener;
import com.b3dgs.lionengine.game.feature.tile.map.TileSheetsConfig;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersister;
import com.b3dgs.lionengine.game.feature.tile.map.persister.MapTilePersisterModel;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;

/**
 * Map game representation.
 */
public final class Map
{
    private static final String FILE_ENTITIES_TABLE = "entity.xml";
    private static final int MAX_LEVELS = 14;
    private static final int MAX_LEVEL_INTERVAL_HEIGHT_IN_TILE = 6;
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Map.class);

    /**
     * Generate a map.
     * 
     * @param services The services reference.
     * @param theme The theme name.
     */
    public static void generate(Services services, String theme)
    {
        for (int i = 0; i <= 20; i++)
        {
            // importLevelAndSave(Medias.create(Constant.FOLDER_LEVELS, "level1", String.valueOf(i) + ".png"));
        }

        final MapTileGame map = services.get(MapTileGame.class);
        final MapTileAppender appender = map.getFeature(MapTileAppender.class);
        map.addFeature(new LayerableModel(Constant.LAYER_MAP.intValue()));
        map.loadSheets(Medias.create(Constant.FOLDER_TILE, theme, TileSheetsConfig.FILENAME));

        final java.util.Map<Integer, Media> entities = getEntities(theme);
        final TileSetListener listener = createListener(entities, map, services);
        map.addListener(listener);

        final List<MapTile> maps = getMaps(theme, MAX_LEVELS);

        final Collection<MapTile> levels = new ArrayList<>();
        for (int i = 0; i < MAX_LEVELS; i++)
        {
            final MapTile current = maps.get(UtilRandom.getRandomInteger(maps.size() - 1));
            levels.add(current);
        }
        appender.append(levels, 0, 1, 0, MAX_LEVEL_INTERVAL_HEIGHT_IN_TILE);

        map.removeListener(listener);
        entities.clear();
    }

    /**
     * Import the level and save it.
     * 
     * @param level The level to import.
     */
    @SuppressWarnings("unused")
    private static void importLevelAndSave(Media level)
    {
        final Services services = new Services();
        services.add(new Factory(services));
        services.add(new Handler(services));

        final MapTileGame map = services.create(MapTileGame.class);
        final MapTilePersister mapPersister = map.addFeature(new MapTilePersisterModel());
        final HandlerPersister handlerPersister = new HandlerPersister(services);
        map.create(level, Medias.create(Constant.FOLDER_TILE, "level1", "sheets.xml"));

        try (FileWriting output = new FileWriting(Medias.create(level.getParentPath(),
                                                                level.getName().replace(".png", "") + ".map")))
        {
            mapPersister.save(output);
            handlerPersister.save(output);
        }
        catch (final IOException exception)
        {
            LOGGER.error("importLevelAndSave error", exception);
        }
    }

    /**
     * Create the map listener to add entities over tiles.
     * 
     * @param entities The entities mapping.
     * @param map The map reference.
     * @param services The services reference.
     * @return The created listener.
     */
    private static TileSetListener createListener(java.util.Map<Integer, Media> entities,
                                                  MapTile map,
                                                  Services services)
    {
        final Factory factory = services.get(Factory.class);
        final Handler handler = services.get(Handler.class);

        return tile ->
        {
            final Integer tileRef = tile.getKey();
            if (entities.containsKey(tileRef))
            {
                final Media media = entities.get(tileRef);
                final Featurable entity = factory.create(media);
                final Transformable transformable = entity.getFeature(Transformable.class);
                final int x = (int) (tile.getX() + transformable.getWidth() / 2);
                final int y = (int) (tile.getY() + map.getTileHeight() - transformable.getHeight() / 2);
                transformable.teleport(x, y);
                handler.add(entity);
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
        final List<MapTile> maps = new ArrayList<>();
        for (int i = 0; i <= count; i++)
        {
            final Media level = Medias.create(Constant.FOLDER_LEVELS, theme, i + ".map");
            final Services services = new Services();
            final MapTileGame map = services.create(MapTileGame.class);
            final MapTilePersister persister = map.addFeature(new MapTilePersisterModel());
            try (FileReading reading = new FileReading(level))
            {
                persister.load(reading);
            }
            catch (final IOException exception)
            {
                LOGGER.error("getMaps error", exception);
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
    private static java.util.Map<Integer, Media> getEntities(String theme)
    {
        final java.util.Map<Integer, Media> entities = new HashMap<>();
        final Xml config = new Xml(Medias.create(Constant.FOLDER_TILE, theme, FILE_ENTITIES_TABLE));
        for (final XmlReader nodeTile : config.getChildren(TileConfig.NODE_TILE))
        {
            final Integer tile = Integer.valueOf(TileConfig.imports(nodeTile));
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
