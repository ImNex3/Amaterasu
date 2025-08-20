package com.yourname.amaterasu.listeners;

import com.yourname.amaterasu.AmaterasuPlugin;
import com.yourname.amaterasu.data.PlayerData;
import com.yourname.amaterasu.data.PlayerDataStore;
import com.yourname.amaterasu.items.ItemsManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryWatcher implements Listener {
    private final AmaterasuPlugin plugin;
    private final PlayerDataStore store;
    private final ItemsManager items;

    public InventoryWatcher(AmaterasuPlugin plugin, PlayerDataStore store, ItemsManager items) {
        this.plugin = plugin;
        this.store = store;
        this.items = items;
    }

    private boolean hasUpgrader(Player p) {
        for (ItemStack it : p.getInventory().getContents()) if (items.isUpgrader(it)) return true;
        return false;
    }

    private void check(Player p) {
        boolean upgrader = hasUpgrader(p);
        boolean prev = p.getPersistentDataContainer().getOrDefault(
                new org.bukkit.NamespacedKey(plugin, "hasUpgrader"), org.bukkit.persistence.PersistentDataType.BYTE, (byte)0) == (byte)1;
        if (upgrader && !prev) {
            p.getPersistentDataContainer().set(new org.bukkit.NamespacedKey(plugin, "hasUpgrader"),
                    org.bukkit.persistence.PersistentDataType.BYTE, (byte)1);
            p.sendMessage("§5You feel the corrupted course through your veins... your ability has reached a new height.");
        } else if (!upgrader && prev) {
            p.getPersistentDataContainer().set(new org.bukkit.NamespacedKey(plugin, "hasUpgrader"),
                    org.bukkit.persistence.PersistentDataType.BYTE, (byte)0);
            p.sendMessage("§7You have downgraded back to your ability’s weakened state.");
        }
    }

    // Triggers that can change inventory
    @EventHandler public void invClick(InventoryClickEvent e){ if (e.getWhoClicked() instanceof Player p) Bukkit.getScheduler().runTask(plugin, ()->check(p)); }
    @EventHandler public void drop(PlayerDropItemEvent e){ Bukkit.getScheduler().runTask(plugin, ()->check(e.getPlayer())); }
    @EventHandler public void pickup(PlayerPickupItemEvent e){ Bukkit.getScheduler().runTask(plugin, ()->check(e.getPlayer())); }
    @EventHandler public void death(PlayerDeathEvent e){ if (e.getEntity()!=null) Bukkit.getScheduler().runTask(plugin, ()->check(e.getEntity())); }
    @EventHandler public void join(PlayerJoinEvent e){ Bukkit.getScheduler().runTask(plugin, ()->check(e.getPlayer())); }
    @EventHandler public void swap(PlayerSwapHandItemsEvent e){ Bukkit.getScheduler().runTask(plugin, ()->check(e.getPlayer())); }
    @EventHandler public void sneak(PlayerToggleSneakEvent e){ Bukkit.getScheduler().runTask(plugin, ()->check(e.getPlayer())); }
}
