package cyberdyne.generator.Controllers;

import cyberdyne.generator.Functions.RandomLoginPassword;
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
        if(parametrs_json.get("password").equals(RandomLoginPassword.Password))
        {
            return new ResponseModel("200", "text/html", "<script> window.top.location.href=/Dashboard </script>");
        }
        else
        {
            return new ResponseModel("200", "text/html", new HttpView().View("Auth/LoginIsFalse"));
        }
    }

}
