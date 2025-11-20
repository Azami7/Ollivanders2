package net.pottercraft.ollivanders2.player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.ArrayList;

import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.effect.O2Effects;
import net.pottercraft.ollivanders2.GsonDAO;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.house.O2HouseType;
import net.pottercraft.ollivanders2.house.O2Houses;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.spell.O2SpellType;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Player management class for all player-specific data and functions.
 */
public class O2Players {
    /**
     * A map of MC player UUIDs and the player O2Player object.
     */
    private Map<UUID, O2Player> O2PlayerMap = new HashMap<>();

    /**
     * Effects manager for player effects.
     */
    public O2Effects playerEffects;

    /**
     * The MC plugin callback
     */
    private final Ollivanders2 p;

    /**
     * Common functions
     */
    private final Ollivanders2Common common;

    /**
     * A count of the player records read at start. This can be used to prevent writing back out at server
     * shut down if something goes wrong on plugin load.
     */
    private int recordCount = 0;

    /**
     * Labels for serializing player data
     */
    private final String nameLabel = "Name";
    private final String woodLabel = "Wood";
    private final String coreLabel = "Core";
    private final String soulsLabel = "Souls";
    private final String invisibleLabel = "Invisible";
    private final String foundWandLabel = "Found_Wand";
    private final String masterSpellLabel = "Master_Spell";
    private final String animagusLabel = "Animagus";
    private final String animagusColorLabel = "Animagus_Color";
    private final String muggleLabel = "Muggle";
    private final String yearLabel = "Year";
    private final String spellLabelPrefix = "Spell_";
    private final String potionLabelPrefix = "Potion_";
    private final String priorIncantatumLabel = "Prior_Incantatum";
    private final String lastSpellLabel = "Last_Spell";

    /**
     * Constructor
     *
     * @param plugin the MC plugin
     */
    public O2Players(@NotNull Ollivanders2 plugin) {
        p = plugin;

        playerEffects = new O2Effects(p);
        common = new Ollivanders2Common(p);
    }

    /**
     * Load player and player effects on plugin enable
     */
    public void onEnable() {
        loadO2Players();

        playerEffects.onEnable();
    }

    /**
     * Save player data on disable
     */
    public void onDisable() {
        saveO2Players();
    }

    /**
     * Add a new O2Player.
     *
     * @param pid  the UUID of this player
     * @param name the effectType of this player
     */
    public void addPlayer(@NotNull UUID pid, @NotNull String name) {
        // todo make this return o2p so that Ollivanders2.getO2Player can guarantee NotNull
        O2Player o2p = new O2Player(pid, name, p);

        updatePlayer(pid, o2p);
    }

    /**
     * Update an existing O2Player.
     *
     * @param pid the UUID of this player
     * @param o2p the O2Player object for this player
     */
    public synchronized void updatePlayer(@NotNull UUID pid, @NotNull O2Player o2p) {
        O2PlayerMap.put(pid, o2p);
    }

    /**
     * Get an O2Player by UUID
     *
     * @param pid the UUID of the player
     * @return the O2Player if found, null otherwise.
     */
    @Nullable
    public O2Player getPlayer(@NotNull UUID pid) {
        return O2PlayerMap.getOrDefault(pid, null);
    }

    /**
     * Get an O2Player by name. This is inefficient compared to by UUID and should only be used when UUID is not available.
     *
     * @param playerName the name of the player
     * @return the O2Player if found, null otherwise.
     */
    @Nullable
    public O2Player getPlayer(@NotNull String playerName) {
        for (Entry<UUID, O2Player> entry : O2PlayerMap.entrySet()) {
            if (entry.getValue().getPlayerName().equalsIgnoreCase(playerName))
                return entry.getValue();
        }

        return null;
    }

    /**
     * Remove a player
     *
     * @param pid the id of the player to remove
     */
    public void removePlayer(@NotNull UUID pid) {
        O2PlayerMap.remove(pid);
    }

    /**
     * Get a list of all player unique ids.
     *
     * @return a list of all known player MC UUIDs
     */
    @NotNull
    public ArrayList<UUID> getPlayerIDs() {
        return new ArrayList<>(O2PlayerMap.keySet());
    }

    /**
     * Write all players to the plugin config directory
     */
    public void saveO2Players() {
        // serialize the player map
        Map<String, Map<String, String>> serializedMap = serializeO2Players(O2PlayerMap);
        if (serializedMap == null) {
            p.getLogger().warning("Something went wrong serializing players, no records will be saved.");
            return;
        }

        GsonDAO gsonLayer = new GsonDAO();
        gsonLayer.writeSaveData(serializedMap, GsonDAO.o2PlayerJSONFile);
    }

    /**
     * Load players from save file
     */
    public void loadO2Players() {
        GsonDAO gsonLayer = new GsonDAO();

        // load players from the save file, if it exists
        Map<String, Map<String, String>> serializedMap = gsonLayer.readSavedDataMapStringMap(GsonDAO.o2PlayerJSONFile);

        if (serializedMap != null) {
            Map<UUID, O2Player> deserializedMap = deserializeO2Players(serializedMap);

            if (deserializedMap != null && !deserializedMap.isEmpty())
                O2PlayerMap = deserializedMap;

            p.getLogger().info("Loaded " + O2PlayerMap.size() + " saved players.");
        }
        else {
            p.getLogger().info("No saved O2Players.");
        }
    }

    /**
     * Serialize the O2Player to a list of key value pairs for all the data we want to save.
     * <p>
     * Map structure:<br>
     * Key - UUID<br>
     * <pre>
     * ArrayList {{
     *    Name : playerName
     *    FoundWand : foundWand
     *    WandWood : wandWood
     *    WandCore : wandCore
     *    Souls : souls
     *    Animagus : [EntityType]
     *    AnimagusColor : [EntityColorType]
     *    MasterSpell : [Spell Type]
     *    Year : [Year]
     *    Muggle: isMuggle
     *    [Spell] : [Count]
     *    [Potion] : [Count]
     *    [Effect] : [Duration]
     * }};
     * </pre>
     *
     * @param o2PlayerMap a map of all player MC UUIDs and corresponding O2Player object
     * @return all player data as a map of strings per player
     */
    @Nullable
    private Map<String, Map<String, String>> serializeO2Players(@NotNull Map<UUID, O2Player> o2PlayerMap) {
        Map<String, Map<String, String>> serializedMap = new HashMap<>();

        common.printDebugMessage("Serializing O2Players...", null, null, false);

        if (recordCount != 0) {
            if (o2PlayerMap.isEmpty()) {
                p.getLogger().warning("Something went wrong and all player records lost, skipping save for safety.");
                return null;
            }

            if (o2PlayerMap.size() < (recordCount / 2))
                p.getLogger().info("Player list is less than half the size when the server started, this may indicate a problem.");
        }

        for (Map.Entry<UUID, O2Player> e : o2PlayerMap.entrySet()) {
            UUID pid = e.getKey();
            O2Player o2p = e.getValue();

            Map<String, String> playerData = new HashMap<>();

            //
            // Name
            //
            String pName = o2p.getPlayerName();
            playerData.put(nameLabel, pName);

            //
            // Wand
            //
            playerData.put(woodLabel, o2p.getDestinedWandWood());
            playerData.put(coreLabel, o2p.getDestinedWandCore());

            //
            // Souls
            //
            int souls = o2p.getSouls();
            playerData.put(soulsLabel, Integer.toString(souls));

            //
            // Found Wand
            //
            if (o2p.foundWand()) {
                playerData.put(foundWandLabel, Boolean.toString(true));
            }

            //
            // Master Spell
            //
            O2SpellType spellType = o2p.getMasterSpell();
            if (spellType != null) {
                playerData.put(masterSpellLabel, spellType.toString());
            }

            //
            // Last Spell
            //
            O2SpellType lastSpell = o2p.getLastSpell();
            if (lastSpell != null) {
                playerData.put(lastSpellLabel, lastSpell.toString());
            }

            //
            // Prior Incantatum
            //
            O2SpellType prior = o2p.getPriorIncantatem();
            if (prior != null) {
                playerData.put(priorIncantatumLabel, prior.toString());
            }

            //
            // Animagus
            //
            EntityType animagus = o2p.getAnimagusForm();
            if (animagus != null) {
                playerData.put(animagusLabel, animagus.toString());
                String color = o2p.getAnimagusColor();
                if (color != null)
                    playerData.put(animagusColorLabel, color);
            }

            //
            // Muggle
            //
            playerData.put(muggleLabel, Boolean.toString(o2p.isMuggle()));

            //
            // Year
            //
            Integer year = o2p.getYear().ordinal();
            playerData.put(yearLabel, year.toString());

            //
            // Effects
            //
            Map<String, String> effects = Ollivanders2API.getPlayers().playerEffects.serializeEffects(pid);
            for (Entry<String, String> entry : effects.entrySet()) {
                playerData.put(entry.getKey(), entry.getValue());
            }

            //
            // Spell Experience
            //
            Map<O2SpellType, Integer> knownSpells = o2p.getKnownSpells();
            for (Entry<O2SpellType, Integer> s : knownSpells.entrySet()) {
                playerData.put(spellLabelPrefix + s.getKey().toString(), s.getValue().toString());
            }

            //
            // Potion Experience
            //
            Map<O2PotionType, Integer> knownPotions = o2p.getKnownPotions();
            for (Entry<O2PotionType, Integer> p : knownPotions.entrySet()) {
                playerData.put(potionLabelPrefix + p.getKey().toString(), p.getValue().toString());
            }

            serializedMap.put(pid.toString(), playerData);
        }

        return serializedMap;
    }

    /**
     * Deserialize an O2Player map
     * <p></p>
     * Map structure:<br>
     * Key - UUID<br>
     * <pre>
     * ArrayList {{
     *    Name : playerName
     *    FoundWand : foundWand
     *    WandWood : wandWood
     *    WandCore : wandCore
     *    Souls : souls
     *    Invisible : invisible
     *    Animagus : [EntityType]
     *    AnimagusColor : [EntityColorType]
     *    MasterSpell : [Spell Type]
     *    Year : [Year]
     *    [Spell] : [Count]
     *    [Potion] : [Count]
     *    [Effect] : [Duration]
     * }};
     * </pre>
     *
     * @param map a map of player data as strings
     * @return the deserialized map of O2Players, null if map could not be deserialized
     */
    @Nullable
    private Map<UUID, O2Player> deserializeO2Players(@NotNull Map<String, Map<String, String>> map) {
        Map<UUID, O2Player> deserializedMap = new HashMap<>();

        if (map.isEmpty())
            return null;

        for (Entry<String, Map<String, String>> e : map.entrySet()) {
            UUID pid = Ollivanders2API.common.uuidFromString(e.getKey());
            if (pid == null)
                continue;

            Map<String, String> playerData = e.getValue();
            if (playerData == null || playerData.isEmpty())
                continue;

            // get player name
            String playerName = playerData.get(nameLabel);
            if (playerName == null || playerName.isEmpty())
                continue;

            O2Player o2p = new O2Player(pid, playerName, p);

            for (Entry<String, String> data : playerData.entrySet()) {
                String label = data.getKey();
                String value = data.getValue();

                if (label.equalsIgnoreCase(nameLabel))
                    // already got their name
                    continue;
                else if (label.equalsIgnoreCase(woodLabel))
                    o2p.setWandWood(value);
                else if (label.equalsIgnoreCase(coreLabel))
                    o2p.setWandCore(value);
                else if (label.equalsIgnoreCase(soulsLabel)) {
                    Integer souls = Ollivanders2API.common.integerFromString(value);
                    if (souls != null)
                        o2p.setSouls(souls);
                }
                else if (label.equalsIgnoreCase(foundWandLabel)) {
                    Boolean foundWand = Ollivanders2API.common.booleanFromString(value);
                    if (foundWand != null)
                        o2p.initFoundWand(foundWand);
                }
                else if (label.equalsIgnoreCase(masterSpellLabel)) {
                    O2SpellType spellType = O2SpellType.spellTypeFromString(value);
                    if (spellType != null)
                        o2p.setMasterSpell(spellType);
                }
                else if (label.equalsIgnoreCase(lastSpellLabel)) {
                    O2SpellType lastSpell = O2SpellType.spellTypeFromString(value);
                    if (lastSpell != null)
                        o2p.setLastSpell(lastSpell);
                }
                else if (label.equalsIgnoreCase(priorIncantatumLabel)) {
                    O2SpellType prior = O2SpellType.spellTypeFromString(value);
                    if (prior != null)
                        o2p.setPriorIncantatem(prior);
                }
                else if (label.equalsIgnoreCase(animagusLabel)) {
                    EntityType animagus = Ollivanders2API.entityCommon.entityTypeFromString(value);
                    if (animagus != null)
                        o2p.setAnimagusForm(animagus);
                }
                else if (label.equalsIgnoreCase(animagusColorLabel))
                    o2p.setAnimagusColor(value);
                else if (label.equalsIgnoreCase(muggleLabel)) {
                    Boolean muggle = Ollivanders2API.common.booleanFromString(value);
                    if (muggle != null)
                        o2p.setMuggle(muggle);
                }
                else if (label.equalsIgnoreCase(yearLabel)) {
                    Integer y = Ollivanders2API.common.integerFromString(value);
                    if (y != null) {
                        Year year = Year.getYearByValue(y);

                        if (year != null)
                            o2p.setYear(year);
                    }
                }
                else if (label.startsWith(O2Effects.effectLabelPrefix))
                    playerEffects.deserializeEffect(pid, label, value);
                else if (label.startsWith(spellLabelPrefix)) {
                    String spellName = label.replaceFirst(spellLabelPrefix, "");
                    deserializeSpell(o2p, spellName, value);
                }
                else if (label.startsWith(potionLabelPrefix)) {
                    String potionName = label.replaceFirst(potionLabelPrefix, "");
                    deserializePotion(o2p, potionName, value);
                }
                else
                    // assume it is a spell
                    deserializeSpell(o2p, label, value);
            }

            // handle backwards compatibility for older save files when the plugin changes
            o2p.fix();

            deserializedMap.put(pid, o2p);
            recordCount = recordCount + 1;
        }

        return deserializedMap;
    }

    /**
     * Deserialize a spell and set spell experience on the player.
     *
     * @param o2p   the player this spell count is for
     * @param label the serialized name of the spell
     * @param value the serialized count of spell experience
     */
    private void deserializeSpell(@NotNull O2Player o2p, @NotNull String label, @NotNull String value) {
        O2SpellType spellType = O2SpellType.spellTypeFromString(label);
        if (spellType == null)
            return;

        Integer count = Ollivanders2API.common.integerFromString(value);
        if (count != null)
            o2p.setSpellCount(spellType, count);
    }

    /**
     * Deserialize a potion and set potion experience on the player.
     *
     * @param o2p   the player this potion count is for
     * @param label the serialized name of the potion
     * @param value the serialized count of potion experience
     */
    private void deserializePotion(@NotNull O2Player o2p, @NotNull String label, @NotNull String value) {
        O2PotionType potionType = O2PotionType.potionTypeFromString(label);
        if (potionType == null)
            return;

        Integer count = Ollivanders2API.common.integerFromString(value);
        if (count != null)
            o2p.setPotionCount(potionType, count);
    }

    /**
     * Correct a player's animagus color form - for use when valueOf() on the current value fails
     *
     * @param pid              the player to correct
     * @param correctedVariant the corrected value
     */
    public void fixPlayerAnimagusColorVariant(@NotNull UUID pid, @NotNull String correctedVariant) {
        O2Player player = getPlayer(pid);

        if (player != null)
            player.setAnimagusColor(correctedVariant);
    }

    /**
     * Commands related to players.
     *
     * @param sender the command sender
     * @param args   the args for the command
     * @return true if the command succeeded
     */
    public boolean runSummary(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            common.printDebugMessage("Running playerSummary for sender: " + sender.getName(), null, null, false);
            playerSummary(sender, (Player) sender);
            return true;
        }
        else if (args.length == 2 && sender.hasPermission("Ollivanders2.admin")) {
            Player target = p.getServer().getPlayer(args[1]);
            if (target != null) {
                common.printDebugMessage("Running playerSummary for player: " + target.getName(), null, null, false);
                playerSummary(sender, target);
                return true;
            }
        }

        usageSummary(sender);
        return true;
    }

    /**
     * Usage summary for player commands
     *
     * @param sender the command sender
     */
    public void usageSummary(CommandSender sender) {
        if (sender.hasPermission("Ollivanders2.admin"))
            usageSummaryAdmin(sender);
        else
            usageSummaryPlayer(sender);
    }

    /**
     * Usage summary for players
     *
     * @param sender the command sender
     */
    public void usageSummaryPlayer(CommandSender sender) {
        sender.sendMessage(Ollivanders2.chatColor
                + "Usage: /olli summary - gives a summary of wand type, house, year, and known spells\n");
    }

    /**
     * Usage summary for admins
     *
     * @param sender the command sender
     */
    public void usageSummaryAdmin(CommandSender sender) {
        sender.sendMessage(Ollivanders2.chatColor
                + "\nUsage:"
                + "\n/olli summary - gives a summary of your player info such as wand type, house, year, and known spells"
                + "\n/olli summary <player name> - gives a summary of the player's info such as wand type, house, year, and known spells\n");
    }

    /**
     * The year subCommand for managing everything related to years.
     *
     * @param sender the player that issued the command
     * @param args   the arguments for the command, if any
     * @return true unless an error occurred
     */
    public boolean runYear(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission("Ollivanders2.admin"))
            return false;

        if (!Ollivanders2.useYears) {
            sender.sendMessage(Ollivanders2.chatColor
                    + "Years are not currently enabled for your server."
                    + "\nTo enable years, update the Ollivanders2 config.yml setting to true and restart your server."
                    + "\nFor help, see our documentation at https://github.com/Azami7/Ollivanders2/wiki");

            return true;
        }

        if (args.length == 2) {
            String playerName = args[1];
            if (playerName.isEmpty()) {
                usageYear(sender);
                return true;
            }

            O2Player o2p = getPlayer(playerName);
            if (o2p != null)
                sender.sendMessage(Ollivanders2.chatColor + "Player " + playerName + " is a " + o2p.getYear().getDisplayText() + " year.");
            else
                sender.sendMessage(Ollivanders2.chatColor + "Unable to find player.");

            return true;
        }
        else if (args.length > 2) {
            String subCommand = args[1];

            if (subCommand.equalsIgnoreCase("set")) {
                if (args.length < 4) {
                    usageYear(sender);
                    return true;
                }

                return runYearSet(sender, args[2], args[3]);
            }
            else if (subCommand.equalsIgnoreCase("promote"))
                return runYearChange(sender, args[2], 1);
            else if (subCommand.equalsIgnoreCase("demote"))
                return runYearChange(sender, args[2], -1);
        }

        usageYear(sender);

        return true;
    }

    /**
     * Run the command to set a player's year
     *
     * @param sender       the player that issued the command
     * @param targetPlayer the player to set the year for
     * @param targetYear   the year to set for the player
     * @return true unless an error occurred
     */
    private boolean runYearSet(@NotNull CommandSender sender, @NotNull String targetPlayer, @NotNull String targetYear) {
        if (!sender.hasPermission("Ollivanders2.admin"))
            return false;

        if (targetPlayer.isEmpty() || targetYear.isEmpty()) {
            usageYear(sender);
            return true;
        }

        O2Player o2p = getPlayer(targetPlayer);
        if (o2p == null) {
            sender.sendMessage(Ollivanders2.chatColor + "Unable to find a player named " + targetPlayer + ".\n");
            common.printDebugMessage("O2Player is null", null, null, true);
            return true;
        }

        Year year = stringToYear(targetYear);

        if (year != null)
            o2p.setYear(year);

        return true;
    }

    /**
     * Run promote and demote year commands
     *
     * @param sender       the player that issued the command
     * @param targetPlayer the player to promote or demote
     * @param yearChange   the year to change the player to
     * @return true unless an error occurred
     */
    private boolean runYearChange(@NotNull CommandSender sender, @NotNull String targetPlayer, int yearChange) {
        if (!sender.hasPermission("Ollivanders2.admin"))
            return false;

        O2Player o2p = getPlayer(targetPlayer);
        if (o2p == null) {
            sender.sendMessage(Ollivanders2.chatColor + "Unable to find player.");

            return true;
        }

        int y = o2p.getYear().ordinal() + yearChange;
        if (y >= 0 && y < 7) {
            Year year = Year.getYearByValue(y);

            if (year != null)
                o2p.setYear(year);
        }
        return true;
    }

    /**
     * Get a year from a string.
     *
     * @param yearStr the year as a string
     * @return the Year if parsed, null otherwise
     */
    @Nullable
    private Year stringToYear(@NotNull String yearStr) {
        int y;
        try {
            y = Integer.parseInt(yearStr);
        }
        catch (NumberFormatException e) {
            return null;
        }

        if (y < 1 || y > 7)
            return null;
        return Year.getYearByValue(y);
    }

    /**
     * Display the usage message for /ollivanders2 year set
     *
     * @param sender the player that issued the command
     */
    private void usageYear(@NotNull CommandSender sender) {
        sender.sendMessage(Ollivanders2.chatColor
                + "Year commands: "
                + "\n/olli year <player name> - tells you the year or a player"
                + "\n/olli year set <player name> <1-7> - sets a player's year, years must be between 1 and 7"
                + "\n/olli year promote <player name> - increases a player's year by 1 year"
                + "\n/olli year demote <player name> - decreases a player's year by 1 year\n");
    }

    /**
     * Displays a summary of player info for the Ollivanders2 plugin
     *
     * @param sender the player who issued the command
     * @param player the player to send the info for
     */
    public void playerSummary(@NotNull CommandSender sender, @NotNull Player player) {
        O2Player o2p = getPlayer(player.getUniqueId());

        if (o2p == null) {
            sender.sendMessage(Ollivanders2.chatColor + "Unable to find player.");
            return;
        }

        StringBuilder summary = new StringBuilder();

        summary.append("Player summary:\n\n");

        // are they a muggle?
        if (o2p.isMuggle()) {
            common.printDebugMessage("Player " + player.getName() + " is a muggle", null, null, false);
            summary.append("is a Muggle.\n\n");
        }
        else {
            summary.append("is a Wizard.\n\n");

            // wand type
            if (o2p.foundWand()) {
                String wandlore = Ollivanders2API.getItems().getWands().createLore(o2p.getDestinedWandWood(), o2p.getDestinedWandCore());
                summary.append("\nWand Type: ").append(wandlore);

                O2SpellType masterSpell = o2p.getMasterSpell();
                if (masterSpell != null)
                    summary.append("\nMaster Spell: ").append(masterSpell.getSpellName());

                summary.append("\n");
            }

            // sorted
            if (O2Houses.useHouses) {
                summary.append("\nHouse: ");
                String house = null;
                if (Ollivanders2API.getHouses().isSorted(player)) {
                    O2HouseType houseType = Ollivanders2API.getHouses().getHouse(player);
                    if (houseType != null)
                        house = houseType.getName();
                }

                if (house != null)
                    summary.append(house);
                else
                    summary.append("not sorted");

                summary.append("\n");
            }

            //year
            if (Ollivanders2.useYears)
                summary.append("\nYear: ").append(o2p.getYear().getDisplayText()).append("\n");

            //animagus
            if (o2p.isAnimagus()) {
                EntityType animagusForm = o2p.getAnimagusForm();
                if (animagusForm != null)
                    summary.append("\nAnimagus Form: ").append(Ollivanders2Common.enumRecode(animagusForm.toString()));
            }

            // spells
            Map<O2SpellType, Integer> knownSpells = o2p.getKnownSpells();

            if (!knownSpells.isEmpty()) {
                summary.append("\n\nKnown Spells and Spell Level:");

                for (O2SpellType spellType : O2SpellType.values()) {
                    if (knownSpells.containsKey(spellType) && Ollivanders2API.getSpells().isLoaded(spellType))
                        summary.append("\n* ").append(spellType.getSpellName()).append(" ").append(knownSpells.get(spellType).toString());
                }
            }
            else {
                if (sender.getName().equalsIgnoreCase(player.getName()))
                    summary.append("\n\nYou have not learned any spells.");
                else
                    summary.append("\n\n").append(player.getName()).append(" has not learned any spells.");
            }

            // potions
            Map<O2PotionType, Integer> knownPotions = o2p.getKnownPotions();
            if (!knownPotions.isEmpty()) {
                summary.append("\n\nKnown potions and Potion Level:");

                for (O2PotionType potionType : O2PotionType.values()) {
                    if (knownPotions.containsKey(potionType) && Ollivanders2API.getPotions().isLoaded(potionType))
                        summary.append("\n* ").append(potionType.getPotionName()).append(" ").append(knownPotions.get(potionType).toString());
                }
            }
            else {
                if (sender.getName().equalsIgnoreCase(player.getName()))
                    summary.append("\n\nYou have not learned any potions.");
                else
                    summary.append("\n\n").append(player.getName()).append(" has not learned any potions.");
            }
        }

        // effects
        if (sender.hasPermission("Ollivanders2.admin")) {
            List<O2EffectType> effects = Ollivanders2API.getPlayers().playerEffects.getEffects(o2p.getID());
            summary.append("\n\nAffected by:\n");

            if (effects.isEmpty())
                summary.append("Nothing");
            else {
                for (O2EffectType effectType : effects) {
                    summary.append(Ollivanders2Common.enumRecode(effectType.toString())).append("\n");
                }
            }

            summary.append("\n");
        }

        sender.sendMessage(Ollivanders2.chatColor + summary.toString());
    }

    /**
     * The year subCommand for managing everything related to animagus.
     *
     * @param sender the player that issued the command
     * @param args   the arguments for the command, if any
     * @return true unless an error occurred
     */
    public boolean runAnimagus(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!sender.hasPermission("Ollivanders2.admin"))
            return false;

        if (args.length != 2) {
            usageAnimagus(sender);
            return true;
        }

        String targetPlayer = args[1];
        O2Player o2p = getPlayer(targetPlayer);
        if (o2p == null) {
            usageAnimagus(sender);
            return true;
        }

        if (o2p.isAnimagus()) {
            sender.sendMessage(Ollivanders2.chatColor + targetPlayer + " is already an animagus.");
            return true;
        }

        o2p.setIsAnimagus();

        sender.sendMessage(Ollivanders2.chatColor + targetPlayer + " is now an animagus.");
        return true;
    }

    /**
     * Usage message for the animagus command.
     *
     * @param sender the command sender
     */
    private void usageAnimagus(CommandSender sender) {
        sender.sendMessage(Ollivanders2.chatColor
                + "\nUsage:"
                + "\n/olli animagus <player name> - make a player an animagus, has no effect if they already are\n");
    }
}