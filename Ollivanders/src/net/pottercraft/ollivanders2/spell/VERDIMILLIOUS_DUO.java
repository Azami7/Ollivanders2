package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

/**
 * The enhanced Verdimillious Duo spell that damages entities and reveals cursed items.
 *
 * <p>VERDIMILLIOUS_DUO is a more powerful variant of VERDIMILLIOUS that combines damage-dealing
 * with curse detection. When cast, it emits green sparks that:</p>
 * <ul>
 * <li>Deal damage to nearby entities (damageModifier = 0.0625, radius = 2 blocks)</li>
 * <li>Detect cursed items within range and make them glow for 60 seconds</li>
 * </ul>
 *
 * <p>The spell searches for cursed items using skill-based detection and schedules a task
 * to turn off the glow effect after the duration expires.</p>
 *
 * @author Azami7
 * @see VERDIMILLIOUS for the basic variant
 */
public class VERDIMILLIOUS_DUO extends Sparks {
    /**
     * Duration in ticks that a revealed cursed item glows (60 seconds).
     */
    private static final int glowTime = Ollivanders2Common.ticksPerSecond * 60;

    /**
     * The cursed item found by this spell, or null if no cursed item was detected.
     *
     * <p>Set when the spell detects a cursed item nearby. Used to track which item
     * should have its glow turned off when the duration expires.</p>
     */
    Item cursedItem = null;

    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public VERDIMILLIOUS_DUO(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.VERDIMILLIOUS_DUO;

        flavorText = new ArrayList<>() {{
            add("\"Now, Harry, cast a fully-charged Verdimillious Duo Spell. Once cast, the spell will show you objects hidden by dark magic. Use what you find to leave the room!\" -Gilderoy Lockhart");
        }};

        text = "Shoots green sparks out of the caster's wand which can damage entities and can also reveal dark magic in items by making them temporarily glow.";
    }

    /**
     * Constructor for casting VERDIMILLIOUS_DUO spells.
     *
     * <p>Initializes the spell with:</p>
     * <ul>
     * <li>GREEN_STAINED_GLASS visual effect for projectile movement</li>
     * <li>Damage-dealing enabled with 0.0625 modifier</li>
     * <li>2-block radius for both damage and curse detection</li>
     * </ul>
     *
     * @param plugin    the Ollivanders2 plugin
     * @param player    the player casting this spell
     * @param rightWand the wand correctness factor (1.0 = correct wand)
     */
    public VERDIMILLIOUS_DUO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.VERDIMILLIOUS_DUO;

        moveEffectData = Material.GREEN_STAINED_GLASS;
        doDamage = true;
        damageModifier = 0.0625;
        radius = 2;

        initSpell();
    }

    /**
     * Executes spell effects each game tick.
     *
     * <p>Performs the following:</p>
     * <ul>
     * <li>Calls parent {@link Sparks#doCheckEffect()} to handle damage and sound</li>
     * <li>Searches for cursed items within radius</li>
     * <li>Makes the first cursed item glow and schedules glow removal after 60 seconds</li>
     * <li>Kills the spell after finding a cursed item</li>
     * </ul>
     *
     * <p>The glow effect is skill-level aware: only cursed items appropriate to the player's
     * spell level are detected.</p>
     */
    @Override
    public void doCheckEffect() {
        super.doCheckEffect();

        // highlight cursed items
        List<Item> items = getNearbyItems(radius);

        for (Item item : items) {
            common.printDebugMessage("VERDIMILLIOUS_DUO.doCheckEffect: checking item " + item.getItemStack().getType(), null, null, false);
            // check for cursed items based on the level of this spell
            boolean cursed = Ollivanders2API.getItems().enchantedItems.isCursedLevelBased(item, spellType.getLevel());

            // add to the cursed item list if it is cursed, also exclude things already glowing because we don't want to change their state
            if (cursed && !item.isGlowing()) {
                common.printDebugMessage("VERDIMILLIOUS_DUO.doCheckEffect: found cursed item " + item.getItemStack().getType(), null, null, false);
                item.setGlowing(true);
                cursedItem = item;

                kill();
                break;
            }
        }

        if (cursedItem != null) {
            // schedule turning off the glow after 60 seconds
            new BukkitRunnable() {
                @Override
                public void run() {
                    stopGlow();
                }
            }.runTaskLater(p, Ollivanders2Common.ticksPerSecond * 60);
        }
    }

    /**
     * Removes the glow effect from the cursed item.
     *
     * <p>Called by a scheduled task after the glow duration expires (60 seconds).
     * Checks that the item is still valid before removing the glow.</p>
     */
    private void stopGlow() {
        if (cursedItem != null && !cursedItem.isDead())
            cursedItem.setGlowing(false);
    }

    /**
     * Gets the duration cursed items will glow after detection.
     *
     * <p>Used primarily by tests to verify glow timing behavior.</p>
     *
     * @return the glow duration in game ticks (60 seconds)
     */
    public int getGlowTime() {
        return glowTime;
    }
}
