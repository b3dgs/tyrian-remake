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

import com.b3dgs.lionengine.LionEngineException;

/**
 * Game constants.
 */
public final class Constant
{
    /** Ships folder. */
    public static final String FOLDER_SHIP = "ship";
    /** Entity folder. */
    public static final String FOLDER_ENTITY = "entity";
    /** Dynamic folder. */
    public static final String FOLDER_DYNAMIC = "dynamic";
    /** Bonus folder. */
    public static final String FOLDER_BONUS = "bonus";
    /** Static folder. */
    public static final String FOLDER_SCENERY = "scenery";
    /** Background folder. */
    public static final String FOLDER_BACKGROUND = "background";
    /** Musics folder. */
    public static final String FOLDER_MUSIC = "music";
    /** Weapons folder. */
    public static final String FOLDER_WEAPON = "weapon";
    /** Front weapon folder. */
    public static final String FOLDER_FRONT = "front";
    /** Rear weapon folder. */
    public static final String FOLDER_REAR = "rear";
    /** Effect folder. */
    public static final String FOLDER_EFFECT = "effect";
    /** Sound FX folder. */
    public static final String FOLDER_SFX = "sfx";
    /** Tiles folder. */
    public static final String FOLDER_TILE = "tile";
    /** Levels folder. */
    public static final String FOLDER_LEVELS = "level";

    /** Ship layer update. */
    public static final int LAYER_SHIP_UPDATE = 0;
    /** Map layer. */
    public static final int LAYER_MAP = LAYER_SHIP_UPDATE + 1;
    /** Entities layer. */
    public static final int LAYER_ENTITIES_STATIC = LAYER_MAP + 1;
    /** Entities layer. */
    public static final int LAYER_ENTITIES_MOVING = LAYER_ENTITIES_STATIC + 1;
    /** Projectiles layer. */
    public static final int LAYER_PROJECTILES = LAYER_ENTITIES_MOVING + 1;
    /** Bonus layer. */
    public static final int LAYER_BONUS = LAYER_PROJECTILES + 1;
    /** Effects layer. */
    public static final int LAYER_EFFECT = LAYER_BONUS + 1;
    /** Ship layer. */
    public static final int LAYER_SHIP = LAYER_EFFECT + 1;

    /** Horizontal movement margin. */
    public static final double MARGIN_H = 10.0;

    /**
     * Private constructor.
     */
    private Constant()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
