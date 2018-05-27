package me.jet315.staking.database;

import me.jet315.staking.Core;
import me.jet315.staking.StatsPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

public abstract class Database {

    Connection connection;
    // The name of the table we created back in SQLite class.
    public String table = "stats";

    public Database(String tableName) {
        this.table = tableName;
        initialize();
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void initialize() {
        //connection = getSQLConnection();
    /*    try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table);
            ResultSet rs = ps.executeQuery();
            close(ps, rs);
        } catch (SQLException ex) {
            Core.getInstance().getLogger().log(Level.SEVERE, "Unable to retrieve connection (First time starting the plugin?)", ex);
        }*/
    }

    // Loads values for a player in the databse
    public void loadPlayerStats(Player p) {
        String playersUUID = p.getUniqueId().toString();
        Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance(), new Runnable() {
            @Override
            public void run() {
                Connection conn = null;
                PreparedStatement ps = null;
                ResultSet rs;
                try {
                    conn = connection;
                    ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE uuid = '" + playersUUID + "';");

                    rs = ps.executeQuery();
                    if (rs.next()) {

                        int wins = rs.getInt("wins");
                        int looses = rs.getInt("looses");

                        StatsPlayer statsPlayer = new StatsPlayer(p, wins, looses);
                        Core.getInstance().getStatsManager().getPlayerStats().put(p, statsPlayer);

                    } else {
                        StatsPlayer statsPlayer = new StatsPlayer(p, 0, 0);
                        Core.getInstance().getStatsManager().getPlayerStats().put(p, statsPlayer);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                } finally {
                    try {
                        if (ps != null)
                            ps.close();
/*                        if (conn != null)
                            conn.close();*/
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * When a player logs off, his/her stats need to be saved - this is the method
     */
    public void savePlayerStats(StatsPlayer statsPlayer) {
        if (statsPlayer.isHasStatsBeenUpdated()) {
            String uuid = statsPlayer.getPlayer().getUniqueId().toString();

            Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance(), new Runnable() {
                @Override
                public void run() {
                    Connection conn = null;
                    PreparedStatement ps = null;
                    try {
                        conn = connection;
                        ps = conn.prepareStatement("REPLACE INTO " + table + " (uuid,wins,looses) VALUES(?,?,?)");
                        ps.setString(1, uuid);
                        ps.setInt(2, statsPlayer.getMatchesWon());
                        ps.setInt(3, statsPlayer.getMatchesLost());
                        ps.executeUpdate();
                        return;
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    } finally {
                        try {
                            if (ps != null)
                                ps.close();
/*                        if (conn != null)
                            conn.close();*/
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                }

            });

        }
    }

    /**
     * Should not be used unless server is being disabled
     */
    public void forceSyncPlayersData(StatsPlayer statsPlayer) {
        if (statsPlayer.isHasStatsBeenUpdated()) {
            String uuid = statsPlayer.getPlayer().getUniqueId().toString();
            Connection conn = null;
            PreparedStatement ps = null;
            try {
                conn = connection;
                ps = conn.prepareStatement("REPLACE INTO " + table + " (uuid,wins,looses) VALUES(?,?,?)");
                ps.setString(1, uuid);
                ps.setInt(2, statsPlayer.getMatchesWon());
                ps.setInt(3, statsPlayer.getMatchesLost());
                ps.executeUpdate();
                return;
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (ps != null)
                        ps.close();
/*                        if (conn != null)
                            conn.close();*/
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }


        }
    }
}