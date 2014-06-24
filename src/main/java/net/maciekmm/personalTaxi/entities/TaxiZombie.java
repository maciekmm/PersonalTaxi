/*
 * Copyright (C) 2014 maciekmm <maciekmm@maciekmm.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.maciekmm.personalTaxi.entities;

import net.maciekmm.personalTaxi.DestinationEntry;
import net.minecraft.server.v1_7_R3.EntityZombie;
import net.minecraft.server.v1_7_R3.GenericAttributes;
import net.minecraft.server.v1_7_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.util.UnsafeList;

import java.lang.reflect.Field;

public class TaxiZombie extends EntityZombie {
    private final PathfinderWalkToLocation walkTo = new PathfinderWalkToLocation(this);

    public TaxiZombie(World world) {
        super(world);
        try {
            Field b = this.goalSelector.getClass().getDeclaredField("b");
            b.setAccessible(true);
            ((UnsafeList<?>)b.get(this.goalSelector)).clear(); //WEIRD in code it's ArrayList
            ((UnsafeList<?>)b.get(this.targetSelector)).clear();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        //Longer pathfinding distance
        this.targetSelector.a(0,this.walkTo);
        this.getAttributeInstance(GenericAttributes.b).setValue(10000);
        this.getNavigation().a(true); //avoid water
    }

    public void setDestination(DestinationEntry destination) {
        this.walkTo.setWalkTo(destination);
    }

    @Override
    public boolean isInvulnerable() {
        return walkTo.getWalkTo() != null && this.passenger != null;
    }

    public DestinationEntry getDestination() {
        return this.walkTo.getWalkTo();
    }

    public void destroy() {
        this.setDestination(null);
        this.setHealth(0f);
    }

    public void setLocation(Location location) {
        this.setLocation(location.getX(), location.getY() + 1, location.getZ(), location.getYaw(), location.getPitch());
    }
}
