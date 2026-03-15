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
 * Repletus — the Refilling Charm that replenishes empty containers.
 *
 * <p>REPLETUS fills empty water bottles and buckets held in the caster's off-hand.
 * The spell converts glass bottles to water potions and buckets to water buckets.
 * The refilling is instant with no projectile, and a splash sound is played when the
 * spell successfully fills a container.</p>
 *
 * <p>Supported items:</p>
 * <ul>
 * <li><strong>Glass Bottle:</strong> Converts to water potion (same amount)</li>
 * <li><strong>Bucket:</strong> Converts to water bucket (same amount)</li>
 * </ul>
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
     * Instantly refills containers held in the caster's off hand.
     *
     * <p>Checks the item in the caster's off hand and converts it if it's a supported container:</p>
     * <ul>
     * <li>Glass bottles → water potions (with the same stack size)</li>
     * <li>Buckets → water buckets (with the same stack size)</li>
     * </ul>
     *
     * <p>If a supported item is converted, a splash sound is played at the spell location.
     * If the item is not a supported container, the spell has no effect.
     * The spell always kills itself immediately after attempting to fill.</p>
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
