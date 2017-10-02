package net.pottercraft.Ollivanders2.Spell;

import java.util.List;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Spell which will grab the targeted item and pull it toward you
 * with a force determined by your level in the spell.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class ACCIO extends Charms
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public ACCIO ()
   {
      super();

      flavorText.add("\"Accio Firebolt!\" -Harry Potter");
      flavorText.add("The Summoning Charm");

      text = "Can use used to pull an item towards you. The strength of the pull is determined by your experience. "
            + "This cannot be used on living things.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public ACCIO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      move();
      List<Item> items = getItems(1);
      for (Item item : items)
      {
         item.setVelocity(player.getEyeLocation().toVector().subtract(item.getLocation().toVector()).normalize().multiply(usesModifier / 10));
         kill();
         return;
      }
   }
}