package service.utils;

import service.model.Seller;

public class SellerFactory implements UserFactory<Seller> {

    @Override
    public Seller createUser(String name) {
        return new Seller(name);
    }
}
