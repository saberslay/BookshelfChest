package me.saberslay.bookshelfchest.Manegers;

import me.saberslay.bookshelfchest.Util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;

public class EventManager implements Listener {

    private final HashMap<Location, Inventory> bookshelfInventories = new HashMap<>();
    private final File dataFile;

    public EventManager(File dataFile) {
        this.dataFile = dataFile;
        loadInventories();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inv = event.getInventory();
        if (event.getView().getTitle().equals("Bookshelf Chest")) {
            if (event.getCurrentItem() != null &&
                    (event.getCurrentItem().getType() != Material.BOOK &&
                            event.getCurrentItem().getType() != Material.ENCHANTED_BOOK &&
                            event.getCurrentItem().getType() != Material.WRITABLE_BOOK &&
                            event.getCurrentItem().getType() != Material.WRITTEN_BOOK &&
                            event.getCurrentItem().getType() != Material.KNOWLEDGE_BOOK)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Check if the interaction is a right-click action
        if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                (event.getHand() == EquipmentSlot.HAND) &&
                (event.getClickedBlock() != null) &&
                (event.getClickedBlock().getType() == Material.BOOKSHELF)) {
            event.setCancelled(true);  // Cancel the default interaction

            Player player = event.getPlayer();
            Block bookshelf = event.getClickedBlock();
            Location loc = bookshelf.getLocation();
            Inventory gui = bookshelfInventories.computeIfAbsent(loc, k -> createBookshelfChestGUI());
            player.openInventory(gui);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType() == Material.BOOKSHELF) {
            Location loc = block.getLocation();
            Inventory inv = bookshelfInventories.get(loc);
            if (inv != null) {
                for (ItemStack item : inv.getContents()) {
                    if (item != null) {
                        block.getWorld().dropItemNaturally(block.getLocation(), item);
                    }
                }
                bookshelfInventories.remove(loc);
            }
        }
    }

    private Inventory createBookshelfChestGUI() {
        Inventory inv = Bukkit.createInventory(null, 27, "Bookshelf Chest");
        // Add default items or setup here if needed
        return inv;
    }

    public void saveInventories() {
        InventoryUtils.saveBookshelfInventories(bookshelfInventories, dataFile);
    }

    private void loadInventories() {
        bookshelfInventories.putAll(InventoryUtils.loadBookshelfInventories(dataFile));
    }
}
