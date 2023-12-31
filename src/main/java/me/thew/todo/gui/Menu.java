package me.thew.todo.gui;

import me.thew.todo.Todo;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;

public class Menu {
    private final Inventory inventory = Bukkit.createInventory(null, 54, "Todo");
    private final Player player;
    private int page = 0;
    private PaginatedArrayList list;
    private final List<ItemStack> items;

    public Menu(Player player) {

        this.player = player;
        this.items = Todo.getInstance().getItemStackList();

        Bukkit.getScheduler().runTaskAsynchronously(Todo.getInstance(), () -> {

            if (items.size() > 0) {
                list = new PaginatedArrayList(items, 45);

                list.gotoPage(page);

                for (int i = 0; i < 45; ++i) {
                    if(i >= list.size())
                        break;
                    setItem((ItemStack) list.get(i), i);
                }
                if (list.isNextPageAvailable()) {
                    setItem(50, Material.OAK_BUTTON, "§fСледующая страница");
                }
                if (list.isPreviousPageAvailable()) {
                    setItem(48, Material.OAK_BUTTON, "§fПредыдущая страница");
                }
                player.playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 0.7f, 1.0f);
            } else {
                player.sendMessage(" Тут пусто :3");
                return;
            }

            Bukkit.getScheduler().runTask(Todo.getInstance(), () -> {
                player.openInventory(inventory);
                Todo.getOpenedMenus().put(player, this);
            });
        });
    }

    private boolean isClear(){
        int x = 0;
        for (int i = 0; i < 45; i++){
            if(inventory.getItem(i) != null)
                x++;
        }
        return x == 0;
    }

    private void generateSection(int page){
        list.gotoPage(page);
        for(int i = 0; i < 45; i++){
            setItem(i, Material.AIR, null);
        }
        for (int i = 0; i < 45; ++i) {
            if(i >= list.size())
                break;
            setItem((ItemStack) list.get(i), i);
        }
        if (list.isNextPageAvailable()) {
            setItem(50, Material.OAK_BUTTON, "§fСледующая страница");
        } else {
            setItem(50, Material.AIR, null);
        }
        if (list.isPreviousPageAvailable()) {
            setItem(48, Material.OAK_BUTTON, "§fПредыдущая страница");
        } else {
            setItem(48, Material.AIR, null);
        }
    }

    public void onClick(int slot, ItemStack item, ClickType clickType) {
        if(isPaper(item)){
            String key = Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().get(Todo.getInstance().getNamespacedKey(), PersistentDataType.STRING);
            if(clickType.isLeftClick()){
                player.closeInventory();
                player.performCommand("todo open " + key);
                return;
            } else if (clickType.isRightClick()) {
                player.performCommand("todo remove " + key);
                setItem(slot, Material.AIR, null);
            }
        }
        if(isClear()){
            if(page == 0){
                player.closeInventory();
                return;
            } else{
                generateSection(page -1);
            }
        }
        if(isPageSlot(slot)){
            generateSection(page);
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(Todo.getInstance(), player::updateInventory, 3L);
    }

    private boolean isPaper(ItemStack itemStack){
        return itemStack.getType().equals(Material.PAPER);
    }

    private boolean isPageSlot(int slot){
        switch (slot){
            case 50:
                page += 1;
                return true;
            case 48:
                page -= 1;
                return true;
        }
        return false;
    }

    private void setItem(ItemStack item, int slot){
        inventory.setItem(slot, item);
    }

    private void setItem(int slot, Material material, String displayName) {
        setItem(slot, material, displayName, null);
    }

    private void setItem(int slot, Material material, String displayName, List<String> lore) {
        ItemStack itemStack = new ItemStack(material);

        if (!material.equals(Material.AIR)) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            assert itemMeta != null;

            itemMeta.setDisplayName(displayName);
            if (lore != null) itemMeta.setLore(lore);

            itemMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemMeta.addItemFlags(ItemFlag.HIDE_DYE);

            itemStack.setItemMeta(itemMeta);
        }
        inventory.setItem(slot, itemStack);
    }


}