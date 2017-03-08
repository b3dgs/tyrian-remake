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
package com.b3dgs.tyrian.effect;

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.game.FramesConfig;
import com.b3dgs.lionengine.game.Setup;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.graphic.SpriteAnimated;

/**
 * Effect model implementation.
 */
final class EffectModel extends FeatureModel
{
    private final SpriteAnimated surface;

    /**
     * Create an effect.
     * 
     * @param setup The setup reference.
     */
    EffectModel(Setup setup)
    {
        super();

        final FramesConfig config = FramesConfig.imports(setup);
        surface = Drawable.loadSpriteAnimated(setup.getSurface(), config.getHorizontal(), config.getVertical());
        surface.setOrigin(Origin.MIDDLE);
    }

    /**
     * Get the surface representation.
     * 
     * @return The surface representation.
     */
    public SpriteAnimated getSurface()
    {
        return surface;
    }
}
