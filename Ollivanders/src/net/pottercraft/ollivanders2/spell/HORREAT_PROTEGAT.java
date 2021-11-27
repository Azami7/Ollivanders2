package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.stationaryspell.O2StationarySpell;
import org.jetbrains.annotations.NotNull;

/**
 * Shrinks a stationarySpellObject's radius. Only the player who created the stationarySpellObject can change it's size.
 *
 * @author lownes
 * @author Azami7
 */
public final class HORREAT_PROTEGAT extends O2Spell
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public HORREAT_PROTEGAT(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.HORREAT_PROTEGAT;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<>()
      {{
         add("The Spell-Reduction Charm");
      }};

      text = "Horreat Protegat will shrink a stationary spell's radius. Only the creator of the stationary spell can affect it with this spell.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public HORREAT_PROTEGAT(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.HORREAT_PROTEGAT;
      branch = O2MagicBranch.CHARMS;

      // pass-through materials
      projectilePassThrough.remove(Material.WATER);
      projectilePassThrough.remove(Material.LAVA);
      projectilePassThrough.add(Material.CAVE_AIR);

      initSpell();
   }

   /**
    * Reduce the radius of any stationary spells within a radius of the target, if they were cast by this player
    */
   @Override
   protected void doCheckEffect()
   {
      if (!hasHitTarget())
         return;

      List<O2StationarySpell> inside = new ArrayList<>();
      for (O2StationarySpell spell : Ollivanders2API.getStationarySpells(p).getActiveStationarySpells())
      {
         if (spell.isInside(location) && spell.radius > (int) (10 / usesModifier))
         {
            inside.add(spell);
            kill();
         }
      }

      int limit = (int) (10 / usesModifier);
      if (limit < 1)
      {
         limit = 1;
      }
      for (O2StationarySpell spell : inside)
      {
         if (spell.radius > limit && spell.getCasterID().equals(player.getUniqueId()))
         {
            spell.radius = spell.radius - 1;
            spell.flair(10);
         }
      }

      kill();
   }
}