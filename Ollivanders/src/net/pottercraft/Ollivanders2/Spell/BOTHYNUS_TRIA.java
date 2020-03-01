package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Fanciest version of Bothynus.
 *
 * @author Azami7
 * @see BOTHYNUS
 * @see Pyrotechnia
 */
public final class BOTHYNUS_TRIA extends Pyrotechnia
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public BOTHYNUS_TRIA()
   {
      super();

      spellType = O2SpellType.BOTHYNUS_TRIA;
      branch = O2MagicBranch.CHARMS;

      text = "Creates one or more yellow and orange star fireworks with trails and that fades to silver.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public BOTHYNUS_TRIA (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.BOTHYNUS_TRIA;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.YELLOW);
      fireworkColors.add(Color.ORANGE);

      fadeColors = new ArrayList<>();
      fadeColors.add(Color.SILVER);

      fireworkType = Type.STAR;
      hasTrails = true;
      hasFade = true;

      setMaxFireworks(15);
   }
}
