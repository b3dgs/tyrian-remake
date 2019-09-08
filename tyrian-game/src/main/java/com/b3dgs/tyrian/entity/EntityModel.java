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
package com.b3dgs.tyrian.entity;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.ForceConfig;
import com.b3dgs.lionengine.game.SizeConfig;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.tyrian.Constant;
import com.b3dgs.tyrian.effect.Effect;

/**
 * Entity model implementation.
 */
@FeatureInterface
public final class EntityModel extends FeatureModel implements Recyclable
{
    private static final String ANIM_IDLE = "idle";

    private final Alterable life = new Alterable(3);
    private final Direction direction;
    private final SpriteAnimated surface;
    private final Media explode;
    private final Animation anim;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    EntityModel(Services services, Setup setup)
    {
        super(services, setup);

        final Xml root = setup.getRoot();
        if (root.hasChild(ForceConfig.NODE_FORCE))
        {
            direction = ForceConfig.imports(setup);
        }
        else
        {
            direction = DirectionNone.INSTANCE;
        }
        explode = Medias.create(Constant.FOLDER_EFFECT,
                                setup.getText(Effect.NODE_EXPLODE) + Factory.FILE_DATA_DOT_EXTENSION);

        final SizeConfig sizeConfig = SizeConfig.imports(setup);
        final ImageBuffer buffer = setup.getSurface();
        surface = Drawable.loadSpriteAnimated(buffer,
                                              buffer.getWidth() / sizeConfig.getWidth(),
                                              buffer.getHeight() / sizeConfig.getHeight());
        surface.setOrigin(Origin.MIDDLE);

        final AnimationConfig animConfig = AnimationConfig.imports(setup);
        if (animConfig.hasAnimation(ANIM_IDLE))
        {
            anim = animConfig.getAnimation(ANIM_IDLE);
        }
        else
        {
            anim = null;
        }
    }

    /**
     * Get the entity life.
     * 
     * @return The entity life.
     */
    public Alterable getLife()
    {
        return life;
    }

    /**
     * Get the entity force.
     * 
     * @return The entity force.
     */
    public Direction getDirection()
    {
        return direction;
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
     * Get the associated explode media.
     * 
     * @return The explode media.
     */
    public Media getExplode()
    {
        return explode;
    }

    /**
     * Get the associated animation.
     * 
     * @return The associated animation, <code>null</code> if none.
     */
    public Animation getAnim()
    {
        return anim;
    }

    @Override
    public void recycle()
    {
        life.fill();
        if (anim != null)
        {
            surface.play(anim);
        }
    }
}
