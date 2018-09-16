package net.pottercraft.Ollivanders2.Spell;

import org.bukkit.entity.Player;

import net.pottercraft.Ollivanders2.Ollivanders2;

import java.util.ArrayList;

/**
 * Causes blindness in a radius
 *
 * @see FumosSuper
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class FUMOS extends FumosSuper
{
   public O2SpellType spellType = O2SpellType.FUMOS;

   protected ArrayList<String> flavorText = new ArrayList<String>() {{
      add("The Smoke-Screen Spell");
   }};

   protected String text = "Fumos will cause those in an area to be blinded by a smoke cloud.";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public FUMOS () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public FUMOS (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      strength = 1;
   }
}
