package body.sportActivity;


import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import utils.Utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;


public class UpdateSportActivityBody {


    public JSONObject getBodyFromFile(String filePath, String activity_date) throws JSONException, FileNotFoundException {
        FileInputStream file = new FileInputStream(filePath);
        JSONObject body = new JSONObject(new JSONTokener(file));
        body.put("activity_date", activity_date);
        body.put("title", Utils.generateRandomTitle());
        return body;
    }
}
