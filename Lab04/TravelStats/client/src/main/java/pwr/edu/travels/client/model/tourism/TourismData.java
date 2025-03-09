package pwr.edu.travels.client.model.tourism;

import com.google.gson.annotations.SerializedName;
import pwr.edu.travels.client.model.base.BaseData;

import java.util.ArrayList;
import java.util.List;

public class TourismData extends BaseData {
    @SerializedName("id-wymiar-4")
    private int dimensionId4;

    @SerializedName("id-pozycja-4")
    private long positionId4;

    public List<Integer> getTourismDimensionIds() {
        List<Integer> tourismDimensionIds = new ArrayList<>(super.getDimensionIds());
        tourismDimensionIds.add(dimensionId4);

        return tourismDimensionIds;
    }

    public List<Long> getTourismPositionIds() {
        List<Long> tourismPositionIds = new ArrayList<>(super.getPositionIds());
        tourismPositionIds.add(positionId4);

        return tourismPositionIds;
    }
}
