package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.divination.O2DivinationType;
import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Centaur divination is the most accurate form of divination, but requires the most skill to learn.
 */
public class MANTEIA_KENTAVROS extends Divination
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public MANTEIA_KENTAVROS(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.MANTEIA_KENTAVROS;
      divinationType = O2DivinationType.CENTAUR_DIVINATION;
      branch = O2MagicBranch.DIVINATION;

      flavorText = new ArrayList<>()
      {{
         add("\"Trivial hurts, tiny human accidents, these are of no more significance than the scurryings of ants to the wide universe, and are unaffected by planetary movements.\" -Firenze");
         add("\"I know that you have learned the names of the planets and their moons in Astronomy, and that you have mapped the stars’ progress through the heavens. Centaurs have unravelled the mysteries of these movements over centuries. Our findings teach us that the future may be glimpsed in the sky above us.\" -Firenze");
         add("\"I am here to explain the wisdom of centaurs, which is impersonal and impartial.\" -Firenze");
      }};

      text = "Centaurs have spent hundreds of years observing celestial movements, unlocking their secrets and thereby learning to see signs. The ancient knowledge practice Centaur divination, when mastered, is the most accurate form of divination.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public MANTEIA_KENTAVROS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.ASTROLOGIA;
      divinationType = O2DivinationType.ASTROLOGY;
      branch = O2MagicBranch.DIVINATION;

      initSpell();
   }

   /**
    * Override setUsesModifier because this spell does not require holding a wand.
    */
   @Override
   protected void setUsesModifier()
   {
      usesModifier = (double) p.getSpellCount(player, spellType) / 3;

      if (Ollivanders2API.getPlayers().playerEffects.hasEffect(player.getUniqueId(), O2EffectType.HIGHER_SKILL))
      {
         usesModifier *= 2;
      }
   }
}
