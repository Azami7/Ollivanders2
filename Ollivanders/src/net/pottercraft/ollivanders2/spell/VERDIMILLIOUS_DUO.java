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
 * A stronger {@link VERDIMILLIOUS}: shoots green sparks that damage nearby entities and reveal cursed items in range
 * (appropriate to the caster's skill) by making them glow temporarily.
 *
 * @author Azami7
 * @see VERDIMILLIOUS
 */
public class VERDIMILLIOUS_DUO extends Sparks {
    /**
     * Duration in ticks that a revealed cursed item glows (60 seconds).
     */
    private static final int glowTime = Ollivanders2Common.ticksPerSecond * 60;

    /**
     * The cursed item this spell revealed (made glow), or null if none was detected; its glow is turned off when the
     * duration expires.
     */
    Item cursedItem = null;

    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
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
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
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
     * Run the base spark damage, then make the first nearby cursed item appropriate to this spell's level glow, end
     * the spell, and schedule the glow to turn off after {@link #getGlowTime()}.
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
     * Turn off the revealed cursed item's glow, if it still exists.
     */
    private void stopGlow() {
        if (cursedItem != null && !cursedItem.isDead())
            cursedItem.setGlowing(false);
    }

    /**
     * @return how long a revealed cursed item glows, in ticks
     */
    public int getGlowTime() {
        return glowTime;
    }
}
