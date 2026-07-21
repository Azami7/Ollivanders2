package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Refilling Charm — refills an empty container held in the caster's off hand: a glass bottle becomes a water potion
 * and a bucket becomes a water bucket. Cast directly with no projectile.
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Refilling_Charm">Refilling Charm on Harry Potter Wiki</a>
 */
public class REPLETUS extends O2Spell {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public REPLETUS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.REPLETUS;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("\"...he pointed his wand under the table at the emptying bottles and they immediately began to refill.\"");
            add("The Refilling Charm");
        }};

        text = "Repletus can be used to refill water bottles and buckets the caster holds in their off hand.";
    }

    /**
     * Constructor for casting the spell.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using (correctness factor)
     */
    public REPLETUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.REPLETUS;
        branch = O2MagicBranch.CHARMS;

        noProjectile = true;

        initSpell();
    }

    /**
     * Refill a supported container held in the caster's off hand, then end the spell.
     * <p>
     * A glass bottle becomes a water potion and a bucket becomes a water bucket, keeping the stack size, and a fill
     * sound is played. Any other off-hand item is left unchanged.
     * </p>
     */
    @Override
    protected void doCheckEffect() {
        ItemStack itemStack = caster.getInventory().getItemInOffHand();

        ItemStack filled = null;

        if (itemStack.getType() == Material.GLASS_BOTTLE) {
            filled = new ItemStack(Material.POTION, itemStack.getAmount());
            PotionMeta meta = (PotionMeta) filled.getItemMeta();
            if (meta != null) {
                meta.setBasePotionType(PotionType.WATER);
                filled.setItemMeta(meta);
            }
        }
        else if (itemStack.getType() == Material.BUCKET) {
            filled = new ItemStack(Material.WATER_BUCKET, itemStack.getAmount());
        }

        if (filled != null) {
            caster.getInventory().setItemInOffHand(filled);
            world.playSound(location, Sound.ITEM_BOTTLE_FILL, 1, 0);
        }

        kill();
    }
}
