package service.dao;

import service.model.Offer;

import java.util.List;

public interface OfferDao {
    void addOffer(Offer offer);
    List<Offer> getOffers();
    Offer getOffer(int id);
}
