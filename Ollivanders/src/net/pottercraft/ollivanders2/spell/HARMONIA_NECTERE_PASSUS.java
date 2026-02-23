package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.*;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Creates a pair of vanishing cabinets for teleportation between two locations.
 *
 * <p>The spell requires two properly configured vanishing cabinet signs. Each sign must contain
 * the world name and XYZ coordinates of the other cabinet on four separate lines. When cast on a sign,
 * the spell creates a pair of stationary vanishing cabinets that allow players to teleport between them.
 *
 * <p>The spell validates:
 * <ul>
 * <li>Target block is a sign</li>
 * <li>Sign contains valid destination coordinates</li>
 * <li>To and from locations are different</li>
 * <li>No vanishing cabinet already exists at either location</li>
 * </ul>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Harmonia_Nectere_Passus">Harmonia Nectere Passus</a>
 */
public final class HARMONIA_NECTERE_PASSUS extends O2Spell {
    /**
     * Default constructor for use in generating spell text. Do not use to cast the spell.
     *
     * @param plugin the Ollivanders2 plugin
     */
    public HARMONIA_NECTERE_PASSUS(Ollivanders2 plugin) {
        super(plugin);

        spellType = O2SpellType.HARMONIA_NECTERE_PASSUS;
        branch = O2MagicBranch.CHARMS;

        flavorText = new ArrayList<>() {{
            add("The Vanishing Cabinet Charm");
            add("He stares at the monolith before him, lifts his wand and begins to chant eerily. The surface of the cabinet glimmers, atremble in the ambient light. Almost alive. Then he stops. Looking back, his eyes haunted, he slips away. Light plays within the cabinet. Movement. Shadows flicker within, coalesce.");
            add("\" ...we forced him head-first into that Vanishing Cabinet on the first floor.\"\n" +
                    "\"But you'll get into terrible trouble!\"\n" +
                    "\"Not until Montague reappears, and that could take weeks, I dunno where we sent him...\" -Fred Weasley and Hermione Granger");
        }};

        text = "Harmonia Nectere Passus will create a pair of vanishing cabinets if the cabinets on both ends are configured correctly."
                + "\n\nVanishing cabinet construction is very precise and the cabinets will only work if the directions are followed exactly."
                + "\n\nStep 1 - determine the XYZ coordinates for the cabinets. These must be in whole numbers."
                + "\n\nStep 2 - create a sign at each XYZ coordinate that contains the coordinates for the other cabinet."
                + "Write the world name on the first line, the X coordinate on the second, Y on the third, and Z on the fourth."
                + "\n\nStep 3 - place any type of solid block 2-blocks high on each side, in front, and in back of the sign - 4 blocks total."
                + "\n\nStep 4 - leave an air block above the sign and place any type of solid block above that air block."
                + "\n\nStep 5 - construct the other cabinet following steps 3 and 4."
                + "\n\nStep 6 - cast the vanishing cabinet repair spell, Harmonia Nectere Passus, at either of the two signs."
                + "\n\nYou can now walk in to the cabinet and appear in the other.";
    }

    /**
     * Constructor.
     *
     * @param plugin    a callback to the MC plugin
     * @param player    the player who cast this spell
     * @param rightWand which wand the player was using
     */
    public HARMONIA_NECTERE_PASSUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand) {
        super(plugin, player, rightWand);

        spellType = O2SpellType.HARMONIA_NECTERE_PASSUS;
        branch = O2MagicBranch.CHARMS;

        // world guard flags
        if (Ollivanders2.worldGuardEnabled)
            worldGuardFlags.add(Flags.BUILD);

        if (Ollivanders2.testMode)
            projectilePassThrough.addAll(Ollivanders2Common.getDoorsStrict());

        initSpell();
    }

    /**
     * Create vanishing cabinets when spell hits a valid sign target.
     *
     * <p>Validates the target sign and creates a pair of stationary vanishing cabinets if all
     * requirements are met. Sends appropriate failure messages to the caster if validation fails.</p>
     *
     * <p>Validation checks:
     * <ul>
     * <li>Target block is a sign ({@link #getTargetBlock()})</li>
     * <li>Sign contains valid destination coordinates via {@link #getSignLocation(Block)}</li>
     * <li>From and to locations are different (cannot create self-referential cabinet)</li>
     * <li>No stationary vanishing cabinet already exists at either location</li>
     * </ul>
     *
     * <p>If all checks pass, creates two stationary HARMONIA_NECTERE_PASSUS spells (one at each location)
     * that enable bidirectional teleportation.</p>
     */
    @Override
    protected void doCheckEffect() {
        if (!hasHitTarget())
            return;

        kill();

        Block signBlock = getTargetBlock();
        if (signBlock == null) {
            common.printDebugMessage("HARMONIA_NECTERE_PASSUS.doCheckEffect: from block is null", null, null, true);
            failureMessage = "Nothing seems to happen";
            sendFailureMessage();
            return;
        }

        Material blockType = signBlock.getType();
        if (!Ollivanders2Common.isSign(blockType)) {
            common.printDebugMessage("Block is not a sign", null, null, false);
            failureMessage = "Nothing seems to happen";
            sendFailureMessage();
            return;
        }

        // determine the location of the other vanishing cabinet
        Location toLoc = getSignLocation(signBlock);
        if (toLoc == null) {
            common.printDebugMessage("Unable to get toLoc from sign.", null, null, false);
            failureMessage = "Sign does not have the location of the twin cabinet";
            sendFailureMessage();
            return;
        }

        Location fromLoc = signBlock.getLocation(); // this is slightly different from the location of the projectile (in fractions)
        if (Ollivanders2Common.locationEquals(toLoc, fromLoc)) {
            common.printDebugMessage("Vanishing cabinet to and from locations are the same", null, null, false);
            failureMessage = "To and from locations on vanishing cabinet signs are the same.";
            sendFailureMessage();
            return;
        }

        if (!Ollivanders2API.getStationarySpells().getActiveStationarySpellsAtLocationByType(fromLoc, O2StationarySpellType.HARMONIA_NECTERE_PASSUS).isEmpty() || !Ollivanders2API.getStationarySpells().getActiveStationarySpellsAtLocationByType(toLoc, O2StationarySpellType.HARMONIA_NECTERE_PASSUS).isEmpty()) {
            failureMessage = "There is already a vanishing cabinet here.";
            sendFailureMessage();
            return;
        }

        net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS harmoniaFrom = new net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS(p, player.getUniqueId(), fromLoc, toLoc);
        net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS harmoniaTo = new net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS(p, player.getUniqueId(), toLoc, fromLoc);

        harmoniaFrom.flair(20);
        harmoniaTo.flair(20);

        Ollivanders2API.getStationarySpells().addStationarySpell(harmoniaFrom);
        Ollivanders2API.getStationarySpells().addStationarySpell(harmoniaTo);
    }

    /**
     * Extract destination coordinates from a vanishing cabinet sign.
     *
     * <p>Reads the four lines of text from the sign's front face:
     * <ul>
     * <li>Line 0: World name</li>
     * <li>Line 1: X coordinate</li>
     * <li>Line 2: Y coordinate</li>
     * <li>Line 3: Z coordinate</li>
     * </ul>
     *
     * @param block the sign block to parse
     * @return a Location constructed from the sign's coordinates, or null if the sign is missing
     * coordinates, coordinates are not integers, or the world does not exist
     */
    @Nullable
    private Location getSignLocation(Block block) {
        Location location = null;

        Sign sign = (Sign) block.getState();
        String[] lines = sign.getSide(Side.FRONT).getLines();

        if (lines.length == 4) {
            World world = Bukkit.getWorld(lines[0]);
            if (world == null)
                return null;

            int x;
            int y;
            int z;

            try {
                x = Integer.parseInt(lines[1]);
                y = Integer.parseInt(lines[2]);
                z = Integer.parseInt(lines[3]);
            }
            catch (NumberFormatException e) {
                common.printDebugMessage("Unable to parse coordinates from sign.", null, null, false);
                return null;
            }

            location = new Location(world, x, y, z);
        }

        return location;
    }
}