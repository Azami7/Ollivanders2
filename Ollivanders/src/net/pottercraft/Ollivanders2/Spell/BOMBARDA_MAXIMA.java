package net.pottercraft.Ollivanders2.Spell;

import net.pottercraft.Ollivanders2.Ollivanders2;

import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Creates an explosion at the target location twice as powerful as bombarda. Doesn't break blocks.
 *
 * @see BombardaSuper
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class BOMBARDA_MAXIMA extends BombardaSuper
{
   public O2SpellType spellType = O2SpellType.BOMBARDA_MAXIMA;

   protected ArrayList<String> flavorText = new ArrayList<String>() {{
      add("A more powerful explosion incantation.");
      add("\"Come on, let’s get destroying... Confringo? Stupefy? Bombarda? Which would you use?\" -Albus Potter");
   }};

   protected String text = "Bombarda Maxima creates an explosion twice as powerful as Bombarda which doesn't damage the terrain.";

   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public BOMBARDA_MAXIMA () { }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public BOMBARDA_MAXIMA (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      strength = 1.6;
   }
}