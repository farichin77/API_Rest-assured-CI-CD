package body.sportCategory;

import org.json.JSONObject;

public class UpdateSportCategoryBody {

    public JSONObject getBody(String newName) {
        JSONObject body = new JSONObject();
        body.put("name", newName);
        return body;
    }
}
