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
package com.b3dgs.tyrian.ship;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.collision.object.CollidableModel;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.SetupSurface;
import com.b3dgs.lionengine.game.feature.layerable.LayerableModel;
import com.b3dgs.lionengine.game.feature.transformable.TransformableModel;
import com.b3dgs.tyrian.Constant;

/**
 * Ship model base implementation.
 */
public class Ship extends FeaturableModel
{
    /** Stalker media. */
    public static final Media STALKER = Medias.create(Constant.FOLDER_SHIP, "stalker.xml");
    /** Controller feature node name. */
    private static final String NODE_CONTROLLER = com.b3dgs.lionengine.Constant.XML_PREFIX + "controller";

    /**
     * Create a ship.
     * 
     * @param setup The setup reference.
     */
    public Ship(SetupSurface setup)
    {
        super();

        addFeature(new LayerableModel(Constant.LAYER_SHIP_UPDATE, Constant.LAYER_SHIP));
        addFeature(new TransformableModel(setup));
        addFeature(new CollidableModel(setup));

        final ShipModel model = addFeatureAndGet(new ShipModel(setup));
        addFeature(new ShipUpdater(model));
        addFeature(new ShipRenderer(model));
        addFeature(setup.getImplementation(ShipController.class, NODE_CONTROLLER));
    }
}
