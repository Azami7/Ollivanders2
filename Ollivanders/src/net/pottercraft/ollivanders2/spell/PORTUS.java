package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;

/**
 * Creates a port key.
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
            add("For a moment the kettle trembled, glowing with an odd blue light; then it quivered to rest, as solidly black as ever.");
            add("Almost any inanimate object can be turned into a Portkey. Once bewitched, the object will transport anyone who grasps it to a pre-arranged destination.");
        }};

        text = "Portus is a spell which creates a portkey. To cast it, hold a wand in your hand "
                + "and hold the item you wish to enchant in your off hand, then say the spell and flick your wand. "
                + "You can leave this item in world and when the enchanted item is picked up, the holder and the entities "
                + "around them will be teleported to your current location. Anti-apparation spells in your location will "
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

        initSpell();
    }

    @Override
    void doInitSpell() {
        super.doInitSpell();

        if (args == null || args.length() < 1) {
            Location loc = player.getLocation();

            args = player.getLocation().getWorld().getName() + " " + player.getLocation().getX() + " " + player.getLocation().getY() + " " + player.getLocation().getZ();
        }
    }
}