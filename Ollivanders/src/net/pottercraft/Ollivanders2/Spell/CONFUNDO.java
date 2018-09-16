package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Casts a powerful confusion potion effect on the player that scales with the caster's level in this spell.
 *
 * @version Ollivanders
 * @see ConfundusSuper
 * @author Azami7
 */
public final class CONFUNDO extends ConfundusSuper
{
   public O2SpellType spellType = O2SpellType.CONFUNDO;

   protected ArrayList<String> flavorText = new ArrayList<String>() {{
      add("The Confundus Charm");
      add("\"Look who's talking. Confunded anyone lately?\" -Harry Potter");
   }};

   protected String text = "Confundo causes the target to become confused.";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public CONFUNDO () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public CONFUNDO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      modifier = 1;
   }
}