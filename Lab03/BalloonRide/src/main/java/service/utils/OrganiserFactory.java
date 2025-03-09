package service.utils;

import service.model.Organiser;

public class OrganiserFactory implements UserFactory<Organiser> {

    @Override
    public Organiser createUser(String name) {
        return new Organiser(name);
    }
}
