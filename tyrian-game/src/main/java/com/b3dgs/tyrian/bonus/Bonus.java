/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.tyrian.bonus;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.LayerableModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.TransformableModel;
import com.b3dgs.lionengine.game.feature.collidable.CollidableModel;
import com.b3dgs.tyrian.Constant;

/**
 * Entity base implementation.
 */
public class Bonus extends FeaturableModel
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

    /**
     * Create bonus.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    public Bonus(Services services, Setup setup)
    {
        super(services, setup);

        addFeature(new TransformableModel(services, setup));
        addFeature(new CollidableModel(services, setup));
        addFeature(new LayerableModel(services, setup));

        final BonusModel model = addFeatureAndGet(new BonusModel(services, setup));
        addFeature(new BonusUpdater(services, setup, model));
        addFeature(new BonusRenderer(services, setup, model));
    }
}
