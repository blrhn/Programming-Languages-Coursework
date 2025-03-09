package service.utils;

import service.model.Client;

public class ClientFactory implements UserFactory<Client>{
    @Override
    public Client createUser(String name) {
        return new Client(name);
    }
}
