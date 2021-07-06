package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.item.ItemEnchantmentType;
import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;
import org.jetbrains.annotations.NotNull;

/**
 * Places a geminio affect on the item.
 *
 * @author lownes
 * @author Azami7
 * @version Ollivanders2
 */
public final class GEMINIO extends ItemEnchant
{
   public static final String geminio = "Geminio";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public GEMINIO()
   {
      super();

      spellType = O2SpellType.GEMINIO;
      branch = O2MagicBranch.DARK_ARTS;

      flavorText = new ArrayList<>()
      {{
         add("Hermione screamed in pain, and Harry turned his wand on her in time to see a jewelled goblet tumbling from her grip. But as it fell, it split, became a shower of goblets, so that a second later, with a great clatter, the floor was covered in identical cups rolling in every direction, the original impossible to discern amongst them.");
         add("The Doubling Curse");
      }};

      text = "Geminio will cause an item to duplicate when held by a person.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public GEMINIO(@NotNull Ollivanders2 plugin, @NotNull Player player, @NotNull Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.GEMINIO;
      branch = O2MagicBranch.DARK_ARTS;
      enchantmentType = ItemEnchantmentType.GEMINIO;

      strength = 0.25;

      initSpell();
   }
}