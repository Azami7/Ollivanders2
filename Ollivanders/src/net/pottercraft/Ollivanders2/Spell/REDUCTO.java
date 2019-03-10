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
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public REDUCTO ()
   {
      super();

      spellType = O2SpellType.REDUCTO;

      flavorText = new ArrayList<String>() {{
         add("The Reductor Curse");
         add("With this powerful curse, skilled wizards can easily reduce obstacles to pieces. For obvious reasons great care must be exercised when learning and practising this spell, lest you find yourself sweeping up in detention for it is all too easy to bring your classroom ceiling crashing down, or to reduce your teacher's desk to a fine mist.");
      }};

      text = "Reducto creates an explosion which will damage the terrain.";
   }

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

      spellType = O2SpellType.REDUCTO;
      setUsesModifier();
   }

   @Override
   public void checkEffect ()
   {
      move();
      Material targetBlockType = getBlock().getType();
      if (targetBlockType != Material.AIR && targetBlockType != Material.FIRE && targetBlockType != Material.WATER)
      {
         Location backLoc = super.location.clone().subtract(vector);
         backLoc.getWorld().createExplosion(backLoc, (float) (usesModifier * 0.4));
      }
   }
}