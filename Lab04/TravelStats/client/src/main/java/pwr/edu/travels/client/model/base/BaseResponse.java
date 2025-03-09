package pwr.edu.travels.client.model.base;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// used to deserialise variable json
public class BaseResponse {
    @SerializedName("page-number")
    private int pageNumber;

    @SerializedName("page-size")
    private int pageSize;

    @SerializedName("page-count")
    private int pageCount;

    public List<Integer> getPageInfo() {
        return List.of(pageNumber, pageSize, pageCount);
    }
}
