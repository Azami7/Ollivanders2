package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Effects;
import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Created by Azami7 on 6/28/17.
 *
 * Turn a target player in to a cow.
 *
 * @see IncarnatioSuper
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class INCARNATIO_VACCULA extends IncarnatioSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public INCARNATIO_VACCULA ()
   {
      super();

      text = "Turns target player in to a cow.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public INCARNATIO_VACCULA(Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);
      effect = new net.pottercraft.Ollivanders2.Effect.INCARNATIO_VACCULA(player, Effects.INCARNATIO_VACCULA, 1);
   }
}
