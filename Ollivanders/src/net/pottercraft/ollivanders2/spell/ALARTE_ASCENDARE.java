package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Shoots target high into air.
 *
 * @author lownes
 * @author Azami7
 * @version Ollivanders2
 */
public final class ALARTE_ASCENDARE extends O2Spell
{
   int maxVelocity = 5;
   int minVelocity = 0;

   Vector vector;

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public ALARTE_ASCENDARE(Ollivanders2 plugin)
   {
      super(plugin);
      spellType = O2SpellType.ALARTE_ASCENDARE;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<>()
      {{
         add("The Winged-Ascent Charm");
         add("He brandished his wand at the snake and there was a loud bang; the snake, instead of vanishing, "
                 + "flew ten feet into the air and fell back to the floor with a loud smack.");
      }};

      text = "Shoots target entity in to the air.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public ALARTE_ASCENDARE(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.ALARTE_ASCENDARE;
      branch = O2MagicBranch.CHARMS;

      initSpell();
   }

   @Override
   void doInitSpell()
   {
      double up = usesModifier / 20;
      if (up < minVelocity)
         up = minVelocity;
      else if (up > maxVelocity)
         up = maxVelocity;

      vector = new Vector(0, up, 0);
   }

   /**
    * Search for entities or items at the projectile's current location
    */
   @Override
   protected void doCheckEffect()
   {
      // projectile has stopped, kill the spell
      if (hasHitTarget())
      {
         kill();
         return;
      }

      // check for entities first
      Collection<Entity> entities = common.getEntitiesInRadius(location, 1.5);

      for (Entity entity : entities)
      {
         if (entity.getUniqueId() == player.getUniqueId())
            continue;

         // check entity to see if it can be targeted
         if (!entityHarmWGCheck(entity))
            continue;

         common.printDebugMessage("targeting entity " + entity.getName(), null, null, false);
         entity.setVelocity(entity.getVelocity().add(vector));

         kill();
         return;
      }
   }
}