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

package net.maciekmm.personalTaxi;

import net.maciekmm.personalTaxi.entities.TaxiZombie;
import net.maciekmm.personalTaxi.menu.DestinationMenu;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftZombie;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.HashMap;
import java.util.UUID;

public class TaxiListener implements Listener {
    private final PersonalTaxi plugin;

    public TaxiListener(PersonalTaxi plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) || !event.hasItem() || !plugin.isTaxiItem(event.getItem())) {
            return;
        }

        if (event.getPlayer().getVehicle() != null) {
            event.getPlayer().sendMessage(PersonalTaxi.getMessage("destination.inventory.open.dismount"));
            return;
        }

        DestinationManagement management = plugin.getManager(event.getPlayer().getWorld());
        if (management == null) {
            event.getPlayer().sendMessage(PersonalTaxi.getMessage("destination.world.notSupported", event.getPlayer().getWorld().getName()));
            return;
        }
        event.setCancelled(true);
        event.getPlayer().openInventory(management.getMenuForPlayer(event.getPlayer()));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof DestinationMenu) {
            event.setCancelled(true);
            DestinationEntry entry = ((DestinationMenu) event.getInventory().getHolder()).getManager().getEntryFromIndex((Player) event.getWhoClicked(), event.getRawSlot());
            if (entry == null) {
                return;
            }
            //TODO: CHECKS for length of trip etc.
            this.takePlayerToDestination((Player) event.getWhoClicked(), entry);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryMove(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof DestinationMenu) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDismount(EntityDismountEvent event) {
        if (event.getDismounted() instanceof CraftZombie && ((CraftZombie) event.getDismounted()).getHandle() instanceof TaxiZombie) {
            ((TaxiZombie) ((CraftZombie) event.getDismounted()).getHandle()).destroy();
        }
    }

    @EventHandler
    public void onSuffocation(EntityDamageEvent event) {
        if (event.getEntity().getVehicle() != null && (event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION || event.getCause() == EntityDamageEvent.DamageCause.DROWNING)) {
            if (event.getEntity().getVehicle() instanceof CraftZombie && ((CraftZombie) event.getEntity().getVehicle()).getHandle() instanceof TaxiZombie) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDisconnect(PlayerQuitEvent event) {
        if (event.getPlayer().getVehicle() != null) {
            if (event.getPlayer().getVehicle() instanceof CraftZombie && ((CraftZombie) event.getPlayer().getVehicle()).getHandle() instanceof TaxiZombie) {
                ((TaxiZombie) ((CraftZombie) event.getPlayer().getVehicle()).getHandle()).destroy();
            }
        }
    }

    private void takePlayerToDestination(Player player, DestinationEntry entry) {
        if (player.getWorld() != entry.getWorld()) {
            throw new IllegalArgumentException("Worlds of player and entry must match");
        }

        TaxiZombie zombie = new TaxiZombie(((CraftWorld) player.getWorld()).getHandle());
        zombie.setBaby(true); //Cute little zombie :]
        //zombie.setSprinting(true); //LOL zombie can sprint?
        zombie.setLocation(player.getLocation());
        zombie.setCustomName(String.format(plugin.getConfig().getString("general.taxiName"), player.getName()));
        zombie.setCustomNameVisible(true);
        ((CraftPlayer) player).getHandle().mount(zombie); //Probably there's faster way :D
        zombie.setDestination(entry);
        ((CraftWorld) player.getWorld()).getHandle().addEntity(zombie);
        player.sendMessage(PersonalTaxi.getMessage("destination.prepare", entry.getName()));
    }


}
