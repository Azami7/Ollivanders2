package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.common.Cuboid;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.item.O2ItemType;
import net.pottercraft.ollivanders2.player.O2Player;
import net.pottercraft.ollivanders2.player.O2PlayerCommon;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Central manager for all Ollivanders2 spells: registration, loading, per-tick upkeep of active projectiles, and
 * zone-based cast permissions.
 * <p>
 * A spell's cast permission is resolved by {@link #isSpellTypeAllowed(Location, O2SpellType)}: a global allow list,
 * if set, takes precedence; otherwise a matching zone's allow list (WORLD, WORLD_GUARD, or CUBOID) governs; finally
 * global and zone disallow lists can deny an otherwise-allowed spell.
 * </p>
 *
 * @author Azami7
 */
public class O2Spells {
    private final Ollivanders2 p;

    private final Ollivanders2Common common;

    final private List<O2Spell> activeSpells = new ArrayList<>();

    /**
     * Loaded spell types keyed by lowercased display name. Static, so only spells that survived {@link #onEnable()}
     * plugin-dependency filtering are present.
     */
    final static private Map<String, O2SpellType> O2SpellMap = new HashMap<>();

    /**
     * Spells that can be cast without a wand. Populated at startup; divination spells are added in {@link #onEnable()}.
     */
    public static final List<O2SpellType> wandlessSpells = new ArrayList<>() {{
        add(O2SpellType.AMATO_ANIMO_ANIMATO_ANIMAGUS);
    }};

    private ConfigurationSection zoneConfig;

    /**
     * Spells denied everywhere. A disallow list denies a spell that is not otherwise permitted by an allow list.
     */
    private ArrayList<O2SpellType> globalDisallowedSpells = new ArrayList<>();

    /**
     * Spells permitted everywhere. When non-empty, only these spells may be cast anywhere, overriding all other lists.
     */
    private ArrayList<O2SpellType> globalAllowedSpells = new ArrayList<>();

    /**
     * The configured per-zone allow/disallow rules.
     */
    final ArrayList<SpellZone> spellZones = new ArrayList<>();

    /**
     * @param plugin a callback to the MC plugin
     */
    public O2Spells(@NotNull Ollivanders2 plugin) {
        p = plugin;
        common = new Ollivanders2Common(plugin);
    }

    /**
     * Initialize the spell subsystem at plugin startup. Only spells whose plugin dependencies are met are registered,
     * so a spell needing an absent plugin (e.g. LibsDisguises) is left unloaded.
     */
    public void onEnable() {
        // load enabled spells
        for (O2SpellType spellType : O2SpellType.values()) {
            if (!Ollivanders2.libsDisguisesEnabled && Ollivanders2Common.requiresLibsDisguises(spellType))
                continue;

            O2SpellMap.put(spellType.getSpellName().toLowerCase(), spellType);
        }
        p.getLogger().info("Loaded " + O2SpellMap.size() + " spells.");

        // load any spell static data
        APPARATE.loadApparateLocations(p);

        // load the zone config
        loadZoneConfig();

        // add divination spells to wandless spells
        wandlessSpells.addAll(Divination.divinationSpells);
    }

    /**
     * Kill all active spells at plugin shutdown.
     */
    public void onDisable() {
        // kill all active spells
        for (O2Spell spell : activeSpells) {
            spell.kill();
        }
    }

    /**
     * Advance every active spell one game tick via {@link O2Spell#checkEffect()} and drop any that were killed.
     */
    public void upkeep() {
        if (activeSpells.isEmpty())
            return;

        List<O2Spell> spellsTemp = new ArrayList<>(activeSpells);
        for (O2Spell spell : spellsTemp) {
            spell.checkEffect();
            if (spell.isKilled()) {
                activeSpells.remove(spell);
            }
        }
    }

    /**
     * Get all loaded spell types.
     *
     * @return a copy of the loaded spell type list
     */
    public static List<O2SpellType> getAllSpellTypes() {
        return new ArrayList<>(O2SpellMap.values());
    }

    /**
     * Get a spell type by its display name, matched case-insensitively.
     *
     * @param name the spell's display name
     * @return the matching spell type, or null if none is loaded under that name
     */
    @Nullable
    public O2SpellType getSpellTypeByName(@NotNull String name) {
        return O2SpellMap.get(name.toLowerCase());
    }

    /**
     * Verify this spell type is loaded. A spell may not be loaded if it depends on something such as LibsDisguises and that
     * dependency plugin does not exist.
     *
     * @param spellType the spell type to check
     * @return true if this spell type is loaded, false otherwise
     */
    public boolean isLoaded(@NotNull O2SpellType spellType) {
        return O2SpellMap.containsValue(spellType);
    }

    /**
     * Load the zone config for spells
     */
    public void loadZoneConfig() {
        zoneConfig = p.getConfig().getConfigurationSection("zones");

        if (zoneConfig == null)
            return;

        for (String zone : zoneConfig.getKeys(false)) {
            if (zone.equalsIgnoreCase(SpellZone.globalZoneName)) {
                common.printDebugMessage("Loading global zone config:", null, null, false);

                globalAllowedSpells = getSpellsForZone(zone, SpellZone.allowedList);
                globalDisallowedSpells = getSpellsForZone(zone, SpellZone.disallowList);
            }
            else {
                common.printDebugMessage("Loading zone config for " + zone + ":", null, null, false);

                loadZoneConfig(zone);
            }
        }
    }

    /**
     * Load zone config for allowed and disallowed spells
     *
     * @param zoneName the name of the zone
     */
    private void loadZoneConfig(@NotNull String zoneName) {
        String typeString = zoneConfig.getString(zoneName + "." + "type");
        if (typeString == null || typeString.isEmpty())
            return;

        SpellZone.SpellZoneType type;
        try {
            type = SpellZone.SpellZoneType.valueOf(typeString.toUpperCase());
        }
        catch (Exception e) {
            common.printDebugMessage("O2Spells.loadZoneConfig: zone " + zoneName + " has invalid type " + typeString, null, null, true);
            return;
        }

        String world = "";
        if (type == SpellZone.SpellZoneType.WORLD || type == SpellZone.SpellZoneType.CUBOID) {
            world = zoneConfig.getString(zoneName + "." + "world");

            if (world == null || world.isEmpty()) {
                common.printDebugMessage("O2Spells.loadZoneConfig: world or cuboid zone " + zoneName + " with no world name set, ignored.", null, null, true);
                return;
            }
        }

        int[] area = {0, 0, 0, 0, 0, 0};

        if (type == SpellZone.SpellZoneType.CUBOID) {
            String areaString = zoneConfig.getString(zoneName + "." + "area");

            if (areaString == null || areaString.isEmpty()) {
                common.printDebugMessage("O2Spells.loadZoneConfig: cuboid zone " + zoneName + " with no area coordinates set, ignored", null, null, true);
                return;
            }

            area = Cuboid.parseArea(areaString);
            if (area == null) {
                common.printDebugMessage("O2Spells.loadZoneConfig: zone " + zoneName + " has invalid area " + areaString, null, null, true);
                return;
            }
        }

        ArrayList<O2SpellType> allowed = getSpellsForZone(zoneName, SpellZone.allowedList);
        ArrayList<O2SpellType> disallowed = getSpellsForZone(zoneName, SpellZone.disallowList);

        SpellZone zone = new SpellZone(zoneName, world, type, area, allowed, disallowed);
        spellZones.add(zone);
        p.getLogger().info("Added zone type " + type + " " + zoneName);
    }

    /**
     * Get the spell for a specific list for a zone
     *
     * @param zoneName the name of the zone
     * @param listName the name of the list
     * @return the spells for that zone list
     */
    @NotNull
    private ArrayList<O2SpellType> getSpellsForZone(@NotNull String zoneName, @NotNull String listName) {
        ArrayList<O2SpellType> spellList = new ArrayList<>();

        common.printDebugMessage(listName + ":", null, null, false);

        for (String spell : zoneConfig.getStringList(zoneName + "." + listName)) {
            O2SpellType spellType = O2SpellType.spellTypeFromString(spell.toUpperCase());
            if (spellType != null && isLoaded(spellType)) {
                common.printDebugMessage(" - " + spellType, null, null, false);

                spellList.add(spellType);
            }
            else if (spellType == null)
                common.printDebugMessage("invalid spell " + spell, null, null, false);
            else if (!isLoaded(spellType))
                common.printDebugMessage(spell + " not loaded", null, null, false);
        }

        return spellList;
    }

    /**
     * Check if a spell is allowed based on zone config
     *
     * @param location  the location of the spell
     * @param spellType the spell type to check
     * @return true if spell is allowed, false otherwise
     */
    public boolean isSpellTypeAllowed(@NotNull Location location, @NotNull O2SpellType spellType) {
        if (!isLoaded(spellType))
            return false;

        // first check global allow lists
        if (!globalAllowedSpells.isEmpty())
            return globalAllowedSpells.contains(spellType);

        // check world permissions
        for (SpellZone zone : spellZones) {
            if (zone.zoneType != SpellZone.SpellZoneType.WORLD)
                continue;

            World world = location.getWorld();
            if (world == null) {
                common.printDebugMessage("O2Spells.isSpellTypeAllowed: null world on spell location", null, null, true);
                return false;
            }

            if (world.getName().equalsIgnoreCase(zone.zoneWorldName) && !zone.allowedSpells.isEmpty())
                return zone.allowedSpells.contains(spellType);
        }

        // check world guard zone permissions
        for (SpellZone zone : spellZones) {
            if (zone.zoneType != SpellZone.SpellZoneType.WORLD_GUARD)
                continue;

            if (Ollivanders2.worldGuardO2.isLocationInRegionByName(zone.zoneName, location) && !zone.allowedSpells.isEmpty())
                return zone.allowedSpells.contains(spellType);
        }

        // check cuboid zone permissions
        for (SpellZone zone : spellZones) {
            if (zone.zoneType != SpellZone.SpellZoneType.CUBOID)
                continue;

            if (zone.cuboid.isInside(location) && !zone.allowedSpells.isEmpty())
                return zone.allowedSpells.contains(spellType);
        }

        return !(isExplicitlyDisallowed(location, spellType));
    }

    /**
     * Determine if this spell is explicitly disallowed
     *
     * @param location  the location to check
     * @param spellType the spell type
     * @return true if the spell is explicitly disallowed at this location, false otherwise
     */
    private boolean isExplicitlyDisallowed(@NotNull Location location, @NotNull O2SpellType spellType) {
        // first check global disallow lists
        if (globalDisallowedSpells.contains(spellType))
            return true;

        // check world permissions
        for (SpellZone zone : spellZones) {
            if (zone.zoneType != SpellZone.SpellZoneType.WORLD)
                continue;

            World world = location.getWorld();
            if (world == null) {
                common.printDebugMessage("O2Spells.isSpellTypeAllowed: null world on spell location", null, null, true);
                return true;
            }

            if (world.getName().equalsIgnoreCase(zone.zoneWorldName) && zone.disallowedSpells.contains(spellType))
                return true;
        }

        // check world guard zone permissions
        for (SpellZone zone : spellZones) {
            if (zone.zoneType != SpellZone.SpellZoneType.WORLD_GUARD)
                continue;

            if (Ollivanders2.worldGuardO2.isLocationInRegionByName(zone.zoneName, location) && zone.disallowedSpells.contains(spellType))
                return true;
        }

        // check cuboid zone permissions
        for (SpellZone zone : spellZones) {
            if (zone.zoneType != SpellZone.SpellZoneType.CUBOID)
                continue;

            if (zone.cuboid.isInside(location) && zone.disallowedSpells.contains(spellType))
                return true;
        }

        return false;
    }

    /**
     * Register a cast spell as active and increment the player's cast count for it. The count is incremented twice
     * when the player has the FAST_LEARNING effect.
     *
     * @param player the player who cast the spell
     * @param spell  the spell cast
     */
    public void addSpell(@NotNull Player player, @NotNull O2Spell spell) {
        activeSpells.add(spell);

        p.incrementSpellCount(player, spell.spellType);

        if (Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.FAST_LEARNING))
            p.incrementSpellCount(player, spell.spellType);
    }

    /**
     * Get the active spells.
     *
     * @return a copy of the active spell list
     */
    @NotNull
    public List<O2Spell> getActiveSpells() {
        return new ArrayList<>(activeSpells);
    }

    /**
     * Construct a spell instance of the given type. The returned spell is not yet active; register it with
     * {@link #addSpell(Player, O2Spell)} to run it.
     *
     * @param player the player that cast the spell
     * @param name   the spell type to create
     * @param wandC  the wand check value for the held wand
     * @return the new spell, or null if construction failed
     */
    @Nullable
    public O2Spell createSpell(@NotNull Player player, @NotNull O2SpellType name, double wandC) {
        common.printDebugMessage("OllivandersListener.createSpellProjectile: enter", null, null, false);

        //spells go here, using any of the three types of magic
        String spellClass = "net.pottercraft.ollivanders2.spell." + name;

        Constructor<?> c;
        try {
            c = Class.forName(spellClass).getConstructor(Ollivanders2.class, Player.class, Double.class);
        }
        catch (Exception e) {
            common.printDebugMessage("OllivandersListener.createSpellProjectile: exception creating spell constructor", e, null, true);
            return null;
        }

        O2Spell spell;

        try {
            spell = (O2Spell) c.newInstance(p, player, wandC);
        }
        catch (Exception e) {
            common.printDebugMessage("OllivandersListener.createSpellProjectile: exception creating spell", e, null, true);
            return null;
        }

        return spell;
    }

    /**
     * Identify the spell a player named in a chat message. Matches the whole message as a spell name first, then the
     * shortest run of leading words that names a spell, so any trailing words (an APPARATE destination, a divination
     * target) are ignored.
     *
     * @param message the words the player chatted
     * @return the matching spell type, or null if no spell name matches
     */
    @Nullable
    public O2SpellType parseSpell(@NotNull String message) {
        O2SpellType spellType;

        // an exact whole-message match takes priority over a shorter leading-word match
        spellType = getSpellTypeByName(message);

        if (spellType != null)
            return spellType;

        String[] words = message.split(" ");

        StringBuilder spellName = new StringBuilder();
        for (String word : words) {
            spellName.append(word);
            spellType = getSpellTypeByName(spellName.toString());

            if (spellType != null)
                break;

            spellName.append(" ");
        }

        if (spellType != null) {
            common.printDebugMessage("Spell is " + spellType, null, null, false);
        }
        else {
            common.printDebugMessage("No spell found", null, null, false);
        }

        return spellType;
    }

    /**
     * Speak the incantation for the spell. APPARATE, the animagus toggle, and divinations are cast immediately;
     * every other spell is queued on the player to fire when they next swing their wand.
     * <p>
     * No-ops if the player may not cast the spell, has not learned it while bookLearning is enabled, or fails the wand
     * check. Wandless spells always pass the wand check; otherwise the player must hold their destined wand or the
     * elder wand, and any other wand passes with a probability that rises with their experience in the spell.
     * </p>
     *
     * @param player    the player casting
     * @param spellType the spell the player spoke
     * @param words     the chat message split on spaces, supplying APPARATE and divination arguments
     */
    public void speakIncantation(@NotNull Player player, @NotNull O2SpellType spellType, @NotNull String[] words) {
        if (!p.canCast(player, spellType, true)) {
            common.printDebugMessage("O2Spells.speakIncantation: Either no spell cast attempted or spell not allowed", null, null, false);
            return;
        }

        if (Ollivanders2.bookLearning && p.getO2Player(player).getSpellCount(spellType) < 1) {
            // if bookLearning is set to true then spell count must be > 0 to cast this spell
            common.printDebugMessage("O2Spells.speakIncantation: bookLearning enforced", null, null, false);
            player.sendMessage(Ollivanders2.chatColor + "You do not know that spell yet. To learn a spell, you'll need to read a book about that spell.");

            return;
        }

        // wand check
        boolean wandCheck;
        if (wandlessSpells.contains(spellType)) {
            common.printDebugMessage("O2Spells.speakIncantation: allow wandless casting of " + spellType, null, null, false);
            wandCheck = true;
        }
        else {
            if (!Ollivanders2API.getItems().getWands().holdsWandInPrimary(player)) {
                common.printDebugMessage("O2Spells.speakIncantation: player not holding a wand", null, null, false);
                wandCheck = false;
            }
            else {
                ItemStack held = player.getInventory().getItemInMainHand();
                if (Ollivanders2API.getItems().getWands().isDestinedWand(player, held)) {
                    common.printDebugMessage("O2Spells.speakIncantation: player holds destined wand", null, null, false);
                    wandCheck = true;
                }
                else if (O2ItemType.ELDER_WAND.isItemThisType(held)) {
                    common.printDebugMessage("O2Spells.speakIncantation: player holds elder wand", null, null, false);
                    wandCheck = true;
                }
                else {
                    common.printDebugMessage("O2Spells.speakIncantation: player not holding destined wand or elder wand", null, null, false);
                    int uses = p.getO2Player(player).getSpellCount(spellType);

                    // success chance rises with experience: ~1% at 1 cast, ~50% at 100, approaching 100%.
                    // in test mode a cast at or above spell mastery always succeeds so tests are deterministic, while
                    // the random roll is still exercised at lower skill levels.
                    if (Ollivanders2.testMode && uses >= O2Spell.spellMasteryLevel)
                        wandCheck = true;
                    else if (uses > 0)
                        wandCheck = Ollivanders2Common.random.nextDouble() < (1.0 - (100.0 / (uses + 101.0)));
                    else
                        wandCheck = false;
                }
            }
        }

        if (wandCheck) {
            common.printDebugMessage("O2Spells.speakIncantation: Incantation spoken for " + spellType, null, null, false);

            if (spellType == O2SpellType.APPARATE)
                addSpell(player, new APPARATE(p, player, 1.0, words));
            else if (spellType == O2SpellType.AMATO_ANIMO_ANIMATO_ANIMAGUS)
                addSpell(player, new AMATO_ANIMO_ANIMATO_ANIMAGUS(p, player, 1.0));
            else if (Divination.divinationSpells.contains(spellType)) {
                divine(spellType, player, words);
            }
            else {
                O2Player o2p = p.getO2Player(player);
                o2p.setWandSpell(spellType);
            }
        }
    }

    /**
     * Cast a divination spell, taking the target player's name from the last word the caster spoke. Messages the
     * caster and does nothing if no name was given or no online player matches it.
     *
     * @param spellType the divination spell type
     * @param sender    the player casting the divination
     * @param words     the caster's chat message split on spaces; the last element is the target player's name
     */
    private void divine(@NotNull O2SpellType spellType, @NotNull Player sender, @NotNull String[] words) {
        common.printDebugMessage("Casting divination spell", null, null, false);

        // parse the words for the target player's name
        if (words.length < 2) {
            sender.sendMessage(Ollivanders2.chatColor + "You must say the name of the player. Example: 'astrologia steve'.");
            return;
        }

        // name should be the last word the player said
        String targetName = words[words.length - 1];
        Player target = p.getServer().getPlayer(targetName);

        if (target == null) {
            sender.sendMessage(Ollivanders2.chatColor + "Unable to find player named " + targetName + ".");
            return;
        }

        O2Spell spell = createSpell(sender, spellType, O2PlayerCommon.rightWand);

        if (!(spell instanceof Divination))
            return;

        ((Divination) spell).setTarget(target);
        addSpell(sender, spell);
    }

    /**
     * Cast the spell the player queued by speaking its incantation, or their selected master spell if none is queued
     * and non-verbal casting is enabled. Consumes the queued spell on a successful cast.
     * <p>
     * No-ops if the player has no O2Player record, has neither a queued nor a master spell, or is not holding a wand
     * in their primary hand. On success adds the active spell, records the cast time, and — for a spoken spell — sets
     * prior incantatem.
     * </p>
     *
     * @param player the player casting
     */
    public void castSpell(@NotNull Player player) {
        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
        if (o2p == null) {
            common.printDebugMessage("O2Spells.castSpell: Unable to find o2player casting spell.", null, null, false);
            return;
        }

        O2SpellType spellType = o2p.getWandSpell();

        // if no spell set, check to see if they have a master spell
        boolean nonverbal = false;
        if (spellType == null && Ollivanders2.enableNonVerbalSpellCasting) {
            spellType = o2p.getMasterSpell();
            nonverbal = true;
        }

        if (spellType == null) {
            common.printDebugMessage("O2Spells.castSpell: spellType is null", null, null, false);
            return;
        }

        if (!Ollivanders2API.getItems().getWands().holdsWandInPrimary(player)) {
            common.printDebugMessage("O2Spells.castSpell: player does not hold a wand in their primary hand", null, null, false);
            return;
        }
        common.printDebugMessage("O2Spells.castSpell: player holds a wand in their primary hand", null, null, false);

        double wandCheck = Ollivanders2API.playerCommon.wandCheck(player, EquipmentSlot.HAND);

        O2Spell spell = createSpell(player, spellType, wandCheck);
        if (spell == null) {
            return;
        }

        addSpell(player, spell);

        o2p.setSpellRecentCastTime(spellType);
        if (!nonverbal) {
            o2p.setPriorIncantatem(spellType);
        }

        common.printDebugMessage("O2Spells.castSpell: " + player.getName() + " cast " + spell.getName(), null, null, false);

        o2p.setWandSpell(null);
    }

    /**
     * Rotate the player's selected master spell for non-verbal casting and tell them the new selection. A left click
     * advances the selection, a right click reverses it.
     * <p>
     * No-ops if non-verbal casting is disabled, the player is not holding a wand in their off-hand, or has no O2Player
     * record.
     * </p>
     *
     * @param player the player rotating their master spell
     * @param action the click that triggered the rotation; a right click reverses direction
     */
    public void rotateNonVerbalSpell(@NotNull Player player, @NotNull Action action) {
        if (!Ollivanders2.enableNonVerbalSpellCasting)
            return;

        common.printDebugMessage("Rotating mastered spells for non-verbal casting.", null, null, false);

        if (!Ollivanders2API.getItems().getWands().holdsWandInOff(player))
            return;

        O2Player o2p = Ollivanders2API.getPlayers().getPlayer(player.getUniqueId());
        if (o2p == null)
            return;

        boolean reverse = false;
        // right click rotates through spells backwards
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK)
            reverse = true;

        o2p.shiftMasterSpell(reverse);
        O2SpellType spell = o2p.getMasterSpell();
        if (spell != null) {
            String spellName = spell.getSpellName();
            player.sendMessage(Ollivanders2.chatColor + "Wand master spell set to " + spellName);
        }
        // only in debug: an errant off-hand click with no mastered spells shouldn't nag the player on every click
        else if (Ollivanders2.debug) {
            player.sendMessage(Ollivanders2.chatColor + "You have not mastered any spells.");
        }
    }
}
