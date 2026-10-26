package com.outlaw;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Random;

public class OutlawSkillsListener implements Listener {

    private final Random random = new Random();

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;

        Player attacker = null;
        if (event.getDamager() instanceof Player p) {
            attacker = p;
        } else if (event.getDamager() instanceof Arrow arrow && arrow.getShooter() instanceof Player p) {
            attacker = p;
        }

        long now = System.currentTimeMillis();

        // Victim Defense Skills
        String victimSkill = OutlawSMP.getInstance().getActiveSkills().get(victim.getUniqueId());
        if (victimSkill != null) {
            switch (victimSkill) {
                case "Mirage":
                    if (random.nextInt(100) < 15) {
                        event.setCancelled(true);
                        victim.sendMessage(Component.text("Dodged! Mirage activated.", NamedTextColor.GREEN));
                        if (attacker != null) attacker.sendMessage(Component.text("Your attack was dodged by Mirage!", NamedTextColor.RED));
                        return;
                    }
                    break;
                case "Tank":
                    event.setDamage(event.getDamage() * 0.85);
                    break;
                case "Hypnosis":
                    if (attacker != null && random.nextInt(100) < 10) {
                        attacker.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 1));
                        attacker.sendMessage(Component.text("You are disoriented by Hypnosis!", NamedTextColor.DARK_PURPLE));
                    }
                    break;
            }
        }

        if (attacker == null) return;
        String attackerSkill = OutlawSMP.getInstance().getActiveSkills().get(attacker.getUniqueId());
        if (attackerSkill == null) return;

        // Attacker Offense Skills
        switch (attackerSkill) {
            case "Inferno":
                victim.setFireTicks(Math.max(victim.getFireTicks(), 80));
                attacker.sendMessage(Component.text("Inferno ignited your target!", NamedTextColor.RED));
                break;
            case "Venom":
                victim.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 1));
                break;
            case "Frostbite":
                victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 2));
                victim.sendMessage(Component.text("You feel frozen by Frostbite!", NamedTextColor.BLUE));
                break;
            case "Fatigue":
                victim.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 100, 1));
                break;
            case "Degenerate":
                if (random.nextInt(100) < 20) {
                    double currentDmg = event.getDamage();
                    event.setDamage(currentDmg * 1.5);
                    attacker.sendMessage(Component.text("Degenerate Critical Strike!", NamedTextColor.DARK_RED));
                }
                break;
            case "Lifestealer":
                double healAmount = event.getFinalDamage() * 0.15;
                attacker.setHealth(Math.min(attacker.getMaxHealth(), attacker.getHealth() + healAmount));
                break;
            case "Breach":
                if (victim.isBlocking()) {
                    event.setDamage(event.getDamage() * 1.3);
                    victim.sendMessage(Component.text("Your guard was breached!", NamedTextColor.RED));
                }
                break;
            case "Wager":
                if (random.nextInt(100) < 50) {
                    event.setDamage(event.getDamage() * 1.4);
                } else {
                    event.setDamage(event.getDamage() * 0.7);
                }
                break;
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        String skill = OutlawSMP.getInstance().getActiveSkills().get(player.getUniqueId());
        if (!"Swift".equals(skill) && !"Shockwave".equals(skill)) return;
        if (!event.getAction().name().contains("RIGHT_CLICK")) return;
        if (player.getInventory().getItemInMainHand().getType() != Material.AIR) return;

        long now = System.currentTimeMillis();
        String cooldownKey = player.getUniqueId() + "_" + skill;
        long lastUse = OutlawSMP.getInstance().getGlobalCooldowns().getOrDefault(cooldownKey, 0L);

        if (now - lastUse < 20000) {
            player.sendMessage(Component.text("Skill cooldown active!", NamedTextColor.RED));
            return;
        }

        OutlawSMP.getInstance().getGlobalCooldowns().put(cooldownKey, now);

        if ("Swift".equals(skill)) {
            Vector dir = player.getLocation().getDirection().normalize().multiply(1.8).setY(0.4);
            player.setVelocity(dir);
            player.playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1.0f, 1.2f);
            player.sendMessage(Component.text("Swift dash activated!", NamedTextColor.AQUA));
        } else if ("Shockwave".equals(skill)) {
            player.getNearbyEntities(5, 3, 5).forEach(entity -> {
                if (entity instanceof Player target && !target.equals(player)) {
                    Vector push = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(1.5).setY(0.6);
                    target.setVelocity(push);
                    target.sendMessage(Component.text("Knocked back by a Shockwave!", NamedTextColor.RED));
                }
            });
            player.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 1.5f);
            player.sendMessage(Component.text("Shockwave erupted!", NamedTextColor.YELLOW));
        }
    }
}
