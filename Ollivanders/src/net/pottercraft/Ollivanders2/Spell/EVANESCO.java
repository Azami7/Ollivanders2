package net.pottercraft.Ollivanders2.Spell;

import com.sk89q.worldguard.protection.flags.DefaultFlag;
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
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public EVANESCO ()
   {
      super();

      spellType = O2SpellType.EVANESCO;

      flavorText = new ArrayList<String>() {{
         add("The Vanishing Spell");
         add("The contents of Harry’s potion vanished; he was left standing foolishly beside an empty cauldron.");
      }};

      text = "Evanesco will vanish an entity.";
   }

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
      spellType = O2SpellType.EVANESCO;

      // set up usage modifier, has to be done here to get the uses for this specific spell
      setUsesModifier();

      // world guard flags
      worldGuardFlags.add(DefaultFlag.DAMAGE_ANIMALS);
      worldGuardFlags.add(DefaultFlag.USE);
   }

   public void checkEffect ()
   {
      simpleTransfigure(null, null);
   }
}