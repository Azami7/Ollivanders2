package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Creates a port key.
 * <p>
 * {@link net.pottercraft.ollivanders2.item.enchantment.PORTUS}
 *
 * @see <a href = "https://harrypotter.fandom.com/wiki/Portkey_Spell">https://harrypotter.fandom.com/wiki/Portkey_Spell</a>
 */
public final class PORTUS extends ItemEnchant {
    /**
     * Default constructor for use in generating spell text.  Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public PORTUS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.PORTUS;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("\"For a moment the kettle trembled, glowing with an odd blue light; then it quivered to rest, as solidly black as ever.\"");
            add("Almost any inanimate object can be turned into a Portkey. Once bewitched, the object will transport anyone who grasps it to a pre-arranged destination.");
        }};

        text = "Portus is a spell which creates a portkey to your current location. To cast it, hold a wand in your hand "
                + "and hold the item you wish to enchant in your off-hand, then say the spell and flick your wand. "
                + "You can leave this item in world and when the enchanted item is picked up, the holder and the entities "
                + "around them will be teleported to the location. Anti-apparation spells in the location will "
                + "will prevent a portkey being created for a location or from an existing portkey working.";
    }

    /**
     * Constructor. This is here just in case some reflection tries to create this spell.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public PORTUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.PORTUS;
        branch = O2MagicBranch.CHARMS;
        enchantmentType = ItemEnchantmentType.PORTUS;
        enchantsHeldItem = true;

        initSpell();
    }

    /**
     * Make the location where the player is
     */
    @Override
    void doInitSpell() {
        super.doInitSpell();

        if (args == null || args.isEmpty()) {
            Location loc = player.getLocation();

            World world = loc.getWorld();
            if (world == null) {
                kill();
                return;
            }

            args = world.getName() + " " + loc.getX() + " " + loc.getY() + " " + loc.getZ();
        }
    }

    /**
     * Can this item be enchanted by this spell? Make sure this location is not protected by apparate limits
     *
     * @param itemStack the item to check
     * @return true if it can be enchanted, false otherwise
     */
    @Override
    protected boolean canBeEnchanted(@NotNull ItemStack itemStack) {
        // is this location inside a nullum apparebit
        for (O2StationarySpell stationarySpell : Ollivanders2API.getStationarySpells().getStationarySpellsAtLocation(location)) {
            if (stationarySpell.getSpellType() == O2StationarySpellType.NULLUM_APPAREBIT)
                return false;
        }

        return super.canBeEnchanted(itemStack);
    }
}