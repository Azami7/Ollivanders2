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
 * Fancier version of VERDIMILLIOUS
 */
public class VERDIMILLIOUS_DUO extends SparksBase {
    /**
     * The cursed items the spell found
     */
    ArrayList<Item> cursedItems = new ArrayList<>();

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
        damageModifier = 0.125;
        radius = 2;

        initSpell();
    }

    /**
     * Set the damage for this spell based on caster's skill level in this spell
     */
    @Override
    void doInitSpell() {
        setDamage();
    }

    @Override
    public void doCheckEffect() {
        super.doCheckEffect();

        // highlight cursed items
        List<Item> items = getNearbyItems(radius);

        for (Item item : items) {
            // check for cursed items based on the level of this spell
            boolean cursed = Ollivanders2API.getItems().enchantedItems.isCursedLevelBased(item, spellType.getLevel());

            // add to the cursed item list if it is cursed, also exclude things already glowing because we don't want to change their state
            if (cursed && !item.isGlowing())
                cursedItems.add(item);
        }

        for (Item item : cursedItems) {
            item.setGlowing(true);
        }

        // schedule turning off the glow after 60 seconds
        new BukkitRunnable() {
            @Override
            public void run() {
                stopGlow();
            }
        }.runTaskLater(p, Ollivanders2Common.ticksPerSecond * 60);
    }

    /**
     * Stop the cursed items from glowing
     */
    private void stopGlow() {
        for (Item item : cursedItems) {
            item.setGlowing(false);
        }
    }
}
