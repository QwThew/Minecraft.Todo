package me.thew.todo;

import me.thew.todo.gui.Menu;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Command implements CommandExecutor, TabCompleter {

    private void setTodo(String key, ItemStack book){
        FileConfiguration config = Todo.getInstance().getConfig();
        config.createSection("todo." + key);
        config.set("todo." + key, book);
        Todo.getInstance().saveConfig();
        Todo.getInstance().reloadConfig();
    }

    public static boolean removeTodo(String key){
        FileConfiguration config = Todo.getInstance().getConfig();
        if(config.get("todo." + key) == null){
            return false;
        }
        config.set("todo." + key, null);
        Todo.getInstance().saveConfig();
        Todo.getInstance().reloadConfig();
        for (ListTodo listTodo : Todo.getInstance().getList()){
            if (listTodo.getKey().equalsIgnoreCase(key)){
                Todo.getInstance().getList().remove(listTodo);
                Todo.getInstance().getHashMapBook().remove(listTodo.getInGui());
                Todo.getInstance().getItemStackList().remove(listTodo.getInGui());
                return true;
            }
        }
        return true;
    }

    public static ListTodo checkExist(String key){
        for(ListTodo listTodo : Todo.getInstance().getList()){
            if(listTodo.getKey().equalsIgnoreCase(key)){
                return listTodo;
            }
        }
        return null;
    }



    @Override
    public boolean onCommand(@Nonnull CommandSender sender,@Nonnull org.bukkit.command.Command command,@Nonnull String label,@Nonnull String[] args) {
        if (!(sender instanceof Player)){
            sender.sendMessage("§cthis is todo only for real-player");
            return true;
        }

        Player player = (Player) sender;
        if(args.length == 1){
            if (args[0].equalsIgnoreCase("list")) {

                TextComponent message = new TextComponent("Список задач: \n");

                for (ListTodo listTodo : Todo.getInstance().getList()) {
                    message.addExtra(" - §6Ключ:§f "
                            + listTodo.getKey()
                            + " §6Описание: §f"
                            + listTodo.getTitle()
                            + "§6 Автор: §7"
                            + listTodo.getAuthor()
                            + " "
                    );

                    TextComponent openButton = new TextComponent(ChatColor.GOLD + "{+} ");
                    openButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/todo open " + listTodo.getKey()));
                    openButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§aОткрыть")));

                    message.addExtra(openButton);

                    TextComponent removeButton = new TextComponent(ChatColor.GOLD + "{-}\n");
                    removeButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/todo remove " + listTodo.getKey()));
                    removeButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text("§cУдалить")));

                    message.addExtra(removeButton);
                }
                player.spigot().sendMessage(message);
            } else if (args[0].equalsIgnoreCase("help")) {
                String helpMessage = " Команды: \n"
                        + "  §7- /todo list - Покажет список TODO \n"
                        + "  §7- /todo open [key] - Откроет выбранную запись \n"
                        + "  §7- /todo add [key] - Добавить запись \n"
                        + "  §7- /todo remove [key] - Удалить запись \n";
                player.sendMessage(helpMessage);
            }
        } else if (args.length == 2) {
            String key = args[1];
            switch (args[0]){
                case "open":
                    if (checkExist(key) != null){
                        player.openBook(Objects.requireNonNull(checkExist(key)).getBook());
                    } else{
                        player.sendMessage(" §cЗапись не найдена! Попробуйте /todo list");
                    }
                    break;
                case "remove":
                    if (removeTodo(key)){
                        player.sendMessage(" §aЗапись успешно удалена!");
                    } else{
                        player.sendMessage(" §cЗапись не найдена! Попробуйте /todo list");
                    }
                    break;
                case "add":
                    ItemStack book = player.getInventory().getItemInMainHand();
                    if(book.getType().equals(Material.WRITTEN_BOOK)){
                        if(checkExist(key) != null){
                            player.sendMessage(" §cЗапись уже существует!");
                            return true;
                        }

                        setTodo(key, book);

                        ListTodo listTodo = new ListTodo(key, book);
                        Todo.getInstance().getList().add(listTodo);

                        player.sendMessage(" §aЗапись успешно создана!");
                    } else {
                        player.sendMessage(" §cВы должны держать в руке книгу!");
                    }
                    break;
            }
        } else if(args.length == 0){
            new Menu(player);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender commandSender,@Nonnull  org.bukkit.command.Command command,@Nonnull  String s,@Nonnull  String[] args) {
        if(args.length > 1){
            return Collections.singletonList("");
        }
        return Arrays.asList("add", "list", "remove", "open", "help");
    }
}
