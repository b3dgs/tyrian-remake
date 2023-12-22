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
package com.b3dgs.tyrian.entity;

import com.b3dgs.lionengine.Animation;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Xml;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.AnimationConfig;
import com.b3dgs.lionengine.game.Direction;
import com.b3dgs.lionengine.game.DirectionNone;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.ForceConfig;
import com.b3dgs.lionengine.game.feature.Animatable;
import com.b3dgs.lionengine.game.feature.Camera;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Layerable;
import com.b3dgs.lionengine.game.feature.Recyclable;
import com.b3dgs.lionengine.game.feature.Routine;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.game.feature.collidable.Collidable;
import com.b3dgs.lionengine.game.feature.collidable.CollidableListener;
import com.b3dgs.lionengine.game.feature.collidable.Collision;
import com.b3dgs.tyrian.Constant;
import com.b3dgs.tyrian.Sfx;

/**
 * Entity model implementation.
 */
@FeatureInterface
public final class EntityModel extends FeatureModel implements Routine, CollidableListener, Recyclable
{
    private static final String ANIM_IDLE = "idle";

    private final Factory factory = services.get(Factory.class);
    private final Handler handler = services.get(Handler.class);
    private final Camera camera = services.get(Camera.class);

    private final Identifiable identifiable;
    private final Transformable transformable;
    private final Collidable collidable;
    private final Animatable animatable;

    private final Alterable life = new Alterable(3);
    private final Direction direction;
    private final Media explode;
    private final Animation anim;

    /**
     * Create feature.
     * 
     * @param services The services reference (must not be <code>null</code>).
     * @param setup The setup reference (must not be <code>null</code>).
     * @param identifiable The identifiable feature.
     * @param layerable The layerable feature.
     * @param transformable The transformable feature.
     * @param collidable The collidable feature.
     * @param animatable The animatable feature.
     * @throws LionEngineException If invalid arguments.
     */
    public EntityModel(Services services,
                       Setup setup,
                       Identifiable identifiable,
                       Layerable layerable,
                       Transformable transformable,
                       Collidable collidable,
                       Animatable animatable)
    {
        super(services, setup);

        this.identifiable = identifiable;
        this.transformable = transformable;
        this.collidable = collidable;
        this.animatable = animatable;

        final Xml root = setup.getRoot();
        if (root.hasNode(ForceConfig.NODE_FORCE))
        {
            direction = ForceConfig.imports(setup);
        }
        else
        {
            direction = DirectionNone.INSTANCE;
        }
        explode = Medias.create(Constant.FOLDER_EFFECT,
                                setup.getText(EffectModel.NODE_EXPLODE) + Factory.FILE_DATA_DOT_EXTENSION);

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
     * Get the entity force.
     * 
     * @return The entity force.
     */
    public Direction getDirection()
    {
        return direction;
    }

    /**
     * Spawn bullet hit effect.
     */
    private void spawnEffectHit()
    {
        final Featurable hit = factory.create(Medias.create(Constant.FOLDER_EFFECT, "bullet_hit.xml"));
        hit.getFeature(EffectModel.class).start(transformable);
        handler.add(hit);
    }

    /**
     * Spawn explode effect.
     */
    private void spawnEffectExplode()
    {
        final Explode explode = factory.create(this.explode);
        explode.start(transformable);
        handler.add(explode);
    }

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        collidable.setCollisionVisibility(false);
    }

    @Override
    public void update(double extrp)
    {
        transformable.moveLocation(extrp, direction);

        if (transformable.getY() < camera.getY() - transformable.getHeight())
        {
            identifiable.destroy();
        }
    }

    @Override
    public void notifyCollided(Collidable collidable, Collision with, Collision by)
    {
        Sfx.BULLET_HIT.play();
        spawnEffectHit();

        life.decrease(1);
        if (life.getCurrent() == 0)
        {
            spawnEffectExplode();
            identifiable.destroy();
        }

        collidable.getFeature(Identifiable.class).destroy();
    }

    @Override
    public void recycle()
    {
        life.fill();
        if (anim != null)
        {
            animatable.play(anim);
        }
    }
}
