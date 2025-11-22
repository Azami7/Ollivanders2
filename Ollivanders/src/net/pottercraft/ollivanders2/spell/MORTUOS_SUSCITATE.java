package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Transfigures a rotten flesh into inferi
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Inferius">https://harrypotter.fandom.com/wiki/Inferius</a>
 */
public final class MORTUOS_SUSCITATE extends ItemToEntityTransfiguration {
    /**
     * Keep track of the number of inferi a player has created
     */
    final static HashMap<Player, Integer> inferiCount = new HashMap<>();

    /**
     * The maximum number of inferi any player can create so this doesn't get out of control
     */
    final static int maxInferi = 10;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public MORTUOS_SUSCITATE(Ollivanders2 plugin) {
        super(plugin);

        branch = O2MagicBranch.DARK_ARTS;
        spellType = O2SpellType.MORTUOS_SUSCITATE;

        flavorText = new ArrayList<>() {{
            add("They are corpses, dead bodies that have been bewitched to do a Dark wizard's bidding. Inferi have not been seen for a long time, however, not since Voldemort was last powerful... He killed enough people to make an army of them, of course.");
        }};

        text = "Mortuos Suscitate will transfigure a piece of rotten flesh into an Inferius. The Inferius will not attack it's owner.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public MORTUOS_SUSCITATE(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        branch = O2MagicBranch.DARK_ARTS;
        spellType = O2SpellType.MORTUOS_SUSCITATE;

        permanent = false;
        maxDuration = Ollivanders2Common.ticksPerMinute * 10; // 10 minutes

        transfigurationMap.put(Material.ROTTEN_FLESH, EntityType.ZOMBIE);
        entityCustomName = "Inferi";
        consumeOriginal = true;

        failureMessage = "You are not skilled enough to create the Inferi.";

        initSpell();
    }

    /**
     * Determine whether this player can create more inferi based on their skill level
     */
    @Override
    void doInitSpell() {
        // can the player create more inferi?
        int playerMax = (int) (usesModifier / 10);
        if (playerMax > maxInferi)
            playerMax = maxInferi;
        else if (playerMax < 1)
            playerMax = 1;

        if (inferiCount.containsKey(player)) {
            int curInferi = inferiCount.get(player);
            if (curInferi >= playerMax) {
                // if the number they currently have created exceeds their max based on skill, set success to 0
                successRate = 0;
            }
        }
    }

    /**
     * Reduce the count of inferi for this player by 1
     */
    @Override
    void doRevert() {
        if (isTransfigured) {
            int curInferi = inferiCount.get(player) - 1;
            if (curInferi <= 0)
                inferiCount.remove(player);
            else
                inferiCount.put(player, curInferi);
        }
    }

    /**
     * Prevent the golem from harming its creator and have it attack anyone who does
     *
     * @param event the entity damage event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamageEvent(EntityDamageByEntityEvent event) {
        if (transfiguredEntity == null)
            return;

        Entity attacker = event.getDamager(); // will never be null
        Entity target = event.getEntity(); // will never be null
        EntityDamageEvent.DamageCause cause = event.getCause(); // will never be null

        if (!EntityCommon.attackDamageCauses.contains(cause))
            return;

        if (attacker.getUniqueId().equals(transfiguredEntity.getUniqueId()) && target.getUniqueId().equals(player.getUniqueId()))
            // prevent the inferi attacking its creator
            event.setCancelled(true);
        else if (target.getUniqueId().equals(player.getUniqueId()) && transfiguredEntity instanceof LivingEntity)
            // attack anyone who attacks the creator
            ((LivingEntity) transfiguredEntity).attack(attacker);
    }

    /**
     * Prevent inferi from targeting its creator.
     *
     * @param event the entity target event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityTargetEvent(EntityTargetEvent event) {
        if (transfiguredEntity == null)
            return;

        Entity attacker = event.getEntity(); // will never be null
        Entity target = event.getTarget();

        if (target == null)
            return;

        if (attacker.getUniqueId().equals(transfiguredEntity.getUniqueId()) && target.getUniqueId().equals(player.getUniqueId()))
            event.setCancelled(true);
    }
}
