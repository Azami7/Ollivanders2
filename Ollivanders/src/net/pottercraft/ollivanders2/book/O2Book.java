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
 * Book limits:<br>
 * Title - 32 characters<br>
 * Pages - 50<br>
 * Lines per page - 14<br>
 * Characters per page - 256, newlines count as 2 characters<br>
 * Characters per line - 16-18 depending on the exact characters</p>
 *
 * @author Azami7
 * @since 2.2.4
 */
public abstract class O2Book {
    /**
     * The book author
     */
    protected O2BookType bookType;

    /**
     * The book item object.
     */
    private ItemStack bookItem;

    /**
     * No more than 256 characters
     */
    String openingPage;
    final private static String openingPageLabel = "_openingPage";

    /**
     * No more than 256 characters
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
     * Constructor
     *
     * @param plugin a callback to the plugin
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
     * Write all the metadata for this book.
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

        // add the names of all spells in the book
        ArrayList<String> bookContents = new ArrayList<>();
        for (O2SpellType spell : spells) {
            bookContents.add(spell.toString());
        }

        // add the name of all the potions in the book
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

            // create the pages for this spell
            ArrayList<String> pages = createPages(text);

            mainContent.addAll(pages);
        }

        // add TOC page(s)
        ArrayList<String> tocPages = createPages(toc.toString());
        for (String tocPage: tocPages) {
            bookMeta.addPage(tocPage);
        }

        // add opening page
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
        if (Ollivanders2.useTranslations && p.getConfig().isSet(bookType.toString() + closingPageLabel)) {
            String s = p.getConfig().getString(bookType.toString() + closingPageLabel);
            if (s != null && !(s.isEmpty()))
                closingPage = s;
        }
        if (!(closingPage.isEmpty()))
            bookMeta.addPage(closingPage);

        // add NBT tags
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

        // set book generation
        bookMeta.setGeneration(BookMeta.Generation.ORIGINAL);

        // set book meta
        bookItem.setItemMeta(bookMeta);
    }

    /**
     * Create the pages for the spells.
     *
     * @param text the text for this spell
     * @return a list of the spell pages
     */
    @NotNull
    private ArrayList<String> createPages(@NotNull String text) {
        //get the words in a spell text
        ArrayList<String> words = getWords(text);

        // turn the word list in to pages
        return makePages(words);
    }

    /**
     * Get a list of all the words in a spell text.
     *
     * @param text the book text for this spell
     * @return a list of the words in a text.
     */
    @NotNull
    private ArrayList<String> getWords(@NotNull String text) {
        ArrayList<String> words = new ArrayList<>();
        String[] splits = text.split(" ");

        Collections.addAll(words, splits);

        return words;
    }

    /**
     * Turn a spell text word list in to a set of pages that fit in an MC book.
     *
     * <p>Book pages cannot be more than 14 lines with ~16 characters per line, 256 characters max,
     * assume 2 lines for spell name, 1 blank line between name and flavor text, 1 blank link between flavor text
     * and description text, means the first page has 9 lines of ~15 characters + continue, subsequent pages are 13
     * lines of ~15 characters + continue.</p>
     *
     * <p>This means the first page for a spell can have ~175 characters and subsequent pages can be ~195.</p>
     *
     * <p>Assumes there is no word in the list that is >= max page size.</p>
     *
     * @param words an array of all the words in the book
     * @return a list of book pages
     */
    @NotNull
    private ArrayList<String> makePages(@NotNull ArrayList<String> words) {
        ArrayList<String> pages = new ArrayList<>();

        // first page
        int remaining = 150;
        StringBuilder page = new StringBuilder();

        // first page
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

        pages.add(page.toString());

        // remaining pages
        while (!words.isEmpty()) {
            remaining = 180;
            page = new StringBuilder();

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

            pages.add(page.toString());
        }

        return pages;
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
}
