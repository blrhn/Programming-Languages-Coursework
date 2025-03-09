package riversection.service;

public interface IRiverSection {
    void setRealDischarge(int realDischarge);
    void setRainfall(int rainfall);
    void assignRetentionBasin(int port, String host);
}
