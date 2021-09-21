package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Creates an explosion at the target location twice as powerful as bombarda. Doesn't break blocks.
 *
 * @see BombardaSuper
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class BOMBARDA_MAXIMA extends BombardaSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public BOMBARDA_MAXIMA(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.BOMBARDA_MAXIMA;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<>() {{
         add("A more powerful explosion incantation.");
         add("\"Come on, let’s get destroying... Confringo? Stupefy? Bombarda? Which would you use?\" -Albus Potter");
      }};

      text = "Bombarda Maxima creates an explosion twice as powerful as Bombarda which doesn't damage the terrain.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public BOMBARDA_MAXIMA(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.BOMBARDA_MAXIMA;
      branch = O2MagicBranch.CHARMS;

      strengthMultiplier = 0.5;
      maxStrength = 8.0; // double TNT strength

      initSpell();
   }
}