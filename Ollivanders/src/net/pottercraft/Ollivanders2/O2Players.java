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

   private String nameLabel = "Name";
   private String woodLabel = "Wood";
   private String coreLabel = "Core";
   private String wandSpellLabel = "WandSpell";
   private String soulsLabel = "Souls";
   private String invisibleLabel = "Invisible";
   private String muggletonLabel = "Muggleton";

   public O2Players (Ollivanders2 plugin)
   {
      p = plugin;
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

      if (serializedMap == null || serializedMap.size() < 1)
      {
         p.getLogger().info("No saved O2Players, checking for legacy OPlayers save file.");
         Map<UUID, OPlayer> OPlayerMap = new HashMap<>();
         try
         {
            OPlayerMap = (HashMap<UUID, OPlayer>) Ollivanders2.SLAPI.load("plugins/Ollivanders2/OPlayerMap.bin");
            p.getLogger().info("Loaded save file OPlayerMap.bin");
         }
         catch (Exception e)
         {
            p.getLogger().warning("Did not find OPlayerMap.bin");
         }

         updateLegacyPlayers(OPlayerMap);
      }
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
         o2p.setSpell(player.getSpell());

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
    *    WandSpell: spell
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
          * Wand Spell
          */
         //OPlayer oPlayer = p.getOPlayer(pid);
         Spells wandSpell = o2p.getSpell();
         if (wandSpell != null)
         {
            playerData.put(wandSpellLabel, wandSpell.toString());
         }

         /**
          * Souls
          */
         Integer souls = new Integer(o2p.getSouls());
         playerData.put(soulsLabel, souls.toString());

         /**
          * Invisible
          */
         Boolean invisible = new Boolean(o2p.isInvisible());
         playerData.put(invisibleLabel, invisible.toString());

         /**
          * Muggleton
          */
         Boolean muggleton = new Boolean(o2p.isMuggleton());
         playerData.put(muggletonLabel, muggleton.toString());

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
}
