package me.saberslay.bookshelfchest;

import me.saberslay.bookshelfchest.Manegers.EventManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Bookshelfchest extends JavaPlugin {

    private EventManager eventManager;

    @Override
    public void onEnable() {
        // Ensure the data folder exists
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // Initialize the EventManager with the data file
        File dataFile = new File(getDataFolder(), "bookshelf_inventories.yml");
        eventManager = new EventManager(dataFile);

        // Register the event manager as a listener
        getServer().getPluginManager().registerEvents(eventManager, this);

        getLogger().info("BookshelfChest has been enabled");
    }

    @Override
    public void onDisable() {
        // Save the inventories when the plugin is disabled
        eventManager.saveInventories();
        getLogger().info("BookshelfChest has been disabled");
    }
}
