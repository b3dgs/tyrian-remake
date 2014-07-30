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
package com.b3dgs.tyrian.entity.dynamic;

import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.FactoryObjectGame;
import com.b3dgs.lionengine.game.purview.Fabricable;
import com.b3dgs.tyrian.AppTyrian;
import com.b3dgs.tyrian.entity.ContextEntity;
import com.b3dgs.tyrian.entity.SetupEntity;

/**
 * Factory entity dynamic.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class FactoryEntityDynamic
        extends FactoryObjectGame<SetupEntity>
{
    /** Context reference. */
    private ContextEntity context;

    /**
     * Constructor.
     */
    public FactoryEntityDynamic()
    {
        super(UtilFile.getPath(AppTyrian.ENTITIES_DIR, AppTyrian.DYNAMICS_DIR));
    }

    /**
     * Set the factory context.
     * 
     * @param context The factory context.
     */
    public void setContext(ContextEntity context)
    {
        this.context = context;
    }

    /*
     * FactoryObjectGame
     */

    @Override
    protected SetupEntity createSetup(Class<? extends Fabricable> key, Media config)
    {
        return new SetupEntity(config, context);
    }
}
