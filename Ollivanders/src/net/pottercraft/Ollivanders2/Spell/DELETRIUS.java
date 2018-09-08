package net.pottercraft.Ollivanders2.Spell;

import java.util.List;

import net.pottercraft.Ollivanders2.Ollivanders2;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

/**
 * Deletes an item entity.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class DELETRIUS extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public DELETRIUS ()
   {
      super();

      flavorText.add("The Eradication Spell");
      flavorText.add("'Deletrius!' Mr Diggory shouted, and the smoky skull vanished in a wisp of smoke.");
      text = "Cause an item entity to stop existing.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public DELETRIUS (Ollivanders2 plugin, Player player, O2SpellType name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   public void checkEffect ()
   {
      move();
      List<Item> items = getItems(1);
      for (Item item : items)
      {
         item.remove();
         kill();
         return;
      }
   }
}