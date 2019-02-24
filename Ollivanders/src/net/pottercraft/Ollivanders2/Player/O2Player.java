package net.pottercraft.Ollivanders2.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.pottercraft.Ollivanders2.House.O2HouseType;
import net.pottercraft.Ollivanders2.Ollivanders2;
import net.pottercraft.Ollivanders2.Ollivanders2API;
import net.pottercraft.Ollivanders2.O2Color;
import net.pottercraft.Ollivanders2.Spell.O2SpellType;
import net.pottercraft.Ollivanders2.Spell.O2Spell;
import net.pottercraft.Ollivanders2.Potion.O2PotionType;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
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
   /**
    * Wand wood material name
    */
   private String wandWood = null;

   /**
    * Wand core material name
    */
   private String wandCore = null;

   /**
    * Player display name
    */
   private String playerName = null;

   /**
    * Player minecraft UUID, this is their primary identifier in Ollivanders2
    */
   private UUID pid = null;

   /**
    * The MC plugin callback
    */
   private Ollivanders2 p = null;

   /**
    * A map of all the spells a player knows and the cast count.
    */
   private Map<O2SpellType, Integer> knownSpells = new HashMap<>();

   /**
    * A map of all the potions a player knows and the brew count.
    */
   private Map<O2PotionType, Integer> knownPotions = new HashMap<>();

   /**
    * A map of the recent spells a player has cast and their cast timestamp
    */
   private Map<O2SpellType, Long> recentSpells = new HashMap<>();

   /**
    * The spell loaded into the wand for casting with left click
    */
   private O2SpellType wandSpell = null;

   /**
    * The last spell cast by this player's wand
    */
   private O2SpellType priorIncantatem = null;

   /**
    * The last spell cast by this player
    */
   private O2SpellType lastSpell = null;

   /**
    * The mastered spell set for silent casting - is cast anytime a player left-clicks their wand in their primary hand.
    */
   private O2SpellType masterSpell = null;

   /**
    * The list of mastered spells - spells with > 100 cast count
    */
   private ArrayList<O2SpellType> masteredSpells = new ArrayList<>();

   /**
    * The number of souls this user has collected
    */
   private int souls = 0;

   /**
    * Whether the player is currently invisible
    */
   private boolean invisible = false;

   /**
    * Whether the player is in a Repello Muggleton area
    */
   private boolean inRepelloMuggleton = false;

   /**
    * Whether the player has found their destined wand yet
    */
   private boolean foundWand = false;

   /**
    * The player'ss animagus form, if they are an animagus
    */
   private EntityType animagusForm = null;

   /**
    * The color variant for the animagus form
    */
   private String animagusColor = null;

   /**
    * Whether the player is a Muggle.
    */
   private boolean muggle = true;

   /**
    * The player's year in school
    */
   private Year year = Year.YEAR_1;

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

      wandWood = O2PlayerCommon.woodArray.get(wood);
      wandCore = O2PlayerCommon.coreArray.get(core);
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

      if (Ollivanders2API.common.isWand(stack))
      {
         List<String> lore = stack.getItemMeta().getLore();
         String[] comps = lore.get(0).split(O2PlayerCommon.wandLoreConjunction);

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
    * @return the wand lore for the players destined wand
    */
   public String getDestinedWandLore ()
   {
      return wandWood + O2PlayerCommon.wandLoreConjunction + wandCore;
   }

   /**
    * Get the player's destined wand wood type.
    *
    * @return the player's destined wand wood type
    */
   public String getWandWood ()
   {
      return wandWood;
   }

   /**
    * Get the player's destined wand core type.
    *
    * @return the player's destined wand core type
    */
   public String getWandCore ()
   {
      return wandCore;
   }

   /**
    * Set the player's destined wand wood type. This overrides the current value.
    *
    * @param wood sets the destined wand wood type
    */
   public void setWandWood (String wood)
   {
      if (O2PlayerCommon.woodArray.contains(wood))
      {
         wandWood = wood;
      }
   }

   /**
    * Set the player's destined wand core type. This overrides the current value.
    *
    * @param core set the destined wand core type
    */
   public void setWandCore (String core)
   {
      if (O2PlayerCommon.coreArray.contains(core))
      {
         wandCore = core;
      }
   }

   /**
    * Get the name of this player for use in commands like listing out house membership. Since player names
    * can change, this should not be used to identify a player. Instead, use the UUID of player and the O2Players
    * map to find their O2Player object.
    *
    * @return the player's name
    */
   public String getPlayerName ()
   {
      return playerName;
   }

   /**
    * Sets the name of this player for use in commands like listing out house membership.
    *
    * @param name the name to set for this player
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
   public int getSpellCount (O2SpellType spell)
   {
      int count = 0;

      if (knownSpells.containsKey(spell))
      {
         count = knownSpells.get(spell);
      }

      return count;
   }

   /**
    * Get the brewing count for a potion
    *
    * @param potionType the spell to get a count for
    * @return the number of times a player has cast this spell
    */
   public int getPotionCount (O2PotionType potionType)
   {
      int count = 0;

      if (knownPotions.containsKey(potionType))
      {
         count = knownPotions.get(potionType);
      }

      return count;
   }

   /**
    * Get the casting count for a spell
    *
    * @param spellType the spell to get a count for
    * @return the number of times a player has cast this spell
    */
   public Long getSpellLastCastTime (O2SpellType spellType)
   {
      Long count = (long) 0;

      if (recentSpells.containsKey(spellType))
      {
         count = recentSpells.get(spellType);
      }

      return count;
   }

   /**
    * Get the list of known spells for this player.
    *
    * @return a map of all the known spells and the spell count for each.
    */
   public Map<O2SpellType, Integer> getKnownSpells ()
   {
      return knownSpells;
   }

   public Map<O2PotionType, Integer> getKnownPotions ()
   {
      return knownPotions;
   }

   /**
    * Get the list of recently cast spells for this player.
    *
    * @return a map of all recent spells and the time they were cast.
    */
   public Map<O2SpellType, Long> getRecentSpells () { return recentSpells; }

   /**
    * Set the spell count for a spell. This will override the existing values for this spell and should
    * not be used when increment is intended.
    *
    * @param spell the spell to set the count for
    * @param count the count to set
    */
   public void setSpellCount (O2SpellType spell, int count)
   {
      if (count >= 1)
      {
         knownSpells.put(spell, count);
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
    * Set the last spell cast by this player.
    *
    * @param spell the spell they most recently cast
    */
   void setLastSpell (O2SpellType spell)
   {
      if (spell != null)
      {
         lastSpell = spell;
      }
   }

   /**
    * Get the last spell cast by this player.
    *
    * @return the last spell cast
    */
   public O2SpellType getLastSpell ()
   {
      return lastSpell;
   }

   /**
    * Set the last spell successfully cast by this player's wand.
    *
    * @param spell the spell they most recently cast
    */
   public void setPriorIncantatem (O2SpellType spell)
   {
      if (spell != null)
      {
         priorIncantatem = spell;
      }
   }

   /**
    * Get the last spell cast by this player's wand.
    *
    * @return the last spell cast by this player with a wand
    */
   public O2SpellType getPriorIncantatem ()
   {
      return priorIncantatem;
   }

   /**
    * Set the potion count for a potion. This will override the existing values for this potion and should
    * not be used when increment is intended.
    *
    * @param potionType the potion to set the count for
    * @param count the count to set
    */
   public void setPotionCount (O2PotionType potionType, int count)
   {
      if (count >= 1)
      {
         knownPotions.put(potionType, count);
      }
      else
      {
         if (knownPotions.containsKey(potionType))
            knownPotions.remove(potionType);
      }
   }

   /**
    * Set the most recent cast time for a spell. This will override the existing values for this spell.
    *
    * @param spellType the spell to set the time for
    */
   public void setSpellRecentCastTime (O2SpellType spellType)
   {
      String spellClass = "net.pottercraft.Ollivanders2.Spell." + spellType.toString();

      Constructor c;
      try
      {
         c = Class.forName(spellClass).getConstructor();
         O2Spell s = (O2Spell) c.newInstance();

         recentSpells.put(spellType, System.currentTimeMillis() + s.getCoolDown());

         setLastSpell(spellType);
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
   public void setMasterSpell (O2SpellType spell)
   {
      masterSpell = spell;
   }

   /**
    * Increment the spell count by 1.
    *
    * @param spellType the spell to increment
    */
   public void incrementSpellCount (O2SpellType spellType)
   {
      if (Ollivanders2.debug)
         p.getLogger().info("Incrementing spell count for " + spellType.toString());

      if (knownSpells.containsKey(spellType))
      {
         int curCount = knownSpells.get(spellType);
         knownSpells.replace(spellType, curCount + 1);

         if (curCount + 1 >= 100)
         {
            addMasteredSpell(spellType);
         }
      }
      else
      {
         knownSpells.put(spellType, 1);
      }
   }

   /**
    * Increment the potion count by 1.
    *
    * @param potionType the potion to increment
    */
   public void incrementPotionCount (O2PotionType potionType)
   {
      if (Ollivanders2.debug)
         p.getLogger().info("Incrementing potion count for " + potionType.toString());

      if (knownPotions.containsKey(potionType))
      {
         int curCount = knownPotions.get(potionType);
         knownPotions.replace(potionType, curCount + 1);
      }
      else
      {
         knownPotions.put(potionType, 1);
      }
   }

   /**
    * Resets the known spells for this player to none.
    */
   public void resetSpellCount ()
   {
      knownSpells.clear();
      masteredSpells.clear();
      masterSpell = null;
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
   public O2SpellType getWandSpell ()
   {
      if (wandSpell == null && masterSpell != null && Ollivanders2.enableNonVerbalSpellCasting)
         return masterSpell;

      return wandSpell;
   }

   /**
    * Loads a spell in to the player's wand.
    *
    * @param spell the spell to load
    */
   public void setWandSpell (O2SpellType spell)
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
    * @return the number of souls this player has collected
    */
   public int getSouls ()
   {
      return souls;
   }

   /**
    * Set the number of souls this player has collected.
    *
    * @param s the number of souls the player has collected
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
    * Set whether the player has found their destined wand before.
    *
    * @param b set whether the player has found their destined wand
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

      StringBuilder content = new StringBuilder();
      content.append("Spell Journal\n\n");
      int lineCount = 2;
      for (Entry <O2SpellType, Integer> e : knownSpells.entrySet())
      {
         // if we have done 14 lines, make a new page
         if (lineCount == 14)
         {
            bookMeta.addPage(content.toString());
            lineCount = 0;
            content = new StringBuilder();
         }

         // add a newline to all lines except the first
         if (lineCount != 0)
         {
            content.append("\n");
         }

         String spell = Ollivanders2API.common.firstLetterCapitalize(Ollivanders2API.common.enumRecode(e.getKey().toString().toLowerCase()));
         String count = e.getValue().toString();
         String line = spell + " " + count;
         content.append(spell).append(" ").append(count);

         lineCount++;
         // ~18 characters per line, this will likely wrap
         if (line.length() > 18)
         {
            lineCount++;
         }
      }

      bookMeta.addPage(content.toString());

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
   private void addMasteredSpell (O2SpellType spell)
   {
      if (spell != O2SpellType.AVADA_KEDAVRA)
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
   }

   /**
    * Remove a mastered spell when the level goes below 100 or is reset. If this spell is also set as the wand's
    * master spell, shift it to the next mastered spell or remove if there are none.
    *
    * @param spell the spell to remove
    */
   private void removeMasteredSpell (O2SpellType spell)
   {
      if (masteredSpells.contains(spell))
      {
         // first remove this from the loaded master spell if it is that spell
         if (masterSpell == spell)
         {
            if (masteredSpells.size() > 1)
            {
               shiftMasterSpell(false);
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
   @Deprecated
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
   public O2SpellType getMasterSpell ()
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

         ArrayList<EntityType> animagusShapes = O2PlayerCommon.getAnimagusShapes();
         form = Math.abs(pid.hashCode() % animagusShapes.size());

         animagusForm = animagusShapes.get(form);
         if (Ollivanders2.debug)
            p.getLogger().info(playerName + " is an animagus type " + animagusForm.toString());

         // determine color variations for certain types
         if (animagusForm == EntityType.OCELOT)
         {
            animagusColor = Ollivanders2API.common.randomOcelotType().toString();
         }
         else if(animagusForm == EntityType.RABBIT)
         {
            animagusColor = Ollivanders2API.common.randomRabbitType().toString();
         }
         else if (animagusForm == EntityType.WOLF)
         {
            animagusColor = O2Color.getRandomPrimaryDyeableColor().getDyeColor().toString();
         }
         else if (animagusForm == EntityType.HORSE)
         {
            animagusColor = Ollivanders2API.common.randomHorseColor().toString();
         }
         else if (Ollivanders2.mcVersion > 11 && animagusForm == EntityType.LLAMA)
         {
            animagusColor = Ollivanders2API.common.randomLlamaColor().toString();
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
      ArrayList<EntityType> animagusShapes = O2PlayerCommon.getAnimagusShapes();

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

   void setAnimagusColor (String color)
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

      return false;
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

   /**
    * Do player onJoin set up
    */
   public void onJoin ()
   {
      Ollivanders2API.getPlayers().playerEffects.onJoin(pid);
      Ollivanders2API.getProphecies().onJoin(pid);
   }

   /**
    * Show log in message
    */
   public String getLogInMessage ()
   {
      p.getLogger().info("creating log in message");

      // check to see what they have done so far to let them know next steps
      StringBuilder message = new StringBuilder();

      if (Ollivanders2.useHouses)
      {
         O2HouseType houseType = Ollivanders2API.getHouses().getHouse(pid);
         if (houseType != null)
         {
            message.append("\n").append(houseType.getName()).append(" is currently ").append(O2HouseType.getHousePlaceTxt(houseType)).append(".");
         }
      }

      if (!foundWand)
         message.append("\nFind your destined wand to begin using magic.");
      else if (Ollivanders2.useHouses && !Ollivanders2API.getHouses().isSorted(pid))
         message.append("\nGet sorted in to your school house to start earning house points.");
      else if (knownSpells.size() < 1 && Ollivanders2.bookLearning)
         message.append("\nFind a spell book to get started learning magic.");
      else if (knownSpells.size() < 1)
         message.append("\nTry casting a spell by saying the incantation and waving your wand.");
      else if (knownPotions.size() < 1 && Ollivanders2.bookLearning)
         message.append("\nFind a potions book and a water-filled cauldron to get started brewing potions.");
      else if (knownPotions.size() < 1)
         message.append("\nTry brewing a potion by using a water-filled cauldron and the potion ingredients.");

      p.getLogger().info(message.toString());
      return message.toString();
   }

   /**
    * Do player onQuit clean up
    */
   public void onQuit ()
   {
      Ollivanders2API.getPlayers().playerEffects.onQuit(pid);
   }

   /**
    * Do player onDeath actions
    */
   public void onDeath ()
   {
      if (Ollivanders2.enableDeathExpLoss)
      {
         resetSpellCount();
         resetPotionCount();
         resetSouls();
         Ollivanders2API.getPlayers().playerEffects.onDeath(pid);
      }

      setWandSpell(null);
   }
}