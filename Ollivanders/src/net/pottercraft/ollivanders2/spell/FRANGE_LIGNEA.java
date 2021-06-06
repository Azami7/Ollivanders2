package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;
import java.util.List;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import net.pottercraft.ollivanders2.Ollivanders2API;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Bursts a log into a stack of coreless wands, whose number depends on the player's spell level.
 *
 * @author lownes
 * @author Azami7
 * @version Ollivanders2
 */
public final class FRANGE_LIGNEA extends O2Spell
{
   public static final String corelessWandLabel = "Coreless Wand";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public FRANGE_LIGNEA()
   {
      super();

      spellType = O2SpellType.FRANGE_LIGNEA;
      branch = O2MagicBranch.CHARMS;

      text = "Frange lignea will cause a log of the spruce, oak, birch, or jungle species to explode into coreless wands.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public FRANGE_LIGNEA (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.FRANGE_LIGNEA;
      branch = O2MagicBranch.CHARMS;

      initSpell();

      // material black list
      materialBlackList.add(Material.WATER);

      // world-guard flags
      if (Ollivanders2.worldGuardEnabled)
         worldGuardFlags.add(DefaultFlag.BUILD);
   }

   /**
    * Break a natural log in to sticks
    */
   @Override
   protected void doCheckEffect ()
   {
      if (!hasHitTarget())
         return;

      Block block = getTargetBlock();

      if (Ollivanders2API.common.isNaturalLog(block))
      {
         // break the log in to the correct wand core type
         String woodType = Ollivanders2API.common.enumRecode(block.getType().toString()).split("_")[0];
         woodType = Ollivanders2API.common.firstLetterCapitalize(woodType);

         block.getLocation().getWorld().createExplosion(block.getLocation(), 0);
         int number = (int) (usesModifier * 0.8);

         // make a stack of sticks
         if (number > 0)
         {
            ItemStack stickStack = new ItemStack(Material.STICK, number);
            ItemMeta stickMeta = stickStack.getItemMeta();

            stickMeta.setDisplayName(corelessWandLabel);
            List<String> lore = new ArrayList<>();
            lore.add(woodType);
            stickMeta.setLore(lore);
            stickStack.setItemMeta(stickMeta);

            player.getWorld().dropItemNaturally(block.getLocation(), stickStack);
         }

         block.setType(Material.AIR);
         kill();
      }
   }
}