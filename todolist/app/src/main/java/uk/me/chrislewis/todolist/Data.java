package uk.me.chrislewis.todolist;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.entity.Player;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

/**
* Data saved to disk
*/
public class Data implements Serializable {
  private static final long serialVersionUID = 1L;
  
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
  public void deletePlayerTodo (final Player player, final int index) {
    String playerName = player.getName();
    ArrayList<String> items = getPlayerTodos(playerName);
    if (index < 0 || index >= items.size()) {
      player.sendMessage(Component.text("Invalid index: " + index).color(TextColor.color(255, 0, 0)));
      return;
    }

    items.remove(index);
    player.sendMessage("Deleted todo");
  }
  
  /**
  * Save data to the file.
  *
  * @param dataFile Data File to save to.
  */
  public void save (final File dataFile, final Logger logger) {
    try {
      FileOutputStream fos = new FileOutputStream(dataFile);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(this);
      oos.close();
      
      logger.info("Saved data file");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
  * Load data from the file.
  *
  * @param dataFile Data File to load from.
  */
  public void load (final File dataFile, final Logger logger) {
    try {
      FileInputStream fis = new FileInputStream(dataFile);
      ObjectInputStream ois = new ObjectInputStream(fis);
      
      // Restore data
      Data loaded = (Data) ois.readObject();
      todos = loaded.todos;

      ois.close();
      logger.info("Loaded data file");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
