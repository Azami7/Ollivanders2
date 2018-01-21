package net.pottercraft.Ollivanders2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.pottercraft.Ollivanders2.Spell.Spells;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

/**
 * Ollivanders2 player
 *
 * This adds additional functionality beyond the original OPlayer. Since the original
 * file-based save used serialization, a new class was created for backwards compatibility.
 *
 * @author Azami7
 * @since 2.5.2
 */
public class O2Player
{
   private String wandWood = null;
   private String wandCore = null;
   private String playerName = null;
   private Ollivanders2 p = null;
   private Map<Spells, Integer> knownSpells = new HashMap<>();
   private List<OEffect> effects = new ArrayList<>();
   //This is the spell loaded into the wand for casting with left click
   private Spells wandSpell = null;
   private int souls = 0;
   private boolean invisible = false;
   private boolean muggleton = false;
   private boolean foundWand = false;

   /**
    * Wand wood types
    */
   private ArrayList<String> woodArray = new ArrayList<String>() {{
      add("Spruce");
      add("Jungle");
      add("Birch");
      add("Oak");
   }};

   /**
    * Wand core types
    */
   private ArrayList<String> coreArray = new ArrayList<String>() {{
      add("Spider Eye");
      add("Bone");
      add("Rotten Flesh");
      add("Gunpowder");
   }};

   private String loreConjunction = " and ";

   /**
    * Constructor.
    *
    * @param pid the UUID of the player
    * @param name the name of the player
    * @param plugin a reference to the plugin
    */
   public O2Player (UUID pid, String name, Ollivanders2 plugin)
   {
      p = plugin;
      playerName = name;

      // set destined wand
      initDestinedWand(pid);
   }

   /**
    * Initialize the player's destined wand seeded with their pid
    *
    * @param pid the player's uuid
    */
   private void initDestinedWand (UUID pid)
   {
      // set destined wand
      int seed = Math.abs(pid.hashCode()%16);
      int wood = seed/4;
      int core = seed%4;

      wandWood = woodArray.get(wood);
      wandCore = coreArray.get(core);
   }

   /**
    * Determine if a wand matches the player's destined wand type.
    *
    * @param stack the wand to check
    * @return true if is a wand and it matches, false otherwise
    */
   public boolean isDestinedWand (ItemStack stack)
   {
      if (wandWood == null || wandCore == null)
         return false;

      if (p.isWand(stack))
      {
         List<String> lore = stack.getItemMeta().getLore();
         String[] comps = lore.get(0).split(loreConjunction);

         if (wandWood.equalsIgnoreCase(comps[0]) && wandCore.equalsIgnoreCase(comps[1]))
         {
            foundWand = true;
            return true;
         }
         else
         {
            return false;
         }
      }
      else
      {
         return false;
      }
   }

   /**
    * Get the player's destined wand lore.
    *
    * @return
    */
   public String getDestinedWandLore ()
   {
      String wandLore = wandWood + loreConjunction + wandCore;

      return wandLore;
   }

   /**
    * Get the player's destined wand wood type.
    *
    * @return
    */
   public String getWandWood ()
   {
      return wandWood;
   }

   /**
    * Get the player's destined wand core type.
    *
    * @return
    */
   public String getWandCore ()
   {
      return wandCore;
   }

   /**
    * Set the player's destined wand wood type. This overrides the current value.
    *
    * @param wood
    */
   public void setWandWood (String wood)
   {
      if (woodArray.contains(wood))
      {
         wandWood = wood;
      }
   }

   /**
    * Set the player's destined wand core type. This overrides the current value.
    *
    * @param core
    */
   public void setWandCore (String core)
   {
      if (coreArray.contains(core))
      {
         wandCore = core;
      }
   }

   /**
    * Get the name of this player for use in commands like listing out house membership. Since player names
    * can change, this should not be used to identify a player. Instead, use the UUID of player and the O2Players
    * map to find their O2Player object.
    *
    * @return
    */
   public String getPlayerName ()
   {
      return playerName;
   }

   /**
    * Sets the name of this player for use in commands like listing out house membership.
    *
    * @param name
    */
   public void setPlayerName (String name)
   {
      playerName = name;
   }

   /**
    * Get the casting count for a spell
    *
    * @param spell the spell to get a count for
    * @return the number of times a player has cast this spell
    */
   public int getSpellCount (Spells spell)
   {
      int count = 0;

      if (knownSpells.containsKey(spell))
      {
         count = knownSpells.get(spell).intValue();
      }

      return count;
   }

   /**
    * Get the list of known spells for this player.
    *
    * @return a map of all the known spells and the spell count for each.
    */
   public Map<Spells, Integer> getKnownSpells ()
   {
      return knownSpells;
   }

   /**
    * Set the spell count for a spell. This will override the existing values for this spell and should
    * not be used when increment is intended.
    *
    * @param spell the spell to set the count for
    * @param count the count to set
    */
   public void setSpellCount (Spells spell, int count)
   {
      if (knownSpells.containsKey(spell))
      {
         knownSpells.replace(spell, new Integer(count));
      }
      else
      {
         knownSpells.put(spell, new Integer(count));
      }
   }

   /**
    * Increment the spell count by 1.
    *
    * @param spell the spell to increment
    */
   public void incrementSpellCount (Spells spell)
   {
      if (knownSpells.containsKey(spell))
      {
         int curCount = knownSpells.get(spell).intValue();

         knownSpells.replace(spell, new Integer(curCount + 1));
      }
      else
      {
         knownSpells.put(spell, new Integer(1));
      }
   }

   /**
    * Resets the known spells for this player to none.
    */
   public void resetSpellCount ()
   {
      knownSpells.clear();
   }

   /**
    * Get the spell currently loaded in to the player's wand.
    *
    * @return the loaded spell
    */
   public Spells getSpell ()
   {
      return wandSpell;
   }

   /**
    * Loads a spell in to the player's wand.
    *
    * @param spell the spell to load
    */
   public void setSpell (Spells spell)
   {
      wandSpell = spell;
   }

   /**
    * Determine if this player is invisible.
    *
    * @return true if the player is invisible, false otherwise.
    */
   public boolean isInvisible ()
   {
      return invisible;
   }

   /**
    * Set whether a player is invisible
    *
    * @param isInvisible true if the player is invisible, false if they are not
    */
   public void setInvisible(boolean isInvisible)
   {
      invisible = isInvisible;
   }

   /**
    * Determine if the player is a Muggleton.
    *
    * @return true if they are a muggleton, false otherwise.
    */
   public boolean isMuggleton ()
   {
      return muggleton;
   }

   /**
    * Set if a player is a muggleton.
    *
    * @param isMuggleton true if the player is a muggleton, false otherwise
    */
   public void setMuggleton (boolean isMuggleton)
   {
      muggleton = isMuggleton;
   }

   /**
    * Get the number of souls this player has collected.
    *
    * @return
    */
   public int getSouls ()
   {
      return souls;
   }

   /**
    * Set the number of souls this player has collected.
    *
    * @param s
    */
   public void setSouls (int s)
   {
      souls = s;
   }

   /**
    * Add a soul to this player.
    */
   public void addSoul ()
   {
      souls++;
   }

   /**
    * Remove a soul from this player.
    */
   public void subtractSoul()
   {
      if (souls > 0)
      {
         souls--;
      }
   }

   /**
    * Reset the soul count to zero.
    */
   public void resetSouls ()
   {
      souls = 0;
   }

   /**
    * Get a list of all the Ollivanders effects this user has on them.
    *
    * @return
    */
   public List<OEffect> getEffects ()
   {
      return effects;
   }

   /**
    * Add an effect to this player.
    *
    * @param e
    */
   public void addEffect (OEffect e)
   {
      effects.add(e);
   }

   /**
    * Remove an effect from this player.
    *
    * @param e
    */
   public void removeEffect (OEffect e)
   {
      effects.remove(e);
   }

   /**
    * Remove all effects from this player.
    */
   public void resetEffects ()
   {
      effects.clear();
   }

   /**
    * Set whether the player has found their destined wand before.
    *
    * @param b
    */
   public void setFoundWand (boolean b)
   {
      foundWand = b;
   }

   /**
    * Has this player found their destined wand?
    *
    * @return true if they have, false if not
    */
   public boolean foundWand ()
   {
      return foundWand;
   }

   /**
    * Returns this player's spell journal, a book with all known spells and their level.
    *
    * @return the player's spell journal
    */
   public ItemStack getSpellJournal ()
   {
      ItemStack spellJournal = new ItemStack(Material.WRITTEN_BOOK, 1);

      BookMeta bookMeta = (BookMeta)spellJournal.getItemMeta();
      bookMeta.setAuthor(playerName);
      bookMeta.setTitle("Spell Journal");

      String content = new String("Spell Journal\n\n");
      int lineCount = 2;
      for (Entry <Spells, Integer> e : knownSpells.entrySet())
      {
         // if we have done 14 lines, make a new page
         if (lineCount == 14)
         {
            bookMeta.addPage(content);
            lineCount = 0;
            content = "";
         }

         // add a newline to all lines except the first
         if (lineCount != 0)
         {
            content = content + "\n";
         }

         String line = e.getKey().toString() + " " + e.getValue().toString();
         content = content + line;

         lineCount++;
         // ~18 characters per line, this will likely wrap
         if (line.length() > 18)
         {
            lineCount++;
         }
      }

      bookMeta.addPage(content);

      bookMeta.setGeneration(BookMeta.Generation.ORIGINAL);
      spellJournal.setItemMeta(bookMeta);

      return spellJournal;
   }
}
