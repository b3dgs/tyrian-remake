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

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.game.feature.Factory;
import com.b3dgs.lionengine.game.feature.FeaturableModel;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Handler;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.Refreshable;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.game.feature.Transformable;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Rectangle;
import com.b3dgs.lionengine.graphic.engine.SourceResolutionProvider;
import com.b3dgs.tyrian.Sfx;

/**
 * Explode effect implementation.
 */
public class Explode extends FeaturableModel
{
    /** Explode count attribute. */
    public static final String ATT_COUNT = "count";
    /** Explode delay in millisecond. */
    private static final long DELAY = 40L;
    /** Explode sound delay in millisecond. */
    private static final long DELAY_SFX = 300L;
    /** Empty action. */
    private static final PostAction EMPTY_ACTION = () ->
    {
        // Nothing to do
    };

    private final Rectangle area = new Rectangle();
    private final Media media;
    private final int countMax;
    private PostAction action = EMPTY_ACTION;
    private int count = -1;

    private final SourceResolutionProvider source;
    private final Factory factory;
    private final Handler handler;

    /**
     * Create an explode effect.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public Explode(Services services, Setup setup)
    {
        super(services, setup);

        source = services.get(SourceResolutionProvider.class);
        factory = services.get(Factory.class);
        handler = services.get(Handler.class);

        media = Medias.create(setup.getText(Effect.NODE_EXPLODE));
        countMax = setup.getInteger(ATT_COUNT, Effect.NODE_EXPLODE);

        addFeature(new ExplodeUpdater());
    }

    /**
     * Start the explode.
     * 
     * @param transformable The transformable reference.
     */
    public void start(Transformable transformable)
    {
        area.set(transformable.getX(), transformable.getY(), transformable.getWidth(), transformable.getHeight());
        count++;
    }

    /**
     * Start the explode.
     * 
     * @param rectangle The rectangle reference.
     * @param action Action called once effect ended.
     */
    public void start(Rectangle rectangle, PostAction action)
    {
        this.action = action;
        area.set(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
        count++;
    }

    /**
     * Explode updater implementation.
     */
    private class ExplodeUpdater extends FeatureModel implements Refreshable
    {
        private static final long EXTRA_DELAY = 1000L;

        private final Tick tick = new Tick();
        private final Tick tickSfx = new Tick();
        private final Tick extraDelay = new Tick();
        private Effect effect;

        /**
         * Create explode medium updater.
         */
        ExplodeUpdater()
        {
            super();
        }

        @Override
        public void update(double extrp)
        {
            tick.update(extrp);
            tickSfx.update(extrp);
            extraDelay.update(extrp);

            if (count == 0 || tick.elapsedTime(source.getRate(), DELAY) && count <= countMax)
            {
                final double x = area.getX() - area.getWidth() / 2 + UtilRandom.getRandomInteger(area.getWidth());
                final double y = area.getY() - UtilRandom.getRandomInteger(area.getHeight());

                effect = factory.create(media);
                effect.start(Geom.createLocalizable(x, y));
                handler.add(effect);

                checkSfx();

                count++;
                tick.restart();
            }
            checkEnd();
        }

        /**
         * Check sound timing.
         */
        private void checkSfx()
        {
            if (count == 0 || tickSfx.elapsedTime(source.getRate(), DELAY_SFX))
            {
                if (count % 2 == 0)
                {
                    Sfx.EXPLODE_LARGE.play();
                }
                tickSfx.restart();
            }
        }

        /**
         * Check when effect ended.
         */
        private void checkEnd()
        {
            if (count > countMax && effect != null && effect.getFeature(EffectUpdater.class).isFinished())
            {
                extraDelay.start();
                if (extraDelay.elapsedTime(source.getRate(), EXTRA_DELAY))
                {
                    action.execute();
                    getFeature(Identifiable.class).destroy();
                    count = 0;
                    effect = null;
                    action = EMPTY_ACTION;
                }
            }
        }
    }

    /**
     * Post action interface.
     */
    public interface PostAction
    {
        /**
         * Execute post action.
         */
        void execute();
    }
}
