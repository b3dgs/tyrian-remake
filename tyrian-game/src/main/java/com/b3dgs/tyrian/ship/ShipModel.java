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
package com.b3dgs.tyrian.ship;

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Tick;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.Setup;
import com.b3dgs.lionengine.game.SizeConfig;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.graphic.Sprite;
import com.b3dgs.lionengine.graphic.SpriteAnimated;
import com.b3dgs.lionengine.graphic.SpriteTiled;
import com.b3dgs.tyrian.Constant;

/**
 * Ship model implementation.
 */
public final class ShipModel extends FeatureModel
{
    private static final int OFFSET_Y = 8;

    private final Alterable shield = new Alterable(15);
    private final Alterable armor = new Alterable(10);
    private final Alterable energy = new Alterable(200);
    private final Tick hitTick = new Tick();
    private final SpriteTiled surface;
    private final SpriteAnimated hit;

    /**
     * Create a ship.
     * 
     * @param setup The setup reference.
     */
    ShipModel(Setup setup)
    {
        super();

        final SizeConfig config = SizeConfig.imports(setup);
        surface = Drawable.loadSpriteTiled(setup.getSurface(), config.getWidth(), config.getHeight());
        surface.setOrigin(Origin.MIDDLE);

        hit = Drawable.loadSpriteAnimated(Medias.create(Constant.FOLDER_EFFECT, "Hit.png"), 1, 1);
        hit.load();
        hit.prepare();
        hit.setOrigin(Origin.CENTER_BOTTOM);
        hit.setFrameOffsets(1, OFFSET_Y);

        shield.fill();
        armor.fill();
        energy.fill();
    }

    /**
     * Get the surface representation.
     * 
     * @return The surface representation.
     */
    public SpriteTiled getSurface()
    {
        return surface;
    }

    /**
     * Get the hit sprite.
     * 
     * @return The hit sprite.
     */
    public Sprite getHit()
    {
        return hit;
    }

    /**
     * Get the hit tick.
     * 
     * @return The hit tick.
     */
    public Tick getHitTick()
    {
        return hitTick;
    }

    /**
     * Get the shield.
     * 
     * @return The shield.
     */
    public Alterable getShield()
    {
        return shield;
    }

    /**
     * Get the armor.
     * 
     * @return The armor.
     */
    public Alterable getArmor()
    {
        return armor;
    }

    /**
     * Get the energy fire.
     * 
     * @return The energy fire.
     */
    public Alterable getEnergy()
    {
        return energy;
    }
}
