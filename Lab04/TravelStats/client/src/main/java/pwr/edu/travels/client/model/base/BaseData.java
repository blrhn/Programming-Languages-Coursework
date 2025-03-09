package pwr.edu.travels.client.model.base;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// used to deserialise the data array in variable json
public class BaseData {
    @SerializedName("id-wymiar-1")
    private int dimensionId1;

    @SerializedName("id-pozycja-1")
    private long positionId1;

    @SerializedName("id-wymiar-2")
    private int dimensionId2;

    @SerializedName("id-pozycja-2")
    private long positionId2;

    @SerializedName("id-wymiar-3")
    private int dimensionId3;

    @SerializedName("id-pozycja-3")
    private long positionId3;

    @SerializedName("wartosc")
    private float value;

    @SerializedName("id-sposob-prezentacji-miara")
    private int wayOfPresentationId;

    @SerializedName("id-brak-wartosci")
    private int noDataId;

    public List<Integer> getDimensionIds() {
        return List.of(dimensionId1, dimensionId2, dimensionId3);
    }

    public List<Long> getPositionIds() {
        return List.of(positionId1, positionId2, positionId3);
    }

    public float getValue() {
        return value;
    }

    public int getWayOfPresentationId() {
        return wayOfPresentationId;
    }

    public int getNoDataId() {
        return noDataId;
    }
}
