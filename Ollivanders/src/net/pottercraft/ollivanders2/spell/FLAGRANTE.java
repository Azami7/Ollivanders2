package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.item.enchantment.ItemEnchantmentType;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Places a flagrante affect on the item.
 *
 * @author Azami7
 */
public final class FLAGRANTE extends ItemEnchant
{
   public static final String flagrante = "Flagrante";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public FLAGRANTE ()
   {
      super();

      spellType = O2SpellType.FLAGRANTE;
      branch = O2MagicBranch.DARK_ARTS;

      flavorText = new ArrayList<>() {{
         add("Burning Curse");
         add("They have added Geminio and Flagrante curses! Everything you touch will burn and multiply, but the copies are worthless.");
      }};

      text = "Flagrante will cause an item to burn it's bearer when picked up.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public FLAGRANTE(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.FLAGRANTE;
      branch = O2MagicBranch.DARK_ARTS;
      enchantmentType = ItemEnchantmentType.FLAGRANTE;

      initSpell();
   }
}