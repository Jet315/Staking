package me.jet315.staking.database;

import me.jet315.staking.Core;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class SQLite extends Database{

    private String table;
    private String SQLiteCreateTokensTable;

    public SQLite(String database){
        super(database);
        this.table = database;
        SQLiteCreateTokensTable = "CREATE TABLE IF NOT EXISTS "+database+" (" +
                "`uuid` varchar(32) NOT NULL," + // UUID
                "`wins` int(6) NOT NULL," +
                "`looses` int(6) NOT NULL," +
                "PRIMARY KEY (`uuid`)" +
                ");";
    }


    // SQL creation stuff
    public Connection getSQLConnection() {
        File dataFolder = new File(Core.getInstance().getDataFolder(), table+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                Core.getInstance().getLogger().log(Level.SEVERE, "File write error: "+table+".db");
            }
        }
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            Core.getInstance().getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            Core.getInstance().getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

    public void load() {
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(SQLiteCreateTokensTable);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}