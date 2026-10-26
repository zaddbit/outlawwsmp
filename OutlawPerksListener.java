package com.outlaw;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Random;

public class OutlawPerksListener implements Listener {

    private final Random random = new Random();

    @EventHandler
    public void onPerkCombat(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player attacker) {
            String perk = OutlawSMP.getInstance().getActivePerks().get(attacker.getUniqueId());
            if ("Quickdraw".equals(perk)) {
                // Increases base weapon hit speeds or gives minimal punch damage additions
                event.setDamage(event.getDamage() + 0.75);
            }
        }

        if (event.getEntity() instanceof Player victim) {
            String perk = OutlawSMP.getInstance().getActivePerks().get(victim.getUniqueId());
            if ("Counter".equals(perk) && event.getDamager() instanceof Player attacker) {
                if (random.nextInt(100) < 12) {
                    attacker.damage(event.getDamage() * 0.3, victim);
                }
            }
        }
    }
}
