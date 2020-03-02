package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Adds a MUCUS_AD_NAUSEAM oeffect to the player
 *
 * @author lownes
 * @author Azami7
 */
public final class MUCUS_AD_NAUSEAM extends AddO2Effect
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public MUCUS_AD_NAUSEAM()
   {
      super();

      spellType = O2SpellType.MUCUS_AD_NAUSEAM;
      branch = O2MagicBranch.DARK_ARTS;

      flavorText = new ArrayList<String>()
      {{
         add("The Curse of the Bogies");
      }};

      text = "Mucus Ad Nauseam will cause your opponent to drip with slime.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public MUCUS_AD_NAUSEAM (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.MUCUS_AD_NAUSEAM;
      branch = O2MagicBranch.DARK_ARTS;
      initSpell();

      // effect
      effectsToAdd.add(O2EffectType.MUCUS);

      // duration
      durationInSeconds = ((int) usesModifier + 30);
      maxDurationInSeconds = 180; // 3 minutes

      // pass-through materials
      projectilePassThrough.remove(Material.WATER);
   }
}