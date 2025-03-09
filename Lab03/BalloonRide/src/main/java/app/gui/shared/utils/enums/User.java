package app.gui.shared.utils.enums;

public enum User {
    CLIENT("Witaj w katalogu voucherów na lot balonem"),
    ORGANISER("Strefa organizatora lotów balonem"),
    SELLER("Strefa sprzedawcy lotów balonem");

    private final String text;
    User(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }
}
