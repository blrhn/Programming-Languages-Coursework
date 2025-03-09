package service.impl;

import service.dao.UserDao;
import service.model.Organiser;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrganiserDaoImpl implements UserDao<Organiser> {
    private Connection conn = null;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void connect() throws SQLException {
        if (conn != null) {
            return;
        }
        String url = "jdbc:sqlite:organiserdb";
        conn = DriverManager.getConnection(url);
    }

    public void disconnect() throws SQLException {
        if (conn == null) {
            return;
        }
        conn.close();
        conn = null;
    }

    @Override
    public void addUser(Organiser organiser) {
        String sql = "INSERT INTO organiser(name) VALUES (?)";

        try {
            connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, organiser.getName());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                organiser.setId(id);
            }
            ps.close();
            disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Organiser getUser(String name) {
        String sql = "SELECT id, name FROM organiser WHERE name=?";
        Organiser organiser = null;

        try {
            connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                organiser = new Organiser(rs.getInt("id"), name);
            }
            ps.close();
            disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return organiser;
    }
}
