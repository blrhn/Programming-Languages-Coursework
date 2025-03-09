package service.dao;

import service.exceptions.AlreadyUpdatedException;
import service.model.Order;

import java.util.List;

public interface OrderDao {
    void addOrder(Order order);
    void updateOrderDate(int id, String date);
    void updateOrganiser(int orderId, int organiserId);
    void setClientDeclaration(int orderId);
    void approveClient(int orderId, int organiserId);
    void markAsFinalised(int orderId, int organiserId);
    void changeStatus(int id, String newStatus, String currentStatus);
    void deleteCompletedOrder(int id);
    void assureOrderNotTaken(int id) throws AlreadyUpdatedException;
    List<Order> getNonApprovedOrders();
    List<Order> getApprovedOrders();
    List<Order> getClientOrders(int clientId);
    List<Order> getUpdatedOrders(int clientId);
    List<Order> getUpdatedOrdersOrganiser(int clientId);
    List<Order> getOrdersWithDeclaredClients();
    List<Order> getFinalisedOrders();
    List<Order> getOrdersWithDates(int clientId);
    List<Order> getOrdersToComplete(int organiserId);
    List<Order> getCompletedOrders(int clientId);
}
