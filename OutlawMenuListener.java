package com.outlaw;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class OutlawMenuListener implements Listener {

    @EventHandler
    public void onMenuClick(InventoryClickEvent event) {
        String title = event.getView().getTitle();
        if (!title.contains("Outlaw Selection Menu")) return;
        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;

        String displayName = item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : "";

        if (displayName.contains("Skill:")) {
            String skill = displayName.split("Skill: ")[1].trim();
            OutlawSMP.getInstance().getActiveSkills().put(player.getUniqueId(), skill);
            player.sendMessage(Component.text("Equipped Skill: " + skill, NamedTextColor.GREEN));
            player.closeInventory();
        } else if (displayName.contains("Perk:")) {
            String perk = displayName.split("Perk: ")[1].trim();
            OutlawSMP.getInstance().getActivePerks().put(player.getUniqueId(), perk);
            OutlawSMP.getInstance().updatePlayerMaxHealth(player);
            player.sendMessage(Component.text("Equipped Perk: " + perk, NamedTextColor.GREEN));
            player.closeInventory();
        }
    }
}
