package com.yourname.amaterasu.items;

import com.yourname.amaterasu.AmaterasuPlugin;
import com.yourname.amaterasu.data.PlayerDataStore;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class ItemsManager implements Listener {
    private final AmaterasuPlugin plugin;
    private final PlayerDataStore store;
    public final NamespacedKey KEY_REROLLER;
    public final NamespacedKey KEY_UPGRADER;

    public ItemsManager(AmaterasuPlugin plugin, PlayerDataStore store) {
        this.plugin = plugin;
        this.store = store;
        KEY_REROLLER = new NamespacedKey(plugin, "reroller");
        KEY_UPGRADER = new NamespacedKey(plugin, "upgrader");
        registerRecipes();
    }

    public ItemStack makeReroller() {
        ItemStack it = new ItemStack(Material.ECHO_SHARD);
        ItemMeta meta = it.getItemMeta();
        meta.displayName(Component.text(ChatColor.AQUA + "Reroller"));
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        meta.getPersistentDataContainer().set(KEY_REROLLER, PersistentDataType.BYTE, (byte)1);
        it.setItemMeta(meta);
        return it;
    }

    public ItemStack makeUpgrader() {
        ItemStack it = new ItemStack(Material.HEART_OF_THE_SEA);
        ItemMeta meta = it.getItemMeta();
        meta.displayName(Component.text(ChatColor.LIGHT_PURPLE + "Upgrader"));
        meta.addEnchant(Enchantment.UNBREAKING, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        meta.getPersistentDataContainer().set(KEY_UPGRADER, PersistentDataType.BYTE, (byte)1);
        it.setItemMeta(meta);
        return it;
    }

    private void registerRecipes() {
        // Reroller recipe: Wither skull center + surrounded by Netherite Ingots
        ShapedRecipe reroller = new ShapedRecipe(new NamespacedKey(plugin, "reroller"), makeReroller());
        reroller.shape("NNN","NSN","NNN");
        reroller.setIngredient('N', Material.NETHERITE_INGOT);
        reroller.setIngredient('S', Material.WITHER_SKELETON_SKULL);
        Bukkit.addRecipe(reroller);

        // Upgrader: corners = diamonds, edges = dragon's breath, center = sculk catalyst
        ShapedRecipe upgrader = new ShapedRecipe(new NamespacedKey(plugin, "upgrader"), makeUpgrader());
        upgrader.shape("DBD","BCB","DBD");
        upgrader.setIngredient('D', Material.DIAMOND);
        upgrader.setIngredient('B', Material.DRAGON_BREATH);
        upgrader.setIngredient('C', Material.SCULK_CATALYST);
        Bukkit.addRecipe(upgrader);
    }

    // Droppable but invincible & no despawn
    @EventHandler
    public void onItemDespawn(ItemDespawnEvent e) {
        ItemStack st = e.getEntity().getItemStack();
        if (isSpecial(st)) e.setCancelled(true);
    }

    @EventHandler
    public void onItemDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Item item) {
            ItemStack st = item.getItemStack();
            if (isSpecial(st)) e.setCancelled(true);
        }
    }

    public boolean isUpgrader(ItemStack it) {
        return it != null && it.hasItemMeta() &&
                it.getItemMeta().getPersistentDataContainer().has(KEY_UPGRADER, PersistentDataType.BYTE);
    }
    public boolean isReroller(ItemStack it) {
        return it != null && it.hasItemMeta() &&
                it.getItemMeta().getPersistentDataContainer().has(KEY_REROLLER, PersistentDataType.BYTE);
    }
    private boolean isSpecial(ItemStack it) { return isReroller(it) || isUpgrader(it); }
}
