package pwr.edu.travels.client.service.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import pwr.edu.travels.client.model.Reference;

import java.lang.reflect.Type;
import java.util.List;

/*
Retrieves essential API data based on API docs instructions ("How to use the DBW API?" section):
https://api-dbw.stat.gov.pl/apidocs/index.html?urls.primaryName=API%20DBW%20(en%201.1.0)
*/
public class DataRetrieverImpl<T> implements IResponseRetriever<T>, IMetadataRetriever {
    private final Gson gson;
    private final ApiClient apiClient;
    private final Type type;

    public DataRetrieverImpl(Type type) {
     this.type = type;
     this.gson = new Gson();
     this.apiClient = new ApiClient();
    }

    @Override
    public List<Reference> getReferences(int sectionId) {
        String endpoint = "variable/variable-section-position?id-przekroj=" + sectionId + "&lang=pl";
        String json = apiClient.get(endpoint);

        if (json != null) {
            Type listType = TypeToken.getParameterized(List.class, type).getType();

            try {
                return gson.fromJson(json, listType);
            } catch (JsonSyntaxException e) {
                return null;
            }
        }

        return null;
    }

    @Override
    public String getWayOfPresentation(int wayOfPresentationId) {
        String endpoint = "dictionaries/way-of-presentation?filters=id-sposob-prezentacji-miara==" + wayOfPresentationId;

        return getSingleField(endpoint, "oznaczenie-jednostki");
    }

    @Override
    public String getDataAnnotation(int noDataId) {
        String endpoint = "dictionaries/no-value-dictionary?filters=id-brak-wartosci==" + noDataId;

        return getSingleField(endpoint, "nazwa");
    }


    @Override
    public T getResponseData(int variableId, int sectionId, int year, int period, int page) {
        String endpoint = "variable/variable-data-section?id-zmienna=" + variableId + "&id-przekroj=" + sectionId
                + "&id-rok=" + year + "&id-okres=" + period + "&ile-na-stronie=16&numer-strony=" + page + "&lang=pl";
        String json = apiClient.get(endpoint);

        if (json != null) {
            try {
                return gson.fromJson(json, type);
            } catch (JsonSyntaxException e) {
                return null;
            }
        }

        return null;
    }

    private String getSingleField(String endpoint, String fieldName) {
        String json = apiClient.get(endpoint);

        if (json != null) {
            try {
                JsonObject jsonObjectAll = gson.fromJson(json, JsonObject.class).getAsJsonObject();
                JsonArray data = jsonObjectAll.getAsJsonArray("data");
                JsonObject jsonObjectData = data.get(0).getAsJsonObject();

                return jsonObjectData.get(fieldName).getAsString();
            } catch (JsonSyntaxException e) {
                return null;
            }
        }

        return null;
    }
}
