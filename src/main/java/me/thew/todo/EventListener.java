package me.thew.todo;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!Todo.getOpenedMenus().containsKey((Player) e.getWhoClicked())) return;

        if (e.getInventory().equals(e.getWhoClicked().getInventory())) {
            e.setCancelled(true);
            return;
        }

        if (e.getCurrentItem() == null) {
            e.setCancelled(true);
            return;
        }

        e.setCancelled(true);


        Todo.getOpenedMenus().get((Player) e.getWhoClicked()).onClick(e.getSlot(), e.getCurrentItem(), e.getClick());
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Todo.getOpenedMenus().remove((Player) e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        Todo.getOpenedMenus().remove(e.getPlayer());
    }
}
