package uk.me.chrislewis.todolist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;

/**
* Data saved to disk
*/
public class Data {
  
  private HashMap<String, ArrayList<String>> todos = new HashMap<>();

  /**
   * Get the player's todos.
   *
   * @param playerName Name of the player.
   * @return List of their todos.
   */
  public ArrayList<String> getPlayerTodos (final String playerName) {
    ArrayList<String> items = todos.get(playerName);
    if (items == null) return new ArrayList<>();

    return items;
  }

  /**
   * Add a new todo for this player.
   *
   * @param playerName Player to add todo for.
   * @param item New todo to add.
   */
  public void addPlayerTodo (final String playerName, final String item) {
    // Init empty list for new player
    if (todos.get(playerName) == null) {
      todos.put(playerName, new ArrayList<>());
    }

    getPlayerTodos(playerName).add(item);
  }

  /**
   * Delete a player's todo.
   *
   * @param player Player to use.
   * @param index Index to use.
   */
  public String deletePlayerTodo (final Player player, final int index) {
    String playerName = player.getName();
    ArrayList<String> items = getPlayerTodos(playerName);
    if (index < 0 || index >= items.size()) {
      player.sendMessage(Component.text("Invalid index").color(Colors.RED));
      return null;
    }

    String removed = items.get(index);
    items.remove(index);
    return removed;
  }
  
  /**
  * Save data to the file.
  * One file per player (because f Java and any kind of JSON/YAML loading).
  * First line is name.
  * One line per todo.
  *
  * @param dataFolder Data File to save to.
  */
  public void save (final File dataFolder, final Player player, final Logger logger) {
    try {
      String name = player.getName();
      UUID uuid = player.getUniqueId();
      String filename = dataFolder.getAbsolutePath() + "/" + uuid.toString() + ".txt";

      FileWriter fw = new FileWriter(filename);
      fw.write(name + '\n');

      ArrayList<String> items = todos.get(name);
      if (items != null) {
        for(String i : items) {
          fw.write(i + "\n");
        }
      }
      
      fw.close();
      logger.info("Saved data file for " + name);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
  * Load data from the file.
  *
  * @param dataFolder Data File to load from.
  */
  public void load (final File dataFolder, final Logger logger) {
    File[] files = dataFolder.listFiles();

    try {
      for (File file : files) {
        String fileName = file.getName();
        if (!file.isFile()) {
          logger.warning("Ignoring non-file " + fileName);
          continue;
        }

        // Name is player name
        FileReader fr = new FileReader(file.getAbsolutePath());
        BufferedReader br = new BufferedReader(fr);
        String playerName = br.readLine();

        ArrayList<String> items = new ArrayList<>();
        String line = "";
        while ((line = br.readLine()) != null) {
          items.add(line);
        }

        // Restore data
        todos.put(playerName, items);

        br.close();
        logger.info("Loaded data file " + fileName);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
