package boardgames.festival;

public class Table {
    private final int id;
    private int seats;
    private int takenSeatsByGame;

    public Table(int id, int seats) {
        this.id = id;
        this.seats = seats;
        this.takenSeatsByGame = 0;
    }

    public int getRemainingSeats() {
        return seats - takenSeatsByGame;
    }

    public int getId() {
        return id;
    }

    public int getSeats() {
        return seats;
    }

    public void takeSeatByGame(int players) {
        takenSeatsByGame += players;
    }

    public void takeSeats() {
        seats--;
    }

    public void addSeats() {
        seats++;
    }
}
