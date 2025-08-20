package com.yourname.amaterasu.ability;

import com.yourname.amaterasu.AmaterasuPlugin;
import com.yourname.amaterasu.data.PlayerData;
import com.yourname.amaterasu.data.PlayerDataStore;
import com.yourname.amaterasu.items.ItemsManager;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class AbilityManager implements Listener {
    private final AmaterasuPlugin plugin;
    private final PlayerDataStore store;
    private final CooldownManager cds = new CooldownManager();

    public AbilityManager(AmaterasuPlugin plugin, PlayerDataStore store) {
        this.plugin = plugin;
        this.store = store;
    }

    public boolean handleCommand(org.bukkit.command.CommandSender sender, String abilityName) {
        if (!(sender instanceof Player p)) { sender.sendMessage("Players only."); return true; }
        AbilityType type = AbilityType.valueOf(abilityName);
        PlayerData d = store.get(p);

        if (d.selected() != type && !(type==AbilityType.PIERCINGSPIKE && d.selected()==AbilityType.PIERCING_SPIKE)) {
            p.sendMessage(Component.text(ChatColor.RED + "This isn't your selected ability. Use a Reroller to switch."));
            return true;
        }

        if (!cds.ready(p, type)) {
            long ms = cds.remaining(p, type);
            sendActionBar(p, type.display()+" Cooldown: " + (ms/1000+1) + "s", ChatColor.YELLOW);
            return true;
        }

        switch (type) {
            case DRAGON_WRATH -> castDragonWrath(p);
            case DEVILS_FOOTPRINT -> castDevilsFootprint(p);
            default -> {
                p.sendMessage(Component.text(ChatColor.GRAY + "Ability is currently incomplete."));
                cds.set(p, type, 3000);
                startActionBar(p, type, 3000);
            }
        }
        return true;
    }

    private boolean hasEgg(Player p) {
        for (ItemStack it : p.getInventory().getContents()) if (it != null && it.getType()== Material.DRAGON_EGG) return true;
        return false;
    }
    private boolean hasUpgrader(Player p) {
        ItemsManager im = plugin.items();
        for (ItemStack it : p.getInventory().getContents()) if (im.isUpgrader(it)) return true;
        return false;
    }

    private void castDragonWrath(Player p) {
        boolean egg = hasEgg(p);
        boolean up = hasUpgrader(p);
        if (egg && up) { eternalDragonWrath(p); cds.set(p, AbilityType.DRAGON_WRATH, 40_000); startActionBar(p, AbilityType.DRAGON_WRATH, 40_000); }
        else if (up) { corruptedDragonWrath(p); cds.set(p, AbilityType.DRAGON_WRATH, 28_000); startActionBar(p, AbilityType.DRAGON_WRATH, 28_000); }
        else { baseDragonWrath(p); cds.set(p, AbilityType.DRAGON_WRATH, 35_000); startActionBar(p, AbilityType.DRAGON_WRATH, 35_000); }
    }

    private void baseDragonWrath(Player p) {
        p.setVelocity(p.getVelocity().setY(1.0));
        p.getWorld().playSound(p, Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1.6f);
        spiral(p, Particle.FLAME, 20);
        impactLater(p, 30, 3.0, 3.0, true);
    }

    private void corruptedDragonWrath(Player p) {
        p.setVelocity(p.getVelocity().setY(1.2));
        p.getWorld().playSound(p, Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 0.6f);
        spiral(p, Particle.SOUL_FIRE_FLAME, 40);
        impactLater(p, 32, 4.0, 3.0, true);
        cursedBurnAoE(p, 4.0, 60, 1.0);
    }

    private void eternalDragonWrath(Player p) {
        p.setVelocity(p.getVelocity().setY(1.6));
        p.getWorld().playSound(p, Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 0.3f);
        spiral(p, Particle.SCULK_SOUL, 60);
        impactLater(p, 34, 5.0, 4.0, true);
        cursedBurnAoE(p, 5.0, 80, 1.0);
        p.setAbsorptionAmount(Math.min(20.0, p.getAbsorptionAmount()+2.0));
        sendActionBar(p, "Eternal Dragon's Brand applied!", ChatColor.DARK_PURPLE);
    }

    private void castDevilsFootprint(Player p) {
        boolean up = hasUpgrader(p);
        if (!up) {
            dash(p, 1.2);
            p.getWorld().playSound(p, Sound.ITEM_FIRECHARGE_USE, 1f, 1.4f);
            trail(p, Particle.FLAME, 40);
            lineDamage(p, 1.0, 2.0, 30, false);
            cds.set(p, AbilityType.DEVILS_FOOTPRINT, 12_000);
            startActionBar(p, AbilityType.DEVILS_FOOTPRINT, 12_000);
        } else {
            dash(p, 1.4);
            p.getWorld().playSound(p, Sound.ITEM_FIRECHARGE_USE, 1f, 0.6f);
            trail(p, Particle.SOUL_FIRE_FLAME, 60);
            lineDamage(p, 1.5, 2.0, 30, true);
            new BukkitRunnable(){@Override public void run(){
                p.getWorld().spawnParticle(Particle.EXPLOSION, p.getLocation(), 10, .6,.2,.6,.01);
                aoe(p, p.getLocation(), 3.0, 2.0, true);
            }}.runTaskLater(plugin, 40L);
            cds.set(p, AbilityType.DEVILS_FOOTPRINT, 10_000);
            startActionBar(p, AbilityType.DEVILS_FOOTPRINT, 10_000);
        }
    }

    // Utilities
    private void impactLater(Player p, int ticks, double radius, double hearts, boolean ignite) {
        new BukkitRunnable(){@Override public void run(){ aoe(p, p.getLocation(), radius, hearts, ignite);} }.runTaskLater(plugin, ticks);
    }
    private void cursedBurnAoE(Player src, double r, int ticks, double heartsTotal) {
        double perPulse = (heartsTotal*2.0) / (ticks/10.0);
        new BukkitRunnable(){
            int t=0; @Override public void run(){
                t+=10;
                for (Entity e : src.getWorld().getNearbyEntities(src.getLocation(), r,r,r)) {
                    if (e instanceof LivingEntity le && !le.getUniqueId().equals(src.getUniqueId())) {
                        le.damage(perPulse, src);
                        le.getWorld().spawnParticle(Particle.SOUL, le.getLocation().add(0,1,0), 2, .1,.1,.1, 0.0);
                    }
                }
                if (t>=ticks) cancel();
            }
        }.runTaskTimer(plugin, 0L, 10L);
    }
    private void spiral(Player p, Particle pt, int points) {
        for (int i=0;i<points;i++){
            double y = i*0.1, a=i*0.5, x=Math.cos(a), z=Math.sin(a);
            p.getWorld().spawnParticle(pt, p.getLocation().clone().add(x,y,z), 2,0,0,0,0);
        }
    }
    private void dash(Player p, double s) { Vector v = p.getLocation().getDirection().normalize().multiply(s); p.setVelocity(new Vector(v.getX(), 0.1, v.getZ())); }
    private void trail(Player p, Particle pt, int dur){
        new BukkitRunnable(){int t=0;@Override public void run(){t+=2;
            p.getWorld().spawnParticle(pt, p.getLocation(), 4, .3,.05,.3,.01);
            if (t>=dur) cancel();
        }}.runTaskTimer(plugin,0L,2L);
    }
    private void aoe(Player src, org.bukkit.Location loc, double r, double hearts, boolean ignite){
        double dmg = hearts*2.0;
        for (Entity e : loc.getWorld().getNearbyEntities(loc, r,r,r)) {
            if (e instanceof LivingEntity le && !le.getUniqueId().equals(src.getUniqueId())) {
                le.damage(dmg, src);
                if (ignite) le.setFireTicks(30);
            }
        }
    }
    private void lineDamage(Player p, double hearts, double length, int steps, boolean cursed){
        double dmg = hearts*2.0;
        org.bukkit.Location start = p.getLocation();
        org.bukkit.util.Vector dir = start.getDirection().normalize();
        for (int i=1;i<=steps;i++){
            org.bukkit.Location point = start.clone().add(dir.clone().multiply((length/steps)*i));
            for (Entity e : point.getWorld().getNearbyEntities(point, .8,1.0,.8)) {
                if (e instanceof LivingEntity le && !le.getUniqueId().equals(p.getUniqueId())) {
                    le.damage(dmg, p);
                    if (cursed) cursedBurnAoE(p, .1, 30, 0.5); else le.setFireTicks(30);
                }
            }
        }
    }

    // Action bar timer
    private void startActionBar(Player p, AbilityType t, long ms) {
        final long end = System.currentTimeMillis() + ms;
        new BukkitRunnable(){@Override public void run(){
            long left = end - System.currentTimeMillis();
            if (left <= 0) { sendActionBar(p, t.display()+" Ready!", ChatColor.GREEN); cancel(); return; }
            sendActionBar(p, t.display()+" Cooldown: " + ((left/1000)+1) + "s", ChatColor.YELLOW);
        }}.runTaskTimer(plugin, 0L, 20L);
    }
    private void sendActionBar(Player p, String text, ChatColor color) {
        p.sendActionBar(net.kyori.adventure.text.Component.text(color + text));
    }

    @EventHandler public void thorns(EntityDamageByEntityEvent e){ /* placeholder for passives */ }
}
