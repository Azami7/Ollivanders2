package net.pottercraft.ollivanders2.player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.pottercraft.ollivanders2.common.EntityCommon;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.O2Color;
import net.pottercraft.ollivanders2.item.wand.O2WandCoreType;
import net.pottercraft.ollivanders2.item.wand.O2WandWoodType;
import net.pottercraft.ollivanders2.player.events.OllivandersPlayerFoundWandEvent;
import net.pottercraft.ollivanders2.spell.O2SpellType;
import net.pottercraft.ollivanders2.spell.O2Spell;
import net.pottercraft.ollivanders2.potion.O2PotionType;

import org.bukkit.Material;
import org.bukkit.entity.Cat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Ollivanders2 player
 */

public class O2Player {
    /**
     * Destined wand wood material name
     */
    private String wandWood = null;

    /**
     * Destined wand core material name
     */
    private String wandCore = null;

    /**
     * Player display name
     */
    private String playerName;

    /**
     * Player minecraft UUID, this is their primary identifier in Ollivanders2
     */
    private final UUID pid;

    /**
     * The MC plugin callback
     */
    private final Ollivanders2 p;

    /**
     * Common functions for Ollivanders2
     */
    private final Ollivanders2Common common;

    /**
     * A map of all the spells a player knows and the cast count.
     */
    private final Map<O2SpellType, Integer> knownSpells = new HashMap<>();

    /**
     * A map of all the potions a player knows and the brew count.
     */
    private final Map<O2PotionType, Integer> knownPotions = new HashMap<>();

    /**
     * A map of the recent spells a player has cast and their cast timestamp
     */
    private final Map<O2SpellType, Long> recentSpells = new HashMap<>();

    /**
     * The spell loaded into the wand for casting with left click
     */
    private O2SpellType wandSpell = null;

    /**
     * The last spell cast by this player's wand
     */
    private O2SpellType priorIncantatem = null;

    /**
     * The last spell cast by this player
     */
    private O2SpellType lastSpell = null;

    /**
     * The mastered spell set for silent casting - is cast anytime a player left-clicks their wand in their primary hand.
     *
     * @see <a href="https://github.com/Azami7/Ollivanders2/wiki/Configuration#non-verbal-spell-casting">https://github.com/Azami7/Ollivanders2/wiki/Configuration#non-verbal-spell-casting</a>
     */
    private O2SpellType masterSpell = null;

    /**
     * The list of mastered spells - spells with > 100 cast count
     */
    private final ArrayList<O2SpellType> masteredSpells = new ArrayList<>();

    /**
     * The number of souls this user has collected (for horcruxes)
     */
    private int souls = 0;

    /**
     * Whether the player has found their destined wand yet
     */
    private boolean foundWand = false;

    /**
     * The player's animagus form, if they are an animagus
     */
    private EntityType animagusForm = null;

    /**
     * The color variant for the animagus form
     */
    private String animagusColor = null;

    /**
     * Whether the player is a Muggle (i.e. has not found their destined wand), true for all players initially
     */
    private boolean isMuggle = true;

    /**
     * The player's year in school
     */
    private Year year = Year.YEAR_1;

    /**
     * common player functions
     */
    private final O2PlayerCommon o2PlayerCommon;

    /**
     * Constructor.
     *
     * @param id     the UUID of the player
     * @param name   the name of the player
     * @param plugin a reference to the plugin
     */
    public O2Player(@NotNull UUID id, @NotNull String name, @NotNull Ollivanders2 plugin) {
        p = plugin;
        playerName = name;
        pid = id;
        o2PlayerCommon = new O2PlayerCommon(plugin);
        common = new Ollivanders2Common(plugin);

        // set destined wand
        initDestinedWand();
    }

    /**
     * Initialize the player's destined wand seeded with their pid
     */
    private void initDestinedWand() {
        // set destined wand
        int seed = Math.abs(pid.hashCode());

        // get destined wand wood and core
        wandWood = O2WandWoodType.getWandWoodBySeed(seed);
        wandCore = O2WandCoreType.getWandCoreBySeed(seed);
    }

    /**
     * Get the player's destined wand wood type.
     *
     * @return the player's destined wand wood type
     */
    @NotNull
    public String getDestinedWandWood() {
        if (wandWood == null)
            initDestinedWand();

        return wandWood;
    }

    /**
     * Get the player's destined wand core type.
     *
     * @return the player's destined wand core type
     */
    @NotNull
    public String getDestinedWandCore() {
        if (wandCore == null)
            initDestinedWand();

        return wandCore;
    }

    /**
     * Set the player's destined wand wood type. This overrides the current value.
     *
     * @param wood sets the destined wand wood type
     */
    public void setWandWood(@NotNull String wood) {
        if (O2WandWoodType.getAllWandWoodsByName().contains(wood)) {
            wandWood = wood;
        }
    }

    /**
     * Set the player's destined wand core type. This overrides the current value.
     *
     * @param core set the destined wand core type
     */
    public void setWandCore(@NotNull String core) {
        if (O2WandCoreType.getAllWandCoreNames().contains(core)) {
            wandCore = core;
        }
    }

    /**
     * Get the name of this player for use in commands like listing out house membership. Since player names
     * can change, this should not be used to identify a player. Instead, use the UUID of player and the O2Players
     * map to find their O2Player object.
     *
     * @return the player's name
     */
    @NotNull
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Sets the name of this player for use in commands like listing out house membership.
     *
     * @param name the name to set for this player
     */
    public void setPlayerName(@NotNull String name) {
        playerName = name;
    }

    /**
     * Get the casting count for a spell
     *
     * @param spell the spell to get a count for
     * @return the number of times a player has cast this spell
     */
    public int getSpellCount(@NotNull O2SpellType spell) {
        int count = 0;

        if (knownSpells.containsKey(spell)) {
            count = knownSpells.get(spell);
        }

        return count;
    }

    /**
     * Get the brewing count for a potion
     *
     * @param potionType the spell to get a count for
     * @return the number of times a player has cast this spell
     */
    public int getPotionCount(@NotNull O2PotionType potionType) {
        int count = 0;

        if (knownPotions.containsKey(potionType)) {
            count = knownPotions.get(potionType);
        }

        return count;
    }

    /**
     * Get the casting count for a spell
     *
     * @param spellType the spell to get a count for
     * @return the number of times a player has cast this spell
     */
    @NotNull
    public Long getSpellLastCastTime(@NotNull O2SpellType spellType) {
        Long count = (long) 0;

        if (recentSpells.containsKey(spellType)) {
            count = recentSpells.get(spellType);
        }

        return count;
    }

    /**
     * Get the list of known spells for this player.
     *
     * @return a map of all the known spells and the spell count for each.
     */
    @NotNull
    public Map<O2SpellType, Integer> getKnownSpells() {
        return knownSpells;
    }

    /**
     * Get the list of known potions for this player.
     *
     * @return a map of all the known potions and potion brew count for each.
     */
    @NotNull
    public Map<O2PotionType, Integer> getKnownPotions() {
        return knownPotions;
    }

    /**
     * Get the list of recently cast spells for this player.
     *
     * @return a map of all recent spells and the time they were cast.
     */
    @NotNull
    public Map<O2SpellType, Long> getRecentSpells() {
        return recentSpells;
    }

    /**
     * Set the spell count for a spell. This will override the existing values for this spell and should
     * not be used when increment is intended.
     *
     * @param spell the spell to set the count for
     * @param count the count to set
     */
    public void setSpellCount(@NotNull O2SpellType spell, int count) {
        if (count >= 1) {
            knownSpells.put(spell, count);
        }
        else {
            knownSpells.remove(spell);
        }

        // remove spell from mastered list if level is less than 100
        if (count < 100) {
            removeMasteredSpell(spell);
        }
        // add spell to mastered list if level is at or over 100
        else {
            addMasteredSpell(spell);
        }
    }

    /**
     * Set the last spell cast by this player.
     *
     * @param spell the spell they most recently cast
     */
    void setLastSpell(@NotNull O2SpellType spell) {
        lastSpell = spell;
    }

    /**
     * Get the last spell cast by this player.
     *
     * @return the last spell cast
     */
    @Nullable
    public O2SpellType getLastSpell() {
        return lastSpell;
    }

    /**
     * Set the last spell successfully cast by this player's wand.
     *
     * @param spell the spell they most recently cast
     */
    public void setPriorIncantatem(@NotNull O2SpellType spell) {
        priorIncantatem = spell;
    }

    /**
     * Get the last spell cast by this player's wand.
     *
     * @return the last spell cast by this player with a wand
     */
    @Nullable
    public O2SpellType getPriorIncantatem() {
        return priorIncantatem;
    }

    /**
     * Set the potion count for a potion. This will override the existing values for this potion and should
     * not be used when increment is intended.
     *
     * @param potionType the potion to set the count for
     * @param count      the count to set
     */
    public void setPotionCount(@NotNull O2PotionType potionType, int count) {
        if (count >= 1)
            knownPotions.put(potionType, count);
        else
            knownPotions.remove(potionType);
    }

    /**
     * Set the most recent cast time for a spell. This will override the existing values for this spell.
     *
     * @param spellType the spell to set the time for
     */
    public void setSpellRecentCastTime(@NotNull O2SpellType spellType) {
        String spellClass = "net.pottercraft.ollivanders2.spell." + spellType;

        Constructor<?> c;
        try {
            c = Class.forName(spellClass).getConstructor(Ollivanders2.class);
            O2Spell s = (O2Spell) c.newInstance(p);

            recentSpells.put(spellType, System.currentTimeMillis() + s.getCoolDown());

            setLastSpell(spellType);
        }
        catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the wand's mastered spell.
     *
     * @param spell the mastered spell
     */
    public void setMasterSpell(@NotNull O2SpellType spell) {
        masterSpell = spell;
    }

    /**
     * Increment the spell count by 1.
     *
     * @param spellType the spell to increment
     */
    public void incrementSpellCount(@NotNull O2SpellType spellType) {
        common.printDebugMessage("Incrementing spell count for " + spellType, null, null, false);

        if (knownSpells.containsKey(spellType)) {
            int curCount = knownSpells.get(spellType);
            knownSpells.replace(spellType, curCount + 1);

            if (curCount + 1 >= 100)
                addMasteredSpell(spellType);
        }
        else
            knownSpells.put(spellType, 1);
    }

    /**
     * Increment the potion count by 1.
     *
     * @param potionType the potion to increment
     */
    public void incrementPotionCount(@NotNull O2PotionType potionType) {
        common.printDebugMessage("Incrementing potion count for " + potionType, null, null, false);

        if (knownPotions.containsKey(potionType)) {
            int curCount = knownPotions.get(potionType);
            knownPotions.replace(potionType, curCount + 1);
        }
        else
            knownPotions.put(potionType, 1);
    }

    /**
     * Resets the known spells for this player to none.
     */
    public void resetSpellCount() {
        knownSpells.clear();
        masteredSpells.clear();
        masterSpell = null;
    }

    /**
     * Resets the known spells for this player to none.
     */
    public void resetPotionCount() {
        knownPotions.clear();
    }

    /**
     * Get the spell currently loaded in to the player's wand.
     *
     * @return the loaded spell
     */
    @Nullable
    public O2SpellType getWandSpell() {
        if (wandSpell == null && masterSpell != null && Ollivanders2.enableNonVerbalSpellCasting)
            return masterSpell;

        return wandSpell;
    }

    /**
     * Loads a spell in to the player's wand.
     *
     * @param spell the spell to load
     */
    public void setWandSpell(@Nullable O2SpellType spell) {
        String spellName;
        if (spell == null)
            spellName = "null";
        else
            spellName = spell.getSpellName();
        common.printDebugMessage("O2Player.setWandSpell: setting wand spell to " + spellName, null, null, false);

        wandSpell = spell;
    }

    /**
     * Determine if player is a muggle. Admins are never muggles.
     *
     * @return true if they are a muggle, false otherwise
     */
    public boolean isMuggle() {
        Player player = p.getServer().getPlayer(pid);
        if (player != null && player.isOp())
            return false;

        return isMuggle;
    }

    /**
     * Set if a player is a muggle. Admins are never muggles.
     *
     * @param isMuggle true if the player is a muggle
     */
    public void setMuggle(boolean isMuggle) {
        Player player = p.getServer().getPlayer(pid);
        if (player != null && player.isOp())
            isMuggle = false;

        this.isMuggle = isMuggle;
    }

    /**
     * Get the number of souls this player has collected.
     *
     * @return the number of souls this player has collected
     */
    public int getSouls() {
        return souls;
    }

    /**
     * Set the number of souls this player has collected.
     *
     * @param souls the number of souls the player has collected
     */
    public void setSouls(int souls) {
        this.souls = souls;
    }

    /**
     * Add a soul to this player.
     */
    public void addSoul() {
        souls = souls + 1;
    }

    /**
     * Remove a soul from this player.
     */
    public void subtractSoul() {
        if (souls > 0)
            souls = souls - 1;
    }

    /**
     * Get the year this player is in.
     *
     * @return The year the player is in
     */
    @NotNull
    public Year getYear() {
        return year;
    }

    /**
     * Set the year this player is in.
     *
     * @param year The year to set them to
     */
    public void setYear(@NotNull Year year) {
        this.year = year;
    }

    /**
     * Reset the soul count to zero.
     */
    public void resetSouls() {
        souls = 0;
    }

    /**
     * Initializer for the foundWand class variable for loading the player from saved data. This is different from setFoundWand
     * which triggers a found wand event.
     *
     * @param found the value to set for foundWand
     */
    protected void initFoundWand(boolean found) {
        foundWand = found;
    }

    /**
     * Set whether the player has found their destined wand before. Once a player finds their destined wand they are also
     * no longer a muggle since they can do magic. This should not be called when loading from saved data, use initFoundWand().
     *
     * @param found set whether the player has found their destined wand
     */
    public void setFoundWand(boolean found) {
        if (found) {
            Player player = p.getServer().getPlayer(pid);
            if (player == null)
                return;

            OllivandersPlayerFoundWandEvent event = new OllivandersPlayerFoundWandEvent(player);

            p.getServer().getPluginManager().callEvent(event);
            common.printDebugMessage("Fired OllivandersPlayerFoundWandEvent", null, null, false);

            // once they have found their wand and can cast spells, they are no longer a muggle
            setMuggle(false);
        }

        foundWand = found;
    }

    /**
     * Has this player found their destined wand?
     *
     * @return true if they have, false if not
     */
    public boolean foundWand() {
        return foundWand;
    }

    /**
     * Returns this player's spell journal, a book with all known spells and their level.
     *
     * @return the player's spell journal
     */
    @Nullable
    public ItemStack getSpellJournal() {
        ItemStack spellJournal = new ItemStack(Material.WRITTEN_BOOK, 1);

        BookMeta bookMeta = (BookMeta) spellJournal.getItemMeta();

        if (bookMeta == null) {
            common.printDebugMessage("getSpellJournal book meta is null", null, null, false);
            return null;
        }

        bookMeta.setAuthor(playerName);
        bookMeta.setTitle("Spell Journal");

        StringBuilder content = new StringBuilder();
        content.append("Spell Journal\n\n");
        int lineCount = 2;
        for (Entry<O2SpellType, Integer> e : knownSpells.entrySet()) {
            // if we have done 14 lines, make a new page
            if (lineCount == 14) {
                bookMeta.addPage(content.toString());
                lineCount = 0;
                content = new StringBuilder();
            }

            // add a newline to all lines except the first
            if (lineCount != 0)
                content.append("\n");

            String spell = e.getKey().getSpellName();
            String count = e.getValue().toString();
            String line = spell + " " + count;
            content.append(spell).append(" ").append(count);

            lineCount = lineCount + 1;
            // ~18 characters per line, this will likely wrap
            if (line.length() > 18)
                lineCount = lineCount + 1;
        }

        bookMeta.addPage(content.toString());

        bookMeta.setGeneration(BookMeta.Generation.ORIGINAL);
        spellJournal.setItemMeta(bookMeta);

        return spellJournal;
    }

    /**
     * Add a spell to the list of spells that have a level of 100 or more. If this spell is the first mastered
     * spell then also load it to the wand master spell.
     *
     * @param spell the spell to add
     */
    private void addMasteredSpell(@NotNull O2SpellType spell) {
        if (spell != O2SpellType.AVADA_KEDAVRA) {
            if (!masteredSpells.contains(spell)) {
                if (masteredSpells.isEmpty())
                    // this is their first mastered spell, set it on their wand
                    masterSpell = spell;
                masteredSpells.add(spell);
            }
        }
    }

    /**
     * Remove a mastered spell when the level goes below 100 or is reset. If this spell is also set as the wand's
     * master spell, shift it to the next mastered spell or remove if there are none.
     *
     * @param spell the spell to remove
     */
    private void removeMasteredSpell(@NotNull O2SpellType spell) {
        if (masteredSpells.contains(spell)) {
            // first remove this from the loaded master spell if it is that spell
            if (masterSpell == spell) {
                if (masteredSpells.size() > 1)
                    shiftMasterSpell(false);
                else
                    masterSpell = null;
            }

            masteredSpells.remove(spell);
        }
    }

    /**
     * Shift the wand's master spell to the next spell.
     */
    @Deprecated
    public void shiftMasterSpell() {
        shiftMasterSpell(false);
    }

    /**
     * Shift the wand's master spell to the next spell.
     *
     * @param reverse if set to true, iterates backwards through spell list
     */
    public void shiftMasterSpell(boolean reverse) {
        // shift to the next spell if there is more than one mastered spell
        if (!masteredSpells.isEmpty()) {
            if (masterSpell == null || masteredSpells.size() == 1) {
                masterSpell = masteredSpells.getFirst();
            }
            else {
                int curSpellIndex = masteredSpells.indexOf(masterSpell);
                int nextSpellIndex;

                if (reverse)
                    nextSpellIndex = curSpellIndex + 1;
                else
                    nextSpellIndex = curSpellIndex - 1;

                // handle rollovers
                if (nextSpellIndex >= masteredSpells.size())
                    nextSpellIndex = 0;
                else if (nextSpellIndex < 0)
                    nextSpellIndex = masteredSpells.size() - 1;

                masterSpell = masteredSpells.get(nextSpellIndex);
            }
        }
        else
            masterSpell = null;
    }

    /**
     * Get the wand's master spell.
     *
     * @return the wand's master spell
     */
    @Nullable
    public O2SpellType getMasterSpell() {
        return masterSpell;
    }

    /**
     * Sets that this player is an Animagus.
     */
    public void setIsAnimagus() {
        if (animagusForm == null) {
            animagusForm = o2PlayerCommon.getAnimagusForm(pid);

            // determine color variations for certain types
            if (animagusForm == EntityType.CAT)
                animagusColor = EntityCommon.getRandomCatType(pid.hashCode()).toString();
            else if (animagusForm == EntityType.RABBIT)
                animagusColor = EntityCommon.getRandomRabbitType(pid.hashCode()).toString();
            else if (animagusForm == EntityType.WOLF || animagusForm == EntityType.SHULKER)
                animagusColor = O2Color.getRandomPrimaryDyeableColor(pid.hashCode()).getDyeColor().toString();
            else if (animagusForm == EntityType.HORSE)
                animagusColor = EntityCommon.getRandomHorseColor(pid.hashCode()).toString();
            else if (animagusForm == EntityType.LLAMA)
                animagusColor = EntityCommon.getRandomLlamaColor(pid.hashCode()).toString();
            else if (animagusForm == EntityType.FOX) {
                if ((pid.hashCode() % 10) == 1)
                    animagusColor = Fox.Type.SNOW.toString();
                else
                    animagusColor = Fox.Type.RED.toString();
            }
            else if (animagusForm == EntityType.SHEEP)
                animagusColor = EntityCommon.getRandomNaturalSheepColor(pid.hashCode()).toString();

            if (animagusColor != null)
                common.printDebugMessage("Color variation " + animagusColor, null, null, false);

            common.printDebugMessage(playerName + " is an animagus type " + animagusForm.toString(), null, null, false);
        }
    }

    /**
     * Sets the Animagus form for this player.
     *
     * @param type the player's Animagus form.
     */
    public void setAnimagusForm(@NotNull EntityType type) {
        if (o2PlayerCommon.isAllowedAnimagusForm(type))
            animagusForm = type;
        else {
            // they do not have an allowed type, reset their type
            animagusForm = null;
            animagusColor = null;
            setIsAnimagus();
        }
    }

    /**
     * Set the player's animagus color
     *
     * @param color the color/type as a string
     */
    void setAnimagusColor(@NotNull String color) {
        animagusColor = color;
    }

    /**
     * Get the Animagus form for this player.
     *
     * @return the player's Animagus form or null if they are not an Animagus
     */
    @Nullable
    public EntityType getAnimagusForm() {
        return animagusForm;
    }

    /**
     * Get the color variation for this animagus.
     *
     * @return the color variation or null if not applicable
     */
    @Nullable
    public String getAnimagusColor() {
        // in case color was set when player has no animagus form
        if (animagusForm != null)
            return animagusColor;
        else
            return null;
    }

    /**
     * Determine if this player an Animagus.
     *
     * @return true if they have an Animagus form, false otherwise
     */
    public boolean isAnimagus() {
        if (animagusForm != null)
            return true;

        return false;
    }

    /**
     * Get the player's UUID
     *
     * @return the UUID of the player this O2Player represents
     */
    @NotNull
    public UUID getID() {
        return pid;
    }

    /**
     * Do player onJoin set up
     */
    public void onJoin() {
        Ollivanders2API.getPlayers().playerEffects.onJoin(pid);
        Ollivanders2API.getProphecies().onJoin(pid);
    }

    /**
     * Do player onQuit clean up
     */
    public void onQuit() {
        Ollivanders2API.getPlayers().playerEffects.onQuit(pid);
    }

    /**
     * Do player onDeath actions
     */
    public void onDeath() {
        if (Ollivanders2.enableDeathExpLoss) {
            resetSpellCount();
            resetPotionCount();
        }

        resetSouls();
        Ollivanders2API.getPlayers().playerEffects.onDeath(pid);
        setWandSpell(null);
    }

    /**
     * Called after a player's data has been read from save to fix changes made to the plugin between versions.
     */
    protected void fix() {
        //
        // animagus form was an ocelot
        //
        // MC split Cat from Ocelot so now players that were actually Cats are not anymore
        if (animagusForm == EntityType.OCELOT) {
            fixOcelotAnimagus();
        }
    }

    /**
     * Fix where a player's animagus form is actually a Cat but is saved as Ocelot from pre-MC 1.14
     */
    private void fixOcelotAnimagus() {
        if (animagusForm != EntityType.OCELOT || animagusColor == null)
            return;

        if (animagusColor.contains("OCELOT")) {
            animagusColor = null;
            return;
        }

        animagusForm = EntityType.CAT;

        if (animagusColor.contains("BLACK"))
            animagusColor = Cat.Type.ALL_BLACK.toString();
        else if (animagusColor.contains("RED"))
            animagusColor = Cat.Type.RED.toString();
        else // siamese
            animagusColor = Cat.Type.SIAMESE.toString();
    }

    /**
     * Is this player currently online? For use when we have an O2Player and not a player (which has an isOnline() method in Spigot)
     *
     * @return true if they are online, false otherwise
     */
    public boolean isOnline() {
        Player player = p.getServer().getPlayer(pid);

        if (player == null)
            return false;
        else
            return player.isOnline();
    }
}