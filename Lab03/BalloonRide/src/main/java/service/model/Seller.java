package service.model;

public class Seller {
    private int id;
    private String name;

    public Seller(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Seller(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }
}
