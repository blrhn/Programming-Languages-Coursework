package service.impl;

import service.dao.OfferDao;
import service.model.Offer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OfferDaoImpl implements OfferDao {
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
        String url = "jdbc:sqlite:offerdb";
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
    public void addOffer(Offer offer) {
        String sql = "INSERT INTO offer(title) VALUES(?)";

        try {
            connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, offer.getTitle());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                offer.setId(id);
            }
            ps.close();
            disconnect();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Offer> getOffers() {
        String sql = "SELECT id, title FROM offer";
        List<Offer> nonApprovedOffers = new ArrayList<>();

        try {
            connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                nonApprovedOffers.add(new Offer(rs.getInt("id"), rs.getString("title")));
            }
            stmt.close();
            disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nonApprovedOffers;
    }

    @Override
    public Offer getOffer(int id) {
        String sql = "SELECT id, title FROM offer WHERE id=?";
        Offer offer = null;

        try {
            connect();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                offer = new Offer(rs.getInt("id"), rs.getString("title"));
            }
            ps.close();
            disconnect();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return offer;
    }
}
