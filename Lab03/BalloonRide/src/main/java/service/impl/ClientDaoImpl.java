package service.impl;

import service.dao.UserDao;
import service.model.Client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientDaoImpl implements UserDao<Client> {
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
        String url = "jdbc:sqlite:clientdb";
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
    public void addUser(Client client) {
        String sql = "INSERT INTO client(name) VALUES (?)";

        try {
            connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, client.getName());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                client.setId(id);
            }
            disconnect();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public Client getUser(String name) {
        String sql = "SELECT id, name FROM client WHERE name=?";
        Client client = null;

        try {
            connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                client = new Client(rs.getInt("id"), name);
            }
            ps.close();
            disconnect();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return client;
    }
}
