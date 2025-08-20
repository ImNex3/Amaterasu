package com.yourname.amaterasu;

import com.yourname.amaterasu.ability.AbilityManager;
import com.yourname.amaterasu.data.PlayerDataStore;
import com.yourname.amaterasu.items.ItemsManager;
import com.yourname.amaterasu.listeners.InventoryWatcher;
import com.yourname.amaterasu.listeners.JoinListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AmaterasuPlugin extends JavaPlugin {
    private static AmaterasuPlugin inst;
    private PlayerDataStore dataStore;
    private ItemsManager items;
    private AbilityManager abilities;

    public static AmaterasuPlugin get() { return inst; }
    public PlayerDataStore data() { return dataStore; }
    public ItemsManager items() { return items; }
    public AbilityManager abilities() { return abilities; }

    @Override public void onEnable() {
        inst = this;
        saveDefaultConfig();
        dataStore = new PlayerDataStore(this);
        items = new ItemsManager(this, dataStore);
        abilities = new AbilityManager(this, dataStore);

        getCommand("devilsfootprint").setExecutor((s,c,l,a)->abilities.handleCommand(s,"DEVILS_FOOTPRINT"));
        getCommand("plasmablessing").setExecutor((s,c,l,a)->abilities.handleCommand(s,"PLASMA_BLESSING"));
        getCommand("dragonwrath").setExecutor((s,c,l,a)->abilities.handleCommand(s,"DRAGON_WRATH"));
        getCommand("hellflame").setExecutor((s,c,l,a)->abilities.handleCommand(s,"HELLFLAME"));
        getCommand("flarerebirth").setExecutor((s,c,l,a)->abilities.handleCommand(s,"FLARE_REBIRTH"));
        getCommand("piercingspike").setExecutor((s,c,l,a)->abilities.handleCommand(s,"PIERCING_SPIKE"));

        Bukkit.getPluginManager().registerEvents(items, this);
        Bukkit.getPluginManager().registerEvents(new InventoryWatcher(this, dataStore, items), this);
        Bukkit.getPluginManager().registerEvents(new JoinListener(this, dataStore), this);
        getLogger().info("Amaterasu enabled.");
    }

    @Override public void onDisable() {
        dataStore.saveAll();
    }
}
