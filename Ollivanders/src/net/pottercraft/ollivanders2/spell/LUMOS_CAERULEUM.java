package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/**
 * Captures magical blue flames in a glass bottle, creating a portable light source.
 * <p>
 * Lumos Caeruleum is the Minecraft adaptation of the Bluebell Flames charm. When cast while the caster holds a
 * glass bottle in their off-hand, it converts a single bottle into a {@link Material#SOUL_LANTERN soul lantern}
 * renamed "Jar of Bluebell Flames" - a waterproof, portable light source. If the caster is holding a stack of
 * bottles, only one is consumed and the remainder are dropped at the caster's location so they are not lost.
 * </p>
 * <p>
 * This is a {@code noProjectile} charm: it acts immediately on the caster's inventory in {@link #doCheckEffect()}
 * rather than firing a projectile, and does nothing if the off-hand is not holding a glass bottle.
 * </p>
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Bluebell_Flames">Harry Potter Wiki - Bluebell Flames</a>
 */
public final class LUMOS_CAERULEUM extends O2Spell {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public LUMOS_CAERULEUM(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.LUMOS_CAERULEUM;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("Bluebell Flames in a Jar");
            add("\"Hermione had conjured up a portable, waterproof fire that could be scooped into a jam jar and carried around. They were standing next to it, getting warm, when Snape crossed the yard.\"");
        }};

        text = "Captures magical blue flames in a glass bottle, creating a portable light source. " +
                "Hold a glass bottle in your off-hand while casting.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public LUMOS_CAERULEUM(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.LUMOS_CAERULEUM;
        branch = O2MagicBranch.CHARMS;

        noProjectile = true;

        initSpell();
    }
    
    /**
     * Convert a glass bottle in the caster's off-hand into a "Jar of Bluebell Flames" soul lantern.
     * <p>
     * If the caster is holding a glass bottle in their off-hand, a single bottle is replaced with a renamed
     * {@link Material#SOUL_LANTERN} and a success message is sent. When the off-hand holds more than one bottle,
     * only one is consumed and the remaining bottles are dropped at the spell's location so they are not lost.
     * If the off-hand is not holding a glass bottle, the spell does nothing. The spell is killed once the effect
     * resolves, whether or not a bottle was converted.
     * </p>
     */
    @Override
    protected void doCheckEffect() {
        if (!isSpellAllowed()) {
            kill();
            return;
        }

        ItemStack offHandItem = caster.getInventory().getItemInOffHand();

        // Check if the player is holding a glass bottle in their off-hand
        if (offHandItem.getType() == Material.GLASS_BOTTLE) {
            // Create a soul fire lantern
            ItemStack lantern = new ItemStack(Material.SOUL_LANTERN, 1);
            ItemMeta meta = lantern.getItemMeta();

            if (meta != null) {
                meta.setDisplayName("§9Jar of Bluebell Flames");
                meta.setLore(new ArrayList<>() {{
                    add("§7A jar containing magical blue flames");
                    add("§7that provide light and warmth without burning.");
                }});
                lantern.setItemMeta(meta);
            }

            if (offHandItem.getAmount() > 1) { // handle if the player was holding more than 1 bottle
                int newAmount = offHandItem.getAmount() - 1;
                offHandItem.setAmount(newAmount);
                world.dropItemNaturally(location, offHandItem);
            }

            // Replace the glass bottle with the lantern
            caster.getInventory().setItemInOffHand(lantern);

            // Send success message
            caster.sendMessage(Ollivanders2.chatColor + "You capture bluebell flames in the bottle.");
        }
        // No failure message - spell simply does nothing if requirements aren't met

        // Kill the spell immediately after effect
        kill();
    }
}