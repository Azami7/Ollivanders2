package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;

import net.pottercraft.Ollivanders2.O2MagicBranch;
import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Places a geminio affect on the item.
 *
 * @author lownes
 * @author Azami7
 * @version Ollivanders2
 */
public final class GEMINIO extends ItemCurse
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

      flavorText = new ArrayList<String>()
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
   public GEMINIO(Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.GEMINIO;
      branch = O2MagicBranch.DARK_ARTS;

      curseLabel = GEMINIO.geminio;
      strength = 0.25;

      initSpell();
   }
}