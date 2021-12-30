package net.pottercraft.ollivanders2.spell;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Vanishes an entity. The entity will reappear after a certain time.
 *
 * @author lownes
 * @author Azami7
 */
public final class EVANESCO extends EntityTransfiguration
{
   /**
    * Default constructor for use in generating spell text. Do not use to cast the spell.
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

      text = "Evanesco will vanish items, boats, and minecarts.";
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

      // world guard flags
      if (Ollivanders2.worldGuardEnabled)
      {
         worldGuardFlags.add(Flags.USE);
         worldGuardFlags.add(Flags.BUILD);
      }

      entityWhitelist.add(EntityType.DROPPED_ITEM);
      entityWhitelist.addAll(Ollivanders2Common.minecarts);
      entityWhitelist.add(EntityType.BOAT);

      durationModifier = 4.0;

      initSpell();
   }

   /**
    * Remove the entity and set the transfigured entity to the original
    *
    * @param entity the entity to transfigure
    * @return the original entity
    */
   @Override
   @Nullable
   protected Entity transfigureEntity(@NotNull Entity entity)
   {
      originalEntity.remove();

      return originalEntity;
   }
}