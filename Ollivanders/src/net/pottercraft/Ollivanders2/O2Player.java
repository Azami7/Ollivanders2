package net.pottercraft.Ollivanders2;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.disguisetypes.MobDisguise;
import me.libraryaddict.disguise.disguisetypes.TargetedDisguise;
import me.libraryaddict.disguise.disguisetypes.watchers.*;
import net.pottercraft.Ollivanders2.Spell.SpellProjectile;
import net.pottercraft.Ollivanders2.Spell.Spells;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

/**
 * Ollivanders2 player
 *
 * This adds additional functionality beyond the original OPlayer. Since the original
 * file-based save used serialization, a new class was created for backwards compatibility.
 *
 * @author Azami7
 * @author autumnwoz
 * @since 2.5.2
 */

public class O2Player
{
   private String wandWood = null;
   private String wandCore = null;
   private String playerName = null;
   private UUID pid = null;
   private Ollivanders2 p = null;
   private Ollivanders2Common common;
   private Map<Spells, Integer> knownSpells = new HashMap<>();
   private Map<String, Integer> knownPotions = new HashMap<>();
   private Map<Spells, Long> recentSpells = new HashMap<>();
   private List<OEffect> effects = new ArrayList<>();
   ArrayList<EntityType> animagusShapes = new ArrayList<>();
   //This is the spell loaded into the wand for casting with left click
   private Spells wandSpell = null;
   private Spells masterSpell = null;
   private ArrayList<Spells> masteredSpells = new ArrayList<>();
   private int souls = 0;
   private boolean invisible = false;
   private boolean inRepelloMuggleton = false;
   private boolean foundWand = false;
   private EntityType animagusForm = null;
   private String animagusColor = null;
   private boolean isTransformed = false;
   private TargetedDisguise disguise;
   private boolean muggle = true;
   private Year year = Year.YEAR_1;

   /**
    * Wand wood types
    */
   private final ArrayList<String> woodArray = new ArrayList<String>() {{
      add("Spruce");
      add("Jungle");
      add("Birch");
      add("Oak");
   }};

   /**
    * Wand core types
    */
   private final ArrayList<String> coreArray = new ArrayList<String>() {{
      add("Spider Eye");
      add("Bone");
      add("Rotten Flesh");
      add("Gunpowder");
   }};

   private String loreConjunction = " and ";

   /**
    * Constructor.
    *
    * @param id the UUID of the player
    * @param name the name of the player
    * @param plugin a reference to the plugin
    */
   public O2Player (UUID id, String name, Ollivanders2 plugin)
   {
      p = plugin;
      playerName = name;
      pid = id;

      // set destined wand
      initDestinedWand();

      animagusShapes.add(EntityType.OCELOT);
      animagusShapes.add(EntityType.WOLF);
      animagusShapes.add(EntityType.COW);
      animagusShapes.add(EntityType.PIG);
      animagusShapes.add(EntityType.HORSE);
      animagusShapes.add(EntityType.SHEEP);
      animagusShapes.add(EntityType.RABBIT);
      animagusShapes.add(EntityType.MULE);
      animagusShapes.add(EntityType.DONKEY);

      if (Ollivanders2.hostileMobAnimagi)
      {
         animagusShapes.add(EntityType.SPIDER);
         animagusShapes.add(EntityType.SLIME);
         animagusShapes.add(EntityType.CAVE_SPIDER);
         animagusShapes.add(EntityType.CREEPER);
         animagusShapes.add(EntityType.EVOKER);
         animagusShapes.add(EntityType.HUSK);
         animagusShapes.add(EntityType.SILVERFISH);
         animagusShapes.add(EntityType.WITCH);
         animagusShapes.add(EntityType.VINDICATOR);
         animagusShapes.add(EntityType.SHULKER);
      }

      if (Ollivanders2.mcVersionCheck())
      {
         animagusShapes.add(EntityType.POLAR_BEAR);
         animagusShapes.add(EntityType.LLAMA);
      }

      common = new Ollivanders2Common(plugin);
   }

   /**
    * Initialize the player's destined wand seeded with their pid
    */
   private void initDestinedWand ()
   {
      // set destined wand
      int seed = Math.abs(pid.hashCode()%16);
      int wood = seed/4;
      int core = seed%4;

      wandWood = woodArray.get(wood);
      wandCore = coreArray.get(core);
   }

   /**
    * Determine if a wand matches the player's destined wand type.
    *
    * @param stack the wand to check
    * @return true if is a wand and it matches, false otherwise
    */
   public boolean isDestinedWand (ItemStack stack)
   {
      if (wandWood == null || wandCore == null)
         return false;

      if (p.isWand(stack))
      {
         List<String> lore = stack.getItemMeta().getLore();
         String[] comps = lore.get(0).split(loreConjunction);

         if (wandWood.equalsIgnoreCase(comps[0]) && wandCore.equalsIgnoreCase(comps[1]))
         {
            foundWand = true;
            muggle = false;
            return true;
         }
         else
         {
            return false;
         }
      }
      else
      {
         return false;
      }
   }

   /**
    * Get the player's destined wand lore.
    *
    * @return
    */
   public String getDestinedWandLore ()
   {
      String wandLore = wandWood + loreConjunction + wandCore;

      return wandLore;
   }

   /**
    * Get the player's destined wand wood type.
    *
    * @return
    */
   public String getWandWood ()
   {
      return wandWood;
   }

   /**
    * Get the player's destined wand core type.
    *
    * @return
    */
   public String getWandCore ()
   {
      return wandCore;
   }

   /**
    * Set the player's destined wand wood type. This overrides the current value.
    *
    * @param wood
    */
   public void setWandWood (String wood)
   {
      if (woodArray.contains(wood))
      {
         wandWood = wood;
      }
   }

   /**
    * Set the player's destined wand core type. This overrides the current value.
    *
    * @param core
    */
   public void setWandCore (String core)
   {
      if (coreArray.contains(core))
      {
         wandCore = core;
      }
   }

   /**
    * Get the name of this player for use in commands like listing out house membership. Since player names
    * can change, this should not be used to identify a player. Instead, use the UUID of player and the O2Players
    * map to find their O2Player object.
    *
    * @return
    */
   public String getPlayerName ()
   {
      return playerName;
   }

   /**
    * Sets the name of this player for use in commands like listing out house membership.
    *
    * @param name
    */
   public void setPlayerName (String name)
   {
      playerName = name;
   }

   /**
    * Get the casting count for a spell
    *
    * @param spell the spell to get a count for
    * @return the number of times a player has cast this spell
    */
   public int getSpellCount (Spells spell)
   {
      int count = 0;

      if (knownSpells.containsKey(spell))
      {
         count = knownSpells.get(spell).intValue();
      }

      return count;
   }

   /**
    * Get the brewing count for a potion
    *
    * @param potion the spell to get a count for
    * @return the number of times a player has cast this spell
    */
   public int getPotionCount (String potion)
   {
      int count = 0;

      if (knownPotions.containsKey(potion))
      {
         count = knownPotions.get(potion).intValue();
      }

      return count;
   }

   /**
    * Get the casting count for a spell
    *
    * @param spell the spell to get a count for
    * @return the number of times a player has cast this spell
    */
   public Long getSpellLastCastTime (Spells spell)
   {
      Long count = new Long(0);

      if (recentSpells.containsKey(spell))
      {
         count = recentSpells.get(spell);
      }

      return count;
   }

   /**
    * Get the list of known spells for this player.
    *
    * @return a map of all the known spells and the spell count for each.
    */
   public Map<Spells, Integer> getKnownSpells ()
   {
      return knownSpells;
   }

   public Map<String, Integer> getKnownPotions ()
   {
      return knownPotions;
   }

   /**
    * Get the list of recently cast spells for this player.
    *
    * @return a map of all recent spells and the time they were cast.
    */
   public Map<Spells, Long> getRecentSpells () { return recentSpells; }

   /**
    * Set the spell count for a spell. This will override the existing values for this spell and should
    * not be used when increment is intended.
    *
    * @param spell the spell to set the count for
    * @param count the count to set
    */
   public void setSpellCount (Spells spell, int count)
   {
      if (count >= 1)
      {
         if (knownSpells.containsKey(spell))
         {
            knownSpells.replace(spell, new Integer(count));
         }
         else
         {
            knownSpells.put(spell, new Integer(count));
         }
      }
      else
      {
         if (knownSpells.containsKey(spell))
            knownSpells.remove(spell);
      }

      // remove spell from mastered list if level is less than 100
      if (count < 100)
      {
         removeMasteredSpell(spell);
      }
      // add spell to mastered list if level is at or over 100
      else
      {
         addMasteredSpell(spell);
      }
   }

   /**
    * Set the potion count for a potion. This will override the existing values for this potion and should
    * not be used when increment is intended.
    *
    * @param potion the potion to set the count for
    * @param count the count to set
    */
   public void setPotionCount (String potion, int count)
   {
      if (count >= 1)
      {
         if (knownPotions.containsKey(potion))
         {
            knownPotions.replace(potion, new Integer(count));
         }
         else
         {
            knownPotions.put(potion, new Integer(count));
         }
      }
      else
      {
         if (knownPotions.containsKey(potion))
            knownPotions.remove(potion);
      }
   }

   /**
    * Set the most recent cast time for a spell. This will override the existing values for this spell.
    *
    * @param spell the spell to set the time for
    */
   public void setSpellRecentCastTime (Spells spell)
   {
      String spellClass = "net.pottercraft.Ollivanders2.Spell." + spell.toString();
      @SuppressWarnings("rawtypes")
      Constructor c = null;
      try
      {
         c = Class.forName(spellClass).getConstructor();
         SpellProjectile s = (SpellProjectile) c.newInstance();
         if (recentSpells.containsKey(spell))
         {
            recentSpells.replace(spell, System.currentTimeMillis() + s.getCoolDown());
         }
         else
         {
            recentSpells.put(spell, System.currentTimeMillis() + s.getCoolDown());
         }
      }
      catch (InvocationTargetException e)
      {
         e.getCause().printStackTrace();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Set the wand's mastered spell.
    *
    * @param spell the mastered spell
    */
   public void setMasterSpell (Spells spell)
   {
      masterSpell = spell;
   }

   /**
    * Increment the spell count by 1.
    *
    * @param spell the spell to increment
    */
   public void incrementSpellCount (Spells spell)
   {
      if (knownSpells.containsKey(spell))
      {
         int curCount = knownSpells.get(spell).intValue();
         knownSpells.replace(spell, new Integer(curCount + 1));

         if (curCount + 1 >= 100)
         {
            addMasteredSpell(spell);
         }
      }
      else
      {
         knownSpells.put(spell, new Integer(1));
      }
   }

   /**
    * Increment the potion count by 1.
    *
    * @param potion the potion to increment
    */
   public void incrementPotionCount (String potion)
   {
      if (knownPotions.containsKey(potion))
      {
         int curCount = knownPotions.get(potion).intValue();
         knownPotions.replace(potion, new Integer(curCount + 1));
      }
      else
      {
         knownPotions.put(potion, new Integer(1));
      }
   }

   /**
    * Resets the known spells for this player to none.
    */
   public void resetSpellCount ()
   {
      knownSpells.clear();
      masteredSpells.clear();
   }

   /**
    * Resets the known spells for this player to none.
    */
   public void resetPotionCount ()
   {
      knownPotions.clear();
   }

   /**
    * Get the spell currently loaded in to the player's wand.
    *
    * @return the loaded spell
    */
   public Spells getWandSpell ()
   {
      if (wandSpell == null && masterSpell != null && Ollivanders2.nonVerbalCasting)
         return masterSpell;

      return wandSpell;
   }

   /**
    * Loads a spell in to the player's wand.
    *
    * @param spell the spell to load
    */
   public void setWandSpell (Spells spell)
   {
      if (Ollivanders2.debug)
      {
         if (spell == null)
            p.getLogger().info("Setting wand spell to null");
         else
            p.getLogger().info("Setting wand spell to " + spell.toString());
      }

      wandSpell = spell;
   }

   /**
    * Determine if this player is invisible.
    *
    * @return true if the player is invisible, false otherwise.
    */
   public boolean isInvisible ()
   {
      return invisible;
   }

   /**
    * Set whether a player is invisible
    *
    * @param isInvisible true if the player is invisible, false if they are not
    */
   public void setInvisible(boolean isInvisible)
   {
      invisible = isInvisible;
   }

   /**
    * Determine if the player is in a Repello Muggleton.
    *
    * @return true if they are a in a repello muggleton, false otherwise.
    */
   public boolean isInRepelloMuggleton ()
   {
      return inRepelloMuggleton;
   }

   /**
    * Set if a player is in a repello muggleton.
    *
    * @param isInRepelloMuggleton true if the player is in a repello muggleton, false otherwise
    */
   public void setInRepelloMuggleton (boolean isInRepelloMuggleton)
   {
      inRepelloMuggleton = isInRepelloMuggleton;
   }

   /**
    * Determine if player is a muggle.
    *
    * @return true if they are a muggle, false otherwise
    */
   public boolean isMuggle () { return muggle; }

   /**
    * Set if a player is a muggle.
    *
    * @param isMuggle true if the player is a muggle
    */
   public void setMuggle (boolean isMuggle) { muggle = isMuggle; }

   /**
    * Get the number of souls this player has collected.
    *
    * @return
    */
   public int getSouls ()
   {
      return souls;
   }

   /**
    * Set the number of souls this player has collected.
    *
    * @param s
    */
   public void setSouls (int s)
   {
      souls = s;
   }

   /**
    * Add a soul to this player.
    */
   public void addSoul ()
   {
      souls++;
   }

   /**
    * Remove a soul from this player.
    */
   public void subtractSoul()
   {
      if (souls > 0)
      {
         souls--;
      }
   }

   /**
    * Get the year this player is in.
    * @return The year the player is in
    */
   public Year getYear() {
      return year;
   }

   /**
    * Set the year this player is in.
    * @param y The year to set them to
    */
   public void setYear(Year y) {
      if (year != null) {
         year = y;
      }
   }

   /**
    * Reset the soul count to zero.
    */
   public void resetSouls ()
   {
      souls = 0;
   }

   /**
    * Get a list of all the Ollivanders effects this user has on them.
    *
    * @return
    */
   public List<OEffect> getEffects ()
   {
      List<OEffect> effectsCopy = new ArrayList<>();

      for (OEffect e : effects)
      {
         effectsCopy.add(e);
      }

      return effectsCopy;
   }

   /**
    * Add an effect to this player.
    *
    * @param e
    */
   public void addEffect (OEffect e)
   {
      effects.add(e);

      if (Ollivanders2.debug)
         p.getLogger().info("Adding effect " + e.toString() + " to " + playerName);
   }

   /**
    * Remove an effect from this player.
    *
    * @param e
    */
   public void removeEffect (OEffect e)
   {
      effects.remove(e);

      if (Ollivanders2.debug)
         p.getLogger().info("Removing effect " + e.toString() + " to " + playerName);
   }

   /**
    * Remove all effects from this player.
    */
   public void resetEffects ()
   {
      effects.clear();
   }

   /**
    * Set whether the player has found their destined wand before.
    *
    * @param b
    */
   public void setFoundWand (boolean b)
   {
      foundWand = b;
   }

   /**
    * Has this player found their destined wand?
    *
    * @return true if they have, false if not
    */
   public boolean foundWand ()
   {
      return foundWand;
   }

   /**
    * Returns this player's spell journal, a book with all known spells and their level.
    *
    * @return the player's spell journal
    */
   public ItemStack getSpellJournal ()
   {
      ItemStack spellJournal = new ItemStack(Material.WRITTEN_BOOK, 1);

      BookMeta bookMeta = (BookMeta)spellJournal.getItemMeta();
      bookMeta.setAuthor(playerName);
      bookMeta.setTitle("Spell Journal");

      String content = new String("Spell Journal\n\n");
      int lineCount = 2;
      for (Entry <Spells, Integer> e : knownSpells.entrySet())
      {
         // if we have done 14 lines, make a new page
         if (lineCount == 14)
         {
            bookMeta.addPage(content);
            lineCount = 0;
            content = "";
         }

         // add a newline to all lines except the first
         if (lineCount != 0)
         {
            content = content + "\n";
         }

         String spell = Ollivanders2Common.firstLetterCapitalize(Ollivanders2Common.enumRecode(e.getKey().toString().toLowerCase()));
         String count = e.getValue().toString();
         String line = spell + " " + count;
         content = content + spell + " " + count;

         lineCount++;
         // ~18 characters per line, this will likely wrap
         if (line.length() > 18)
         {
            lineCount++;
         }
      }

      bookMeta.addPage(content);

      bookMeta.setGeneration(BookMeta.Generation.ORIGINAL);
      spellJournal.setItemMeta(bookMeta);

      return spellJournal;
   }

   /**
    * Add a spell to the list of spells that have a level of 100 or more. If this spell is the first mastered
    * spell then also load it to the wand master spell.
    *
    * @param spell the spell to add
    */
   private void addMasteredSpell (Spells spell)
   {
      if (!masteredSpells.contains(spell))
      {
         if (masteredSpells.size() < 1)
         {
            // this is their first mastered spell, set it on their wand
            masterSpell = spell;
         }
         masteredSpells.add(spell);
      }
   }

   /**
    * Remove a mastered spell when the level goes below 100 or is reset. If this spell is also set as the wand's
    * master spell, shift it to the next mastered spell or remove if there are none.
    *
    * @param spell the spell to remove
    */
   private void removeMasteredSpell (Spells spell)
   {
      if (masteredSpells.contains(spell))
      {
         // first remove this from the loaded master spell if it is that spell
         if (masterSpell == spell)
         {
            if (masteredSpells.size() > 1)
            {
               shiftMasterSpell();
            }
            else
            {
               masterSpell = null;
            }
         }

         masteredSpells.remove(spell);
      }
   }

   /**
    * Shift the wand's master spell to the next spell.
    */
   public void shiftMasterSpell ()
   {
      shiftMasterSpell(false);
   }

   /**
    * Shift the wand's master spell to the next spell.
    *
    * @param reverse if set to true, iterates backwards through spell list
    */
   public void shiftMasterSpell (boolean reverse)
   {
      // shift to the next spell if there is more than one mastered spell
      if (masteredSpells.size() >= 1)
      {
         if (masterSpell == null || masteredSpells.size() == 1)
         {
            masterSpell = masteredSpells.get(0);
         }
         else
         {
            int curSpellIndex = masteredSpells.indexOf(masterSpell);
            int nextSpellIndex;

            if (reverse)
            {
               nextSpellIndex = curSpellIndex + 1;
            }
            else
            {
               nextSpellIndex = curSpellIndex - 1;
            }

            // handle roll overs
            if (nextSpellIndex >= masteredSpells.size())
            {
               nextSpellIndex = 0;
            }
            else if (nextSpellIndex < 0)
            {
               nextSpellIndex = masteredSpells.size() - 1;
            }

            masterSpell = masteredSpells.get(nextSpellIndex);
         }
      }
      else
      {
         masterSpell = null;
      }
   }

   /**
    * Get the wand's master spell.
    *
    * @return the wand's master spell
    */
   public Spells getMasterSpell ()
   {
      return masterSpell;
   }

   /**
    * Sets that this player is an Animagus.
    */
   public void setIsAnimagus ()
   {
      if (animagusForm == null)
      {
         int form = 0;

         if (Ollivanders2.mcVersionCheck())
         {
            form = Math.abs(pid.hashCode() % animagusShapes.size());
         }
         else
         {
            // last 2 types are MC 1.12 and higher
            form = Math.abs(pid.hashCode() % (animagusShapes.size() - 2));
         }

         animagusForm = animagusShapes.get(form);
         if (Ollivanders2.debug)
            p.getLogger().info(playerName + " is an animagus type " + animagusForm.toString());

         // determine color variations for certain types
         if (animagusForm == EntityType.OCELOT)
         {
            animagusColor = common.randomOcelotType().toString();
         }
         else if (animagusForm == EntityType.WOLF)
         {
            animagusColor = common.randomSecondaryDyeColor().toString();
         }
         else if (animagusForm == EntityType.HORSE)
         {
            animagusColor = common.randomHorseColor().toString();
         }
         else if (Ollivanders2.mcVersionCheck() && animagusForm == EntityType.LLAMA)
         {
            animagusColor = common.randomLlamaColor().toString();
         }

         if (animagusColor != null && Ollivanders2.debug)
         {
            p.getLogger().info("Color variation " + animagusColor);
         }
      }
   }

   /**
    * Sets the Animagus form for this player.
    *
    * @param type the player's Animagus form.
    */
   public void setAnimagusForm (EntityType type)
   {
      if (animagusShapes.contains(type))
      {
         animagusForm = type;
      }
      else
      {
         // they do not have an allowed type, reset their type
         animagusForm = null;
         animagusColor = null;
         setIsAnimagus();
      }
   }

   public void setAnimagusColor (String color)
   {
      animagusColor = color;
   }

   /**
    * Get the Animagus form for this player.
    *
    * @return the player's Animagus form or null if they are not an Animagus
    */
   public EntityType getAnimagusForm ()
   {
      return animagusForm;
   }

   /**
    * Get the color variation for this animagus.
    *
    * @return the color variation or null if not applicable
    */
   public String getAnimagusColor ()
   {
      // in case color was set when player has no animagus form
      if (animagusForm != null)
         return animagusColor;
      else
         return null;
   }

   /**
    * Determine if this player an Animagus.
    *
    * @return true if they have an Animagus form, false otherwise
    */
   public boolean isAnimagus ()
   {
      if (animagusForm != null)
         return true;
      else
         return false;
   }

   /**
    * Determine if this player is transformed in to their Animagus form. This is only for use in saving
    * player state to disk and when a player logs in.
    *
    * @return
    */
   public boolean isTransformed ()
   {
      return isTransformed;
   }

   /**
    * Changes a player to their Animagus form or transforms them back.
    */
   public void animagusForm ()
   {
      if (animagusForm == null)
      {
         // this player is not an animagus
         return;
      }

      if (isTransformed == false)
      {
         // transform the player in to their Animagus form
         DisguiseType disguiseType = DisguiseType.getType(animagusForm);
         disguise = new MobDisguise(disguiseType);
         LivingWatcher watcher = (LivingWatcher)disguise.getWatcher();

         if (animagusForm == EntityType.OCELOT)
         {
            OcelotWatcher ocelotWatcher = (OcelotWatcher)watcher;
            Ocelot.Type type = Ocelot.Type.WILD_OCELOT;
            ocelotWatcher.isAdult();

            try
            {
               type = Ocelot.Type.valueOf(animagusColor);
            }
            catch (Exception e)
            {
               p.getLogger().warning("Failed to parse Ocelot.Type " + animagusColor);
               if (p.debug)
                  e.printStackTrace();
            }
            finally
            {
               if (type != null)
               {
                  ocelotWatcher.setType(type);
               }
            }
         }
         else if (animagusForm == EntityType.WOLF)
         {
            WolfWatcher wolfWatcher = (WolfWatcher)watcher;
            DyeColor color = DyeColor.WHITE;
            wolfWatcher.isAdult();

            try
            {
               color = DyeColor.valueOf(animagusColor);
            }
            catch (Exception e)
            {
               p.getLogger().warning("Failed to parse DyeColor " + animagusColor);
               if (p.debug)
                  e.printStackTrace();
            }
            finally
            {
               if (color != null)
               {
                  wolfWatcher.isTamed();
                  wolfWatcher.setCollarColor(color);
               }
            }
         }
         else if (animagusForm == EntityType.HORSE)
         {
            HorseWatcher horseWatcher = (HorseWatcher)watcher;
            horseWatcher.setStyle(Horse.Style.NONE);
            Horse.Color color = Horse.Color.WHITE;
            horseWatcher.setBaby();

            try
            {
               color = Horse.Color.valueOf(animagusColor);
            }
            catch (Exception e)
            {
               p.getLogger().warning("Failed to parse Horse.Color " + animagusColor);
               if (p.debug)
                  e.printStackTrace();
            }
            finally
            {
               if (color != null)
               {
                  horseWatcher.setColor(color);
               }
            }
         }
         else if (Ollivanders2.mcVersionCheck() && animagusForm == EntityType.LLAMA)
         {
            LlamaWatcher llamaWatcher = (LlamaWatcher)watcher;
            Llama.Color color = Llama.Color.WHITE;
            llamaWatcher.setBaby();

            try
            {
               color = Llama.Color.valueOf(animagusColor);
            }
            catch (Exception e)
            {
               p.getLogger().warning("Failed to parse Llama.Color " + animagusColor);
               if (p.debug)
                  e.printStackTrace();
            }
            finally
            {
               if (color != null)
               {
                  llamaWatcher.setColor(color);
               }
            }
         }
         else if (animagusForm == EntityType.COW || animagusForm == EntityType.DONKEY
               || animagusForm == EntityType.MULE || animagusForm == EntityType.SLIME
               || animagusForm == EntityType.POLAR_BEAR)
         {
            AgeableWatcher ageableWatcher = (AgeableWatcher)watcher;
            ageableWatcher.setBaby();
         }

         Entity player = p.getServer().getPlayer(pid);

         DisguiseAPI.disguiseToAll(player, disguise);

         isTransformed = true;
      }
      else // isTransformed = true
      {
         // transform this player to their normal form
         Entity entity = disguise.getEntity();
         try
         {
            DisguiseAPI.undisguiseToAll(entity);
         }
         catch (Exception e)
         {
            // in case entity no longer exists
         }

         isTransformed = false;
      }
   }

   /**
    * Get the player's UUID
    *
    * @return
    */
   public UUID getID ()
   {
      return pid;
   }
}
