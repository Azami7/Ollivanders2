package net.pottercraft.ollivanders2.spell;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import net.pottercraft.ollivanders2.Ollivanders2;

/**
 * Spell which will grab the targeted item and pull it toward you
 * with a force determined by your level in the spell.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class ACCIO extends Knockback
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public ACCIO ()
   {
      super();

      spellType = O2SpellType.ACCIO;

      flavorText = new ArrayList<String>() {{
         add("\"Accio Firebolt!\" -Harry Potter");
         add("The Summoning Charm");
      }};

      text = "Can use used to pull an item towards you. The strength of the pull is determined by your experience. "
            + "This can only be used on items.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public ACCIO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.ACCIO;
      pull = true;
      strengthReducer = 10;

      initSpell();
   }
}