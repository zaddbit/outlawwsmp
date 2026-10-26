package com.outlaw;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class OutlawSMP extends JavaPlugin {

    private static OutlawSMP instance;
    private NamespacedKey skillKey;
    private NamespacedKey perkKey;
    private NamespacedKey itemKey;

    // Runtime Storage
    private final HashMap<UUID, String> activeSkills = new HashMap<>();
    private final HashMap<UUID, String> activePerks = new HashMap<>();
    private final HashMap<UUID, Double> bounties = new HashMap<>();
    private final HashMap<UUID, Long> globalCooldowns = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        this.skillKey = new NamespacedKey(this, "active_skill");
        this.perkKey = new NamespacedKey(this, "active_perk");
        this.itemKey = new NamespacedKey(this, "outlaw_item");

        getLogger().info("Loading complete OutlawSMP framework with all 12 Skills, 3 Perks, and Bounties...");

        // Register All Listeners
        getServer().getPluginManager().registerEvents(new OutlawMenuListener(), this);
        getServer().getPluginManager().registerEvents(new OutlawSkillsListener(), this);
        getServer().getPluginManager().registerEvents(new OutlawPerksListener(), this);
        getServer().getPluginManager().registerEvents(new OutlawCoreListener(), this);

        // Register Master Command
        if (this.getCommand("outlaw") != null) {
            OutlawCommand cmd = new OutlawCommand();
            this.getCommand("outlaw").setExecutor(cmd);
            this.getCommand("outlaw").setTabCompleter(cmd);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("OutlawSMP framework safely disabled.");
    }

    public static OutlawSMP getInstance() { return instance; }
    public NamespacedKey getSkillKey() { return skillKey; }
    public NamespacedKey getPerkKey() { return perkKey; }
    public NamespacedKey getItemKey() { return itemKey; }
    
    public HashMap<UUID, String> getActiveSkills() { return activeSkills; }
    public HashMap<UUID, String> getActivePerks() { return activePerks; }
    public HashMap<UUID, Double> getBounties() { return bounties; }
    public HashMap<UUID, Long> getGlobalCooldowns() { return globalCooldowns; }

    public void updatePlayerMaxHealth(Player player) {
        var attr = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attr == null) return;
        double base = 20.0;
        if ("Toughness".equalsIgnoreCase(activePerks.get(player.getUniqueId()))) {
            base += 4.0; // Toughness perk gives 2 bonus hearts
        }
        attr.setBaseValue(base);
    }
}
