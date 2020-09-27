package net.pottercraft.ollivanders2.player;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.ArrayList;

import net.pottercraft.ollivanders2.effect.O2Effects;
import net.pottercraft.ollivanders2.GsonDAO;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.spell.O2SpellType;

import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * O2Players
 *
 * @since 2.2.5
 * @author Azami7
 */
public class O2Players
{
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
   private final JavaPlugin p;

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
   private final String inMuggletonLabel = "Muggleton";
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
   public O2Players(@NotNull JavaPlugin plugin)
   {
      p = plugin;

      playerEffects = new O2Effects(p);
   }

   /**
    * Add a new O2Player.
    *
    * @param pid  the UUID of this player
    * @param name the effectType of this player
    */
   public void addPlayer(@NotNull UUID pid, @NotNull String name)
   {
      O2Player o2p = new O2Player(pid, name, p);

      updatePlayer(pid, o2p);
   }

   /**
    * Update an existing O2Player.
    *
    * @param pid the UUID of this player
    * @param o2p the O2Player object for this player
    */
   public synchronized void updatePlayer(@NotNull UUID pid, @NotNull O2Player o2p)
   {
      O2PlayerMap.put(pid, o2p);
   }

   /**
    * Get an O2Player.
    *
    * @param pid the UUID of the player
    * @return the O2Player if found, null otherwise.
    */
   @Nullable
   public O2Player getPlayer(@NotNull UUID pid)
   {
      return O2PlayerMap.getOrDefault(pid, null);
   }

   /**
    * Remove a player
    *
    * @param pid the id of the player to remove
    */
   public void removePlayer(@NotNull UUID pid)
   {
      O2PlayerMap.remove(pid);
   }

   /**
    * Get a list of all player unique ids.
    *
    * @return a list of all known player MC UUIDs
    */
   @NotNull
   public ArrayList<UUID> getPlayerIDs()
   {
      return new ArrayList<>(O2PlayerMap.keySet());
   }

   /**
    * Write all players to the plugin config directory
    */
   public void saveO2Players()
   {
      // serialize the player map
      Map <String, Map<String, String>> serializedMap = serializeO2Players(O2PlayerMap);
      if (serializedMap == null)
      {
         p.getLogger().warning("Something went wrong serializing players, no records will be saved.");
         return;
      }

      GsonDAO gsonLayer = new GsonDAO(p);
      gsonLayer.writeSaveData(serializedMap, GsonDAO.o2PlayerJSONFile);
   }

   /**
    * Load players from save file
    */
   public void loadO2Players ()
   {
      GsonDAO gsonLayer = new GsonDAO(p);

      // load players from the save file, if it exists
      Map<String, Map<String, String>> serializedMap = gsonLayer.readSavedDataMapStringMap(GsonDAO.o2PlayerJSONFile);

      if (serializedMap != null)
      {
         Map<UUID, O2Player> deserializedMap = deserializeO2Players(serializedMap);

         if (deserializedMap != null && deserializedMap.size() > 0)
            O2PlayerMap = deserializedMap;

         p.getLogger().info("Loaded " + O2PlayerMap.size() + " saved players.");
      }
      else
      {
         p.getLogger().info("No saved O2Players.");
      }
   }

   /**
    * Serialize the O2Player to a list of key value pairs for all the data we want to save.
    *
    * Map structure:
    * Key - UUID
    * ArrayList {{
    *    Name : playerName
    *    FoundWand : foundWand
    *    WandWood : wandWood
    *    WandCore : wandCore
    *    Souls : souls
    *    Invisible : invisible
    *    Muggleton : muggleton
    *    Animagus : [EntityType]
    *    AnimagusColor : [EntityColorType]
    *    MasterSpell : [Spell Type]
    *    Year : [Year]
    *    [Spell] : [Count]
    *    [Potion] : [Count]
    *    [Effect] : [Duration]
    * }};
    * @param o2PlayerMap a map of all player MC UUIDs and corresponding O2Player object
    * @return all player data as a map of strings per player
    */
   @Nullable
   private Map<String, Map<String, String>> serializeO2Players(@NotNull Map<UUID, O2Player> o2PlayerMap)
   {
      Map<String, Map<String, String>> serializedMap = new HashMap<>();

      if (Ollivanders2.debug)
         p.getLogger().info("Serializing O2Players...");

      if (recordCount != 0)
      {
         if (o2PlayerMap.size() == 0)
         {
            p.getLogger().warning("Something went wrong and all player records lost, skipping save for safety.");
            return null;
         }

         if (o2PlayerMap.size() < (recordCount / 2))
         {
            p.getLogger().info("Player list is less than half the size when the server started, this may indicate a problem.");
         }
      }

      for (Map.Entry<UUID, O2Player> e : o2PlayerMap.entrySet())
      {
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
         playerData.put(woodLabel, o2p.getWandWood());
         playerData.put(coreLabel, o2p.getWandCore());

         //
         // Souls
         //
         int souls = o2p.getSouls();
         playerData.put(soulsLabel, Integer.toString(souls));

         //
         // Invisible
         //
         if (o2p.isInvisible())
         {
            playerData.put(invisibleLabel, Boolean.toString(true));
         }

         //
         // Muggleton
         //
         if (o2p.isInRepelloMuggleton())
         {
            playerData.put(inMuggletonLabel, Boolean.toString(true));
         }

         //
         // Found Wand
         //
         if (o2p.foundWand())
         {
            playerData.put(foundWandLabel, Boolean.toString(true));
         }

         //
         // Master Spell
         //
         O2SpellType spellType = o2p.getMasterSpell();
         if (spellType != null)
         {
            playerData.put(masterSpellLabel, spellType.toString());
         }

         //
         // Last Spell
         //
         O2SpellType lastSpell = o2p.getLastSpell();
         if (lastSpell != null)
         {
            playerData.put(lastSpellLabel, lastSpell.toString());
         }

         //
         // Prior Incantatum
         //
         O2SpellType prior = o2p.getPriorIncantatem();
         if (prior != null)
         {
            playerData.put(priorIncantatumLabel, prior.toString());
         }

         //
         // Animagus
         //
         EntityType animagus = o2p.getAnimagusForm();
         if (animagus != null)
         {
            playerData.put(animagusLabel, animagus.toString());
            String color = o2p.getAnimagusColor();
            if (color != null)
               playerData.put(animagusColorLabel, color);
         }

         //
         // Muggle
         //
         if (!o2p.isMuggle())
         {
            playerData.put(muggleLabel, Boolean.toString(false));
         }

         //
         // Year
         //
         Integer year = o2p.getYear().getIntValue();
         playerData.put(yearLabel, year.toString());

         //
         // Effects
         //
         Map<String, String> effects = Ollivanders2API.getPlayers(p).playerEffects.serializeEffects(pid);
         for (Entry<String, String> entry : effects.entrySet())
         {
            playerData.put(entry.getKey(), entry.getValue());
         }

         //
         // Spell Experience
         //
         Map<O2SpellType, Integer> knownSpells = o2p.getKnownSpells();
         for (Entry<O2SpellType, Integer> s : knownSpells.entrySet())
         {
            playerData.put(spellLabelPrefix + s.getKey().toString(), s.getValue().toString());
         }

         //
         // Potion Experience
         //
         Map<O2PotionType, Integer> knownPotions = o2p.getKnownPotions();
         for (Entry<O2PotionType, Integer> p : knownPotions.entrySet())
         {
            playerData.put(potionLabelPrefix + p.getKey().toString(), p.getValue().toString());
         }

         serializedMap.put(pid.toString(), playerData);
      }

      return serializedMap;
   }

   /**
    * Deserialize an O2Player map
    *
    * Map structure:
    * Key - UUID
    * ArrayList {{
    *    Name : playerName
    *    FoundWand : foundWand
    *    WandWood : wandWood
    *    WandCore : wandCore
    *    Souls : souls
    *    Invisible : invisible
    *    Muggleton : muggleton
    *    Animagus : [EntityType]
    *    AnimagusColor : [EntityColorType]
    *    MasterSpell : [Spell Type]
    *    Year : [Year]
    *    [Spell] : [Count]
    *    [Potion] : [Count]
    *    [Effect] : [Duration]
    * }};
    *
    * @param map a map of player data as strings
    * @return the deserialized map of O2Players, null if map could not be deserialized
    */
   @Nullable
   private Map<UUID, O2Player> deserializeO2Players(@NotNull Map<String, Map<String, String>> map)
   {
      Map<UUID, O2Player> deserializedMap = new HashMap<>();

      if (map.size() < 1)
      {
         return null;
      }

      for (Entry<String, Map<String, String>> e : map.entrySet())
      {
         UUID pid = Ollivanders2API.common.uuidFromString(e.getKey());
         if (pid == null)
         {
            continue;
         }

         Map<String, String> playerData = e.getValue();
         if (playerData == null || playerData.size() < 1)
         {
            continue;
         }

         // get player name
         String playerName = playerData.get(nameLabel);
         if (playerName == null || playerName.length() < 1)
         {
            continue;
         }

         O2Player o2p = new O2Player(pid, playerName, p);

         for (Entry<String, String> data : playerData.entrySet())
         {
            String label = data.getKey();
            String value = data.getValue();

            if (label.equalsIgnoreCase(nameLabel))
            {
               // already got their name
               continue;
            }
            else if (label.equalsIgnoreCase(woodLabel))
            {
               o2p.setWandWood(value);
            }
            else if (label.equalsIgnoreCase(coreLabel))
            {
               o2p.setWandCore(value);
            }
            else if (label.equalsIgnoreCase(soulsLabel))
            {
               Integer souls = Ollivanders2API.common.integerFromString(value);
               if (souls != null)
               {
                  o2p.setSouls(souls);
               }
            }
            else if (label.equalsIgnoreCase(inMuggletonLabel))
            {
               Boolean muggleton = Ollivanders2API.common.booleanFromString(value);
               if (muggleton != null)
               {
                  o2p.setInRepelloMuggleton(muggleton);
               }
            }
            else if (label.equalsIgnoreCase(invisibleLabel))
            {
               Boolean invisible = Ollivanders2API.common.booleanFromString(value);
               if (invisible != null)
               {
                  o2p.setInvisible(invisible);
               }
            }
            else if (label.equalsIgnoreCase(foundWandLabel))
            {
               Boolean foundWand = Ollivanders2API.common.booleanFromString(value);
               if (foundWand != null)
               {
                  o2p.setFoundWand(foundWand);
               }
            }
            else if (label.equalsIgnoreCase(masterSpellLabel))
            {
               O2SpellType spellType = O2SpellType.spellTypeFromString(value);
               if (spellType != null)
               {
                  o2p.setMasterSpell(spellType);
               }
            }
            else if (label.equalsIgnoreCase(lastSpellLabel))
            {
               O2SpellType lastSpell = O2SpellType.spellTypeFromString(value);
               if (lastSpell != null)
               {
                  o2p.setLastSpell(lastSpell);
               }
            }
            else if (label.equalsIgnoreCase(priorIncantatumLabel))
            {
               O2SpellType prior = O2SpellType.spellTypeFromString(value);
               if (prior != null)
               {
                  o2p.setPriorIncantatem(prior);
               }
            }
            else if (label.equalsIgnoreCase(animagusLabel))
            {
               EntityType animagus = Ollivanders2API.common.entityTypeFromString(value);
               if (animagus != null)
               {
                  o2p.setAnimagusForm(animagus);
               }
            }
            else if (label.equalsIgnoreCase(animagusColorLabel))
            {
               o2p.setAnimagusColor(value);
            }
            else if (label.equalsIgnoreCase(muggleLabel))
            {
               Boolean muggle = Ollivanders2API.common.booleanFromString(value);
               if (muggle != null)
               {
                  o2p.setMuggle(muggle);
               }
            }
            else if (label.equalsIgnoreCase(yearLabel))
            {
               Integer y = Ollivanders2API.common.integerFromString(value);
               if (y != null)
               {
                  Year year = O2PlayerCommon.intToYear(y);

                  if (year != null)
                     o2p.setYear(year);
               }
            }
            else if (label.startsWith(playerEffects.effectLabelPrefix))
            {
               playerEffects.deserializeEffect(pid, label, value);
            }
            else if (label.startsWith(spellLabelPrefix))
            {
               String spellName = label.replaceFirst(spellLabelPrefix, "");
               deserializeSpell(o2p, spellName, value);
            }
            else if (label.startsWith(potionLabelPrefix))
            {
               String potionName = label.replaceFirst(potionLabelPrefix, "");
               deserializePotion(o2p, potionName, value);
            }
            else
            {
               // assume it is a spell
               deserializeSpell(o2p, label, value);
            }
         }

         o2p.fix();

         deserializedMap.put(pid, o2p);
         recordCount++;
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
   private void deserializeSpell(@NotNull O2Player o2p, @NotNull String label, @NotNull String value)
   {
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
   private void deserializePotion(@NotNull O2Player o2p, @NotNull String label, @NotNull String value)
   {
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
   public void fixPlayerAnimagusColorVariant(@NotNull UUID pid, @NotNull String correctedVariant)
   {
      O2Player player = getPlayer(pid);

      if (player != null)
         player.setAnimagusColor(correctedVariant);
   }
}