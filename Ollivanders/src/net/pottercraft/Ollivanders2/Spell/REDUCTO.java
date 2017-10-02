package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Creates an explosion of magnitude depending on the spell level which destroys blocks and sets fires.
 *
 * @author lownes
 * @author Azami7
 */
public final class REDUCTO extends DarkArts
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public REDUCTO ()
   {
      super();

      flavorText.add("The Reductor Curse");
      flavorText.add("With this powerful curse, skilled wizards can easily reduce obstacles to pieces. For obvious reasons great care must be exercised when learning and practising this spell, lest you find yourself sweeping up in detention for it is all too easy to bring your classroom ceiling crashing down, or to reduce your teacher's desk to a fine mist.");

      text = "Reducto creates an explosion which will damage the terrain.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public REDUCTO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public void checkEffect ()
   {
      move();
      if (super.getBlock().getType() != Material.AIR && getBlock().getType() != Material.FIRE
            && getBlock().getType() != Material.WATER && getBlock().getType() != Material.STATIONARY_WATER)
      {
         Location backLoc = super.location.clone().subtract(vector);
         backLoc.getWorld().createExplosion(backLoc, (float) (usesModifier * 0.4));
      }
   }
}