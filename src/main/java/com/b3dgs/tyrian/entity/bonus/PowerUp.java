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

import com.b3dgs.lionengine.UtilRandom;
import com.b3dgs.lionengine.anim.Anim;
import com.b3dgs.lionengine.anim.Animator;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.configurer.ConfigAnimations;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.tyrian.Sfx;
import com.b3dgs.tyrian.entity.CategoryType;
import com.b3dgs.tyrian.entity.Entity;
import com.b3dgs.tyrian.entity.ship.Ship;

/**
 * Power up bonus implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class PowerUp
        extends Bonus
{
    /** Class media. */
    public static final Media MEDIA = Entity.getConfig(CategoryType.BONUS, PowerUp.class);

    /** Animator. */
    private final Animator animator;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     */
    public PowerUp(SetupSurfaceGame setup)
    {
        super(setup);
        animator = Anim.createAnimator();
        final Configurer configurer = setup.getConfigurer();
        final ConfigAnimations configAnimations = ConfigAnimations.create(configurer);
        animator.play(configAnimations.getAnimation("idle"));
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
        Sfx.POWER_UP.play();
    }

    @Override
    protected void onHit(Ship ship)
    {
        super.onHit(ship);
        ship.increaseWeaponLevel(UtilRandom.getRandomBoolean());
    }
}
