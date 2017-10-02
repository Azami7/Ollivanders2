package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * The super class for all ARITHMANCY projectile spells.
 *
 * @author Azami7
 */
public abstract class Arithmancy extends SpellProjectile implements O2Spell
{
   protected O2MagicBranch branch = O2MagicBranch.ARITHMANCY;
   protected ArrayList<String> flavorText = new ArrayList<>();
   protected String text = "";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public Arithmancy () { }

   /**
    * Constructor for casting a charm spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public Arithmancy (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
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

