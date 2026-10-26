package com.outlaw;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OutlawCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length == 0 || "menu".equalsIgnoreCase(args[0])) {
            openOutlawMenu(player);
            return true;
        }

        if ("give".equalsIgnoreCase(args[0]) && player.hasPermission("outlaw.admin")) {
            ItemStack lasso = new ItemStack(Material.LEAD);
            ItemMeta meta = lasso.getItemMeta();
            meta.setDisplayName("§6Outlaw's Lasso");
            meta.getPersistentDataContainer().set(OutlawSMP.getInstance().getItemKey(), PersistentDataType.STRING, "lasso");
            lasso.setItemMeta(meta);
            player.getInventory().addItem(lasso);
            player.sendMessage(Component.text("Given Outlaw Lasso", NamedTextColor.GREEN));
            return true;
        }

        if ("bounty".equalsIgnoreCase(args[0]) && args.length >= 3) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) return true;
            try {
                double amount = Double.parseDouble(args[2]);
                OutlawSMP.getInstance().getBounties().put(target.getUniqueId(), amount);
                Bukkit.broadcast(Component.text("A bounty of $" + amount + " has been placed on " + target.getName() + "!", NamedTextColor.RED));
            } catch (NumberFormatException ignored) {}
            return true;
        }

        return true;
    }

    private void openOutlawMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§0Outlaw Selection Menu");
        
        String[] skills = {"Mirage", "Degenerate", "Swift", "Fatigue", "Tank", "Venom", "Inferno", "Shockwave", "Frostbite", "Breach", "Lifestealer", "Hypnosis"};
        int i = 0;
        for (String skill : skills) {
            ItemStack is = new ItemStack(Material.ENCHANTED_BOOK);
            ItemMeta im = is.getItemMeta();
            im.setDisplayName("§bSkill: " + skill);
            is.setItemMeta(im);
            inv.setItem(i++, is);
        }

        String[] perks = {"Toughness", "Quickdraw", "Counter"};
        int pSlot = 18;
        for (String perk : perks) {
            ItemStack is = new ItemStack(Material.GOLDEN_APPLE);
            ItemMeta im = is.getItemMeta();
            im.setDisplayName("§ePerk: " + perk);
            is.setItemMeta(im);
            inv.setItem(pSlot++, is);
        }

        player.openInventory(inv);
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) return List.of("menu", "give", "bounty");
        return new ArrayList<>();
    }
}
