package net.pottercraft.ollivanders2.spell;

import net.pottercraft.ollivanders2.divination.O2DivinationType;
import net.pottercraft.ollivanders2.Ollivanders2;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Astrology is the system of using the relative positions of celestial bodies (including the sun, moon, and planets) to try to predict future events or gain insight into personality, relationships, and health.
 * http://harrypotter.wikia.com/wiki/Astrology
 *
 * @author Azami7
 * @since 2.2.9
 */
public class ASTROLOGIA extends Divination
{
   /**
    * Default constructor for use in generating spell text.  Do not use to cast the spell.
    */
   public ASTROLOGIA ()
   {
      super();

      spellType = O2SpellType.ASTROLOGIA;
      divinationType = O2DivinationType.ASTROLOGY;

      flavorText = new ArrayList<String>()
      {{
         add("\"Professor Trelawney did astrology with us! Mars causes accidents and burns and things like that, and when it makes an angle to Saturn, like now, that means people need to be extra careful when handling hot things.\" -Parvati Patil");
         add("\"My dears, it is time for use to consider the stars.\" -Sybill Trelawny");
         add("\"The movements of the planets and the mysterious portents they reveal only to those who understand the steps of the celestial dance. Human destiny may be deciphered by the planetary rays, which intermingle...\" -Sybill Trelawny");
      }};

      text = "Through the study of the position of celestial bodies, one may divine future events or gain insight in to the health or relationships of others.";
   }

   /**
    * Constructor.
    *
    * @param plugin    a callback to the MC plugin
    * @param player    the player who cast this spell
    * @param rightWand which wand the player was using
    */
   public ASTROLOGIA (Ollivanders2 plugin, Player player, Double rightWand)
   {
      super(plugin, player, rightWand);

      spellType = O2SpellType.ASTROLOGIA;
      divinationType = O2DivinationType.ASTROLOGY;

      setUsesModifier();
   }
}
