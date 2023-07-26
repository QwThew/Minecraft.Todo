package me.thew.todo;

import me.thew.todo.gui.Menu;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public final class Todo extends JavaPlugin {

    private static Todo instance;
    public static Todo getInstance(){
        return instance;
    }

    private static final HashMap<Player, Menu> openedMenus = new HashMap<>();
    public static HashMap<Player, Menu> getOpenedMenus() {
        return openedMenus;
    }

    public List<ListTodo> getList(){return list;}
    public List<ItemStack> getItemStackList(){return itemStackList;}
    public HashMap<ItemStack, ItemStack> getHashMapBook(){return hashMapBook;}

    public NamespacedKey getNamespacedKey() {return namespacedKey;}

    List<ListTodo> list = new ArrayList<>();
    List<ItemStack> itemStackList = new ArrayList<>();
    HashMap<ItemStack, ItemStack> hashMapBook = new HashMap<>();

    private NamespacedKey namespacedKey;

    @Override
    public void onEnable() {
        instance = this;
        if (!new File(getDataFolder(), "config.yml").exists()) saveDefaultConfig();
        Objects.requireNonNull(getCommand("todo")).setExecutor(new Command());
        Objects.requireNonNull(getCommand("todo")).setTabCompleter(new Command());
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        namespacedKey = new NamespacedKey(Todo.getInstance(), "todo");
        init();
    }

    @Override
    public void onDisable() {
        openedMenus.forEach((x, y) -> x.closeInventory());
    }

    private void init(){
        ConfigurationSection todo = getConfig().getConfigurationSection("todo");
        assert todo != null;
        for (String key : todo.getKeys(false)){

            ItemStack book = todo.getItemStack(key);
            assert book != null;

            ListTodo listTodo = new ListTodo(key, book);

            list.add(listTodo);
        }

    }

}
