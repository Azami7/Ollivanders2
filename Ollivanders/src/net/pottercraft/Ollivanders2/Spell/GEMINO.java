package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Places a gemino affect on the item.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class GEMINO extends ItemCurse
{
   public static final String geminio = "Geminio";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public GEMINO ()
   {
      super();

      spellType = O2SpellType.GEMINO;

      flavorText = new ArrayList<String>() {{
         add("Hermione screamed in pain, and Harry turned his wand on her in time to see a jewelled goblet tumbling from her grip. But as it fell, it split, became a shower of goblets, so that a second later, with a great clatter, the floor was covered in identical cups rolling in every direction, the original impossible to discern amongst them.");
         add("The Doubling Curse");
      }};

      text = "Gemino will cause an item to duplicate when held by a person.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public GEMINO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.GEMINO;
      setUsesModifier();

      curseLabel = geminio;
      ItemCurse.itemCurseNames.add(curseLabel);
   }
}