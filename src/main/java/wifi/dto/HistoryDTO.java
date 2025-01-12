// HistoryDTO.java
package wifi.dto;

public class HistoryDTO {
    private int id;              // 기록 ID ()
    private double lat;          // X좌표
    private double lnt;          // Y좌표
    private String searchDate;   // 조회일자

  
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getSearchDate() {
        return searchDate;
    }

    public void setSearchDate(String searchDate) {
        this.searchDate = searchDate;
    }
}