package cyberdyne.generator.Controllers;

import com.cyberdyne.skynet.connection.manager.DTO.UsersDTO;
import com.cyberdyne.skynet.connection.manager.Models.Users_Model;
import cyberdyne.generator.Functions.RandomLoginPassword;
import cyberdyne.generator.Http.Models.ResponseModel;
import cyberdyne.generator.Http.View.HttpView;
import org.json.JSONObject;

import java.util.List;

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
        String username = parametrs_json.get("username").toString();
        String password = parametrs_json.get("password").toString();

        List<Users_Model> Users = new UsersDTO().GetSelectUser(username,password);

        if(Users.size()>0)
        {
            return new ResponseModel("200", "text/html", "<script> window.location='/Dashboard'; </script>");
        }
        else
        {
            return new ResponseModel("200", "text/html", new HttpView().View("Auth/LoginIsFalse"));
        }

    }

}
