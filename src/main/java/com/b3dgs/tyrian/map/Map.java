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
package com.b3dgs.tyrian.map;

import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.tyrian.entity.EntityOpponent;
import com.b3dgs.tyrian.entity.HandlerEntity;
import com.b3dgs.tyrian.entity.scenery.DoubleCanon;
import com.b3dgs.tyrian.entity.scenery.DoublePulse;
import com.b3dgs.tyrian.entity.scenery.FactoryEntityScenery;
import com.b3dgs.tyrian.entity.scenery.Generator;
import com.b3dgs.tyrian.entity.scenery.GreenPulse;
import com.b3dgs.tyrian.entity.scenery.House812;
import com.b3dgs.tyrian.entity.scenery.House92;
import com.b3dgs.tyrian.entity.scenery.Lamp;
import com.b3dgs.tyrian.entity.scenery.OpenablePulse;
import com.b3dgs.tyrian.entity.scenery.Pump;
import com.b3dgs.tyrian.entity.scenery.RedBox;
import com.b3dgs.tyrian.entity.scenery.RedEngine;
import com.b3dgs.tyrian.entity.scenery.Silo;
import com.b3dgs.tyrian.entity.scenery.SingleCanon;
import com.b3dgs.tyrian.entity.scenery.Spider;
import com.b3dgs.tyrian.entity.scenery.SubSquare;
import com.b3dgs.tyrian.entity.scenery.ThreeRedH;
import com.b3dgs.tyrian.entity.scenery.ThreeRedV;

/**
 * Map implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Map
        extends MapTileGame<TileCollision, Tile>
{
    /**
     * Create the entity from the tile number.
     * 
     * @param factory The entity static factory.
     * @param n The tile number.
     * @return The entity instance, <code>null</code> if none.
     */
    private static EntityOpponent create(FactoryEntityScenery factory, int n)
    {
        switch (n)
        {
            case 6:
                return factory.create(SingleCanon.class);
            case 7:
                return factory.create(DoubleCanon.class);
            case 8:
                return factory.create(RedEngine.class);
            case 9:
                return factory.create(GreenPulse.class);
            case 19:
                return factory.create(OpenablePulse.class);
            case 13:
                return factory.create(Generator.class);
            case 26:
                return factory.create(DoublePulse.class);
            case 18:
                return factory.create(Lamp.class);
            case 30:
                return factory.create(Spider.class);
            case 32:
                return factory.create(SubSquare.class);
            case 45:
                return factory.create(Silo.class);
            case 46:
                return factory.create(RedBox.class);
            case 56:
                return factory.create(ThreeRedV.class);
            case 50:
                return factory.create(Pump.class);
            case 52:
                return factory.create(House92.class);
            case 54:
                return factory.create(House812.class);
            case 66:
                return factory.create(ThreeRedH.class);
            default:
                return null;
        }
    }

    /**
     * Constructor.
     */
    public Map()
    {
        super(24, 28);
    }

    /**
     * Create static entities depending of the tile.
     * 
     * @param factoryEntityStatic The entity static factory.
     * @param handlerEntity The handler entity.
     */
    public void spawnEntityStatic(FactoryEntityScenery factoryEntityStatic, HandlerEntity handlerEntity)
    {
        for (int tx = 0; tx < widthInTile; tx++)
        {
            for (int ty = 0; ty < heightInTile; ty++)
            {
                final Tile tile = getTile(tx, ty);
                if (tile != null)
                {
                    final int n = tile.getNumber();
                    final EntityOpponent entity = Map.create(factoryEntityStatic, n);
                    if (entity != null)
                    {
                        entity.teleport(tile.getX(), tile.getY() + 28);
                        handlerEntity.add(entity);
                    }
                }
            }
        }
    }

    /*
     * MapTileShmup
     */

    @Override
    public Tile createTile(int width, int height, Integer pattern, int number, TileCollision collision)
    {
        return new Tile(width, height, pattern, number, collision);
    }

    @Override
    public TileCollision getCollisionFrom(String collision)
    {
        return TileCollision.NONE;
    }
}
