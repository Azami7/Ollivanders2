package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Base class for the Reparo family of repair charms, which restore durability to a damaged item the caster aims at.
 * <p>
 * This class holds the shared projectile and repair logic; concrete subclasses ({@link REPARO}, {@link DIAMAS_REPARO})
 * set their own {@code spellType}, {@code branch}, and per-spell repair bounds, then call {@code initSpell()} from their
 * casting constructor. The amount of durability restored is computed at effect time by {@link #calculateRepair()} from
 * the caster's skill ({@code usesModifier}) scaled by {@link #repairMultiplier} and clamped to
 * [{@link #minRepair}, {@link #maxRepair}].
 * </p>
 * <p>
 * Computing the repair amount lazily in {@link #doCheckEffect()} rather than during construction lets a subclass
 * override the repair fields after {@code super(...)} runs but before any repair is applied, so each spell repairs by
 * its own bounds regardless of constructor ordering.
 * </p>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Mending_Charm">Harry Potter Wiki - Mending Charm</a>
 */
public abstract class ReparoBase extends O2Spell {
    /**
     * The minimum durability this spell will repair, regardless of caster skill.
     */
    int minRepair = 30; // half the durability of a wooden sword

    /**
     * The maximum durability this spell will repair.
     */
    int maxRepair = 251; // durability of an iron sword

    /**
     * The multiplier applied to the caster's {@code usesModifier} to determine the level of repair.
     */
    float repairMultiplier = 0.5f;

    /**
     * How much durability to repair on the target item, set by {@link #calculateRepair()}.
     */
    int repair;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public ReparoBase(Ollivanders2 plugin) {
        super(plugin);
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public ReparoBase(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        // world guard flags
        if (Ollivanders2.worldGuardEnabled) {
            worldGuardFlags.add(Flags.ITEM_DROP);
            worldGuardFlags.add(Flags.ITEM_PICKUP);
        }
    }

    /**
     * Calculate the durability to repair from the caster's skill.
     * <p>
     * Scales the caster's {@code usesModifier} by {@link #repairMultiplier} and clamps the result to
     * [{@link #minRepair}, {@link #maxRepair}]. Called at effect time so subclass overrides of the repair
     * fields are already in place.
     * </p>
     */
    void calculateRepair() {
        repair = (int) (usesModifier * repairMultiplier);

        if (repair < minRepair)
            repair = minRepair;
        else if (repair > maxRepair)
            repair = maxRepair;
    }

    /**
     * Find a nearby damaged item and restore a portion of its durability.
     * <p>
     * If the projectile has hit a block, the spell is killed. The projectile's surroundings are searched for dropped
     * items, and the first one whose meta is {@link Damageable} and that actually has damage has {@link #repair}
     * durability points restored. Undamaged items are skipped so the spell is not consumed on a pristine item. Once an
     * item is repaired, a success message is sent and the spell is killed.
     * </p>
     */
    @Override
    protected void doCheckEffect() {
        if (hasHitBlock())
            kill();

        List<Item> items = getNearbyItems(defaultRadius);

        for (Item item : items) {
            ItemStack stack = item.getItemStack();
            ItemMeta itemMeta = stack.getItemMeta();

            if (itemMeta instanceof Damageable) {
                int damage = ((Damageable) itemMeta).getDamage();

                if (damage == 0)
                    continue;

                calculateRepair();
                damage = damage - repair;

                ((Damageable) itemMeta).setDamage(damage);
                stack.setItemMeta(itemMeta);
                item.setItemStack(stack);

                String itemName = itemMeta.getDisplayName();
                if (itemName.isEmpty())
                    itemName = stack.getType().name();
                successMessage = itemName + " looks newer than before.";
                sendSuccessMessage();

                kill();
                break;
            }
        }
    }

    /**
     * Get the minimum durability this spell will repair.
     *
     * @return the minimum repair amount
     */
    public int getMinRepair() {
        return minRepair;
    }

    /**
     * Get the maximum durability this spell will repair.
     *
     * @return the maximum repair amount
     */
    public int getMaxRepair() {
        return maxRepair;
    }

    /**
     * Get the durability repaired on the last target, as computed by {@link #calculateRepair()}.
     *
     * @return the repair amount for this cast, or 0 if no item has been repaired yet
     */
    public int getRepair() {
        return repair;
    }
}
