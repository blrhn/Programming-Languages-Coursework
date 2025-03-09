package pwr.edu.travels.client.model.tourism;

import com.google.gson.annotations.SerializedName;
import pwr.edu.travels.client.model.base.BaseResponse;

import java.util.List;

public class TourismResponse extends BaseResponse {
    @SerializedName("data")
    List<TourismData> tourismData;

    public List<TourismData> getTourismData() {
        return tourismData;
    }
}
