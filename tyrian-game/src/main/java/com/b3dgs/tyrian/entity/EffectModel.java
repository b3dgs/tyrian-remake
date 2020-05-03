/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.AnimState;
import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.AnimatorStateListener;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.FeatureGet;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;

/**
 * Effect model implementation.
 */
@FeatureInterface
public final class EffectModel extends FeatureModel
{
    /** Explode node name. */
    public static final String NODE_EXPLODE = "explode";

    private final Animation anim;

    @FeatureGet private Transformable transformable;
    @FeatureGet private Animatable animatable;
    @FeatureGet private Identifiable identifiable;

    /**
     * Create an effect.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    EffectModel(Services services, Setup setup)
    {
        super(services, setup);

        anim = AnimationConfig.imports(setup).getAnimation("start");
    }

    /**
     * Start the effect.
     * 
     * @param localizable The localizable reference.
     */
    public void start(Localizable localizable)
    {
        transformable.setLocation(localizable.getX(), localizable.getY());
        animatable.play(anim);
    }

    /**
     * Check if finished.
     * 
     * @return <code>true</code> if finished, <code>false</code> else.
     */
    public boolean isFinished()
    {
        return animatable.is(AnimState.FINISHED);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        animatable.addListener((AnimatorStateListener) state ->
        {
            if (AnimState.FINISHED == state)
            {
                identifiable.destroy();
            }
        });
    }
}
