package cn.maxmc.maxnicky;

import cn.maxmc.maxnicky.Utils.IllegalNickException;
import cn.maxmc.maxnicky.Utils.Inject;
import cn.maxmc.maxnicky.Utils.Injectable;
import cn.maxmc.maxnicky.Utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.sql.*;
import java.util.UUID;

public class MysqlConnector implements Injectable {
    @Inject(path = "database.username")
    private static String username;
    @Inject(path = "database.password")
    private static String password;
    @Inject(path = "database.url")
    private static String url;
    @Inject(path = "database.db")
    private static String database;
    private Connection conn;

    public MysqlConnector() {
        init();
    }

    private void connect(){
        try {
            MaxNicky.log("§b正在连接至数据库...");
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url+"/"+database+"?characterEncoding=utf8",username,password);
            MaxNicky.log("§a成功连接至数据库");
            if(!isTableExist()){
                MaxNicky.log("§b数据表不存在,正在创建...");
                try {
                    createTable();
                    MaxNicky.log("§a数据表创建成功...");
                } catch (SQLException e){
                    MaxNicky.log("§c数据表创建失败,原因:§b"+e.getMessage());
                }
            }
        } catch (ClassNotFoundException | SQLException e) {
            MaxNicky.log("§c连接至数据库失败,原因:\n§b"+e.getMessage());
        }
    }

    public void init(){
        connect();
    }

    public String getNickOfPlayer(OfflinePlayer p){
        try {
            return quarryNick(p.getName());
        } catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    }

    public void setNick(OfflinePlayer p,String nick) throws IllegalNickException {
        if(!StringUtils.isContainChinese(nick)){
            throw new IllegalNickException();
        }
        try {
            updateNick(p,nick);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private void updateNick(OfflinePlayer p,String nick) throws SQLException {
        UUID uid = p.getUniqueId();
        String name = p.getName();

        if(conn.isClosed()){
            conn = null;
            connect();
        }
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("select nick from Nicky where uuid = '" + uid + "'");
        boolean has = false;
        if(resultSet.next()){
            has = true;
        }
        statement.close();
        statement = conn.createStatement();
        if(has) {
            statement.executeUpdate("update Nicky set nick='"+nick+"' where uuid = '"+uid+"';");
        }else {
            statement.executeUpdate("insert into Nicky (uuid,name,nick) values ('"+uid+"','"+name+"','"+nick+"');");
        }
    ???}

    private String quarryNick(String name) throws SQLException {
        // Auto Reconnect
        if(conn.isClosed()){
            conn = null;
            connect();
        }
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("select nick from Nicky where name = '" + name + "';");
        String nick = null;
        if (resultSet.next()){
            nick = resultSet.getString(1);
        }
        statement.close();
        return nick;
    }

    private boolean isTableExist() throws SQLException {
        if(conn.isClosed()){
            conn = null;
            connect();
        }
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery("show tables;");
        while (resultSet.next()){
            String string = resultSet.getString(1);
            if(string.equalsIgnoreCase("Nicky")){
                statement.close();
                return true;
            }
        }
        statement.close();
        return false;
    }

    private void createTable() throws SQLException {
        if(conn.isClosed()){
            conn = null;
            connect();
        }
        Statement statement = conn.createStatement();
        statement.executeUpdate("create table if not exists nicky(" +
                "uuid varchar(36) not null primary key," +
                "name varchar(64) not null ," +
                "nick varchar(32) not null )"
        );
    }

}
