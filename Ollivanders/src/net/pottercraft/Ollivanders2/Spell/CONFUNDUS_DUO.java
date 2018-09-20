package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Casts a more powerful confusion potion effect that is twice as strong and lasts twice as long as Confundus.
 *
 * @since 2.2.5
 * @link https://github.com/Azami7/Ollivanders2/issues/95
 * @see ConfundusSuper
 * @author Azami7
 */
public final class CONFUNDUS_DUO extends ConfundusSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public CONFUNDUS_DUO ()
   {
      super();

      spellType = O2SpellType.CONFUNDUS_DUO;

      flavorText = new ArrayList<String>() {{
         add("The Confundus Charm");
         add("The guard was confused. He stared down at the thin, golden Probe and then at his companion, who said in a slightly dazed voice, 'Yeah, you've just checked them, Marius.'");
      }};

      text = "Confundus Duo is a stronger variation of the Confundus Charm.  Effects are twice as strong and last twice as long as Confundo.";

   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public CONFUNDUS_DUO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.CONFUNDUS_DUO;
      setUsesModifier();

      modifier = 2;
   }
}
