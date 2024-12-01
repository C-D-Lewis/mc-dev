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
  public ArrayList<String> getTodos(final String playerName) {
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
  public void addTodo(final String playerName, final String item) {
    if (todos.get(playerName) == null) {
      todos.put(playerName, new ArrayList<>());
    }

    getTodos(playerName).add(item);
  }

  /**
   * Send a player their todo list.
   *
   * @param player Player to use.
   */
  public void sendPlayerTodos(final Player player, final Boolean isReminder) {
    String playerName = player.getName();

    ArrayList<String> items = getTodos(playerName);
    if (items.size() == 0) {
      if (!isReminder) {
        player.sendMessage("You have no todos yet");
      }
      return;
    }

    player.sendMessage("======== " + playerName + "'s todo list ========");
    for (int i = 0; i < items.size(); i ++) {
      player.sendMessage((i + 1) + ": " + items.get(i));
    }
  }
  
  /**
  * Save data to the file.
  *
  * @param dataFile Data File to save to.
  */
  public void saveData(final File dataFile, final Logger logger) {
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
  public void loadData(final File dataFile, final Logger logger) {
    try {
      FileInputStream fis = new FileInputStream(dataFile);
      ObjectInputStream ois = new ObjectInputStream(fis);
      Data loaded = (Data) ois.readObject();

      // Restore data
      todos = loaded.todos;

      ois.close();
      logger.info("Loaded data file");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
}
