package net.pottercraft.Ollivanders2.Book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.pottercraft.Ollivanders2.Ollivanders2;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import net.pottercraft.Ollivanders2.Spell.Spells;

/**
 * Provides the methods to parse a book for spells
 *
 * @deprecated
 * @author cakenggt
 */
@Deprecated
public class SpellBookParser
{
   private final static String ACCIO = "Accio will pull an item toward you. The strength "
         + "of the pull is determined by your experience.";
   private final static String AGUAMENTI = "Aguamenti will cause water to erupt against "
         + "the surface you cast it on.";
   private final static String ALARTE_ASCENDARE = "Alarte Ascendare will shoot the target "
         + "high into the air. It's strength is determined by your experience.";
   private final static String ALIQUAM_FLOO = "Aliquam Floo will register a fireplace with "
         + "the Floo Network. Simply place a sign above a fire with the name of the "
         + "fireplace on the sign and cast this spell at the fire. Once your fireplace "
         + "is registered, you can destroy the sign and even put out the fire, but you "
         + "must not place a solid block where the fire was, or you will have to re-register"
         + " your fireplace. Now, people can come "
         + "to your fireplace via Floo powder, which is made by smelting ender pearl. "
         + "Toss the powder into a fireplace that is registered, walk into the fire, and "
         + "say the name of your destination.";
   private final static String ALOHOMORA = "Alohomora is a spell used to unlock the effects "
         + "of the locking spell.";
   private final static String APARECIUM = "Aparecium will cause any area spells to reveal "
         + "their borders. The amount of revealing depends on your experience.";
   private final static String APPARATE = "Apparition is a two sided spell. To apparate to "
         + "a predetermined location, simply say apparate and list your x, y, and z "
         + "coordinates. To apparate to the location of your cursor, within 140 meters, "
         + "just say the word apparate. Your accuracy is determined by the distance "
         + "traveled and your experience. If there are any entities close to you when "
         + "you apparate, they will be taken with you as well by side-along apparition.";
   private final static String AQUA_ERUCTO = "Aqua Eructo shoots a jet of water from your "
         + "wand tip. The range of this jet is determined by your experience.";
   private final static String ARANIA_EXUMAI = "Arania Exumai will blast away spiders with "
         + "a force dependent on your experience.";
   private final static String ARRESTO_MOMENTUM = "Arresto Momentum will immediately slow down "
         + "any entity. The amount an entity is slowed down is determined by your experience.";
   private final static String ASCENDIO = "Ascendio will propel the caster into the air. The "
         + "strength of the propulsion depends on your experience.";
   private final static String AVADA_KEDAVRA = "Avada kedavra is a forbidden curse which will "
         + "directly damage a living being. The amount of damage done is determiend "
         + "by your experience.";
   private final static String AVIFORS = "Avifors will transfigure the target into a bat for a "
         + "time dependent on your experience.";
   private final static String AVIS = "Avis will cause a bat to fly out of the end of your wand. "
         + "The amount of time the bat is alive depends on your experience.";
   private final static String BOMBARDA = "Bombarda creates an explosion which doesn't damage "
         + "the terrain. The strength of the explosion depends on your experience.";
   private final static String BOMBARDA_MAXIMA = "Bombarda maxima creates an explosion which doesn't "
         + "damage the terrain. The explosion is twice as large as the one created by "
         + "bombarda. The strength of the explosion depends on your experience.";
   private final static String BRACKIUM_EMENDO = "Brackium Emendo will hurt any entity which is "
         + "skeleton based by an amount dependent on your experience.";
   private final static String CARPE_RETRACTUM = "Carpe Retractum will pull a living entity towards "
         + "you. The strength of the pull depends on your experience.";
   private final static String COLLOPORTUS = "Colloportus will lock all blocks within it's area into "
         + "place, not letting them be changed. This spell will not age like other area spells do, "
         + "and must be cancelled with the unlocking spell.";
   private final static String COLOVARIA = "Colovaria changes the dye color of a sheep or the block color in a "
         + "radius to another color randomly. The radius depends on your experience.";
   private final static String CONFUNDO = "Confundo causes the target to become confused. The duration "
         + "of this confusion is determined by your experience.";
   private final static String CRESCERE_PROTEGAT = "Crescere Protegat will grow a stationary spell's "
         + "radius, up to a limit determined by your experience. Only the creator of the "
         + "stationary spell can affect it with this spell.";
   private final static String DEFODIO = "Defodio is a gouging spell that will mine a line of blocks, "
         + "the length of which is determined by your experience.";
   private final static String DELETRIUS = "Deletrius will cause an item entity to stop existing.";
   private final static String DEPRIMO = "Deprimo creates an immense downward pressure which will "
         + "cause all blocks within a radius to fall like sand. The radius is determined "
         + "by your experience.";
   private final static String DEPULSO = "Depulso will repel any entity you hit with it. The strength "
         + "of the repulsion depends on your experience.";
   private final static String DIFFINDO = "Diffindo, if it hits a log, will break any logs within a "
         + "radius of it's impact. The radius depends on your experience. It will also split the "
         + "backpack of any player it hits, spilling out their items. How many items are spilled "
         + "with this event depends on your experience.";
   private final static String DISSENDIUM = "Dissendium will open a door or trapdoor for a few seconds. "
         + "To open a door, aim at the bottom half. "
         + "The distance away which you can open the door depends on your experience.";
   private final static String DUCKLIFORS = "Ducklifors will transfigure an entity into a chicken. "
         + "The length of the transfiguration depends on your experience.";
   private final static String DURO = "Duro will transfigure an entity into a stone. The length "
         + "of the transfiguration depends on your experience. If the stone is destroyed, "
         + "then the entity will die.";
   private final static String DRACONIFORS = "Draconifors will transfigure an entity into a Dragon. "
         + "The length of the transfiguration depends on your experience.";
   private final static String EBUBLIO = "Ebublio, the bubble head charm, will grant your target the ability "
         + "to breathe underwater. The duration of this effect depends on your experience.";
   private final static String ENGORGIO = "Engorgio will grow a baby animal, grow a slime, and grow a zombie "
         + "from a baby into an adult or from adult into a giant. The effects of this spell depend on your "
         + "experience. Growing giants is not possible until complete mastery of the spell is achieved.";
   private final static String ENTOMORPHIS = "Entomorphis will transfigure an entity into a silverfish for "
         + "a duration dependent on your experience. If it hits a stone brick, cobblestone, or chiseled stone, "
         + "it will turn that into a silverfish monster egg.";
   private final static String EPISKEY = "Episkey will heal minor injuries. The duration of the healing "
         + "effect depends on your experience.";
   private final static String ET_INTERFICIAM_ANIMAM_LIGAVERIS = "The unholy incantation, "
         + "et interficiam animam ligaveris, will create a horcrux.";
   private final static String EVANESCO = "Evanesco will vanish an entity. "
         + "The length of the vanishment depends on your experience.";
   private final static String EVERTE_STATUM = "Everte Statum will throw another player backwards. "
         + "The force of the throw depends on your experience.";
   private final static String EXPELLIARMUS = "Expelliarmus will cause an entity's "
         + "held item to be flung at you with a force which depends on your experience.";
   private final static String FIANTO_DURI = "Fianto duri will lengthen the duration of a stationary "
         + "spell, by an amount depending on your experience.";
   private final static String FIENDFYRE = "Fiendfyre is a hellish curse which summons cursed creatures. "
         + "A mix of magma cubes, blazes, and ghasts are spawned depending on your experience with "
         + "the spell. These creatures will be spawned if the spell hits a block or if the spell reaches "
         + "the edge of it's range, which is determined by your experience.";
   private final static String FINITE_INCANTATEM = "Finite Incantatem will reduce the duration of an effect "
         + "on a player. It can also lessen the effects of a spell on an item. It's strength depends on "
         + "your experience in the spell.";
   private final static String FLAGRANTE = "Flagrante will cause an item to burn it's bearer when picked "
         + "up. The length of the burn depends on your experience.";
   private final static String FORSKNING = "Forskning will allow you to transfigure a wand into a research "
         + "platform. This transfiguration unspools a wand's magical energy to create the platform, and "
         + "as such is extremely sensitive to any kind of perturbation. The slightest damage will cause "
         + "it to explode violently. While close to this research platform, begin saying letters. Notes "
         + "will show you when you are on the right path to discovering a spell.";
   private final static String FRANGE_LIGNEA = "Frange lignea will cause a log of the spruce, oak, birch, or "
         + "jungle species to explode into coreless wands. The number of wands dropped depends "
         + "on your experience.";
   private final static String FUMOS = "Fumos will cause those in an area to be blinded by a smoke cloud. "
         + "The blindness lasts for a time determined by your experience.";
   private final static String FUMOS_DUO = "Fumos Duo will cause those in an area to be blinded by a smoke cloud. "
         + "The blindness lasts for a time twice as long as that created by Fumos, and is determined by your "
         + "experience.";
   private final static String GEMINIO = "Geminio will cause an item to duplicate when held "
         + "by a person. The amount of duplications depends on your experience.";
   private final static String GLACIUS = "Glacius will cause a great cold to descend in a radius "
         + "from it's impact point which freezes blocks. The radius and duration of the freeze "
         + "depend on your experience.";
   private final static String GLACIUS_DUO = "Glacius Duo will freeze blocks in a radius twice that of "
         + "glacius, but for half the time.";
   private final static String GLACIUS_TRIA = "Glacius Tria will freeze blocks in a radius four times "
         + "that of glacius, but for one quarter the time.";
   private final static String HARMONIA_NECTERE_PASSUS = "Harmonia Nectere Passus will create a pair of "
         + "vanishing cabinets if the cabinets on both ends are configured correctly.";
   private final static String HERBIVICUS = "Herbivicus causes crops within a radius to grow. The radius "
         + "is determined by your experience.";
   private final static String HORREAT_PROTEGAT = "Horreat Protegat will shrink a stationary spell's "
         + "radius, down to a limit determined by your experience. Only the creator of the "
         + "stationary spell can affect it with this spell.";
   private final static String IMMOBULUS = "Immobulus immobilizes an entity for an amount of time depending "
         + "on your experience.";
   private final static String IMPEDIMENTA = "Impedimenta will slow an entity by a degree and for an amount "
         + "of time depending on your experience.";
   private final static String INCENDIO = "Incendio will burn blocks and entities it passes by. It's range "
         + "and duration depend on your experience.";
   private final static String INCENDIO_DUO = "Incendio duo will burn blocks and entities it passes by. It's "
         + "radius is twice that of incendio and it's duration half. It's range depends on your experience.";
   private final static String INCENDIO_TRIA = "Incendio duo will burn blocks and entities it passes by. It's "
         + "radius is four times that of incendio and it's duration one quarter. It's range depends on your "
         + "experience.";
   private final static String INFORMOUS = "Informous will give information on a stationary spell, an entity, or, "
         + "if pointed into the sky and allowed to travel far enough, the weather. It's range "
         + "depends on your experience.";
   private final static String LACARNUM_INFLAMARI = "Lacarnum Inflamarae will shoot a fire charge out of the "
         + "tip of your wand. This fire charge is not a spell, and thus can pass through normal anti-spell "
         + "barriers.";
   private final static String LEGILIMENS = "Legilimens, when cast at a player, will allow you to open their inventory "
         + "if your level in legilimens is higher than theirs.";
   private final static String LEVICORPUS = "Levicorpus will hoist a player up into the air and keep them there "
         + "for an amount of time determined by your experience.";
   private final static String LIBERACORPUS = "Liberacorpus will reduce the time left on any levicorpus effects "
         + "on the target by an amount determined by your experience.";
   private final static String LIGATIS_COR = "Ligatis cor will bind one of the four types of coreless wands to one "
         + "of the four types of wand cores: spider eye, rotten flesh, bone, and sulfur. Make sure the two "
         + "items are near each other when this spell is cast. You can only use this on one coreless wand and "
         + "one core material at a time.";
   private final static String LUMOS = "Lumos will cause you to gain sight in the dark for an amount of time "
         + "determined by your experience.";
   private final static String LUMOS_DUO = "Lumos duo will create a line of glowstone along your line of sight. "
         + "The duration of the glowstone depends on your experience.";
   private final static String LUMOS_MAXIMA = "Lumos maxima will spawn a glowstone at the impact site that will "
         + "exist for a duration depending on your experience.";
   private final static String LUMOS_SOLEM = "Lumos Solem will cause a sun-like light to erupt in an area around "
         + "the impact which will burn entities sensitive to sun. The radius of the light is dependent on your "
         + "experience.";
   private final static String MELOFORS = "Melofors places a melon on the target's head.";
   private final static String METATREPO_EQUUS = "Metatrepo Equus will transfigure an entity into a horse. "
         + "The length of the transfiguration depends on your experience.";
   private final static String METEOLOJINX = "Meteolojinx will turn a sunny day into a storm for a duration which "
         + "depends on your experience.";
   private final static String METEOLOJINX_RECANTO = "Meteolojinx Recanto will turn a storm into a sunny day for "
         + "a duration which depends on your experience.";
   private final static String MORTUOS_SUSCITATE = "Mortuos Suscitate will transfigure a piece of rotten flesh into "
         + "an inferius. The inferius will not attack it's owner and will disappear after an amount of time which "
         + "depends on your level in the spell.";
   private final static String MUCUS_AD_NAUSEAM = "Mucus Ad Nauseam will cause your opponent to drip with slime for "
         + "a duration dependent on your experience.";
   private final static String MUFFLIATO = "Muffliato creates a stationary spell which only allows the people "
         + "inside to hear anything spoken inside the effect. The duration of the spell depends on "
         + "your experience.";
   private final static String MULTICORFORS = "Multicorfors will change the color of leather armor of the target. How "
         + "different the new color is depends on your experience.";
   private final static String NULLUM_APPAREBIT = "Nullum apparebit creates a stationary spell which will not "
         + "allow apparition into it. The duration depends on your experience.";
   private final static String NULLUM_EVANESCUNT = "Nullum evanescunt creates a stationary spell which will not "
         + "allow disapparition out of it. The duration depends on your experience.";
   private final static String OBLIVIATE = "Obliviate will cause the target to forget some of their magical experience, "
         + "how much depending on your experience.";
   private final static String OBSCURO = "Obscuro will blind the target for a length of time dependent on your experience.";
   private final static String OPPUGNO = "Oppugno will cause any entities transfigured by you to attack the targeted "
         + "entity.";
   private final static String PACK = "When this hits a chest, it will suck any items nearby into it. The radius is "
         + "dependent on your experience.";
   private final static String PARTIS_TEMPORUS = "Partis temporus, if cast at a stationary spell that you have cast, "
         + "will cause that stationary spell's effects to stop for a short time.";
   private final static String PERICULUM = "Periculum shoots red sparks. The height of the sparks depends on your "
         + "experience.";
   private final static String PIERTOTUM_LOCOMOTOR = "Piertotum locomotor, if cast at an iron or snow block, will "
         + "transfigure that block into an iron or snow golem. This transfiguration's duration depends "
         + "on your experience.";
   private final static String PORTUS = "Portus is a spell which creates a portkey. To cast it, hold a wand in your hand "
         + "and look directly at the item you wish to enchant. Then say 'Portus x y z', where x y and z are the coordinates "
         + "you wish the portkey to link to. When this item is picked up, the holder and the entities around them will be "
         + "transported to the destination. Anti-apparition and anti-disapparition spells will stop this, but only if present "
         + "during the creation of the portkey, and will cause the creation to fail. If the portkey is successfully made, then "
         + "it can be used to go to that location regardless of the spells put on it. A portkey creation will not fail if the "
         + "caster of the protective enchantments is the portkey maker. Portkeys can be used to cross worlds as well, if you use "
         + "a portkey which was made in a different world. If the enchantment is said incorrectly, then the portkey will be created "
         + "linking to the caster's current location.";
   private final static String PRAEPANDO = "Praepando is a space-extension spell which allows you to create a pocket of "
         + "extra-dimensional space at a location. Spells can travel from the extra-dimensional pocket through to the real-"
         + "world, but cannot go the other way around. The length of time this pocket lasts depends on the caster's experience.";
   private final static String PROTEGO = "Protego is a shield spell which, while you are crouching, will cause any spells "
         + "cast at it to bounce off.";
   private final static String PROTEGO_HORRIBILIS = "Protego horribilis is a stationary spell which will destroy any "
         + "spells crossing it's barrier. It's duration depends on your experience.";
   private final static String PROTEGO_MAXIMA = "Protego maxima is a stationary spell which will hurt any entities close "
         + "to it's boundary. It's duration depends on your experience.";
   private final static String PROTEGO_TOTALUM = "Protego totalum is a stationary spell which will prevent any entities "
         + "from crossing it's boundary. It's duration depends on your experience.";
   private final static String REDUCIO = "Reducio will shrink a zombie giant to normal size. It's radius of effect, and thus "
         + "how accurate you have to be, depends on your experience.";
   private final static String REDUCTO = "Reducto creates an explosion which will damage the terrain. It's power depends "
         + "on your experience.";
   private final static String REPARIFARGE = "Reparifarge will cause the duration of the transfiguration on the targeted "
         + "entity to decrease by an amount that depends on your experience.";
   private final static String REPARO = "Reparo will repair the duration of a tool.";
   private final static String REPELLO_MUGGLETON = "Repello Muggleton will hide any blocks and players in it's radius "
         + "from those outside of it.";
   private final static String SCUTO_CONTERAM = "Scuto conteram will shorten the duration of a stationary "
         + "spell, by an amount depending on your experience.";
   private final static String SILENCIO = "Silencio silences the target for a duration depending on your experience. "
         + "During this time, the target can only cast nonverbal spells.";
   private final static String MOLLIARE = "Molliare softens the ground in a radius around the site. All fall damage "
         + "will be negated in this radius for a time duration depending on your experience.";
   private final static String STUPEFY = "Stupefy will stun an opponent for a duration depending on your experience.";
   private final static String TERGEO = "Tergeo will siphon off a block of water where it hits. It will also disable any "
         + "aguamenti-placed water blocks nearby.";
   private final static String VENTO_FOLIO = "Vento Folio gives a player the ability to fly unassisted for an amount of "
         + "time determined by your level in the spell.";
   private final static String VOLATUS = "Volatus is used to enchant a broomstick for flight. Your experience with this "
         + "spell determines how fast the broomstick can go.";
   private final static String WINGARDIUM_LEVIOSA = "Wingardium leviosa will allow you to lift up blocks within a radius of "
         + "the spell's impact, as long as you are crouching. The radius depends on your experience. When you drop "
         + "the blocks, they will fall like sand.";

   /**
    * Encodes in the lore of the book the spells and levels the author is at
    *
    * @param p      - The plugin
    * @param player - The author
    * @param meta   - The BookMeta of the book
    * @return newMeta - The new BookMeta of the book, which is passed to the event
    */
   public static BookMeta encode (Ollivanders2 p, Player player, BookMeta meta)
   {
      String pageString = getPageString(meta);
      List<String> spellStrings = spellList();
      List<String> lore = new ArrayList<String>();
      for (String spell : spellStrings)
      {
         if (pageString.contains(spell))
         {
            String newLore = Spells.firstLetterCapitalize(spell) + ":" + p.getSpellNum(player, Spells.decode(spell));
            lore.add(newLore);
         }
      }
      BookMeta newMeta = meta.clone();
      newMeta.setLore(lore);
      return newMeta;
   }

   /**
    * Gets a lowercase string composed of all pages of the book
    *
    * @param meta BookMeta from PlayerEditBookEvent.getNewBookMeta()
    * @return String in lowercase of all page text
    */
   private static String getPageString (BookMeta meta)
   {
      List<String> pages = meta.getPages();
      String pageString = "";
      for (String page : pages)
      {
         pageString = pageString.concat(page);
      }
      pageString = pageString.toLowerCase();
      pageString = pageString.replace('\n', ' ');
      return pageString;
   }

   /**
    * Gets a list of all Spells converted into lower case and spaces for underscores
    *
    * @return List of strings of human readable spells
    */
   private static List<String> spellList ()
   {
      Spells[] spellsList = Spells.values();
      List<String> spellStrings = new ArrayList<String>();
      for (Spells spell : spellsList)
      {
         spellStrings.add(Spells.recode(spell));
      }
      return spellStrings;
   }

   /**
    * Takes a book and decodes the spells in lore, if any, into player uses
    *
    * @param p      - The Plugin
    * @param player - The player reading the book
    * @param imeta  - The book's metadata
    */
   public static void decode (Ollivanders2 p, Player player, ItemMeta imeta)
   {
      if (imeta.hasLore())
      {
         List<String> lore = imeta.getLore();
         String[] line;
         int bookNum;
         int pSpellNum;
         Spells spell = null;
         for (String s : lore)
         {
            line = s.split(":");
            if (line.length == 2)
            {
               spell = Spells.decode(line[0]);
               bookNum = Integer.parseInt(line[1]);
               if (spell != null)
               {
                  pSpellNum = p.getSpellNum(player, spell);
                  if (pSpellNum < bookNum)
                  {
                     p.setSpellNum(player, spell, (int) (pSpellNum + ((bookNum - pSpellNum) / 2)));
                  }
               }
            }
         }
      }
   }

   /**
    * This creates the books for the /Okit command
    *
    * @param amount - number of copies of each book
    * @return - A list of books
    */
   public static List<ItemStack> makeBooks (int amount)
   {
      Map<String, String> bookMap = books();
      List<ItemStack> books = new ArrayList<ItemStack>();
      for (String title : bookMap.keySet())
      {
         ItemStack item = new ItemStack(Material.WRITTEN_BOOK, 1);
         BookMeta bm = (BookMeta) item.getItemMeta();
         bm.setAuthor("cakenggt");
         bm.setTitle(title);
         bm.setPages(splitEqually(bookMap.get(title), 250));
         bm = kitEncode(bm, 20);
         item.setItemMeta(bm);
         item.setAmount(amount);
         books.add(item);
      }
      //code for the debug book
      ItemStack item = new ItemStack(Material.WRITTEN_BOOK, 1);
      BookMeta bm = (BookMeta) item.getItemMeta();
      String title = "DEBUGGER";
      bm.setAuthor("cakenggt");
      bm.setTitle(title);
      String inside = "";
      for (String str : spellList())
      {
         inside += str + " ";
      }
      bm.setPages(splitEqually(inside, 250));
      bm = kitEncode(bm, 200);
      item.setItemMeta(bm);
      item.setAmount(amount);
      books.add(item);
      return books;
   }

   /**
    * This splits a string into equal segments.
    *
    * @param text text to split
    * @param size the size of the chunks
    * @return List of strings of size size or less
    */
   public static List<String> splitEqually (String text, int size)
   {
      List<String> ret = new ArrayList<String>((text.length() + size - 1) / size);
      for (int start = 0; start < text.length(); start += size)
      {
         ret.add(text.substring(start, Math.min(text.length(), start + size)));
      }
      return ret;
   }

   /**
    * Encodes in the lore of the book the spells and levels specified in the kit
    *
    * @param meta  - The BookMeta of the book
    * @param level - The level to encode the spells in
    * @return newMeta - The new BookMeta of the book, which is passed to the event
    */
   private static BookMeta kitEncode (BookMeta meta, int level)
   {
      String pageString = getPageString(meta);
      List<String> spellStrings = spellList();
      List<String> lore = new ArrayList<String>();
      for (String spell : spellStrings)
      {
         if (pageString.contains(spell))
         {
            String newLore = spell + ":" + level;
            lore.add(newLore);
         }
      }
      BookMeta newMeta = meta.clone();
      newMeta.setLore(lore);
      return newMeta;
   }

   /**
    * Returns a map of all books mapped to their titles
    *
    * @return Map whose keys are the titles, entries are the book text
    */
   public static Map<String, String> books ()
   {
      Map<String, String> bookMap = new HashMap<String, String>();
      final String N = "\n";
      bookMap.put("Achievements in Charming",
            AGUAMENTI + N + EBUBLIO + N + HERBIVICUS + N +
                  LUMOS_DUO + N + LUMOS_MAXIMA + N +
                  EPISKEY + N + ALIQUAM_FLOO + N + VOLATUS);
      bookMap.put("Extreme Incantations",
            ALARTE_ASCENDARE + N + LUMOS_MAXIMA + N +
                  OBLIVIATE + N + ASCENDIO + N + FORSKNING + N + VOLATUS + N +
                  PRAEPANDO);
      bookMap.put("Quintessence: A Quest",
            "The five major wards are described as follows: " + N +
                  NULLUM_APPAREBIT + N + NULLUM_EVANESCUNT + N + PROTEGO_HORRIBILIS + N +
                  PROTEGO_TOTALUM + N + COLLOPORTUS + N +
                  "Following are described several ward modifiers and minor wards: " + N +
                  CRESCERE_PROTEGAT + N + FIANTO_DURI + N + HORREAT_PROTEGAT + N +
                  PARTIS_TEMPORUS + N + SCUTO_CONTERAM + N + PROTEGO + N +
                  PROTEGO_MAXIMA + N + REPELLO_MUGGLETON + N + PRAEPANDO);
      bookMap.put("Standard Book of Spells Grade 1",
            INCENDIO + N + LUMOS + N + REPARO + N + MOLLIARE + N +
                  WINGARDIUM_LEVIOSA + N + COLLOPORTUS + N + ALOHOMORA + N +
                  DIFFINDO);
      bookMap.put("Standard Book of Spells Grade 2",
            EXPELLIARMUS + N + IMMOBULUS + N + INCENDIO + N + LUMOS + N +
                  OBLIVIATE + N + ALOHOMORA + N + ENGORGIO + N +
                  FINITE_INCANTATEM + N + REDUCIO + N + DIFFINDO + N +
                  AVIFORS);
      bookMap.put("Standard Book of Spells Grade 3",
            AQUA_ERUCTO + N + BOMBARDA + N + EXPELLIARMUS + N +
                  GLACIUS + N + LUMOS_DUO + N + REPARO + N + DRACONIFORS + N +
                  CARPE_RETRACTUM);
      bookMap.put("Standard Book of Spells Grade 4",
            ACCIO + N + ARRESTO_MOMENTUM + N + BOMBARDA_MAXIMA + N +
                  DUCKLIFORS + N + FIANTO_DURI + N + PROTEGO_HORRIBILIS + N +
                  PROTEGO_MAXIMA + N + PROTEGO_TOTALUM + N + GLACIUS_DUO + N +
                  LUMOS_SOLEM);
      bookMap.put("Standard Book of Spells Grade 5",
            ACCIO + N + METATREPO_EQUUS + N + EXPELLIARMUS + N + INCENDIO + N +
                  PROTEGO + N + REPARO + N + SCUTO_CONTERAM + N + STUPEFY + N +
                  WINGARDIUM_LEVIOSA + N + GLACIUS_TRIA + N + DEPULSO + N +
                  LEVICORPUS);
      bookMap.put("Standard Book of Spells Grade 6",
            APPARATE + N + CRESCERE_PROTEGAT + N + HORREAT_PROTEGAT + N +
                  INCENDIO_DUO + N + COLOVARIA + N + MULTICORFORS + N + DISSENDIUM);
      bookMap.put("Standard Book of Spells Grade 7",
            NULLUM_APPAREBIT + N + NULLUM_EVANESCUNT + N + PARTIS_TEMPORUS + N +
                  PIERTOTUM_LOCOMOTOR + N + PORTUS + N + INCENDIO_TRIA + N +
                  REPELLO_MUGGLETON);
      bookMap.put("Basic Hexes",
            IMMOBULUS + N + OBSCURO);
      bookMap.put("Confronting the Faceless",
            AVADA_KEDAVRA + N + IMPEDIMENTA + N + METEOLOJINX + N +
                  LUMOS_SOLEM);
      bookMap.put("Curses and Counter-Curses",
            METEOLOJINX_RECANTO + N + INCENDIO_DUO + N + SILENCIO + N + LEVICORPUS + N + LIBERACORPUS);
      bookMap.put("Dark Arts Defence",
            ARRESTO_MOMENTUM + N + HARMONIA_NECTERE_PASSUS + N + EPISKEY + N +
                  METEOLOJINX + N + PERICULUM + N + ARANIA_EXUMAI);
      bookMap.put("Defensive Magical Theory",
            INFORMOUS + N + SILENCIO + N + ASCENDIO + N + PERICULUM + N +
                  ARANIA_EXUMAI + N + OBSCURO);
      bookMap.put("The Dark Arts Outsmarted",
            AVADA_KEDAVRA + N + FIENDFYRE + N + INFORMOUS + N + HARMONIA_NECTERE_PASSUS + N +
                  METEOLOJINX_RECANTO + N + FUMOS_DUO);
      bookMap.put("The Dark Forces",
            LUMOS + N + MUCUS_AD_NAUSEAM + N + FUMOS);
      bookMap.put("Guide to Advanced Occlumency",
            LEGILIMENS);
      bookMap.put("Jinxes for the Jinxed",
            MUFFLIATO + N + MOLLIARE + N + FLAGRANTE + N + FINITE_INCANTATEM);
      bookMap.put("Practical Defensive Magic",
            BRACKIUM_EMENDO + N + FUMOS_DUO);
      bookMap.put("Self-Defensive Spellwork",
            APARECIUM + N + DEPRIMO + N + DEPULSO + N + FUMOS);
      bookMap.put("Updated Counter-Curse Handbook",
            ALARTE_ASCENDARE + N + FLAGRANTE + N + INCENDIO_TRIA + N +
                  FINITE_INCANTATEM + N + LIBERACORPUS);
      bookMap.put("Magick Moste Evile",
            FIENDFYRE);
      bookMap.put("Secrets of the Darkest Art",
            "The most horrifying and destructive act man can do is the "
                  + "creation of a horcrux. Through splitting one's soul through "
                  + "the murder of another player, one is able "
                  + "to resurrect with all of their magical experience intact. "
                  + "However, this action has a terrible cost, for as long as "
                  + "the soul is split, the player's maximum health is halved for "
                  + "each horcrux they have made. "
                  + "The only known way of destroying a horcrux is with fiendfyre.\n" +
                  ET_INTERFICIAM_ANIMAM_LIGAVERIS + N + MORTUOS_SUSCITATE + N + VENTO_FOLIO);
      bookMap.put("Beginners Guide to Transfig",
            "Transfiguration involves the transformation of one entity into "
                  + "another. All transfiguration has a time duration, after which "
                  + "the entity will transfigure back into it's previous state.\n" +
                  DELETRIUS + N + DUCKLIFORS + N + REPARIFARGE + N + AVIFORS);
      bookMap.put("Advanced Transfiguration",
            AVIS + N + DURO + N + EVANESCO + N + PIERTOTUM_LOCOMOTOR + N +
                  ENTOMORPHIS);
      bookMap.put("Intermediate Transfiguration",
            AVIS + N + METATREPO_EQUUS + N + OPPUGNO + N + DRACONIFORS);
      bookMap.put("Transubstantial Transfiguration",
            "Transubstantial transfiguration encompasses the spells which "
                  + "seem to break the rules of transfiguration, namely that an "
                  + "entity must be transfigured into another entity and that "
                  + "the transfiguration has a time duration.\n" +
                  EVANESCO + N + GEMINIO + N + REPARIFARGE);
      bookMap.put("Madcap Magic for Wacky Warlocks",
            DEPRIMO + N + MELOFORS + N + COLOVARIA + N + MULTICORFORS);
      bookMap.put("Saucy Tricks for Tricky Sorts",
            CONFUNDO + N + EVERTE_STATUM + N + MELOFORS + N + MUCUS_AD_NAUSEAM);
      bookMap.put("Book of Spells",
            ACCIO + N + AGUAMENTI + N + APARECIUM + N + AVIS + N +
                  DEFODIO + N + DURO + N + EBUBLIO + N + EXPELLIARMUS + N +
                  GEMINIO + N + IMPEDIMENTA + N + INCENDIO + N + LUMOS + N +
                  OPPUGNO + N + PROTEGO + N + REDUCTO + N + REPARO + N +
                  STUPEFY + N + WINGARDIUM_LEVIOSA + N + ALOHOMORA + N +
                  ENGORGIO + N + REDUCIO + N + DIFFINDO);
      bookMap.put("Easy Spells to Fool Muggles",
            CONFUNDO + N + EVERTE_STATUM + N + ENTOMORPHIS + N +
                  LACARNUM_INFLAMARI);
      bookMap.put("Wizard's Spells, Volume 1",
            GLACIUS + N + COLLOPORTUS + N + TERGEO + N + CARPE_RETRACTUM + N +
                  BRACKIUM_EMENDO);
      bookMap.put("Wizard's Spells, Volume 2",
            BOMBARDA + N + MUFFLIATO + N + REDUCTO + N + GLACIUS_DUO);
      bookMap.put("Wizard's Spells, Volume 3",
            BOMBARDA_MAXIMA + N + PACK + N + GLACIUS_TRIA + N + LACARNUM_INFLAMARI + N +
                  DISSENDIUM);
      bookMap.put("Practical Magic",
            APPARATE + N + AQUA_ERUCTO + N + DEFODIO + N + DELETRIUS + N +
                  HERBIVICUS + N + PORTUS + N + TERGEO + N + PACK + N + ALIQUAM_FLOO);
      bookMap.put("The Secrets of Wandlore",
            "The secrets of wandlore are not to be easily had, however " +
                  "they will be related in this book with the greatest of ease.\n" +
                  FRANGE_LIGNEA + N + LIGATIS_COR);
      bookMap.put("Advanced Potion-Making",
            "I do not expect many of you to appreciate the subtle science and exact art that "
                  + "is potion-making, but for those of you who do, rest assured that great things "
                  + "lie in store. Before making any potions, you must be sure to set up your cauldron "
                  + "correctly. Carefully place your cauldron on top of a source of heat, either fire "
                  + "or lava. Then you must fill the cauldron up with water, the level determines "
                  + "the maximum number of potions that can be possibly made. A full cauldron produces "
                  + "three potions if there are three times the total ingredients for the potion. "
                  + "The ingredients for a potion should be placed on top of the cauldron, then the "
                  + "cauldron must be right clicked with a wand. If the correct number of ingredients "
                  + "are present in the cauldron, a number of potions will be made that is between 0 "
                  + "and 3, depending on the multiple of the correct recipe present. Always be sure to "
                  + "include empty glass bottles in the potion ingredients you throw into the cauldron, "
                  + "as these will be filled with the potions." + N + N
                  + "The memory potion will allow you to receive twice the normal amount of experience "
                  + "for each spell you cast. This potion is brewed with 3 sugar cane and 2 glowstone "
                  + "dust." + N + N
                  + "Baruffio's Brain Elixir will cause any spells you cast to be twice as powerful. "
                  + "This potion is brewed with 5 redstone dust and 1 gold nugget." + N + N
                  + "Wolfsbane Potion will relieve you of the effects of Lycanthropy for as long as "
                  + "it is active. It is not a cure, but a treatment. This potion is brewed with 2 "
                  + "spider eyes and 3 rotten flesh.");
      FileConfiguration config = Bukkit.getPluginManager().getPlugin("Ollivanders2").getConfig();
      if (config.getBoolean("divination"))
      {
         bookMap.put("Unfogging the Future",
               "Divination is a complex and monumental area of study for "
                     + "any Seer to undertake. To divine the future, you need to "
                     + "have a crystal ball to look into. This instrument can be "
                     + "represented by any block of " + Material.getMaterial(config.getInt("divinationBlock"))
                     + ". Once you have this block set in front of you, sneak and "
                     + "stare into it." + N + "Your level of curiosity will determine roughly "
                     + "how long you will have to stare until you receive a prophecy. "
                     + "Your level of curiosity can be heightened by casting a certain "
                     + "spell which tells you information about it's targets." + N
                     + "If you are holding a stack of " + Material.getMaterial(config.getInt("divinationBlock"))
                     + " in your hand when you receive a prophecy, one of them will "
                     + "become a prophecy record and it's lore will contain the prophecy "
                     + "you received.");
      }
      return bookMap;
   }
}
