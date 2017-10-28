package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

/**
 * Casts a powerful confusion potion effect on the player that scales with the caster's level in this spell.
 *
 * @version Ollivanders
 * @see ConfundusSuper
 * @author Azami7
 */
public final class CONFUNDO extends ConfundusSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public CONFUNDO ()
   {
      super();

      flavorText.add("The Confundus Charm");
      flavorText.add("\"Look who's talking. Confunded anyone lately?\" -Harry Potter");

      text = "Confundo causes the target to become confused.";
   }

   /**
    * Constructor for casting the spell.
    *
    * @param plugin
    * @param player
    * @param name
    * @param rightWand
    */
   public CONFUNDO (Ollivanders2 plugin, Player player, Spells name, Double rightWand)
   {
      super(plugin, player, name, rightWand);

      modifier = 1;
   }
}