package com.outlaw;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class OutlawCoreListener implements Listener {

    private final HashMap<UUID, Long> lassoCooldown = new HashMap<>();

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        OutlawSMP.getInstance().updatePlayerMaxHealth(event.getPlayer());
    }

    @EventHandler
    public void onLassoUse(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player target)) return;
        Player outlaw = event.getPlayer();
        ItemStack item = outlaw.getInventory().getItem(event.getHand());

        if (item == null || item.getType() != Material.LEAD || !item.hasItemMeta()) return;

        String itemType = item.getItemMeta().getPersistentDataContainer().get(
                OutlawSMP.getInstance().getItemKey(), PersistentDataType.STRING
        );

        if (!"lasso".equals(itemType)) return;
        event.setCancelled(true);

        long now = System.currentTimeMillis();
        if (now - lassoCooldown.getOrDefault(outlaw.getUniqueId(), 0L) < 30000) {
            outlaw.sendMessage(Component.text("Lasso is on cooldown!", NamedTextColor.RED));
            return;
        }

        lassoCooldown.put(outlaw.getUniqueId(), now);
        Vector pull = outlaw.getLocation().toVector().subtract(target.getLocation().toVector()).normalize().multiply(1.4).setY(0.6);
        target.setVelocity(pull);

        outlaw.sendMessage(Component.text("Target caught!", NamedTextColor.GOLD));
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer == null) return;

        // Process Bounty System Mechanics
        double bounty = OutlawSMP.getInstance().getBounties().getOrDefault(victim.getUniqueId(), 0.0);
        if (bounty > 0) {
            OutlawSMP.getInstance().getBounties().remove(victim.getUniqueId());
            killer.sendMessage(Component.text("Claimed bounty of $" + bounty + " from killing " + victim.getName() + "!", NamedTextColor.GREEN));
            Bukkit.broadcast(Component.text(killer.getName() + " collected a bounty of $" + bounty + " placed on " + victim.getName() + "!", NamedTextColor.GOLD));
        }
    }
}
