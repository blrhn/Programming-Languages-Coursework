package service.impl;

import service.dao.OfferDao;
import service.dao.OrderDao;
import service.exceptions.AlreadyUpdatedException;
import service.model.Offer;
import service.model.Order;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderDaoImpl implements OrderDao {
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
        String url = "jdbc:sqlite:orderdb";
        conn = DriverManager.getConnection(url);
    }

    public void disconnect() throws SQLException {
        if (conn == null) {
            return;
        }
        conn.close();
        conn = null;
    }

    private void executeUpdate(String sql, Object[] params) throws SQLException {
        connect();

        PreparedStatement ps = conn.prepareStatement(sql);
        setParameters(ps, params);
        ps.executeUpdate();

        ps.close();
        disconnect();
    }

    private ResultSet executeQuery(String sql, Object[] params) throws SQLException {
        connect();

        PreparedStatement ps = conn.prepareStatement(sql);
        setParameters(ps, params);
        return ps.executeQuery();
    }

    private void setParameters(PreparedStatement ps, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

    List<Order> getOrdersFromResultSet(ResultSet rs, boolean includeDate) throws SQLException {
        List<Order> orders = new ArrayList<>();
        OfferDao offerDao = new OfferDaoImpl();

        while (rs.next()) {
            Offer offer = offerDao.getOffer(rs.getInt("offer_id"));
            Order order = (includeDate
                    ? new Order(rs.getInt("id"), rs.getInt("client_id"), offer,
                    rs.getString("date"))
                    : new Order(rs.getInt("id"), rs.getInt("client_id"), offer));

            orders.add(order);
        }

        return orders;
    }

    @Override
    public void addOrder(Order order) {
        String sql = "INSERT INTO 'order'(client_id, offer_id) VALUES(?,?)";

        try {
            executeUpdate(sql, new Object[]{order.getClientId(), order.getOffer().getId()});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Order> getNonApprovedOrders() {
        String sql = "SELECT id, client_id, offer_id FROM 'order' WHERE status='Oczekujace'";
        List<Order> nonApprovedOrders = new ArrayList<>();

        try {
            ResultSet rs = executeQuery(sql, new Object[]{});
            nonApprovedOrders = getOrdersFromResultSet(rs, false);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nonApprovedOrders;
    }

    @Override
    public List<Order> getApprovedOrders() {
        String sql = "SELECT id, client_id, offer_id FROM 'order' WHERE organiser_id is NULL AND status='Zatwierdzone'";
        List<Order> approvedOrders = new ArrayList<>();

        try {
            ResultSet rs = executeQuery(sql, new Object[]{});
            approvedOrders = getOrdersFromResultSet(rs, false);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return approvedOrders;
    }

    @Override
    public void assureOrderNotTaken(int id) throws AlreadyUpdatedException {
        String sql = "SELECT organiser_id FROM 'order' WHERE id = ?";
        try {
            ResultSet rs = executeQuery(sql, new Object[]{id});

            if (rs.next()) {
                // checks if order has the organiser assigned
                int organiserId = rs.getInt("organiser_id");
                if (!rs.wasNull()) {
                    throw new AlreadyUpdatedException();
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateOrderDate(int id, String date)  {
        String sql = "UPDATE 'order' SET date=? WHERE id=? AND status='Zatwierdzone'";
        try {
            executeUpdate(sql, new Object[]{date, id});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateOrganiser(int orderId, int organiserId) {
        String sql = "UPDATE 'order' SET organiser_id=? WHERE id=? AND organiser_id IS NULL";

        try {
            executeUpdate(sql, new Object[]{organiserId, orderId});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Order> getClientOrders(int clientId) {
        String sql = "SELECT id, date, status, offer_id FROM 'order' WHERE client_id=?";
        List<Order> clientOrders = new ArrayList<>();
        OfferDao offerDao = new OfferDaoImpl();

        try {
            ResultSet rs = executeQuery(sql, new Object[]{clientId});

            while (rs.next()) {
                Offer offer = offerDao.getOffer(rs.getInt("offer_id"));
                clientOrders.add(new Order(rs.getInt("id"), offer, rs.getString("date"),
                        rs.getString("status")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clientOrders;
    }

    @Override
    public List<Order> getUpdatedOrders(int clientId) {
        String sql = "SELECT id, client_id, offer_id, date, client_approved FROM 'order' WHERE client_id = ? " +
                "AND client_declaration = 1 AND status IS NOT 'Zrealizowane'";
        List<Order> updatedOrders = new ArrayList<>();
        OfferDao offerDao = new OfferDaoImpl();

        try {
            ResultSet rs = executeQuery(sql, new Object[]{clientId});

            while (rs.next()) {
                Offer offer = offerDao.getOffer(rs.getInt("offer_id"));
                updatedOrders.add(new Order(rs.getInt("id"), rs.getInt("client_id"),
                        offer, rs.getString("date"), rs.getInt("client_approved")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return updatedOrders;
    }

    @Override
    public List<Order> getUpdatedOrdersOrganiser(int organiserId) {
        String sql = "SELECT id, client_id, offer_id, date FROM 'order' WHERE organiser_id = ? " +
                "AND client_declaration = 1 AND finalised = 0";
        List<Order> updatedOrders = new ArrayList<>();

        try {
            ResultSet rs = executeQuery(sql, new Object[]{organiserId});
            updatedOrders = getOrdersFromResultSet(rs, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return updatedOrders;
    }

    @Override
    public void setClientDeclaration(int orderId) {
        String sql = "UPDATE 'order' SET client_declaration = 1 WHERE id=? " +
                "AND date IS NOT null AND client_declaration = 0";

        try {
            executeUpdate(sql, new Object[]{orderId});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void approveClient(int orderId, int organiserId) {
        String sql = "UPDATE 'order' SET client_approved = 1 WHERE id=? AND organiser_id=?";

        try {
            executeUpdate(sql, new Object[]{orderId, organiserId});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void markAsFinalised(int orderId, int organiserId) {
        String sql = "UPDATE 'order' SET finalised = 1 WHERE id=? AND organiser_id=?";

        try {
            executeUpdate(sql, new Object[]{orderId, organiserId});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Order> getOrdersWithDeclaredClients() {
        String sql = "SELECT id, client_id, offer_id FROM 'order' WHERE client_declaration = 1 " +
                "AND status = 'Zatwierdzone'";
        List<Order> ordersWithDeclaredClients = new ArrayList<>();

        try {
            ResultSet rs = executeQuery(sql, new Object[]{});
            ordersWithDeclaredClients = getOrdersFromResultSet(rs, false);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ordersWithDeclaredClients;
    }

    @Override
    public List<Order> getFinalisedOrders() {
        String sql = "SELECT id, client_id, offer_id, date FROM 'order' WHERE finalised = 1 " +
                "AND client_approved = 1 AND status ='Przyjete do realizacji'";
        List<Order> finalisedOrders = new ArrayList<>();

        try {
            ResultSet rs = executeQuery(sql, new Object[]{});
            finalisedOrders = getOrdersFromResultSet(rs, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return finalisedOrders;
    }

    @Override
    public void changeStatus(int id, String newStatus, String currentStatus) {
        String sql = "UPDATE 'order' SET status=? WHERE id=? AND status=? ";

        try {
            executeUpdate(sql, new Object[]{newStatus, id, currentStatus});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteCompletedOrder(int id) {
        String sql = "DELETE FROM 'order' WHERE id=? AND status = 'Zrealizowane'";

        try {
            executeUpdate(sql, new Object[]{id});
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Order> getOrdersWithDates(int clientId) {
        String sql = "SELECT id, client_id, offer_id, date FROM 'order' WHERE date IS NOT NULL " +
                "AND client_declaration = 0 AND client_id = ?";
        List<Order> ordersWithDates = new ArrayList<>();

        try {
            ResultSet rs = executeQuery(sql, new Object[]{clientId});
            ordersWithDates = getOrdersFromResultSet(rs, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ordersWithDates;
    }

    @Override
    public List<Order> getOrdersToComplete(int organiserId) {
        String sql = "SELECT id, client_id, offer_id, date FROM 'order' WHERE status='Przyjete do realizacji' " +
                "AND organiser_id = ?AND client_approved = 0";
        List<Order> ordersToComplete = new ArrayList<>();

        try {
            ResultSet rs = executeQuery(sql, new Object[]{organiserId});
            ordersToComplete = getOrdersFromResultSet(rs, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ordersToComplete;
    }

    @Override
    public List<Order> getCompletedOrders(int clientId) {
        String sql = "SELECT id, client_id, offer_id FROM 'order' WHERE client_id = ? AND status = 'Zrealizowane'";
        List<Order> completedOrders = new ArrayList<>();

        try {
            ResultSet rs = executeQuery(sql, new Object[]{clientId});
            completedOrders = getOrdersFromResultSet(rs, false);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return completedOrders;
    }
}
