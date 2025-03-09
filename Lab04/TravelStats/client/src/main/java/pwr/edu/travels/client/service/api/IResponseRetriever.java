package pwr.edu.travels.client.service.api;

public interface IResponseRetriever<T> {
    T getResponseData(int variableId, int sectionId, int year, int period, int page);
}
