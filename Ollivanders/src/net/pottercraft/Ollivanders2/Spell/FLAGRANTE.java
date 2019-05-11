package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

/**
 * Places a flagrante affect on the item.
 *
 * @author lownes
 */
public final class FLAGRANTE extends ItemCurse
{
   public static final String flagrante = "Flagrante";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public FLAGRANTE ()
   {
      super();

      spellType = O2SpellType.FLAGRANTE;

      flavorText = new ArrayList<String>() {{
         add("Burning Curse");
         add("They have added Geminio and Flagrante curses! Everything you touch will burn and multiply, but the copies are worthless.");
      }};

      text = "Flagrante will cause an item to burn it's bearer when picked up.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public FLAGRANTE (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.FLAGRANTE;

      // set up usage modifier, has to be done here to get the uses for this specific spell
      setUsesModifier();

      curseLabel = flagrante;
   }
}