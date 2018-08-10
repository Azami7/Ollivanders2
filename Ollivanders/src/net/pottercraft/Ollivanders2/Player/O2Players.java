package net.pottercraft.Ollivanders2.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.ArrayList;

import net.pottercraft.Ollivanders2.GsonDataPersistenceLayer;
import net.pottercraft.Ollivanders2.OPlayer;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2Common;
import net.pottercraft.Ollivanders2.Spell.Spells;
import org.bukkit.entity.EntityType;

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
    * The MC plugin callback
    */
   private Ollivanders2 p;

   /**
    * Common O2 functions
    */
   private Ollivanders2Common common;

   /**
    * Labels for serializing player data
    */
   private String nameLabel = "Name";
   private String woodLabel = "Wood";
   private String coreLabel = "Core";
   private String soulsLabel = "Souls";
   private String invisibleLabel = "Invisible";
   private String inMuggletonLabel = "Muggleton";
   private String foundWandLabel = "Found_Wand";
   private String masterSpellLabel = "Master_Spell";
   private String animagusLabel = "Animagus";
   private String animagusColorLabel = "Animagus_Color";
   private String muggleLabel = "Muggle";
   private String yearLabel = "Year";

   /**
    * Constructor
    *
    * @param plugin the MC plugin
    */
   public O2Players (Ollivanders2 plugin)
   {
      p = plugin;
      common = new Ollivanders2Common(p);
   }

   /**
    * Add a new O2Player.
    *
    * @param pid the UUID of this player
    * @param name the name of this player
    */
   public void addPlayer (UUID pid, String name)
   {
      if (pid == null || name == null)
         return;

      O2Player o2p = new O2Player(pid, name, p);

      updatePlayer(pid, o2p);
   }

   /**
    * Update an existing O2Player.
    *
    * @param pid the UUID of this player
    * @param o2p the O2Player object for this player
    */
   public synchronized void updatePlayer (UUID pid, O2Player o2p)
   {
      if (o2p == null)
      {
         return;
      }

      if (!O2PlayerMap.containsKey(pid))
      {
         O2PlayerMap.put(pid, o2p);
         p.getLogger().info("Added new O2Player " + o2p.getPlayerName());
      }
      else
      {
         O2PlayerMap.replace(pid, o2p);
      }
   }

   /**
    * Get an O2Player.
    *
    * @param pid the UUID of the player
    * @return the O2Player if found, null otherwise.
    */
   public O2Player getPlayer (UUID pid)
   {
      return O2PlayerMap.getOrDefault(pid, null);
   }

   /**
    * Get a list of all player unique ids.
    *
    * @return
    */
   public ArrayList<UUID> getPlayerIDs ()
   {
      ArrayList<UUID> ids = new ArrayList<>();

      ids.addAll(O2PlayerMap.keySet());

      return ids;
   }

   /**
    * Write all players to the plugin config directory
    */
   public void saveO2Players()
   {
      // serialize the player map
      Map <String, Map<String, String>> serializedMap = serializeO2Players(O2PlayerMap);

      GsonDataPersistenceLayer gsonLayer = new GsonDataPersistenceLayer(p);
      gsonLayer.writeO2Players(serializedMap);
   }

   /**
    *
    */
   public void loadO2Players ()
   {
      GsonDataPersistenceLayer gsonLayer = new GsonDataPersistenceLayer(p);

      // load players from the save file, if it exists
      Map <String, Map<String, String>> serializedMap = gsonLayer.readO2Players();

      if (serializedMap != null)
      {
         Map<UUID, O2Player> deserializedMap = deserializeO2Players(serializedMap);

         if (deserializedMap != null && deserializedMap.size() > 0)
            O2PlayerMap = deserializedMap;
      }
      else
      {
         p.getLogger().info("No saved O2Players.");
      }
   }

   @Deprecated
   private void updateLegacyPlayers (Map<UUID, OPlayer> OPlayerMap)
   {
      for (Entry <UUID, OPlayer> e : OPlayerMap.entrySet())
      {
         UUID pid = e.getKey();
         if (O2PlayerMap.containsKey(pid))
         {
            continue;
         }

         String playerName = p.getServer().getOfflinePlayer(pid).getName();
         if (playerName == null)
         {
            continue;
         }

         OPlayer player = e.getValue();

         O2Player o2p = new O2Player(pid, playerName, p);

         o2p.setSouls(player.getSouls());
         o2p.setInvisible(player.isInvisible());
         o2p.setInRepelloMuggleton(player.isMuggleton());
         o2p.setWandSpell(player.getSpell());

         Map <Spells, Integer> spells = player.getSpellCount();
         for (Entry<Spells, Integer> s : spells.entrySet())
         {
            Spells spell = s.getKey();
            int count = s.getValue();

            if (count > 0)
            {
               o2p.setSpellCount(spell, count);
            }
         }

         O2PlayerMap.put(pid, o2p);
         if (Ollivanders2.debug)
         {
            p.getLogger().info("Loaded player " + o2p.getPlayerName());
         }
      }
   }

   /**
    * Serialize the O2Player to a list of key value pairs for all the data we want to save.
    *
    * Map structure:
    * Key - UUID
    * ArrayList {{
    *    Name : playerName
    *    WandWood : wandWood
    *    WandCore : wandCore
    *    Souls : souls
    *    Invisible : invisible
    *    Muggleton : muggleton
    *    [Spell] : [Count]
    * }};
    * @param o2PlayerMap
    * @return all player data as a map of strings per player
    */
   private Map <String, Map<String, String>> serializeO2Players (Map<UUID, O2Player> o2PlayerMap)
   {
      Map <String, Map<String, String>> serializedMap = new HashMap<>();

      if (Ollivanders2.debug)
         p.getLogger().info("Serializing O2Players...");

      for (Map.Entry<UUID, O2Player> e : o2PlayerMap.entrySet())
      {
         UUID pid = e.getKey();
         O2Player o2p = e.getValue();

         Map<String, String> playerData = new HashMap<>();

         /**
          * Name
          */
         String pName = o2p.getPlayerName();
         if (Ollivanders2.debug)
            p.getLogger().info("\tAdding " + pName + "...");

         playerData.put(nameLabel, pName);

         /**
          * Wand
          */
         playerData.put(woodLabel, o2p.getWandWood());
         playerData.put(coreLabel, o2p.getWandCore());

         /**
          * Souls
          */
         Integer souls = o2p.getSouls();
         playerData.put(soulsLabel, souls.toString());

         /**
          * Invisible
          */
         if (o2p.isInvisible())
         {
            Boolean invisible = true;
            playerData.put(invisibleLabel, invisible.toString());
         }

         /**
          * Muggleton
          */
         if (o2p.isInRepelloMuggleton())
         {
            Boolean muggleton = true;
            playerData.put(inMuggletonLabel, muggleton.toString());
         }

         /**
          * Found Wand
          */
         if (o2p.foundWand())
         {
            Boolean foundWand = true;
            playerData.put(foundWandLabel, foundWand.toString());
         }

         /**
          * Master Spell
          */
         Spells spell = o2p.getMasterSpell();
         if (spell != null)
         {
            playerData.put(masterSpellLabel, spell.toString());
         }

         /**
          * Animagus
          */
         EntityType animagus = o2p.getAnimagusForm();
         if (animagus != null)
         {
            playerData.put(animagusLabel, animagus.toString());
            String color = o2p.getAnimagusColor();
            if (color != null)
               playerData.put(animagusColorLabel, color);
         }

         /**
          * Spell Experience
          */
         Map<Spells, Integer> spells = o2p.getKnownSpells();
         if (spells != null)
         {
            for (Entry<Spells, Integer> s : spells.entrySet())
            {
               playerData.put(s.getKey().toString(), s.getValue().toString());
            }
         }

         /**
          * Muggle
          */
         if (!o2p.isMuggle())
         {
            Boolean muggle = false;
            playerData.put(muggleLabel, muggle.toString());
         }

         /**
          * Year
          */
         Integer year = O2PlayerCommon.yearToInt(o2p.getYear());
         playerData.put(yearLabel, year.toString());

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
    *    WandWood : wandWood
    *    WandCore : wandCore
    *    WandSpell: spell
    *    Souls : souls
    *    Invisible : invisible
    *    Muggleton : muggleton
    *    [Spell] : [Count]
    * }};
    *
    * @param map a map of player data as strings
    * @return the deserialized map of O2Players, null if map could not be deserialized
    */
   private Map<UUID, O2Player> deserializeO2Players (Map <String, Map<String, String>> map)
   {
      Map<UUID, O2Player> deserializedMap = new HashMap<>();

      if (map == null || map.size() < 1)
      {
         return null;
      }

      for (Entry<String, Map<String, String>> e : map.entrySet())
      {
         UUID pid = common.uuidFromString(e.getKey());
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

         O2Player player = new O2Player(pid, playerName, p);

         for (Entry<String, String> data : playerData.entrySet())
         {
            String label = data.getKey();
            String value = data.getValue();

            if (label.equalsIgnoreCase(nameLabel))
            {
               continue;
            }
            else if (label.equalsIgnoreCase(woodLabel))
            {
               player.setWandWood(value);
            }
            else if (label.equalsIgnoreCase(coreLabel))
            {
               player.setWandCore(value);
            }
            else if (label.equalsIgnoreCase(soulsLabel))
            {
               Integer souls = common.integerFromString(value);
               if (souls != null)
               {
                  player.setSouls(souls);
               }
            }
            else if (label.equalsIgnoreCase(inMuggletonLabel))
            {
               Boolean muggleton = common.booleanFromString(value);
               if (muggleton != null)
               {
                  player.setInRepelloMuggleton(muggleton);
               }
            }
            else if (label.equalsIgnoreCase(invisibleLabel))
            {
               Boolean invisible = common.booleanFromString(value);
               if (invisible != null)
               {
                  player.setInvisible(invisible);
               }
            }
            else if (label.equalsIgnoreCase(foundWandLabel))
            {
               Boolean foundWand = common.booleanFromString(value);
               if (foundWand != null)
               {
                  player.setFoundWand(foundWand);
               }
            }
            else if (label.equalsIgnoreCase(masterSpellLabel))
            {
               Spells spell = Spells.spellsFromString(value);
               if (spell != null)
               {
                  player.setMasterSpell(spell);
               }
            }
            else if (label.equalsIgnoreCase(animagusLabel))
            {
               EntityType animagus = common.entityTypeFromString(value);
               if (animagus != null)
               {
                  player.setAnimagusForm(animagus);
               }
            }
            else if (label.equalsIgnoreCase(animagusColorLabel))
            {
               player.setAnimagusColor(value);
            } else if (label.equalsIgnoreCase(muggleLabel)) {
               Boolean muggle = common.booleanFromString(value);
               if (muggle != null)
               {
                  player.setMuggle(muggle);
               }
            }
            else if (label.equalsIgnoreCase(yearLabel))
            {
               Integer year = common.integerFromString(value);
               if (year != null)
               {
                  player.setYear(O2PlayerCommon.intToYear(year));
               }
            }
            else
            {
               // it is a spell
               Spells spell = Spells.spellsFromString(label);
               if (spell == null)
               {
                  continue;
               }

               Integer count = common.integerFromString(value);
               if (count != null)
               {
                  player.setSpellCount(spell, count);
               }
            }
         }

         deserializedMap.put(pid, player);
         if (Ollivanders2.debug)
            p.getLogger().info("Loaded player " + player.getPlayerName());
      }

      return deserializedMap;
   }
}
