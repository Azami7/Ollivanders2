package net.pottercraft.ollivanders2.book;

import net.pottercraft.ollivanders2.O2MagicBranch;
import net.pottercraft.ollivanders2.Ollivanders2;
import net.pottercraft.ollivanders2.Ollivanders2API;
import net.pottercraft.ollivanders2.common.Ollivanders2Common;
import net.pottercraft.ollivanders2.potion.O2PotionType;
import net.pottercraft.ollivanders2.spell.O2SpellType;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Super class for all Ollivanders2 books.
 * <p>
 * Book limits:
 * <ul>
 * <li>Title - 32 characters</li>
 * <li>Pages - 50</li>
 * <li>Lines per page - 14</li>
 * <li>Characters per page - 256 (newlines count as 2 characters)</li>
 * <li>Characters per line - 16-18 depending on the exact characters</li>
 * </ul>
 *
 * @author Azami7
 */
public abstract class O2Book {
    /**
     * The type of book defining its author, title, and magic branch
     */
    protected O2BookType bookType;

    /**
     * The book item object.
     */
    private ItemStack bookItem;

    /**
     * Optional opening page content displayed at the start of the book (max 256 characters).
     * Can be customized through configuration file settings.
     */
    String openingPage;
    final private static String openingPageLabel = "_openingPage";

    /**
     * Optional closing page content displayed at the end of the book (max 256 characters).
     * Can be customized through configuration file settings.
     */
    String closingPage;
    final private static String closingPageLabel = "_closingPage";

    /**
     * Namespace key for NBT tag - book type
     */
    public static NamespacedKey o2BookTypeKey;

    /**
     * Namespace key for NBT tag - spells
     */
    public static NamespacedKey o2BookSpellsKey;

    /**
     * Namespace key for NBT tag - potions
     */
    public static NamespacedKey o2BookPotionsKey;

    /**
     * Callback to the plugin
     */
    protected Ollivanders2 p;

    /**
     * Spells in book
     */
    protected ArrayList<O2SpellType> spells;

    /**
     * Potions in book
     */
    protected ArrayList<O2PotionType> potions;

    /**
     * Constructor that initializes a new book instance.
     * <p>
     * Initializes the book type to STANDARD_BOOK_OF_SPELLS_GRADE_1, empty opening and closing pages,
     * empty spell and potion lists, and creates the required NamespacedKeys for NBT data storage.
     * The actual book item is created lazily on the first call to {@link #getBookItem()}.
     * </p>
     *
     * @param plugin a callback to the Ollivanders2 plugin instance
     */
    public O2Book(@NotNull Ollivanders2 plugin) {
        bookType = O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_1;

        openingPage = "";
        closingPage = "";

        // create the book on first request to ensure texts, etc. are loaded before trying to create it
        bookItem = null;

        spells = new ArrayList<>();
        potions = new ArrayList<>();
        p = plugin;

        o2BookTypeKey = new NamespacedKey(p, "O2BookType");
        o2BookSpellsKey = new NamespacedKey(p, "O2SpellTypes");
        o2BookPotionsKey = new NamespacedKey(p, "O2PotionTypes");
    }

    /**
     * Composes and writes all metadata for the book item.
     * <p>
     * This method constructs the complete book content including:
     * <ul>
     * <li>Title page with book name and author</li>
     * <li>Table of contents listing all spells and potions</li>
     * <li>Formatted pages for each spell/potion with name, flavor text, and description</li>
     * <li>Optional opening and closing pages from configuration</li>
     * <li>NBT tags storing book type, spells, and potions for retrieval</li>
     * </ul>
     * Text is automatically split and formatted to fit within Minecraft book page limits.
     * </p>
     */
    private void writeSpellBookMeta() {
        Ollivanders2Common common = new Ollivanders2Common(p);

        if (bookItem == null)
            return;

        BookMeta bookMeta = (BookMeta) bookItem.getItemMeta();
        if (bookMeta == null)
            return;

        String shortTitle = bookType.getShortTitle(p);
        String title = bookType.getTitle(p);
        String author = bookType.getAuthor();

        bookMeta.setAuthor(bookType.getAuthor());
        bookMeta.setTitle(shortTitle);

        // write title page
        String titlePage = title + "\n\nby " + author;
        bookMeta.addPage(titlePage);

        // table of contents
        StringBuilder toc = new StringBuilder();
        toc.append("Contents:\n\n");

        // main book content
        ArrayList<String> mainContent = new ArrayList<>();

        // Collect all spell and potion enum names. These enum names serve as keys to look up:
        // - display names for the table of contents (line 174)
        // - main text and flavor text for content pages (lines 182-184)
        ArrayList<String> bookContents = new ArrayList<>();
        for (O2SpellType spell : spells) {
            bookContents.add(spell.toString());
        }

        // add the name of all the potions in the book
        for (O2PotionType potionType : potions) {
            bookContents.add(potionType.toString());
        }

        // Build table of contents and content pages for each spell/potion in a single pass
        for (String content : bookContents) {
            String name = Ollivanders2API.getBooks().spellText.getName(content);
            if (name == null) {
                common.printDebugMessage("O2Book: " + title + " contains unknown spell or potion " + content, null, null, false);
                continue;
            }

            toc.append(name).append("\n");

            String text;
            String mainText = Ollivanders2API.getBooks().spellText.getText(content);
            String flavorText = Ollivanders2API.getBooks().spellText.getFlavorText(content);

            // Format the spell/potion entry with flavor text if available.
            // With flavor text: name, flavor text, then description.
            // Without flavor text: name, then description (more space for main text).
            if (flavorText == null) {
                text = name + "\n\n" + mainText;
            }
            else
                text = name + "\n\n" + flavorText + "\n\n" + mainText;

            // create the pages for this spell
            ArrayList<String> pages = createPages(text);

            mainContent.addAll(pages);
        }

        // add TOC page(s)
        ArrayList<String> tocPages = createPages(toc.toString());
        for (String tocPage : tocPages) {
            bookMeta.addPage(tocPage);
        }

        // add opening page
        // Configuration keys are constructed by appending the label to the book type enum name
        // (e.g., "STANDARD_BOOK_OF_SPELLS_GRADE_1_openingPage")
        if (Ollivanders2.useTranslations && p.getConfig().isSet(bookType.toString() + openingPageLabel)) {
            String s = p.getConfig().getString(bookType.toString() + openingPageLabel);
            if (s != null && !(s.isEmpty()))
                openingPage = s;
        }
        if (!(openingPage.isEmpty()))
            bookMeta.addPage(openingPage);

        // add spell pages
        for (String page : mainContent) {
            bookMeta.addPage(page);
        }

        // add closing page
        // Configuration keys are constructed by appending the label to the book type enum name
        // (e.g., "STANDARD_BOOK_OF_SPELLS_GRADE_1_closingPage")
        if (Ollivanders2.useTranslations && p.getConfig().isSet(bookType.toString() + closingPageLabel)) {
            String s = p.getConfig().getString(bookType.toString() + closingPageLabel);
            if (s != null && !(s.isEmpty()))
                closingPage = s;
        }
        if (!(closingPage.isEmpty()))
            bookMeta.addPage(closingPage);

        // add NBT tags
        // Build space-separated spell and potion names, then trim trailing whitespace.
        // The trim() is critical because the for loop appends a space after each item,
        // resulting in a trailing space. When parsing this NBT string later, we split by space
        // and try to convert each part to an enum. A trailing space would create an empty string
        // that fails to match any enum value.
        StringBuilder spellsTag = new StringBuilder();
        for (O2SpellType spellType : spells) {
            spellsTag.append(spellType.toString()).append(" ");
        }

        StringBuilder potionsTag = new StringBuilder();
        for (O2PotionType potionType : potions) {
            potionsTag.append(potionType.toString()).append(" ");
        }

        PersistentDataContainer container = bookMeta.getPersistentDataContainer();
        container.set(o2BookTypeKey, PersistentDataType.STRING, bookType.toString());
        container.set(o2BookSpellsKey, PersistentDataType.STRING, spellsTag.toString().trim());
        container.set(o2BookPotionsKey, PersistentDataType.STRING, potionsTag.toString().trim());

        // Set book generation to ORIGINAL.
        // This marks the book as written by the plugin (as opposed to generated/copied).
        // Minecraft uses this to prevent further editing of the book's pages.
        bookMeta.setGeneration(BookMeta.Generation.ORIGINAL);

        // set book meta
        bookItem.setItemMeta(bookMeta);
    }

    /**
     * Splits text into pages formatted to fit within Minecraft book page limits.
     * <p>
     * Text is word-wrapped to ensure each page contains appropriate amounts of content.
     * Pages are created with continuation markers when content spans multiple pages.
     * </p>
     *
     * @param text the text to paginate
     * @return a list of formatted page strings
     */
    @NotNull
    private ArrayList<String> createPages(@NotNull String text) {
        //get the words in a spell text
        ArrayList<String> words = getWords(text);

        // turn the word list in to pages
        return makePages(words);
    }

    /**
     * Splits text into individual words for pagination purposes.
     *
     * @param text the text to split
     * @return a list of words separated by spaces
     */
    @NotNull
    private ArrayList<String> getWords(@NotNull String text) {
        ArrayList<String> words = new ArrayList<>();
        String[] splits = text.split(" ");

        Collections.addAll(words, splits);

        return words;
    }

    /**
     * Formats a list of words into properly-sized pages for Minecraft books.
     *
     * <p>This method implements word-wrapping to accommodate Minecraft book page limits (max 256 characters per page).
     * The first page has a smaller capacity (~150 characters) to account for the title and spacing, while
     * subsequent pages use the full available space (~180 characters). Pages are marked with "(cont.)" if content continues.
     * </p>
     *
     * <p>Note: This implementation assumes no individual word exceeds the page capacity.</p>
     *
     * @param words a list of words to format into pages
     * @return a list of formatted page strings
     */
    @NotNull
    private ArrayList<String> makePages(@NotNull ArrayList<String> words) {
        ArrayList<String> pages = new ArrayList<>();

        // First page has reduced capacity to accommodate title/spacing
        pages.add(buildPage(words, 150));

        // Remaining pages use full capacity
        while (!words.isEmpty()) {
            pages.add(buildPage(words, 180));
        }

        return pages;
    }

    /**
     * Builds a single page by filling it with words up to the specified character capacity.
     * <p>
     * Removes processed words from the list as they are added to the page.
     * If words remain after filling the page, appends "(cont.)" marker.
     * </p>
     *
     * @param words    the list of words to build the page from (modified by this method)
     * @param capacity the maximum character capacity for this page
     * @return the formatted page string
     */
    @NotNull
    private String buildPage(@NotNull ArrayList<String> words, int capacity) {
        int remaining = capacity;
        StringBuilder page = new StringBuilder();

        while (remaining > 0) {
            if (words.isEmpty())
                break;

            String word = words.getFirst();

            if (word.length() >= remaining)
                break;

            page.append(word).append(" ");

            // decrement remaining, remove word from list
            remaining = remaining - word.length() - 1;
            words.removeFirst();
        }

        if (!words.isEmpty())
            page.append("(cont.)");

        return page.toString();
    }

    /**
     * Get the book item for this book
     *
     * @return the book item
     */
    @NotNull
    public ItemStack getBookItem() {
        if (bookItem == null) {
            bookItem = new ItemStack(Material.WRITTEN_BOOK, 1);
            writeSpellBookMeta();
        }

        return bookItem;
    }

    /**
     * Get the title for this book.
     *
     * @return title
     */
    @NotNull
    public String getTitle() {
        return bookType.getTitle(p);
    }

    /**
     * Get the short title for this book
     *
     * @return short title for this book
     */
    @NotNull
    public String getShortTitle() {
        return bookType.getShortTitle(p);
    }

    /**
     * The author for this book.
     *
     * @return author
     */
    @NotNull
    public String getAuthor() {
        return bookType.getAuthor();
    }

    /**
     * The branch for this book.
     *
     * @return branch
     */
    @NotNull
    public O2MagicBranch getBranch() {
        return bookType.getBranch();
    }

    /**
     * Get the number of spells in this book.
     *
     * @return number of spells
     */
    public int getNumberOfSpells() {
        return spells.size();
    }

    /**
     * Get the number of potions in this book.
     *
     * @return number of potions
     */
    public int getNumberOfPotions() {
        return potions.size();
    }
}
