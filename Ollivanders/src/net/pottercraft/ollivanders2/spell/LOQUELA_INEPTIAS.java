package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.effect.O2EffectType;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Babbling curse
 *
 * @author Azami7
 * @since 2.2.7
 */
public class LOQUELA_INEPTIAS extends AddO2Effect
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public LOQUELA_INEPTIAS()
   {
      super();

      branch = O2MagicBranch.DARK_ARTS;
      spellType = O2SpellType.LOQUELA_INEPTIAS;

      flavorText = new ArrayList<String>()
      {{
         add("\"He usually picked Harry to help him with these reconstructions; so far, Harry had been forced to play a simple Transylvanian villager whom Lockhart had cured of a Babbling Curse, a yeti with a head cold, and a vampire who had been unable to eat anything except lettuce since Lockhart had dealt with him.\"");
         add("The Babbling Curse");
      }};

      text = "Causes your target to speak nonsense for a period of time.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public LOQUELA_INEPTIAS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      branch = O2MagicBranch.DARK_ARTS;
      spellType = O2SpellType.LOQUELA_INEPTIAS;
      initSpell();

      effectsToAdd.add(O2EffectType.BABBLING);

      // duration
      maxDurationInSeconds = 300;
      durationInSeconds = ((int) usesModifier + 30);

      // pass-through materials
      projectilePassThrough.remove(Material.WATER);
   }
}