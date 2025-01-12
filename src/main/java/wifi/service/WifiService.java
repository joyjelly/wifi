package wifi.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import wifi.dao.WifiDAO;

import java.io.IOException;
import java.net.URL;

public class WifiService {
    private static final String API_URL = "http://openapi.seoul.go.kr:8088/67416859476778783433477a644f6d/json/TbPublicWifiInfo/";
    private static final OkHttpClient okHttpClient = new OkHttpClient();
    private final WifiDAO wifiDAO;

    public WifiService() {
        this.wifiDAO = new WifiDAO();
    }

    public int getTotalWifiCount() throws IOException {
        int totalCount = 0;
        URL url = new URL(API_URL + "1/1");

        Request.Builder builder = new Request.Builder().url(url).get();
        Response response = okHttpClient.newCall(builder.build()).execute();

        if (response.isSuccessful()) {
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                JsonElement jsonElement = JsonParser.parseString(responseBody.string());
                JsonObject tbPublicWifiInfo = jsonElement.getAsJsonObject().getAsJsonObject("TbPublicWifiInfo");
                totalCount = tbPublicWifiInfo.get("list_total_count").getAsInt();
                System.out.println("Total Wi-Fi Count: " + totalCount);
            }
        } else {
            System.out.println("API call failed: " + response.code());
        }

        return totalCount;
    }

    public int loadPublicWifiData() throws IOException {
        int totalCount = getTotalWifiCount();
        int start = 1;
        int end;
        int insertedCount = 0;

        try {
            wifiDAO.createTable(); // 테이블 생성 확인

            for (int i = 0; i <= totalCount / 1000; i++) {
                start = 1 + (1000 * i);
                end = (i + 1) * 1000;
                if (end > totalCount) {
                    end = totalCount;
                }

                URL url = new URL(API_URL + start + "/" + end);
                Request.Builder builder = new Request.Builder().url(url).get();
                Response response = okHttpClient.newCall(builder.build()).execute();

                if (response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        JsonElement jsonElement = JsonParser.parseString(responseBody.string());
                        JsonArray wifiList = jsonElement.getAsJsonObject()
                                                      .getAsJsonObject("TbPublicWifiInfo")
                                                      .getAsJsonArray("row");
                        
                        insertedCount += wifiDAO.insertWifiData(wifiList);
                    }
                }
                Thread.sleep(100); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return insertedCount;
    }
}