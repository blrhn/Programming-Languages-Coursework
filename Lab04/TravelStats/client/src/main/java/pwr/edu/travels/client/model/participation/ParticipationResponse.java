package pwr.edu.travels.client.model.participation;

import com.google.gson.annotations.SerializedName;
import pwr.edu.travels.client.model.base.BaseData;
import pwr.edu.travels.client.model.base.BaseResponse;

import java.util.List;

public class ParticipationResponse extends BaseResponse {
    @SerializedName("data")
    List<BaseData> participationData;

    public List<BaseData> getParticipationData() {
        return participationData;
    }
}
