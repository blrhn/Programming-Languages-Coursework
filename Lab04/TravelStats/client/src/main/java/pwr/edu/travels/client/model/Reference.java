package pwr.edu.travels.client.model;

import com.google.gson.annotations.SerializedName;

public class Reference {
    @SerializedName("id-wymiar")
    private int dimensionId ;

    @SerializedName("nazwa-wymiar")
    private String dimensionName;

    @SerializedName("id-pozycja")
    private int positionId;

    @SerializedName("nazwa-pozycja")
    private String positionName;

    public int getDimensionId() {
        return dimensionId;
    }

    public String getDimensionName() {
        return dimensionName;
    }

    public long getPositionId() {
        return positionId;
    }

    public String getPositionName() {
        return positionName;
    }
}
