package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Does direct damage to a living entity according to your level in the spell.
 *
 * @author lownes
 * @author Azami7
 * @version Ollivanders2
 */
public final class AVADA_KEDAVRA extends O2Spell
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public AVADA_KEDAVRA()
   {
      spellType = O2SpellType.AVADA_KEDAVRA;
      branch = O2MagicBranch.DARK_ARTS;

      flavorText = new ArrayList<String>()
      {{
         add("The Killing Curse");
         add("There was a flash of blinding green light and a rushing sound, as though a vast, invisible something was soaring through the air — instantaneously the spider rolled over onto its back, unmarked, but unmistakably dead");
         add("\"Yes, the last and worst. Avada Kedavra. ...the Killing Curse.\" -Bartemius Crouch Jr (disguised as Alastor Moody)");
      }};

      text = "Cause direct damage to a living thing, possibly killing it.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public AVADA_KEDAVRA (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.AVADA_KEDAVRA;

      initSpell();

      // world guard flags
      if (Ollivanders2.worldGuardEnabled)
      {
         worldGuardFlags.add(Flags.PVP);
         worldGuardFlags.add(Flags.DAMAGE_ANIMALS);
      }

      moveEffectData = Material.GREEN_WOOL;
   }

   /**
    * Kill a living entity
    */
   @Override
   protected void doCheckEffect ()
   {
      List<LivingEntity> entities = getLivingEntities(1.5);
      if (entities.size() > 0)
      {
         for (LivingEntity entity : entities)
         {
            if (entity.getUniqueId() == player.getUniqueId())
               continue;

            entity.damage(usesModifier * 2, player);

            kill();
            return;
         }
      }

      // if the spell has hit a solid block, the projectile is stopped and wont go further so kill the spell
      if (hasHitTarget())
         kill();
   }
}