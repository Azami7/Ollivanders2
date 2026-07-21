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
     * The type of book, which supplies its title, author, and magic branch.
     */
    protected O2BookType bookType;

    private ItemStack bookItem;

    /**
     * Optional opening page (max 256 characters), overridable via config.
     */
    String openingPage;
    final private static String openingPageLabel = "_openingPage";

    /**
     * Optional closing page (max 256 characters), overridable via config.
     */
    String closingPage;
    final private static String closingPageLabel = "_closingPage";

    /**
     * NBT key for the book type.
     */
    public static NamespacedKey o2BookTypeKey;

    /**
     * NBT key for the book's spells.
     */
    public static NamespacedKey o2BookSpellsKey;

    /**
     * NBT key for the book's potions.
     */
    public static NamespacedKey o2BookPotionsKey;

    protected Ollivanders2 p;

    protected ArrayList<O2SpellType> spells;

    protected ArrayList<O2PotionType> potions;

    /**
     * Create an empty book. Subclasses set the book type and add their spells and potions; the book item itself is
     * built lazily on the first {@link #getBookItem()} so all text is loaded before it is assembled.
     *
     * @param plugin a callback to the Ollivanders2 plugin instance
     */
    public O2Book(@NotNull Ollivanders2 plugin) {
        bookType = O2BookType.STANDARD_BOOK_OF_SPELLS_GRADE_1;

        openingPage = "";
        closingPage = "";

        bookItem = null;

        spells = new ArrayList<>();
        potions = new ArrayList<>();
        p = plugin;

        o2BookTypeKey = new NamespacedKey(p, "O2BookType");
        o2BookSpellsKey = new NamespacedKey(p, "O2SpellTypes");
        o2BookPotionsKey = new NamespacedKey(p, "O2PotionTypes");
    }

    /**
     * Compose the book item's metadata: title page, table of contents, a page per spell/potion, optional config
     * opening/closing pages, and the NBT tags used to identify the book and its contents.
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

        String titlePage = title + "\n\nby " + author;
        bookMeta.addPage(titlePage);

        StringBuilder toc = new StringBuilder();
        toc.append("Contents:\n\n");

        ArrayList<String> mainContent = new ArrayList<>();

        // enum names double as the lookup keys for each entry's display name, main text, and flavor text
        ArrayList<String> bookContents = new ArrayList<>();
        for (O2SpellType spell : spells) {
            bookContents.add(spell.toString());
        }
        for (O2PotionType potionType : potions) {
            bookContents.add(potionType.toString());
        }

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

            if (flavorText == null) {
                text = name + "\n\n" + mainText;
            }
            else
                text = name + "\n\n" + flavorText + "\n\n" + mainText;

            ArrayList<String> pages = createPages(text);

            mainContent.addAll(pages);
        }

        ArrayList<String> tocPages = createPages(toc.toString());
        for (String tocPage : tocPages) {
            bookMeta.addPage(tocPage);
        }

        // opening/closing page config keys are the book-type enum name plus the label suffix
        if (Ollivanders2.useTranslations && p.getConfig().isSet(bookType.toString() + openingPageLabel)) {
            String s = p.getConfig().getString(bookType.toString() + openingPageLabel);
            if (s != null && !(s.isEmpty()))
                openingPage = s;
        }
        if (!(openingPage.isEmpty()))
            bookMeta.addPage(openingPage);

        for (String page : mainContent) {
            bookMeta.addPage(page);
        }

        if (Ollivanders2.useTranslations && p.getConfig().isSet(bookType.toString() + closingPageLabel)) {
            String s = p.getConfig().getString(bookType.toString() + closingPageLabel);
            if (s != null && !(s.isEmpty()))
                closingPage = s;
        }
        if (!(closingPage.isEmpty()))
            bookMeta.addPage(closingPage);

        // trim the trailing space each append adds; when this tag is parsed later it is split on spaces, and a
        // trailing space would yield an empty token that matches no enum
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

        // ORIGINAL marks the book as plugin-written so Minecraft won't let players edit its pages
        bookMeta.setGeneration(BookMeta.Generation.ORIGINAL);

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
        ArrayList<String> words = getWords(text);
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
     * Word-wrap a word list into Minecraft book pages, marking continued pages with "(cont.)". Assumes no single word
     * exceeds a page's capacity.
     *
     * @param words the words to format; emptied as they are consumed
     * @return the formatted pages
     */
    @NotNull
    private ArrayList<String> makePages(@NotNull ArrayList<String> words) {
        ArrayList<String> pages = new ArrayList<>();

        // first page is smaller to leave room for the title and spacing; later pages use the full capacity
        pages.add(buildPage(words, 150));

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
     * Get the author for this book.
     *
     * @return author
     */
    @NotNull
    public String getAuthor() {
        return bookType.getAuthor();
    }

    /**
     * Get the magic branch for this book.
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
