package space.gorogoro.executebyplayercount;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/*
 * ExecuteByPlayerCount
 * @license    LGPv3
 * @copyright  Copyright gorogoro.space 2021
 * @author     kubotan
 * @see        <a href="https://gorogoro.space">Gorogoro Server.</a>
 */
public class ExecuteByPlayerCount extends JavaPlugin implements Listener {

  /**
   * JavaPlugin method onEnable.
   */
  @Override
  public void onEnable() {
    try {
      getLogger().info("The Plugin Has Been Enabled!");

      // If there is no setting file, it is created
      if(!getDataFolder().exists()){
        getDataFolder().mkdir();
      }

      File configFile = new File(getDataFolder(), "config.yml");
      if(!configFile.exists()){
        saveDefaultConfig();
      }
      FileConfiguration config = getConfig();
      int intervalSeconds = config.getInt("interval-seconds");

      getServer().getPluginManager().registerEvents(this, this);

      getServer().getScheduler().runTaskTimer(this, new Runnable() {
        private int pre = 0;
        private int cur = 0;
        public void run() {
          cur = getServer().getOnlinePlayers().size();
          if(cur != pre) {
            String[] range; 
            int min = 0;
            int max = 0;
            for(String step: config.getStringList("step-list")) {
              range = step.split("-");
              min = Integer.parseInt(range[0]);
              max = Integer.parseInt(range[1]);
              if(min <= cur && cur <= max) {
                for(String command: config.getStringList("command-list-" + step)) {
                  Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                }
              }
            }
            pre = cur;
          }
        }
      }, 0L, intervalSeconds * 20L);
    } catch (Exception e) {
      logStackTrace(e);
    }
  }

  /**
   * Output stack trace to log file.
   * @param Exception Exception
   */
  public void logStackTrace(Exception e){
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      pw.flush();
      getLogger().warning(sw.toString());
  }
  
  /**
   * JavaPlugin method onDisable.
   */
  @Override
  public void onDisable() {
    try {
      getLogger().info("The Plugin Has Been Disabled!");
    } catch (Exception e) {
      logStackTrace(e);
    }
  }
}
