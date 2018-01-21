package net.pottercraft.Ollivanders2;

import net.pottercraft.Ollivanders2.Spell.Spells;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.ArrayList;

/**
 * O2Players
 *
 * @since 2.2.5
 * @author Azami7
 */
public class O2Players
{
   private Map<UUID, O2Player> O2PlayerMap = new HashMap<>();

   private Ollivanders2 p;
   private Ollivanders2Common common;

   private String nameLabel = "Name";
   private String woodLabel = "Wood";
   private String coreLabel = "Core";
   private String soulsLabel = "Souls";
   private String invisibleLabel = "Invisible";
   private String muggletonLabel = "Muggleton";
   private String foundWandLabel = "Found_Wand";
   private String masterSpellLabel = "Master_Spell";

   public O2Players (Ollivanders2 plugin)
   {
      p = plugin;
      common = new Ollivanders2Common(p);
   }

   /**
    * Add a new O2Player.
    *
    * @param pid
    * @param name
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
    * @param pid
    * @param o2p
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
    * @param pid
    * @return the O2Player if found, null otherwise.
    */
   public O2Player getPlayer (UUID pid)
   {
      if (O2PlayerMap.containsKey(pid))
      {
         return O2PlayerMap.get(pid);
      }
      else
      {
         return null;
      }
   }

   /**
    * Get a list of all player unique ids.
    *
    * @return
    */
   public ArrayList<UUID> getPlayerIDs ()
   {
      ArrayList<UUID> ids = new ArrayList<>();

      for (UUID id : O2PlayerMap.keySet())
      {
         ids.add(id);
      }

      return ids;
   }

   public void saveO2Players()
   {
      // serialize the player map
      Map <String, Map<String, String>> serializedMap = serializeO2Players(O2PlayerMap);

      GsonDataPersistenceLayer gsonLayer = new GsonDataPersistenceLayer(p);
      gsonLayer.writeO2Players(serializedMap);
   }

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

      p.getLogger().info("Checking for legacy OPlayers save file...");
      Map<UUID, OPlayer> OPlayerMap = new HashMap<>();
      try
      {
         p.getLogger().info("Loading save file OPlayerMap.bin...");
         OPlayerMap = (HashMap<UUID, OPlayer>) Ollivanders2.SLAPI.load("plugins/Ollivanders2/OPlayerMap.bin");
         p.getLogger().info("Found " + OPlayerMap.size() + " legacy players.");
      }
      catch (Exception e)
      {
         p.getLogger().warning("Did not find OPlayerMap.bin");
      }

      updateLegacyPlayers(OPlayerMap);
   }

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
         o2p.setMuggleton(player.isMuggleton());
         o2p.setWandSpell(player.getSpell());

         Map <Spells, Integer> spells = player.getSpellCount();
         for (Entry<Spells, Integer> s : spells.entrySet())
         {
            Spells spell = s.getKey();
            int count = s.getValue().intValue();

            if (count > 0)
            {
               o2p.setSpellCount(spell, count);
            }
         }

         O2PlayerMap.put(pid, o2p);
         if (p.debug)
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
    * @return
    */
   private Map <String, Map<String, String>> serializeO2Players (Map<UUID, O2Player> o2PlayerMap)
   {
      Map <String, Map<String, String>> serializedMap = new HashMap<>();

      if (p.debug)
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
         if (p.debug)
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
         Integer souls = new Integer(o2p.getSouls());
         playerData.put(soulsLabel, souls.toString());

         /**
          * Invisible
          */
         if (o2p.isInvisible())
         {
            Boolean invisible = new Boolean(true);
            playerData.put(invisibleLabel, invisible.toString());
         }

         /**
          * Muggleton
          */
         if (o2p.isMuggleton())
         {
            Boolean muggleton = new Boolean(true);
            playerData.put(muggletonLabel, muggleton.toString());
         }

         /**
          * Found Wand
          */
         if (o2p.foundWand())
         {
            Boolean foundWand = new Boolean(true);
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
    * @param map
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
                  player.setSouls(souls.intValue());
               }
            }
            else if (label.equalsIgnoreCase(muggletonLabel))
            {
               Boolean muggleton = common.booleanFromString(value);
               if (muggleton != null)
               {
                  player.setMuggleton(muggleton.booleanValue());
               }
            }
            else if (label.equalsIgnoreCase(invisibleLabel))
            {
               Boolean invisible = common.booleanFromString(value);
               if (invisible != null)
               {
                  player.setInvisible(invisible.booleanValue());
               }
            }
            else if (label.equalsIgnoreCase(foundWandLabel))
            {
               Boolean foundWand = common.booleanFromString(value);
               if (foundWand != null)
               {
                  player.setFoundWand(foundWand.booleanValue());
               }
            }
            else if (label.equalsIgnoreCase(masterSpellLabel))
            {
               Spells spell = common.spellsFromString(value);
               if (spell != null)
               {
                  player.setMasterSpell(spell);
               }
            }
            else
            {
               // it is a spell
               Spells spell = common.spellsFromString(label);
               if (spell == null)
               {
                  continue;
               }

               Integer count = common.integerFromString(value);
               if (count != null)
               {
                  player.setSpellCount(spell, count.intValue());
               }
            }
         }

         deserializedMap.put(pid, player);
         if (p.debug)
            p.getLogger().info("Loaded player " + player.getPlayerName());
      }

      return deserializedMap;
   }
}
