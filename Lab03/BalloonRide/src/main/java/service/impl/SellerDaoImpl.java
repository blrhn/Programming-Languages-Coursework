package service.impl;

import service.dao.UserDao;
import service.model.Seller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SellerDaoImpl implements UserDao<Seller> {

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
        String url = "jdbc:sqlite:sellerdb";
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
    public void addUser(Seller seller) {
        String sql = "INSERT INTO seller(name) VALUES (?)";

        try {
            connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, seller.getName());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                seller.setId(id);
            }
            ps.close();
            disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Seller getUser(String name) {
        String sql = "SELECT id, name FROM seller WHERE name=?";
        Seller seller = null;

        try {
            connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                seller = new Seller(rs.getInt("id"), name);
            }
            ps.close();
            disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return seller;
    }
}
