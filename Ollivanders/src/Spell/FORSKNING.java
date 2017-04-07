package Spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Spells;
import net.pottercraft.Ollivanders2.Transfiguration;

/**
 * Transfigures wand into research object (ender crystal). Contains code
 * for research.
 *
 * @author lownes
 */
public class FORSKNING extends Transfiguration implements Spell
{

   public FORSKNING (Ollivanders2 plugin, Player player, Spells name,
                     Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      if (!hasTransfigured())
      {
         move();
         for (Item item : getItems(1))
         {
            if (Ollivanders2.isWand(item.getItemStack()))
            {
               transfigureEntity(item, EntityType.ENDER_CRYSTAL, null);
            }
         }
      }
      else
      {
         if (lifeTicks > 160)
         {
            endTransfigure();
         }
         else
         {
            lifeTicks++;
         }
      }
   }

   /**
    * This will allow you to research a string for possible spells that
    * could be made out of it
    *
    * @param message - The string to be researched.
    */
   public void research (String message)
   {
      World world = location.getWorld();
      if (message.length() == 0)
      {
         return;
      }
      Spells[] spells = Spells.values();
      List<String> candidateSpells = new ArrayList<String>();
      for (Spells spell : spells)
      {
         String spellString = Spells.recode(spell);
         if (spellString.startsWith(message.substring(0, 1).toLowerCase()))
         {
            candidateSpells.add(spellString);
         }
      }
      for (int i = 0; i < candidateSpells.size(); i++)
      {
         if (candidateSpells.get(i).startsWith(message.toLowerCase()))
         {
            double x = (double) i / candidateSpells.size();
            double pitch = (x * x) + (0.5 * x) + 0.5;
            if (Spells.decode(message) == null)
            {
               world.playSound(location, Sound.BLOCK_NOTE_PLING, 1, (float) pitch);
            }
            else
            {
               world.playSound(location, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            }
         }
      }
   }

}