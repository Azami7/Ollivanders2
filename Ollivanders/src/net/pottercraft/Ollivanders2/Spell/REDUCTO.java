package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Creates an explosion of magnitude depending on the spell level which destroys blocks and sets fires.
 *
 * @author lownes
 * @author Azami7
 */
public final class REDUCTO extends DarkArts
{
   public O2SpellType spellType = O2SpellType.REDUCTO;

   protected ArrayList<String> flavorText = new ArrayList<String>() {{
      add("The Reductor Curse");
      add("With this powerful curse, skilled wizards can easily reduce obstacles to pieces. For obvious reasons great care must be exercised when learning and practising this spell, lest you find yourself sweeping up in detention for it is all too easy to bring your classroom ceiling crashing down, or to reduce your teacher's desk to a fine mist.");
   }};

   protected String text = "Reducto creates an explosion which will damage the terrain.";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public REDUCTO () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public REDUCTO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
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