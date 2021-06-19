package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Transfigures a rotten flesh into inferi
 *
 * @author lownes
 * @author Azami7
 */
public final class MORTUOS_SUSCITATE extends Transfiguration
{
   int duration;

   static int maxDuration = Ollivanders2Common.ticksPerSecond * 1200; // 10 minutes

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public MORTUOS_SUSCITATE ()
   {
      super();

      branch = O2MagicBranch.DARK_ARTS;
      spellType = O2SpellType.MORTUOS_SUSCITATE;

      flavorText = new ArrayList<String>() {{
         add("They are corpses, dead bodies that have been bewitched to do a Dark wizard's bidding. Inferi have not been seen for a long time, however, not since Voldemort was last powerful... He killed enough people to make an army of them, of course.");
      }};

      text = "Mortuos Suscitate will transfigure a piece of rotten flesh into an Inferius. The Inferius will not attack it's owner.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public MORTUOS_SUSCITATE(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);

      branch = O2MagicBranch.DARK_ARTS;
      spellType = O2SpellType.MORTUOS_SUSCITATE;

      initSpell();

      // world guard flags
      if (Ollivanders2.worldGuardEnabled)
         worldGuardFlags.add(Flags.MOB_SPAWNING);

      duration = ((int) usesModifier * Ollivanders2Common.ticksPerSecond * 2) + (Ollivanders2Common.ticksPerSecond * 30);
      if (duration > maxDuration)
      {
         duration = maxDuration;
      }
   }

   @Override
   protected void doCheckEffect ()
   {
      if (!hasTransfigured())
      {
         for (Item item : getItems(1.5))
         {
            if (item.getItemStack().getType() == Material.ROTTEN_FLESH)
            {
               Zombie inferi = (Zombie) transfigureEntity(item, EntityType.ZOMBIE, null);
               if (inferi == null)
               {
                  common.printDebugMessage("MORTUOS_SUSCITATE.doCheckEffect: inferi is null", null, null, true);
                  kill();
                  return;
               }

               inferi.setCustomName("Inferius");
            }
         }
      }
      else
      {
         duration--;

         if (duration <= 0)
         {
            kill();
         }
      }

      if (hasHitTarget() && !hasTransfigured())
      {
         kill();
      }
   }

   @Override
   protected void revert ()
   {
      endTransfigure();
   }
}
