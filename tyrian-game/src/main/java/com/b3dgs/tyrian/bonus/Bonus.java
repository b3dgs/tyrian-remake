/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.tyrian.bonus;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.tyrian.Constant;

/**
 * Entity base implementation.
 */
public class Bonus
{
    /** Pulse Cannon. */
    public static final Media PULSE_CANNON = Medias.create(Constant.FOLDER_ENTITY,
                                                           Constant.FOLDER_BONUS,
                                                           "pulse_cannon.xml");

    /** Sonic Wave. */
    public static final Media SONIC_WAVE = Medias.create(Constant.FOLDER_ENTITY,
                                                         Constant.FOLDER_BONUS,
                                                         "sonic_wave.xml");
    /** Missile launcher. */
    public static final Media MISSILE_LAUNCHER = Medias.create(Constant.FOLDER_ENTITY,
                                                               Constant.FOLDER_BONUS,
                                                               "missile_launcher.xml");

    /** Missile heavy launcher rear. */
    public static final Media MISSILE_HEAVY_LAUNCHER_REAR = Medias.create(Constant.FOLDER_BONUS,
                                                                          "missile_heavy_launcher.xml");
    /** Power up media. */
    public static final Media POWER_UP = Medias.create(Constant.FOLDER_ENTITY, Constant.FOLDER_BONUS, "power_up.xml");
}
