package service.model;

public class Order {
    private int id;
    private int clientId;
    private Offer offer;
    private String date;
    private String status;
    private int clientApproved;

    public Order(int clientId, Offer offer) {
        this.clientId = clientId;
        this.offer = offer;
    }

    public Order(int id, int clientId, Offer offer) {
        this.id = id;
        this.clientId = clientId;
        this.offer = offer;
    }

    public Order(int id, int clientId, Offer offer, String date) {
        this.id = id;
        this.clientId = clientId;
        this.offer = offer;
        this.date = date;
    }

    public Order(int id, Offer offer, String date, String status) {
        this.id = id;
        this.offer = offer;
        this.date = date;
        this.status = status;
    }

    public Order(int id, int clientId, Offer offer, String date, int clientApproved) {
        this.id = id;
        this.clientId = clientId;
        this.offer = offer;
        this.date = date;
        this.clientApproved = clientApproved;
    }

    public int getClientId() {
        return clientId;
    }

    public int getId() {
        return id;
    }

    public Offer getOffer() {
        return offer;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public int getClientApproved() {
        return clientApproved;
    }

    public void setId(int id) {
        this.id = id;
    }

}
