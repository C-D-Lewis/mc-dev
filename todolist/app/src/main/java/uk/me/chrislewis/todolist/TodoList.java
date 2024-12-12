package uk.me.chrislewis.todolist;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import net.kyori.adventure.text.Component;

public class TodoList extends JavaPlugin implements Listener {

  private Logger logger;
  private Data data;
  private File dataFolder;
  
  @Override
  public void onEnable() {
    Bukkit.getPluginManager().registerEvents(this, this);
    logger = getLogger();

    // Init plugin folder
    dataFolder = getDataFolder();
    if (!dataFolder.exists()) {
      if (!dataFolder.mkdirs()) {
        logger.severe("Could not create plugin directory!");
        getServer().getPluginManager().disablePlugin(this);
        return;
      }
    }

    data = new Data();
    data.load(dataFolder, logger);
  }
  
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    sendPlayerTodos(event.getPlayer(), true);
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (args.length == 0 || args[0].equals("help")) {
      sender.sendMessage(Component.text("================ todolist help ==============").color(Colors.GREY));
      sender.sendMessage(Component.text("  /todo help - show this help").color(Colors.GREY));
      sender.sendMessage(Component.text("  /todo add ... - Add a new todo").color(Colors.GREY));
      sender.sendMessage(Component.text("  /todo list - list your todos").color(Colors.GREY));
      sender.sendMessage(Component.text("  /todo delete [index] - Delete a todo by index").color(Colors.GREY));
      return true;
    }

    String subcmd = args[0];
    
    if (!(sender instanceof Player)) {
      logger.info("todolist: Ignored command " + subcmd + " as you are not a Player");
      return true;
    }
    
    Player player = (Player) sender;
    String playerName = sender.getName();
    
    // Add a new item to a player's list
    if (subcmd.equals("add")) {
      if (args.length < 2) return false;
      
      // All remaining tokens are the todo text
      String newTodo = args[1];
      for (int i = 2; i < args.length; i ++) {
        newTodo += (" " + args[i]);
      }
      
      data.addPlayerTodo(playerName, newTodo);
      data.save(dataFolder, player, logger);
      sender.sendMessage(Component.text("Added \"" + newTodo + "\"").color(Colors.GREEN));
      return true;
    }
    
    // List player's list
    if (subcmd.equals("list")) {
      sendPlayerTodos(player, false);
      return true;
    }

    // Delete a todo
    if (subcmd.equals("delete")) {
      if (args.length < 2) return false;

      try {
        int index = Integer.parseInt(args[1]) - 1;
        String removed = data.deletePlayerTodo(player, index);
        if (removed != null) {
          data.save(dataFolder, player, logger);
          player.sendMessage(Component.text("Deleted \"" + removed + "\"").color(Colors.GREEN));
        }
        return true;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    
    // Not handled by todolist
    return false; 
  }
  
    /**
   * Send a player their todo list.
   *
   * @param player Player to use.
   */
  public void sendPlayerTodos (final Player player, final Boolean isReminder) {
    String playerName = player.getName();
    ArrayList<String> items = data.getPlayerTodos(playerName);

    if (items.size() == 0) {
      if (!isReminder) player.sendMessage(Component.text("You have no todos yet").color(Colors.GREY));
      return;
    }

    player.sendMessage(Component.text("\n=============== Your todo list ==============").color(Colors.GREY));
    for (int i = 0; i < items.size(); i ++) {
      player.sendMessage("  " + (i + 1) + ": " + items.get(i));
    }
    player.sendMessage(Component.text("==========================================").color(Colors.GREY));
  }
}
