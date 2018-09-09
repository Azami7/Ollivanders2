package net.pottercraft.Ollivanders2.Spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.FallingBlockWatcher;
import net.pottercraft.Ollivanders2.Ollivanders2;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Turn animals to flower pots (approximation for water goblets).
 *
 * @since 2.2.6
 * @link https://github.com/Azami7/Ollivanders2/issues/81
 * @author Azami7
 */
public final class VERA_VERTO extends FriendlyMobDisguiseSuper
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public VERA_VERTO (O2SpellType type)
   {
      super(type);
      flavorText.add("\"Could I have your attention please? Right, now, today, we will be transforming animals into water goblets. Like so. One, two, three. Vera Verto.\" -Minerva McGonagall");

      text = "Turns an entity in to a flower pot. Size of animal and duration of the spell depends on your experience.";
   }

   /**
    * Constructor.
    *
    * @param plugin
    * @param player
    * @param type
    * @param rightWand
    */
   public VERA_VERTO (Ollivanders2 plugin, Player player, O2SpellType type, Double rightWand)
   {
      super(plugin, player, type, rightWand);

      spellDuration = (int)(1200 * usesModifier);
      targetType = EntityType.FALLING_BLOCK;
      ItemStack flowerPot = new ItemStack(Material.FLOWER_POT, 1);

      disguiseType = DisguiseType.getType(targetType);
      disguise = new MiscDisguise(disguiseType);
      FallingBlockWatcher watcher = (FallingBlockWatcher)disguise.getWatcher();
      watcher.setBlock(flowerPot);
   }
}
