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
package com.b3dgs.tyrian.entity.ship;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.tyrian.AppTyrian;
import com.b3dgs.tyrian.effect.FactoryEffect;
import com.b3dgs.tyrian.effect.HandlerEffect;
import com.b3dgs.tyrian.entity.SetupEntity;
import com.b3dgs.tyrian.weapon.FactoryWeapon;

/**
 * Factory ship.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class FactoryShip
        extends FactoryObjectGame<SetupEntity, Ship>
{
    /** Factory effect. */
    private final FactoryEffect factoryEffect;
    /** Handler effect. */
    private final HandlerEffect handlerEffect;
    /** Factory weapon. */
    private final FactoryWeapon factoryWeapon;

    /**
     * Constructor
     * 
     * @param factoryWeapon The weapon factory reference.
     * @param factoryEffect The factory effect reference.
     * @param handlerEffect The handler effect reference.
     */
    public FactoryShip(FactoryEffect factoryEffect, HandlerEffect handlerEffect, FactoryWeapon factoryWeapon)
    {
        super(AppTyrian.SHIPS_DIR);
        this.factoryEffect = factoryEffect;
        this.handlerEffect = handlerEffect;
        this.factoryWeapon = factoryWeapon;
    }

    /*
     * FactoryObjectGame
     */

    @Override
    protected SetupEntity createSetup(Class<? extends Ship> type, Media config)
    {
        return new SetupEntity(config, factoryEffect, handlerEffect, factoryWeapon);
    }
}
