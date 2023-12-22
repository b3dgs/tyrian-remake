/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.tyrian.bonus;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;

/**
 * Bonus model implementation.
 */
@FeatureInterface
public final class BonusModel extends FeatureModel implements Routine
{
    private static final String ANIM_IDLE = "idle";
    private static final double FALLING_SPEED = -1.0;

    private final Camera camera = services.get(Camera.class);

    private final Identifiable identifiable;
    private final Transformable transformable;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param identifiable The identifiable feature.
     * @param transformable The transformable feature.
     * @param collidable The collidable feature.
     * @param animatable The animatable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public BonusModel(Services services,
                      Setup setup,
                      Identifiable identifiable,
                      Transformable transformable,
                      Collidable collidable,
                      Animatable animatable)
    {
        super(services, setup);

        this.identifiable = identifiable;
        this.transformable = transformable;

        final AnimationConfig animConfig = AnimationConfig.imports(setup);
        if (animConfig.hasAnimation(ANIM_IDLE))
        {
            animatable.play(animConfig.getAnimation(ANIM_IDLE));
        }
    }

    @Override
    public void update(double extrp)
    {
        transformable.moveLocation(extrp, 0.0, FALLING_SPEED);

        if (camera.getViewpointY(transformable.getY() + transformable.getHeight()) > camera.getHeight())
        {
            identifiable.destroy();
        }
    }
}
