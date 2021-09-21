package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Shoots target high into air.
 *
 * @author lownes
 * @author Azami7
 * @version Ollivanders2
 */
public final class ALARTE_ASCENDARE extends O2Spell
{
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

      initSpell();

      spellType = O2SpellType.ALARTE_ASCENDARE;
      branch = O2MagicBranch.CHARMS;

      // world guard
      if (Ollivanders2.worldGuardEnabled)
      {
         worldGuardFlags.add(Flags.DAMAGE_ANIMALS);
         worldGuardFlags.add(Flags.PVP);
      }
   }

   /**
    * Search for entities or items at the projectile's current location
    */
   @Override
   protected void doCheckEffect()
   {
      double up = usesModifier / 20;
      if (up < 1)
      {
         up = 1;
      }
      else if (up > 5)
      {
         up = 5;
      }

      Vector vec = new Vector(0, up, 0);

      // check for entities first
      List<LivingEntity> livingEntities = getLivingEntities(1.5);

      if (livingEntities.size() > 0)
      {
         for (LivingEntity living : livingEntities)
         {
            if (living.getUniqueId() == player.getUniqueId())
               continue;

            common.printDebugMessage("targeting entity " + living.getName(), null, null, false);
            living.setVelocity(living.getVelocity().add(vec));

            kill();
            return;
         }

         return;
      }

      // check for items next
      List<Item> items = getItems(1.5);

      if (items.size() > 0)
      {
         Item item = items.get(0);

         common.printDebugMessage("targeting item " + item.getName(), null, null, false);
         item.setVelocity(item.getVelocity().add(vec));

         kill();
         return;
      }

      // projectile has stopped, kill the spell
      if (hasHitTarget())
         kill();
   }
}