package net.pottercraft.Ollivanders2;

import java.util.*;

import net.pottercraft.Ollivanders2.Spell.Spells;
import org.bukkit.inventory.ItemStack;

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

   private String[] woodArray = {"Spruce", "Jungle", "Birch", "Oak"};
   private String[] coreArray = {"Spider Eye", "Bone", "Rotten Flesh", "Gunpowder"};
   private String loreConjunction = " and ";

   /*
   public O2Player (Ollivanders2 plugin)
   {
      p = plugin;
   }
   */

   public O2Player (UUID pid, String name, Ollivanders2 plugin)
   {
      p = plugin;
      playerName = name;

      // set destined wand
      initDestinedWand(pid);
   }

   private void initDestinedWand (UUID pid)
   {
      // set destined wand
      int seed = Math.abs(pid.hashCode()%16);
      int wood = seed/4;
      int core = seed%4;

      wandWood = woodArray[wood];
      wandCore = coreArray[core];
   }

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

   public String getDestinedWandLore ()
   {
      String wandLore = wandWood + loreConjunction + wandCore;

      return wandLore;
   }

   public String getWandWood ()
   {
      return wandWood;
   }

   public String getWandCore ()
   {
      return wandCore;
   }

   public String getPlayerName ()
   {
      return playerName;
   }

   public void setPlayerName (String name)
   {
      playerName = name;
   }

   public int getSpellCount (Spells spell)
   {
      int count = 0;

      if (knownSpells.containsKey(spell))
      {
         count = knownSpells.get(spell).intValue();
      }

      return count;
   }

   public Map<Spells, Integer> getKnownSpells ()
   {
      return knownSpells;
   }

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

   public void resetSpellCount ()
   {
      knownSpells.clear();
   }

   public Spells getSpell ()
   {
      return wandSpell;
   }

   public void setSpell (Spells spell)
   {
      if (spell != null)
      {
         wandSpell = spell;
      }
   }

   public boolean isInvisible ()
   {
      return invisible;
   }

   public void setInvisible(boolean isInvisible)
   {
      invisible = isInvisible;
   }

   public boolean isMuggleton ()
   {
      return muggleton;
   }

   public void setMuggleton (boolean isMuggleton)
   {
      muggleton = isMuggleton;
   }

   public int getSouls ()
   {
      return souls;
   }

   public void setSouls (int s)
   {
      souls = s;
   }

   public void addSoul ()
   {
      souls++;
   }

   public void subtractSoul()
   {
      if (souls > 0)
      {
         souls--;
      }
   }

   public void resetSouls ()
   {
      souls = 0;
   }

   public List<OEffect> getEffects ()
   {
      return effects;
   }

   public void addEffect (OEffect e)
   {
      effects.add(e);
   }

   public void remEffect (OEffect e)
   {
      effects.remove(e);
   }

   public void resetEffects ()
   {
      effects.clear();
   }
}
