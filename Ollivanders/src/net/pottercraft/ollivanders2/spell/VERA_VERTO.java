package net.pottercraft.ollivanders2.spell;

import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MiscDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.FallingBlockWatcher;
import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Turn animals to flower pots (approximation for water goblets).
 *
 * @author Azami7
 * @link https://github.com/Azami7/Ollivanders2/issues/81
 * @since 2.2.6
 */
public final class VERA_VERTO extends FriendlyMobDisguise
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    *
    * @param plugin the Ollivanders2 plugin
    */
   public VERA_VERTO(Ollivanders2 plugin)
   {
      super(plugin);

      spellType = O2SpellType.VERA_VERTO;
      branch = O2MagicBranch.TRANSFIGURATION;

      flavorText = new ArrayList<>()
      {{
         add("\"Could I have your attention please? Right, now, today, we will be transforming animals into water goblets. Like so. One, two, three. Vera Verto.\" -Minerva McGonagall");
      }};

      text = "Turns an animal in to a flower pot. Size of animal and duration of the spell depends on your experience.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public VERA_VERTO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.VERA_VERTO;
      branch = O2MagicBranch.TRANSFIGURATION;

      targetType = EntityType.FALLING_BLOCK;
      ItemStack flowerPot = new ItemStack(Material.FLOWER_POT, 1);

      disguiseType = DisguiseType.getType(targetType);
      disguise = new MiscDisguise(disguiseType);
      FallingBlockWatcher watcher = (FallingBlockWatcher)disguise.getWatcher();
      watcher.setBlock(flowerPot);

      initSpell();
   }
}
