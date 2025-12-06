package body.sportActivity;


import org.json.JSONObject;
import utils.Utils;


public class CreateSportActivityBody {

    public JSONObject getBody(String sport_category_id, String price) {
        JSONObject body = new JSONObject();
        body.put("sport_category_id", sport_category_id);
        body.put("city_id", 10);
        body.put("title", Utils.generateRandomTitle());
        body.put("description", Utils.generateRandomTitle());
        body.put("slot", 9);
        body.put("price", price);
        body.put("address", "Lapangan Revo, Jakarta Timur");
        body.put("activity_date", Utils.getDateAfterSevenDays());
        body.put("start_time", "08:00");
        body.put("end_time", "09:00");
        body.put("map_url", "https://maps.app.goo.gl/h1AV4bfB2cojJMxK7");
        return body;
    }
}
