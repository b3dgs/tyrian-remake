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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.game.Service;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Refreshable;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.graphic.SpriteAnimated;

/**
 * Effect updater implementation.
 */
final class EffectUpdater extends FeatureModel implements Refreshable
{
    private final SpriteAnimated surface;

    @Service private Transformable transformable;

    @Service private Viewer viewer;

    /**
     * Create an effect updater.
     * 
     * @param model The model reference.
     */
    EffectUpdater(EffectModel model)
    {
        super();

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
