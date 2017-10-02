package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * The super class for all HEALING projectile spells.
 *
 * @author Azami7
 */
public abstract class Healing extends SpellProjectile implements O2Spell
{
   protected O2MagicBranch branch = O2MagicBranch.HEALING;
   protected ArrayList<String> flavorText = new ArrayList<>();
   protected String text = "";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public Healing () { }

   /**
    * Constructor for casting a charm spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public Healing (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
   }

   @Override
   public String getText ()
   {
      return text;
   }

   @Override
   public String getFlavorText()
   {
      if (flavorText.size() < 1)
      {
         return null;
      }
      else
      {
         int index = Math.abs(Ollivanders2.random.nextInt() % flavorText.size());
         return flavorText.get(index);
      }
   }

   @Override
   public O2MagicBranch getMagicBranch ()
   {
      return branch;
   }
}
