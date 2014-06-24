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
import net.maciekmm.personalTaxi.PersonalTaxi;
import net.minecraft.server.v1_7_R3.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class PathfinderWalkToLocation extends PathfinderGoal {
    private final TaxiZombie creature;
    private DestinationEntry walkTo;
    private final static double SPEED = 1D;

    public PathfinderWalkToLocation(TaxiZombie creature) {
        this.creature = creature;
        this.a(0); //Concurrency mutex
    }

    @Override
    public boolean a() {
        return this.walkTo != null;
    }

    @Override
    public void e() {
        //Check every second
        if (this.getCreature().ticksLived % 20 == 0) {
            if (this.creature.passenger == null) {
                this.creature.destroy();
                return;
            }
            //If PathEntity != null && pathentity.isFinished()
            if (this.creature.getNavigation().e() != null && this.creature.getNavigation().e().b()) {
                if (this.creature.passenger instanceof EntityPlayer) {
                    ((Player) (this.creature.passenger.getBukkitEntity())).sendMessage(PersonalTaxi.getMessage("destination.finished"));
                }
                this.creature.destroy();
                return;
            }

            PathEntity path = this.creature.getNavigation().a(this.walkTo.getLocation().getX(), this.walkTo.getLocation().getY(), this.walkTo.getLocation().getZ());
            this.creature.getNavigation().a(path, SPEED);
        }
    }

    public EntityCreature getCreature() {
        return this.creature;
    }

    public DestinationEntry getWalkTo() {
        return this.walkTo;
    }

    public void setWalkTo(DestinationEntry walkTo) {
        this.walkTo = walkTo;
    }
}
