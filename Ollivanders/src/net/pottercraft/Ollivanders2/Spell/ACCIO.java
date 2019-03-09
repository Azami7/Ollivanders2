package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
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

      spellType = O2SpellType.ACCIO;

      flavorText = new ArrayList<String>() {{
         add("\"Accio Firebolt!\" -Harry Potter");
         add("The Summoning Charm");
      }};

      text = "Can use used to pull an item towards you. The strength of the pull is determined by your experience. "
            + "This can only be used on items.";
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

   /**
    * Check for any items within a radius of the projectile's current location, if one is found, pull it towards the
    * caster.
    */
   @Override
   protected void doCheckEffect ()
   {
      List<Item> items = getItems(1.5);

      if (items != null && items.size() > 0)
      {
         Item item = items.get(0);
         item.setVelocity(player.getEyeLocation().toVector().subtract(item.getLocation().toVector()).normalize().multiply(usesModifier / 10));

         kill();
      }

      // projectile has stopped, kill the spell
      if (hasHitTarget())
      {
         kill();
      }
   }
}