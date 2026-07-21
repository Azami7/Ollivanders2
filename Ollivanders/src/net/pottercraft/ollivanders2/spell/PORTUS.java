package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * PORTUS - The Portkey spell: enchants the caster's off-hand item so that whoever later picks it up is teleported,
 * along with nearby entities, to the caster's location at the time of casting.
 *
 * <p>The destination is stored in the item's enchantment args; the teleport is performed on pickup by the
 * {@link net.pottercraft.ollivanders2.item.enchantment.PORTUS} enchantment.</p>
 *
 * @see net.pottercraft.ollivanders2.item.enchantment.PORTUS the enchantment that powers this spell
 * @see <a href="https://harrypotter.fandom.com/wiki/Portkey_Spell">Harry Potter Wiki - Portkey Spell</a>
 */
public final class PORTUS extends ItemEnchant {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
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
                + "prevent a portkey being created for a location or from an existing portkey working.";
    }

    /**
     * Constructor for casting PORTUS. Enchants the caster's off-hand item, storing the caster's current location as
     * the portkey destination.
     *
     * @param plugin    the Ollivanders2 plugin
     * @param player    the player casting this spell
     * @param rightWand the wand strength/correctness factor
     */
    public PORTUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.PORTUS;
        branch = O2MagicBranch.CHARMS;
        enchantmentType = ItemEnchantmentType.PORTUS;
        noProjectile = true;

        initSpell();
    }

    /**
     * Store the caster's current location as the portkey destination in the enchantment args, formatted as
     * "world_name x y z". Kills the spell if the caster's world is null.
     *
     * @param portkey the item being enchanted as a portkey
     */
    @Override
    protected void createEnchantmentArgs(ItemStack portkey) {
        Location portkeyDestination = caster.getLocation();

        World world = portkeyDestination.getWorld();
        if (world == null) {
            common.printLogMessage("PORTUS.createEnchantmentArgs: null world", null, null, true);
            kill();
            return;
        }

        enchantmentArgs = world.getName() + " " + portkeyDestination.getX() + " " + portkeyDestination.getY() + " " + portkeyDestination.getZ();
    }
}