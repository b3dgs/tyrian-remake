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
package com.b3dgs.tyrian.bonus;

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.SetupSurface;
import com.b3dgs.lionengine.game.feature.SizeConfig;
import com.b3dgs.lionengine.game.feature.layerable.Layerable;
import com.b3dgs.lionengine.game.state.AnimationConfig;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.tyrian.Constant;

/**
 * Bonus model implementation.
 */
public final class BonusModel extends FeatureModel
{
    private static final String ANIM_IDLE = "idle";

    private final SpriteAnimated surface;

    /**
     * Create an entity model.
     * 
     * @param setup The setup reference.
     */
    BonusModel(SetupSurface setup)
    {
        super();

        final SizeConfig sizeConfig = SizeConfig.imports(setup);
        final ImageBuffer buffer = setup.getSurface();
        surface = Drawable.loadSpriteAnimated(buffer,
                                              buffer.getWidth() / sizeConfig.getWidth(),
                                              buffer.getHeight() / sizeConfig.getHeight());
        surface.setOrigin(Origin.MIDDLE);

        final AnimationConfig animConfig = AnimationConfig.imports(setup);
        if (animConfig.hasAnimation(ANIM_IDLE))
        {
            surface.play(animConfig.getAnimation(ANIM_IDLE));
        }
    }

    @Override
    public void prepare(FeatureProvider provider, Services services)
    {
        super.prepare(provider, services);

        getFeature(Layerable.class).setLayer(Constant.LAYER_ENTITIES_MOVING);
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
