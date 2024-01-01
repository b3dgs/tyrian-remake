/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.Force;
import com.b3dgs.lionengine.game.ForceConfig;
import com.b3dgs.lionengine.game.SurfaceConfig;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.launchable.Launchable;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;

/**
 * Projectile model implementation.
 */
@FeatureInterface
public final class ProjectileModel extends FeatureModel implements Routine, Recyclable
{
    private static final String NODE_EFFECT = "effect";
    private static final String ATT_RATE = "rate";
    private static final String ATT_FRAME = "frame";

    private final SourceResolutionProvider source = services.get(SourceResolutionProvider.class);
    private final Factory factory = services.get(Factory.class);
    private final Handler handler = services.get(Handler.class);
    private final Viewer viewer = services.get(Viewer.class);

    private final Transformable transformable;
    private final Animatable animatable;

    private final Tick tick = new Tick();
    private final Force force = new Force();
    private final long effectRate;
    private final Media effectMedia;
    private final Direction acceleration;
    private final int frame;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param transformable The transformable feature.
     * @param launchable The launchable feature.
     * @param collidable The collidable feature.
     * @param animatable The animatable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public ProjectileModel(Services services,
                           Setup setup,
                           Transformable transformable,
                           Launchable launchable,
                           Collidable collidable,
                           Animatable animatable)
    {
        super(services, setup);

        this.transformable = transformable;
        this.animatable = animatable;

        frame = setup.getInteger(ATT_FRAME, SurfaceConfig.NODE_SURFACE);
        effectRate = setup.getInteger(0, ATT_RATE, NODE_EFFECT);
        if (setup.hasNode(NODE_EFFECT))
        {
            effectMedia = Medias.create(setup.getText(NODE_EFFECT));
        }
        else
        {
            effectMedia = null;
        }

        final Xml root = setup.getRoot();
        if (root.hasNode(ForceConfig.NODE_FORCE))
        {
            acceleration = ForceConfig.imports(root);
        }
        else
        {
            acceleration = new Force();
        }

        collidable.setCollisionVisibility(false);

        if (effectMedia != null)
        {
            launchable.addListener(l -> startEffect(transformable));
        }
    }

    /**
     * Start effect.
     * 
     * @param localizable The localizable reference.
     */
    private void startEffect(Localizable localizable)
    {
        final Featurable effect = factory.create(effectMedia);
        handler.add(effect);
        effect.getFeature(EffectModel.class).start(localizable);
        tick.restart();
    }

    @Override
    public void update(double extrp)
    {
        animatable.setFrame(frame);
        transformable.moveLocation(extrp, force);
        force.addDirection(extrp, acceleration);
        tick.update(extrp);

        if (tick.elapsedTime(source.getRate(), effectRate))
        {
            startEffect(transformable);
        }
        if (!viewer.isViewable(transformable, 0, 0))
        {
            getFeature(Identifiable.class).destroy();
        }
    }

    @Override
    public void recycle()
    {
        force.setDirection(DirectionNone.INSTANCE);
    }
}
