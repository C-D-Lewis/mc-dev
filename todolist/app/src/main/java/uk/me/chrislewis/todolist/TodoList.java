package uk.me.chrislewis.todolist;

import java.io.File;
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
  private File dataFile;
  
  @Override
  public void onEnable() {
    Bukkit.getPluginManager().registerEvents(this, this);
    logger = getLogger();

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
    // event.getPlayer().sendMessage(Component.text("Hello, " + event.getPlayer().getName() + "!"));
    data.sendPlayerTodos(event.getPlayer(), true);
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (args.length == 0 || args[0].equals("help")) {
      sender.sendMessage(Component.text("======== todolist help ========"));
      sender.sendMessage(Component.text("  /todo help - show this help"));
      sender.sendMessage(Component.text("  /todo add ... - Add a new todo"));
      sender.sendMessage(Component.text("  /todo list - list your todos"));
      sender.sendMessage(Component.text("  /todo delete [index] - Delete a todo by index"));
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
      String item = "";
      for (int i = 1; i < args.length; i ++) item += (" " + args[i]);
      
      data.addPlayerTodo(playerName, item);
      data.save(dataFile, logger);
      sender.sendMessage("Added new todo");
      return true;
    }
    
    // List player's list
    if (subcmd.equals("list")) {
      Player player = (Player) sender;
      data.sendPlayerTodos(player, false);
      return true;
    }

    // Delete a todo
    
    // Not handled by todolist
    return false; 
  }
  
}