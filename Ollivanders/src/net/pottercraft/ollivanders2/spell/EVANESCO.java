package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Vanishes an entity. The entity will reappear after a certain time.
 *
 * @author lownes
 * @author Azami7
 */
public final class EVANESCO extends Transfiguration
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public EVANESCO(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.EVANESCO;
      branch = O2MagicBranch.TRANSFIGURATION;

      flavorText = new ArrayList<>() {{
         add("The Vanishing Spell");
         add("The contents of Harry’s potion vanished; he was left standing foolishly beside an empty cauldron.");
      }};

      text = "Evanesco will vanish an entity.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public EVANESCO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.EVANESCO;
      branch = O2MagicBranch.TRANSFIGURATION;

      initSpell();

      // world guard flags
      if (Ollivanders2.worldGuardEnabled)
      {
         worldGuardFlags.add(Flags.USE);
         worldGuardFlags.add(Flags.BUILD);
      }

      // pass-through materials
      projectilePassThrough.remove(Material.WATER);
   }

   @Override
   protected void doCheckEffect()
   {
      if (!hasTransfigured())
      {
         List<Entity> entities = getCloseEntities(1.5);

         for (Entity e : entities)
         {
            if (e.getUniqueId() == player.getUniqueId())
            {
               continue;
            }

            if (e.getType() != EntityType.PLAYER)
            {
               if (transfigureEntity(e, null, null) == null)
               {
                  kill();
                  return;
               }
            }
         }

         // if the spell has hit a solid block, the projectile is stopped and wont go further so kill the spell
         if (hasHitTarget())
         {
            kill();
         }
      }
      else
      {
         // check time to live on the spell
         if (spellDuration <= 0)
         {
            // spell duration is up, kill the spell
            kill();
         }
         else
         {
            spellDuration = spellDuration - 1;
         }
      }
   }
}