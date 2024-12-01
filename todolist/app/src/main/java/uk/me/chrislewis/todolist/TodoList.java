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
import net.kyori.adventure.text.format.TextColor;

public class TodoList extends JavaPlugin implements Listener {

  private Logger logger;
  private Data data;
  private File dataFile;
  
  @Override
  public void onEnable() {
    Bukkit.getPluginManager().registerEvents(this, this);
    logger = getLogger();

    // Init plugin folder
    File dataFolder = getDataFolder();
    if (!dataFolder.exists()) {
      if (!dataFolder.mkdirs()) {
        logger.severe("Could not create plugin directory!");
        getServer().getPluginManager().disablePlugin(this);
        return;
      }
    }

    // TODO: Debuggable file format
    dataFile = new File(dataFolder, "data");
    data = new Data();
    data.load(dataFile, logger);
  }
  
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event) {
    data.sendPlayerTodos(event.getPlayer(), true);
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (args.length == 0 || args[0].equals("help")) {
      sender.sendMessage("======== todolist help ========");
      sender.sendMessage("  /todo help - show this help");
      sender.sendMessage("  /todo add ... - Add a new todo");
      sender.sendMessage("  /todo list - list your todos");
      sender.sendMessage("  /todo delete [index] - Delete a todo by index");
      return true;
    }

    String subcmd = args[0];
    
    if (!(sender instanceof Player)) {
      logger.info("todolist: Ignored command " + subcmd + " as you are not a Player");
      return true;
    }
    
    String playerName = sender.getName();
    
    // Add a new item to a player's list
    if (subcmd.equals("add")) {
      if (args.length < 2) return false;
      
      // All remaining tokens are the todo text
      String newTodo = "";
      for (int i = 1; i < args.length; i ++) newTodo += (" " + args[i]);
      
      data.addPlayerTodo(playerName, newTodo);
      data.save(dataFile, logger);
      sender.sendMessage(Component.text("Added new todo").color(TextColor.color(128, 128, 128)));
      return true;
    }
    
    // List player's list
    if (subcmd.equals("list")) {
      data.sendPlayerTodos((Player) sender, false);
      return true;
    }

    // Delete a todo
    if (subcmd.equals("delete")) {
      if (args.length < 2) return false;

      try {
        int index = Integer.parseInt(args[1]);
        data.deletePlayerTodo((Player) sender, index);
        data.save(dataFile, logger);
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
      if (!isReminder) player.sendMessage("You have no todos yet");
      return;
    }

    player.sendMessage("======== " + playerName + "'s todo list ========");
    for (int i = 0; i < items.size(); i ++) {
      player.sendMessage(i + ": " + items.get(i));
    }
  }
}