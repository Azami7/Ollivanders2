package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Causes blindness in a radius
 *
 * @see FumosSuper
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class FUMOS extends FumosSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public FUMOS ()
   {
      super();

      spellType = O2SpellType.FUMOS;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<String>() {{
         add("The Smoke-Screen Spell");
      }};

      text = "Fumos will cause those in an area to be blinded by a smoke cloud.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public FUMOS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.FUMOS;
      branch = O2MagicBranch.CHARMS;

      initSpell();
   }
}
