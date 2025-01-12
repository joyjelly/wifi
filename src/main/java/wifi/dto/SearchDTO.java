package wifi.dto;

public class SearchDTO {
    private double lat;  // 위도
    private double lnt;  // 경도

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLnt() {
        return lnt;
    }

    public void setLnt(double lnt) {
        this.lnt = lnt;
    }
}