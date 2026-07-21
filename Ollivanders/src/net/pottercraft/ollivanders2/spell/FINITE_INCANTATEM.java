package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.Collection;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.MagicLevel;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import net.pottercraft.ollivanders2.potion.O2Potions;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

/**
 * Reduces any spell or potion effects on an item or player.
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/General_Counter-Spell">General Counter-Spell</a>
 */
public final class FINITE_INCANTATEM extends O2Spell {
    /**
     * The maximum number of effects that can be targeted at the same time
     */
    public final static int maxEffects = 10;

    /**
     * The number of effects that can be targeted by this spell.
     */
    private int effectsRemaining;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public FINITE_INCANTATEM(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.FINITE_INCANTATEM;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("\"He pointed his wand at the rampart, cried, \"Finite!\" and it steadied.\"");
            add("\"Try Finite Incantatem, that should stop the rain if it’s a hex or curse.\"  -Hermione Granger");
            add("\"Stop! Stop!\" screamed Lockhart, but Snape took charge. \"Finite Incantatum!\" he shouted; Harry's feet stopped dancing, Malfoy stopped laughing, and they were able to look up.");
            add("The General Counter-Spell");
        }};

        text = "Reduces spell and potion effects on an item or player. Is not effective against more powerful curses and enchantments.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public FINITE_INCANTATEM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);
        spellType = O2SpellType.FINITE_INCANTATEM;
        branch = O2MagicBranch.CHARMS;

        initSpell();
    }

    /**
     * Set the number of effects this cast can clear from the caster's skill, limited to [1, {@link #maxEffects}].
     */
    @Override
    void doInitSpell() {
        effectsRemaining = (int)Math.floor(usesModifier / 20);
        if (effectsRemaining < 1)
            effectsRemaining = 1;
        else if (effectsRemaining > maxEffects)
            effectsRemaining = maxEffects;
    }

    /**
     * Check players and items in the projectiles path for enchantments and effects
     */
    @Override
    protected void doCheckEffect() {
        // look for players first
        finiteIncantatemPlayers();

        // look for items next
        if (!isKilled())
            finiteIncantatemItems();

        // projectile has stopped, kill the spell
        if (hasHitBlock())
            kill();
    }

    /**
     * Finite Incantatem on entities.
     */
    private void finiteIncantatemPlayers() {
        for (Player target : getNearbyPlayers(defaultRadius)) {
            if (target.getUniqueId().equals(caster.getUniqueId()))
                continue;

            common.printDebugMessage("finite incantatem targeting " + target.getName(), null, null, false);

            // look for any O2Effects on the player
            if (Ollivanders2API.getPlayers().playerEffects.hasEffects(target.getUniqueId())) {
                for (O2EffectType effectType : Ollivanders2API.getPlayers().playerEffects.getEffects(target.getUniqueId())) {
                    if (effectType.getLevel().ordinal() <= spellType.getLevel().ordinal())
                        Ollivanders2API.getPlayers().playerEffects.removeEffect(target.getUniqueId(), effectType);

                    effectsRemaining = effectsRemaining - 1;
                    if (effectsRemaining <= 0)
                        break;
                }
            }

            if (effectsRemaining > 0) {
                // look for any minecraft PotionEffects
                Collection<PotionEffect> potionEffects = target.getActivePotionEffects();

                for (PotionEffect effect : potionEffects) {
                    MagicLevel level = O2Potions.getPotionEffectMagicLevel(effect.getType());

                    if (level.ordinal() <= spellType.getLevel().ordinal())
                        target.removePotionEffect(effect.getType());

                    effectsRemaining = effectsRemaining - 1;
                    if (effectsRemaining <= 0)
                        break;
                }
            }

            kill();
            return;
        }
    }

    /**
     * Finite Incantatem on items. Does not work for item curses.
     */
    private void finiteIncantatemItems() {
        for (Item item : getNearbyItems(defaultRadius)) {
            ItemEnchantmentType enchantmentType = Ollivanders2API.getItems().enchantedItems.getEnchantmentType(item.getItemStack());

            if (enchantmentType == null || enchantmentType.isCursed())
                continue;

            if (enchantmentType.getLevel().ordinal() <= spellType.getLevel().ordinal()) {
                ItemStack disenchantedItemStack = Ollivanders2API.getItems().enchantedItems.removeEnchantment(item);

                // remove the old item
                item.remove();

                // drop the new item in world
                world.dropItem(location, disenchantedItemStack);
            }

            kill();
            return;
        }
    }

    /**
     * @return the number of effects this cast can still clear, decremented as it clears them
     */
    public int getEffectsRemaining() {
        return effectsRemaining;
    }
}