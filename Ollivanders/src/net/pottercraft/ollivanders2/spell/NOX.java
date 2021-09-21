package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Created by Azami7 on 6/29/17.
 * <p>
 * Cancels the effect of the LUMOS spell or removes the effect of a Night Vision potion.
 *
 * @author Azami7
 * @version Ollivanders2
 */
public final class NOX extends RemovePotionEffectInRadius
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public NOX(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.NOX;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<>()
      {{
         add("The Wand-Extinguishing Charm");
         add("With difficulty he dragged it over himself, murmured, 'Nox,' extinguishing his wand light, and continued on his hands and knees, as silently as possible, all his senses straining, expecting every second to be discovered, to hear a cold clear voice, see a flash of green light.");
      }};

      text = "Cancels the effect of the Lumos spell or removes the effect of a Night Vision potion.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public NOX(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.NOX;
      branch = O2MagicBranch.CHARMS;
      targetSelf = true;

      initSpell();

      potionEffectTypes.add(PotionEffectType.NIGHT_VISION);
   }
}
