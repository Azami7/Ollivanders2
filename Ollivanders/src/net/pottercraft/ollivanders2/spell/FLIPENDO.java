package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Throws another player away from the caster. Twice as powerful as depulso.
 *
 * @author lownes
 */
public final class FLIPENDO extends Knockback
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public FLIPENDO(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.FLIPENDO;
      branch = O2MagicBranch.DARK_ARTS;

      flavorText = new ArrayList<>() {{
         add("The Knockback Jinx");
         add("The incantation for the knockback jinx is 'Flipendo'. This jinx is the most utilitarian of Grade 2 spell, in that it will allow the caster to 'knock back' an opponent or object and can also be used to push and activate certain magically charmed switches. Like many Grade 2 spells, Flipendo can be targeted.");
         add("\"There was a loud bang and he felt himself flying backwards as if punched; as he slammed into the kitchen wall and slid to the floor, he glimpsed the tail of Lupin's cloak disappearing round the door.\"");
      }};

      text = "Flipendo can be used to repel an opponent away from oneself.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public FLIPENDO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.FLIPENDO;
      branch = O2MagicBranch.DARK_ARTS;

      initSpell();

      strengthReducer = 10;

      // world guard flags
      if (Ollivanders2.worldGuardEnabled)
      {
         worldGuardFlags.add(Flags.PVP);
         worldGuardFlags.add(Flags.DAMAGE_ANIMALS);
      }
   }
}