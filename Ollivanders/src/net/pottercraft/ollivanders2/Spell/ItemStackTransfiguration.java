package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

/**
 * The super class for all ItemStack transfigurations.  This cannot be used on Entities or Blocks.
 *
 * @author Azami7
 * @see BlockTransfiguration
 * @since 2.2.6
 */
public abstract class ItemStackTransfiguration extends BlockTransfiguration
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public ItemStackTransfiguration()
   {
      super();
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public ItemStackTransfiguration(Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      permanent = true;
      radius = 1;
   }
}
