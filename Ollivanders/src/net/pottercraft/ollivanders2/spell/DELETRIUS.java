package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldguard.protection.flags.Flags;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Deletes an item entity.
 *
 * @author lownes
 * @author Azami7
 * @version Ollivanders2
 */
public final class DELETRIUS extends O2Spell
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public DELETRIUS(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.DELETRIUS;
      branch = O2MagicBranch.CHARMS;

      flavorText = new ArrayList<>()
      {{
         add("The Eradication Spell");
         add("'Deletrius!' Mr Diggory shouted, and the smoky skull vanished in a wisp of smoke.");
      }};

      text = "Cause an item entity to stop existing.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public DELETRIUS(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.DELETRIUS;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      // world guard flags
      if (Ollivanders2.worldGuardEnabled)
         worldGuardFlags.add(Flags.ITEM_PICKUP);
   }

   /**
    * Delete a item
    */
   @Override
   protected void doCheckEffect()
   {
      List<Item> items = getItems(1.5);

      if (items.size() > 0)
      {
         Item item = items.get(0);

         item.remove();
         kill();
      }

      // projectile has stopped, kill the spell
      if (hasHitTarget())
         kill();
   }
}