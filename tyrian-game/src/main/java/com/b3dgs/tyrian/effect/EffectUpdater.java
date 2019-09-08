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
package com.b3dgs.tyrian.effect;

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Refreshable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;

/**
 * Effect updater implementation.
 */
@FeatureInterface
final class EffectUpdater extends FeatureModel implements Refreshable
{
    private final SpriteAnimated surface;

    private final Viewer viewer = services.get(Viewer.class);

    @FeatureGet private Transformable transformable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param model The model reference (must not be <code>null</code>).
     * @throws LionEngineException If invalid arguments.
     */
    EffectUpdater(Services services, Setup setup, EffectModel model)
    {
        super(services, setup);

        Check.notNull(model);

        surface = model.getSurface();
    }

    /**
     * Get the effect finished state.
     * 
     * @return <code>true</code> if animation finished, <code>false</code> else.
     */
    public boolean isFinished()
    {
        return surface.getAnimState() == AnimState.FINISHED;
    }

    @Override
    public void update(double extrp)
    {
        surface.update(extrp);
        surface.setLocation(viewer, transformable);

        if (isFinished())
        {
            getFeature(Identifiable.class).destroy();
        }
    }
}
