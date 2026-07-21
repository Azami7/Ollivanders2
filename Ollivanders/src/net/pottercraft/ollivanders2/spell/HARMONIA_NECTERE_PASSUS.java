package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.*;
import net.pottercraft.ollivanders2.block.BlockCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpellType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Creates a linked pair of vanishing cabinets that teleport players between two locations.
 *
 * <p>Cast at a sign whose four lines give the world name and XYZ coordinates of a matching sign elsewhere; if a valid
 * twin sign is found at those coordinates and neither end already holds a cabinet, a stationary vanishing cabinet is
 * anchored at each sign, enabling bidirectional travel.</p>
 *
 * @see <a href="https://harrypotter.fandom.com/wiki/Harmonia_Nectere_Passus">Harry Potter Wiki - Harmonia Nectere Passus</a>
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
     * On the tick the projectile stops, find a vanishing cabinet sign near the hit point and link it to the twin sign
     * named on it. On success a stationary vanishing cabinet is anchored at each sign, enabling bidirectional
     * teleportation; on failure the caster is told why (no sign found, malformed or missing twin sign, the two ends
     * are the same location, or a cabinet already exists at either end).
     */
    @Override
    protected void doCheckEffect() {
        if (!hasHitBlock())
            return;

        kill();

        // find a sign block within radius of the hit point - sometimes the spell hits the door because exact aiming is
        // tricky so this makes it more robust
        Block signBlock = null;

        for (Block block : BlockCommon.getBlocksInRadius(getLocation(), defaultRadius)) {
            if (Ollivanders2Common.isSign(block.getType())) {
                signBlock = block;
            }
        }

        if (signBlock == null) {
            common.printDebugMessage("HARMONIA_NECTERE_PASSUS.doCheckEffect: sign block not found", null, null, true);
            failureMessage = "Nothing seems to happen";
            sendFailureMessage();
            return;
        }

        // determine the location of the other vanishing cabinet
        Location twinLocation = getTwinLocation(signBlock);
        if (twinLocation == null) {
            common.printDebugMessage("HARMONIA_NECTERE_PASSUS.doCheckEffect: Unable to get twinLocation from sign.", null, null, false);
            failureMessage = "The twin location on the sign is malformed.";
            sendFailureMessage();
            return;
        }

        // now actually find a sign near that location
        Location toLoc = null;
        for (Block twin : BlockCommon.getBlocksInRadius(twinLocation, defaultRadius)) {
            if (Ollivanders2Common.isSign(twin)) {
                toLoc = twin.getLocation();
                break;
            }
        }
        if (toLoc == null) {
            common.printDebugMessage("HARMONIA_NECTERE_PASSUS.doCheckEffect: sign not found at twin location.", null, null, false);
            failureMessage = "A sign was not found at the twin location.";
            sendFailureMessage();
            return;
        }

        Location fromLoc = signBlock.getLocation(); // this can be different from the location of the projectile by up to half a block
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

        net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS harmoniaFrom = new net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS(p, caster.getUniqueId(), fromLoc, toLoc);
        net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS harmoniaTo = new net.pottercraft.ollivanders2.stationaryspell.HARMONIA_NECTERE_PASSUS(p, caster.getUniqueId(), toLoc, fromLoc);

        harmoniaFrom.flair(20);
        harmoniaTo.flair(20);

        Ollivanders2API.getStationarySpells().addStationarySpell(harmoniaFrom);
        Ollivanders2API.getStationarySpells().addStationarySpell(harmoniaTo);
    }

    /**
     * Read a twin-cabinet destination from a vanishing cabinet sign. The sign's front face must carry the world name
     * on line 0 and integer X, Y, Z coordinates on lines 1–3.
     *
     * @param block the sign block to parse
     * @return the destination location, or null if the sign does not have four lines, names an unknown world, or has
     * non-integer coordinates
     */
    @Nullable
    private Location getTwinLocation(Block block) {
        Location twinLocation = null;

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

            twinLocation = new Location(world, x, y, z);
        }

        return twinLocation;
    }
}