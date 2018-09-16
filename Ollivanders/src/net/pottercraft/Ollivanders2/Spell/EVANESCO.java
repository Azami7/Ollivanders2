package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Vanishes an entity. The entity will reappear after a certain time.
 *
 * @author lownes
 * @author Azami7
 */
public final class EVANESCO extends Transfiguration
{
   public O2SpellType spellType = O2SpellType.EVANESCO;

   protected ArrayList<String> flavorText = new ArrayList<String>() {{
      add("The Vanishing Spell");
      add("The contents of Harry’s potion vanished; he was left standing foolishly beside an empty cauldron.");
   }};

   protected String text = "Evanesco will vanish an entity.";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public EVANESCO () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public EVANESCO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
   }

   public void checkEffect ()
   {
      simpleTransfigure(null, null);
   }
}