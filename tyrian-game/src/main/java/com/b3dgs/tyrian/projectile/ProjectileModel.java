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
package com.b3dgs.tyrian.projectile;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.ForceConfig;
import com.b3dgs.lionengine.game.FramesConfig;
import com.b3dgs.lionengine.game.SurfaceConfig;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;

/**
 * Projectile model implementation.
 */
@FeatureInterface
public final class ProjectileModel extends FeatureModel
{
    private static final String NODE_EFFECT = Constant.XML_PREFIX + "effect";
    private static final String ATT_RATE = "rate";
    private static final String ATT_FRAME = "frame";

    private final SpriteAnimated surface;
    private final long effectRate;
    private final Media effectMedia;
    private final Direction acceleration;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    ProjectileModel(Services services, Setup setup)
    {
        super(services, setup);

        final FramesConfig frames = FramesConfig.imports(setup);
        surface = Drawable.loadSpriteAnimated(setup.getSurface(), frames.getHorizontal(), frames.getVertical());
        surface.setFrame(setup.getInteger(ATT_FRAME, SurfaceConfig.NODE_SURFACE));
        surface.setOrigin(Origin.MIDDLE);

        effectRate = setup.getIntegerDefault(0, ATT_RATE, NODE_EFFECT);
        if (setup.getRoot().hasChild(NODE_EFFECT))
        {
            effectMedia = Medias.create(setup.getText(NODE_EFFECT));
        }
        else
        {
            effectMedia = null;
        }

        final Xml root = setup.getRoot();
        if (root.hasChild(ForceConfig.NODE_FORCE))
        {
            acceleration = ForceConfig.imports(root);
        }
        else
        {
            acceleration = new Force();
        }
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

    /**
     * Get the effect rate.
     * 
     * @return The effect rate in milliseconds.
     */
    public long getEffectRate()
    {
        return effectRate;
    }

    /**
     * Get the effect media if has.
     * 
     * @return The effect media, <code>null</code> if none.
     */
    public Media getEffectMedia()
    {
        return effectMedia;
    }

    /**
     * Get the acceleration.
     * 
     * @return The acceleration.
     */
    public Direction getAcceleration()
    {
        return acceleration;
    }
}
