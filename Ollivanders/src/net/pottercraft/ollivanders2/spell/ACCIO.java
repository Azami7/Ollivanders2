package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;

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

      spellType = O2SpellType.ACCIO;

      flavorText = new ArrayList<String>() {{
         add("\"Accio Firebolt!\" -Harry Potter");
         add("The Summoning Charm");
      }};

      text = "Can use used to pull an item towards you. The strength of the pull is determined by your experience. "
            + "This cannot be used on living things.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public ACCIO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.ACCIO;
      setUsesModifier();
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