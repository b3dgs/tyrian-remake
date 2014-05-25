/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.tyrian.entity.bonus;

import com.b3dgs.lionengine.anim.Anim;
import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.game.purview.Configurable;
import com.b3dgs.tyrian.Sfx;
import com.b3dgs.tyrian.effect.Effect;

/**
 * Coin bonus implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
abstract class Coin
        extends Bonus
{
    /** Animator. */
    private final Animator animator;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    protected Coin(SetupEntityBonus setup)
    {
        super(setup);
        animator = Anim.createAnimator();
        final Configurable configurable = setup.getConfigurable();
        animator.play(configurable.getAnimation("idle"));
    }

    /*
     * Bonus
     */

    @Override
    public void update(double extrp)
    {
        super.update(extrp);
        animator.updateAnimation(extrp);
        setTileOffset(animator.getFrame() - 1);
    }

    @Override
    protected void onDestroyed()
    {
        final Effect taken = factoryEffect.create(com.b3dgs.tyrian.effect.Coin10.class);
        taken.start(getLocationIntX() + getWidth() / 2 - taken.getWidth() / 2, getLocationIntY() + getHeight() / 2
                - taken.getHeight() / 2, 0);
        handlerEffect.add(taken);
        Sfx.BONUS.play();
    }
}
