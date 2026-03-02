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
 * PORTUS - The Portkey spell.
 *
 * <p>Enchants an item to become a portkey that transports anyone who picks it up to the caster's
 * current location. The destination coordinates are stored in the enchantment arguments. When a
 * player picks up a PORTUS-enchanted item, the enchanted items system triggers the portkey effect,
 * teleporting the player and nearby entities to the destination.</p>
 *
 * <p>Spell Mechanics:</p>
 *
 * <ul>
 * <li>Mode: Held-item (noProjectile = true) — enchants off-hand item instead of projectile target</li>
 * <li>Enchantment argument: Destination coordinates (world name and x, y, z)</li>
 * <li>Effect: Teleports holder and nearby entities to stored destination (see {@link net.pottercraft.ollivanders2.item.enchantment.PORTUS})</li>
 * <li>Classification: Charms</li>
 * <li>Target item type: Any (no restrictions)</li>
 * </ul>
 *
 * @see net.pottercraft.ollivanders2.item.enchantment.PORTUS the enchantment that powers this spell
 * @see <a href="https://harrypotter.fandom.com/wiki/Portkey_Spell">Harry Potter Wiki - Portkey Spell</a>
 */
public final class PORTUS extends ItemEnchant {
    /**
     * Constructor for generating spell information.
     *
     * <p>Initializes the spell with flavor text and description. Do not use to cast the spell.
     * Use the full constructor with player and wand parameters instead.</p>
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
     * Constructor for casting the PORTUS portkey spell.
     *
     * <p>Initializes the spell with the player and wand information needed to cast and track the spell.
     * PORTUS uses held-item mode (noProjectile = true), enchanting the item held in the caster's
     * off-hand. The caster's current location is stored as the portkey destination.</p>
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
     * Store the portkey destination coordinates as enchantment arguments.
     *
     * <p>Extracts the caster's current location at enchantment time and formats it as
     * "world_name x y z" for storage. This destination is used when the portkey is activated
     * to teleport the holder to the specified location.</p>
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