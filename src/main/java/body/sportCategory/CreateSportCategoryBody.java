package body.sportCategory;

import org.json.JSONObject;

public class CreateSportCategoryBody {

    public JSONObject getBody(String name) {
        JSONObject body = new JSONObject();
        body.put("name", name);
        return body;
    }
}
