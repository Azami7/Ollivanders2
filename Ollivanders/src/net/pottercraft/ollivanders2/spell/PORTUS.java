package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

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
            add("For a moment the kettle trembled, glowing with an odd blue light; then it quivered to rest, as solidly black as ever.");
            add("Almost any inanimate object can be turned into a Portkey. Once bewitched, the object will transport anyone who grasps it to a pre-arranged destination.");
        }};

        text = "Portus is a spell which creates a portkey. To cast it, hold a wand in your hand "
                + "and look directly at the item you wish to enchant. Then say 'Portus x y z', where x y and z are the coordinates "
                + "you wish the portkey to link to. When this item is picked up, the holder and the entities around them will be "
                + "transported to the destination. Anti-apparition and anti-disapparition spells will stop this, but only if present "
                + "during the creation of the portkey, and will cause the creation to fail. If the portkey is successfully made, then "
                + "it can be used to go to that location regardless of the spells put on it. A portkey creation will not fail if the "
                + "caster of the protective enchantments is the portkey maker. Portkeys can be used to cross worlds as well, if you use "
                + "a portkey which was made in a different world. If the enchantment is said incorrectly, then the portkey will be created "
                + "linking to the caster's current location.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     * @param words     the location this portkey goes to
     */
    public PORTUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand, @NotNull String[] words) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.PORTUS;
        branch = O2MagicBranch.CHARMS;
        enchantmentType = ItemEnchantmentType.PORTUS;

        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(word).append(" ");
        }
        args = sb.toString().trim();

        initSpell();
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
}