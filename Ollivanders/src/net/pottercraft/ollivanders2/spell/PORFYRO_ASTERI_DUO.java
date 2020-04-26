package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * A fancier version of PORFYRO_ASTERI.
 *
 * @see PORFYRO_ASTERI
 * @see PyrotechniaSuper
 * @author Azami7
 */
public final class PORFYRO_ASTERI_DUO extends PyrotechniaSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public PORFYRO_ASTERI_DUO  ()
   {
      super();

      spellType = O2SpellType.PORFYRO_ASTERI_DUO;
      text = "Conjures purple star fireworks that fade to white.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public PORFYRO_ASTERI_DUO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.PORFYRO_ASTERI_DUO;
      setUsesModifier();

      fireworkColors = new ArrayList<>();
      fireworkColors.add(Color.PURPLE);
      fireworkColors.add(Color.FUCHSIA);

      hasTrails = true;

      hasFade = true;
      fadeColors = new ArrayList<>();
      fadeColors.add(Color.WHITE);

      fireworkType = FireworkEffect.Type.STAR;

      setMaxFireworks(10);
   }
}
