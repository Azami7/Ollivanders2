package net.pottercraft.Ollivanders2.Spell;

import java.util.ArrayList;
import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

/**
 * Shoots the hit entity away from you.
 *
 * @version Ollivanders2
 * @author lownes
 * @author Azami7
 */
public final class DEPULSO extends Knockback
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public DEPULSO ()
   {
      super();

      spellType = O2SpellType.DEPULSO;

      flavorText = new ArrayList<String>() {{
         add("They were supposed to be practising the opposite of the Summoning Charm today — the Banishing Charm. Owing to the potential for nasty accidents when objects kept flying across the room. Professor Flitwick had given each student a stack of cushions on which to practise, the theory being that these wouldn’t hurt anyone if they went off target.");
         add("The Banishing Charm");
      }};

      text = "Depulso will repel any entity you hit with it.";
   }

   /**
    * Constructor.
    *
    * @param plugin a callback to the MC plugin
    * @param player the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public DEPULSO (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);
      spellType = O2SpellType.DEPULSO;

      // set up usage modifier, has to be done here to get the uses for this specific spell
      setUsesModifier();

      strengthReducer = 20;
   }
}