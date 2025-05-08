package cyberdyne.generator.Controllers;

import cyberdyne.generator.Http.Models.ResponseModel;
import cyberdyne.generator.Http.View.HttpView;
import org.json.JSONObject;

public class LoginController
{

    //Get Login Page
    public ResponseModel Login()
    {
        return new ResponseModel("200","text/html",new HttpView().View("Auth/Login"));
    }


    //Get Login Submit
    public ResponseModel LoginDone(JSONObject parametrs_json,JSONObject Header)
    {
        return new ResponseModel("200","text/html",new HttpView().View("Auth/LoginIsFalse"));
    }

}
