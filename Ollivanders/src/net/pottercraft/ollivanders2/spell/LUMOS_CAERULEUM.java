package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

/**
 * Captures magical blue flames in a glass bottle, creating a portable light source.
 * <p>
 * This spell transforms a glass bottle held in the player's off-hand into a soul fire lantern.
 *
 * @author Azami7
 * @see <a href="https://harrypotter.fandom.com/wiki/Bluebell_Flames">https://harrypotter.fandom.com/wiki/Bluebell_Flames</a>
 * @since 2.21.4
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

        initSpell();
    }

    /**
     * Transform a glass bottle into a soul fire lantern
     * Override checkEffect to prevent projectile creation
     */
    @Override
    public void checkEffect() {
        if (!isSpellAllowed()) {
            kill();
            return;
        }
        
        ItemStack offHandItem = player.getInventory().getItemInOffHand();
        
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
            
            // Replace the glass bottle with the lantern
            player.getInventory().setItemInOffHand(lantern);
            
            // Send success message
            player.sendMessage(Ollivanders2.chatColor + "You capture bluebell flames in the bottle.");
        }
        // No failure message - spell simply does nothing if requirements aren't met
        
        // Kill the spell immediately after effect
        kill();
    }
    
    /**
     * Nothing to do since we overrode checkEffect() itself
     */
    @Override
    protected void doCheckEffect() {
        // Not used since we override checkEffect
    }
}