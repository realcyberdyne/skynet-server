package cyberdyne.generator.Controllers;

import cyberdyne.generator.Http.Auth.Auth;
import cyberdyne.generator.Http.Models.ResponseModel;
import cyberdyne.generator.Http.View.HttpView;
import org.json.JSONObject;

public class LoginController
{

    //Get Login Page
    public ResponseModel Login(JSONObject Header)
    {
        if(new Auth().AuthCheckExist(Header))
        {
            return new ResponseModel("200", "text/html", "<script> window.location='/Panel/Dashboard'; </script>");
        }
        else
        {
            return new ResponseModel("200", "text/html", new HttpView().View("Auth/Login"));
        }
    }


    //Get Login Submit
    public ResponseModel LoginDone(JSONObject parametrs_json,JSONObject Header)
    {
        String username = parametrs_json.get("username").toString();
        String password = parametrs_json.get("password").toString();

        String AuthToken = new Auth().GetLoginCheckByUsernamePassword(username,password);

        if(!AuthToken.equals(""))
        {
            return new ResponseModel("200", "text/html", "<script>   document.cookie = \""+Auth.AuthCookieName+"="+AuthToken+"; path=/; max-age=\" + 7 * 24 * 60 * 60 + \";\"; window.location='/Panel/Dashboard'; </script>");
        }
        else
        {
            return new ResponseModel("200", "text/html", new HttpView().View("Auth/LoginIsFalse"));
        }

    }

}
