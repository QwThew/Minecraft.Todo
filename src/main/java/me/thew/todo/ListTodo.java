package me.thew.todo;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class ListTodo {

    String key;

    ItemStack book;
    ItemStack inGui;
    BookMeta bookMeta;

    public ListTodo(String key, ItemStack book){
        this.book = book;
        this.key = key;
        bookMeta = (BookMeta) book.getItemMeta();

        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta paperMeta = paper.getItemMeta();

        assert paperMeta != null;

        paperMeta.setDisplayName(getTitle());

        List<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add(" §6Автор §7-> §f" + getAuthor());
        lore.add(" §6Ключ §7-> §f" + getKey());
        lore.add(" ");
        lore.add(" §aЛКМ §7-> §fОткрыть запись");
        lore.add(" §cПКМ §7-> §fУдалить запись");
        lore.add(" ");
        paperMeta.setLore(lore);

        paperMeta.getPersistentDataContainer().set(Todo.getInstance().getNamespacedKey(), PersistentDataType.STRING, key);

        paper.setItemMeta(paperMeta);

        this.inGui = paper;
        Todo.getInstance().getHashMapBook().put(paper, book);
        Todo.getInstance().getItemStackList().add(paper);
    }

    public String getKey(){return key;}
    public String getTitle(){return bookMeta.getTitle();}
    public String getAuthor() {return bookMeta.getAuthor();}
    public ItemStack getBook(){return book;}
    public ItemStack getInGui(){return inGui;}
}
