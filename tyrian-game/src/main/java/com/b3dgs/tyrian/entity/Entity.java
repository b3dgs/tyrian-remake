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
package com.b3dgs.tyrian.entity;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.ForceConfig;
import com.b3dgs.lionengine.game.collision.object.CollidableModel;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.SetupSurface;
import com.b3dgs.lionengine.game.feature.layerable.LayerableModel;
import com.b3dgs.lionengine.game.feature.transformable.TransformableModel;
import com.b3dgs.lionengine.stream.XmlNode;
import com.b3dgs.tyrian.Constant;

/**
 * Entity base implementation.
 */
public class Entity extends FeaturableModel
{
    /** Meteor little 1 media. */
    public static final Media METEOR_LITTLE_1 = Medias.create(Constant.FOLDER_ENTITY,
                                                              Constant.FOLDER_DYNAMIC,
                                                              "meteor_little_1.xml");
    /** Meteor medium 1 media. */
    public static final Media METEOR_MEDIUM_1 = Medias.create(Constant.FOLDER_ENTITY,
                                                              Constant.FOLDER_DYNAMIC,
                                                              "meteor_medium_1.xml");

    /**
     * Get the entity layer.
     * 
     * @param setup The setup reference.
     * @return The entity layer.
     */
    private static int getLayer(Setup setup)
    {
        final XmlNode root = setup.getRoot();
        if (root.hasChild(ForceConfig.NODE_FORCE))
        {
            return Constant.LAYER_ENTITIES_MOVING;
        }
        return Constant.LAYER_ENTITIES_STATIC;
    }

    /**
     * Create an entity.
     * 
     * @param setup The setup reference.
     */
    public Entity(final SetupSurface setup)
    {
        super();

        addFeature(new TransformableModel(setup));
        addFeature(new CollidableModel(setup));
        addFeature(new LayerableModel(getLayer(setup)));

        final EntityModel model = addFeatureAndGet(new EntityModel(setup));
        addFeature(new EntityUpdater(model));
        addFeature(new EntityRenderer(model));
    }
}
