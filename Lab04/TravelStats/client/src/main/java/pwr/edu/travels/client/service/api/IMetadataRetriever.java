package pwr.edu.travels.client.service.api;

import pwr.edu.travels.client.model.Reference;

import java.util.List;

public interface IMetadataRetriever {
    List<Reference> getReferences(int sectionId);

    String getWayOfPresentation(int wayOfPresentationId);

    String getDataAnnotation(int noDataId);
}
