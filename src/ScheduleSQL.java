//import com.oracle.tools.packager.Log;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScheduleSQL {

    private final static String DBURL = "jdbc:mysql://127.0.0.1:3306/studia?serverTimezone=UTC";
    private final static String DBUSER = "studia";
    private final static String DBPASS = "studia";
    //private final static String DBDRIVER = "com.mysql.jdbc.Driver";
    private final static String DBDRIVER = "com.mysql.cj.jdbc.Driver";

    private Connection connection;
    private Statement statement;
    private String query;

    private boolean status = false;

    public ScheduleSQL() {
        try {
            Class.forName(DBDRIVER).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public boolean connect() {
        if (status) return true;
        try {
            connection = DriverManager.getConnection(DBURL, DBUSER, DBPASS);
            statement = connection.createStatement();
            status = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return status;
    }

    public boolean disconnect() {
        try {
            statement.close();
            connection.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int insert(Schedule schedule) {
        int insert_id = 0;
        if (connect()) {
            query = createInsertQuery(schedule);
            PreparedStatement ps = null;
            ResultSet rs = null;
            try {
                ps = connection.prepareStatement(query,
                        Statement.RETURN_GENERATED_KEYS);
                ps.execute();
                rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    insert_id = rs.getInt(1);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        return insert_id;
    }

    public void selectAll() {
        if (connect()) {
            String query = "SELECT `id`, `nazwa`, `opis`, `kategoria`, `deadline`, `priorytet` FROM `scheduler` ORDER BY `scheduler`.`id` ASC";
            //String query = "SELECT * FROM `scheduler` ORDER BY `scheduler`.`id` ASC";
            try {
                ResultSet result = statement.executeQuery(query);
                while (result.next()) {
                    int id = result.getInt("id");
                    String title = result.getString("nazwa");
                    String desc = result.getString("opis");
                    String cat = result.getString("kategoria");
                    Date dead = result.getDate("deadline");
                    int priority = result.getInt("priorytet");

                    System.out.format("%s, %s, %s, %s, %s, %s\n", id, title, desc, cat, dead, priority);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public ResultSet selectAllResult() {
        ResultSet result = null;
        if (connect()) {
            String query = "SELECT `id`, `nazwa`, `opis`, `kategoria`, `deadline`, `priorytet` FROM `scheduler` ORDER BY `scheduler`.`id` ASC";
            try {
                result = statement.executeQuery(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private String createInsertQuery(Schedule schedule) {
        String query = "";
        String title = schedule.getTitle();
        String desc = schedule.getDesc();
        String cat = schedule.getCat();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String dead = dateFormat.format(schedule.getDead());
        String prio = String.valueOf(schedule.getPrio());
        query = "INSERT INTO `scheduler` (`id`, `nazwa`, `opis`, `kategoria`, `deadline`, `priorytet`) VALUES (NULL, '" + title + "', '" + desc + "', '" + cat + "', '" + dead + "', '" + prio + "');";
        return query;
    }

    public void delete(int idpola) {
        if(idpola <= 0) return;
        String query = "DELETE FROM `scheduler` WHERE `id`="+idpola;
        if (connect()) {
            try {
                statement.execute(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}