package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Creates an explosion of magnitude depending on the spell level which destroys blocks and sets fires.
 *
 * @author lownes
 * @author Azami7
 */
public final class REDUCTO extends O2Spell
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public REDUCTO(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.REDUCTO;
      branch = O2MagicBranch.DARK_ARTS;

      flavorText = new ArrayList<>()
      {{
         add("The Reductor Curse");
         add("With this powerful curse, skilled wizards can easily reduce obstacles to pieces. For obvious reasons great care must be exercised when learning and practising this spell, lest you find yourself sweeping up in detention for it is all too easy to bring your classroom ceiling crashing down, or to reduce your teacher's desk to a fine mist.");
      }};

      text = "Reducto creates an explosion which will damage the terrain.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public REDUCTO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.REDUCTO;
      branch = O2MagicBranch.DARK_ARTS;

      // world guard flags
      if (Ollivanders2.worldGuardEnabled)
         worldGuardFlags.add(Flags.OTHER_EXPLOSION);

      initSpell();
   }

   @Override
   protected void doCheckEffect()
   {
      if (!hasHitTarget())
      {
         return;
      }

      Location backLoc = location.clone().subtract(vector);
      World world = backLoc.getWorld();
      if (world == null)
      {
         common.printDebugMessage("REDUCTO.doCheckEffect: world is null", null, null, true);
      }
      else
      {
         world.createExplosion(backLoc, (float) (usesModifier * 0.4));
      }

      kill();
   }
}