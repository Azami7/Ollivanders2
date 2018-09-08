package net.pottercraft.Ollivanders2;

import net.pottercraft.Ollivanders2.Effect.OEffect;
import net.pottercraft.Ollivanders2.Spell.O2SpellType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serializable player object
 *
 * @author lownes
 * @deprecated use net.pottercraft.Ollivanders2.Player.O2Player
 */
@Deprecated
public class OPlayer implements Serializable
{
   private Map<O2SpellType, Integer> SpellCount = new HashMap<>();
   private List<OEffect> effects = new ArrayList<>();
   //This is the spell loaded into the wand for casting with left click
   private O2SpellType spell;
   private int souls;
   private boolean invisible = false;
   private boolean muggleton = false;

   public OPlayer ()
   {
      O2SpellType[] spells = O2SpellType.values();
      for (O2SpellType spell : spells)
      {
         SpellCount.put(spell, 0);
      }
      souls = 0;
   }

   public Map<O2SpellType, Integer> getSpellCount ()
   {
      return SpellCount;
   }

   public void setSpellCount (Map<O2SpellType, Integer> map)
   {
      SpellCount = map;
   }

   public O2SpellType getSpell ()
   {
      return spell;
   }

   public void setSpell (O2SpellType s)
   {
      spell = s;
   }

   public void resetSpellCount ()
   {
      O2SpellType[] spells = O2SpellType.values();
      for (O2SpellType spell : spells)
      {
         SpellCount.put(spell, 0);
      }
   }

   public int getSouls ()
   {
      return souls;
   }

   public void resetSouls ()
   {
      souls = 0;
   }

   public void addSoul ()
   {
      souls++;
   }

   public void subSoul ()
   {
      souls--;
   }

   public List<OEffect> getEffects ()
   {
      return effects;
   }

   public void addEffect (OEffect e)
   {
      effects.add(e);
   }

   public void remEffect (OEffect e)
   {
      effects.remove(e);
   }

   public void resetEffects ()
   {
      effects.clear();
   }

   public boolean isInvisible ()
   {
      return invisible;
   }

   public void setInvisible (boolean invisible)
   {
      this.invisible = invisible;
   }

   /**
    * Has the player been rendered invisible from all other players by repello muggleton
    *
    * @return true if player is hidden
    */
   public boolean isMuggleton ()
   {
      return muggleton;
   }

   public void setMuggleton (boolean mug)
   {
      muggleton = mug;
   }
}