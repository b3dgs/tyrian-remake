/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Version;

/**
 * Game constants.
 */
public final class Constant
{
    /** Application name. */
    public static final String NAME = "Tyrian Remake";
    /** Application version. */
    public static final Version VERSION = Version.create(0, 4, 0);
    /** Native resolution. */
    public static final Resolution NATIVE = new Resolution(200, 355, 60);

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
    /** Sprite folder. */
    public static final String FOLDER_SPRITE = "sprite";
    /** Pictures folder. */
    public static final String FOLDER_PIC = "pic";
    /** Font folder. */
    public static final String FOLDER_FONT = "font";
    /** Tiles folder. */
    public static final String FOLDER_TILE = "tile";
    /** Levels folder. */
    public static final String FOLDER_LEVELS = "level";

    /** Ship layer update. */
    public static final Integer LAYER_SHIP_UPDATE = Integer.valueOf(0);
    /** Map layer. */
    public static final Integer LAYER_MAP = Integer.valueOf(LAYER_SHIP_UPDATE.intValue() + 1);
    /** Entities layer. */
    public static final Integer LAYER_ENTITIES_STATIC = Integer.valueOf(LAYER_MAP.intValue() + 1);
    /** Entities layer. */
    public static final Integer LAYER_ENTITIES_MOVING = Integer.valueOf(LAYER_ENTITIES_STATIC.intValue() + 1);
    /** Projectiles layer. */
    public static final Integer LAYER_PROJECTILES = Integer.valueOf(LAYER_ENTITIES_MOVING.intValue() + 1);
    /** Bonus layer. */
    public static final Integer LAYER_BONUS = Integer.valueOf(LAYER_PROJECTILES.intValue() + 1);
    /** Effects layer. */
    public static final Integer LAYER_EFFECT = Integer.valueOf(LAYER_BONUS.intValue() + 1);
    /** Ship layer. */
    public static final Integer LAYER_SHIP = Integer.valueOf(LAYER_EFFECT.intValue() + 1);

    /** Collision group ship. */
    public static final Integer COLLISION_GROUP_SHIP = Integer.valueOf(0);
    /** Collision group projectiles. */
    public static final Integer COLLISION_GROUP_PROJECTILES_SHIP = Integer.valueOf(COLLISION_GROUP_SHIP.intValue() + 1);
    /** Collision group projectiles. */
    public static final Integer COLLISION_GROUP_PROJECTILES_ENTITIES = Integer.valueOf(COLLISION_GROUP_PROJECTILES_SHIP.intValue()
                                                                                       + 1);
    /** Collision group entities. */
    public static final Integer COLLISION_GROUP_ENTITIES = Integer.valueOf(COLLISION_GROUP_PROJECTILES_ENTITIES.intValue()
                                                                           + 1);
    /** Collision group bonus. */
    public static final Integer COLLISION_GROUP_BONUS = Integer.valueOf(COLLISION_GROUP_ENTITIES.intValue() + 1);

    /** Horizontal movement margin. */
    public static final double MARGIN_H = 60.0;

    /** Maximum weapon level. */
    public static final int WEAPON_LEVEL_MAX = 2;

    /**
     * Private constructor.
     */
    private Constant()
    {
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
