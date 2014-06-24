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

import com.google.common.collect.ImmutableList;
import net.maciekmm.personalTaxi.commands.ReloadCommand;
import net.maciekmm.personalTaxi.entities.TaxiZombie;
import net.minecraft.server.v1_7_R3.EntityInsentient;
import net.minecraft.server.v1_7_R3.EntityTypes;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.concurrent.Immutable;
import javax.print.attribute.standard.Destination;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class PersonalTaxi extends JavaPlugin {
    private static ResourceBundle messages = ResourceBundle.getBundle("messages");
    private ImmutableList<DestinationManagement> managers;
    private ItemStack item;

    public enum RecipeType {
        SHAPED, SHAPELESS
    }

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.loadManagers();
        this.getServer().getPluginManager().registerEvents(new TaxiListener(this), this);
        this.loadItem();
        this.loadRecipe();
        this.registerEntity("Zombie", 54, TaxiZombie.class);
        this.getCommand("ptreload").setExecutor(new ReloadCommand(this));
    }

    /**
     * Reloads plugin
     */
    public void reloadPlugin() {
        this.reloadConfig();
        this.loadManagers();
        this.loadItem();
    }

    private void loadManagers() {
        managers = null;
        List<Map<?, ?>> destinations = this.getConfig().getMapList("destinations");
        if (destinations != null) {
            ImmutableList.Builder<DestinationManagement> managersB = new ImmutableList.Builder<>();
            for (Map<?, ?> map : destinations) {
                try {
                    managersB.add(DestinationManagement.fromConfig(new MemoryConfiguration().createSection("temporarySection", map)));
                } catch (IllegalArgumentException e) {
                    this.getLogger().warning(e.getMessage());
                }
            }
            this.managers = managersB.build();
            return;
        }

        if (managers == null) {
            this.getLogger().severe("No managers registered, correspond your config. DISABLING");
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void loadRecipe() {
        RecipeType type = RecipeType.valueOf(this.getConfig().getString("general.item.recipe.type"));
        Recipe recipe;
        switch (type) {
            case SHAPED:
                ShapedRecipe shaped = new ShapedRecipe(item);
                shaped.shape(this.getConfig().getStringList("general.item.recipe.layout").toArray(new String[3]));
                for (String key : this.getConfig().getConfigurationSection("general.item.recipe.ingredients").getKeys(false)) {
                    shaped.setIngredient(key.charAt(0), Material.matchMaterial(this.getConfig().getString("general.item.recipe.ingredients." + key)));
                }
                recipe = shaped;
                break;
            case SHAPELESS:
                ShapelessRecipe shapeless = new ShapelessRecipe(item);
                for (String key : this.getConfig().getConfigurationSection("general.item.recipe.ingredients").getKeys(false)) {
                    shapeless.addIngredient(Material.matchMaterial(this.getConfig().getString("general.item.recipe.ingredients" + key)));
                }
                recipe = shapeless;
                break;
            default:
                return;
        }
        this.getServer().addRecipe(recipe);
    }

    private void loadItem() {
        Material material = Material.matchMaterial(this.getConfig().getString("general.item.name"));
        if (material == null) {
            material = Material.COMPASS;
        }
        List<String> lore = this.getConfig().getStringList("general.item.description");
        lore.add(this.getName());
        String name = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("general.item.name", "Call your taxi!"));
        item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta); //stupid cloning -.-
    }

    public static String getMessage(String key, String... args) {
        return MessageFormat.format(messages.getString(key), args);
    }

    /**
     * Gets an taxi item
     * @return taxi item
     */
    public ItemStack getItem() {
        return this.item;
    }

    /**
     * Check whether or not item is a Taxi item
     *
     * @param item - item to check
     * @return boolean - true if item is taxi item
     */
    public boolean isTaxiItem(ItemStack item) {
        return this.item.isSimilar(item); //IS this sufficient?
    }

    /**
     * Get all available managers
     *
     * @return ImmutableList of managers
     */
    public ImmutableList<DestinationManagement> getManagers() {
        return this.managers;
    }

    /**
     * Get destination manager for certain world
     *
     * @param world - world to get manager associated with
     * @return - Manager for world or null if world is not supported
     */
    public DestinationManagement getManager(World world) {
        for (DestinationManagement entry : this.managers) {
            if (entry.getWorld() == world) {
                return entry;
            }
        }
        return null;
    }

    /**
     * Checks if players is going anywhere
     * @param player - player to check
     * @return boolean - whether player is riding or not.
     */
    public boolean isOnRoad(Player player) {
        Entity taxi = player.getVehicle();
        return taxi != null && taxi instanceof TaxiZombie; //Can we use instanceof on null?
    }

    /**
     * Gets Destination of certain player
     * @param player - player to get destination of.
     * @return - DestinationEntry if isOnRoad and null otherwise
     */
    public DestinationEntry getDestination(Player player) {
        Entity veh = player.getVehicle();
        if(veh == null || !(veh instanceof TaxiZombie)) {
            return null;
        }
        return ((TaxiZombie)veh).getDestination();
    }

    /**
     * This is the only thing that came from external sources
     * <b>Hope you will pardon me :(</b>
     * <b>It's just time saving :]</b>
     */
    @SuppressWarnings("unchecked")
    private void registerEntity(String name, int id, Class<? extends EntityInsentient> customClass) {
        try {

            List<Map<?, ?>> dataMaps = new ArrayList<Map<?, ?>>();
            for (Field f : EntityTypes.class.getDeclaredFields()) {
                if (f.getType().getSimpleName().equals(Map.class.getSimpleName())) {
                    f.setAccessible(true);
                    dataMaps.add((Map<?, ?>) f.get(null));
                }
            }

            ((Map<Class<? extends EntityInsentient>, String>) dataMaps.get(1)).put(customClass, name);
            ((Map<Class<? extends EntityInsentient>, Integer>) dataMaps.get(3)).put(customClass, id);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
