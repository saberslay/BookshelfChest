package me.saberslay.bookshelfchest.Util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class InventoryUtils {

    /**
     *
     * Getting this Error when trying to load up the saved dater fot the Bookshelves
     * the test dater is in the bookshelf_inventories.yml file
     * Failed to load inventory at location key: world,-63. Invalid location string format. Expected 4 parts but got: 2. Location string: world,-63
     * Failed to load inventory at location key: world,-60. Invalid location string format. Expected 4 parts but got: 2. Location string: world,-60
     *
     */

    public static void saveBookshelfInventories(HashMap<Location, Inventory> bookshelfInventories, File file) {
        YamlConfiguration config = new YamlConfiguration();

        for (Map.Entry<Location, Inventory> entry : bookshelfInventories.entrySet()) {
            String locKey = serializeLocation(entry.getKey());
            ItemStack[] items = entry.getValue().getContents();
            config.set(locKey, items);
            Bukkit.getLogger().info("Saved inventory at " + locKey + " with " + items.length + " items.");
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<Location, Inventory> loadBookshelfInventories(File file) {
        HashMap<Location, Inventory> bookshelfInventories = new HashMap<>();
        if (!file.exists()) {
            Bukkit.getLogger().info("Data file does not exist. No inventories to load.");
            return bookshelfInventories;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        for (String locKey : config.getKeys(false)) {
            try {
                Location loc = deserializeLocation(locKey);
                Inventory inv = Bukkit.createInventory(null, 27, "Bookshelf Chest");
                ItemStack[] items = (ItemStack[]) config.get(locKey);
                if (items != null) {
                    inv.setContents(items);
                    bookshelfInventories.put(loc, inv);
                    Bukkit.getLogger().info("Loaded inventory at " + locKey + " with " + items.length + " items.");
                } else {
                    Bukkit.getLogger().warning("No items found for location " + locKey);
                }
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning("Failed to load inventory at location key: " + locKey + ". " + e.getMessage());
            }
        }

        return bookshelfInventories;
    }

    private static String serializeLocation(Location loc) {
        return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ();
    }

    private static Location deserializeLocation(String locStr) {
        String[] parts = locStr.split(",");

        // Validate parts length
        if (parts.length != 4) {
            throw new IllegalArgumentException("Invalid location string format. Expected 4 parts but got: " + parts.length + ". Location string: " + locStr);
        }

        try {
            return new Location(
                    Bukkit.getWorld(parts[0]),
                    Double.parseDouble(parts[1]),
                    Double.parseDouble(parts[2]),
                    Double.parseDouble(parts[3])
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number format in location data: " + locStr, e);
        }
    }
}