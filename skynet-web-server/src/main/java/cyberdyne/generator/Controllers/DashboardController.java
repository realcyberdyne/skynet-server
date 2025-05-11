package cyberdyne.generator.Controllers;

import cyberdyne.generator.Http.Auth.Auth;
import cyberdyne.generator.Http.Models.ResponseModel;
import cyberdyne.generator.Http.View.HttpView;
import org.json.JSONObject;

public class DashboardController
{

    //Global variables
    boolean Authed=false;



    //Constractor for auth
    public DashboardController(JSONObject Header)
    {
        Authed = new Auth().AuthCheckLogined(Header);
    }


    //Get Dashboard Page
    public ResponseModel Dashboard(JSONObject Header)
    {
        if(!Authed)
        {
            return unauthorizedResponse();
        }

        return new ResponseModel("200", "text/html", new HttpView().View("Dashboard/dashboard"));
    }



    //When unauth
    private ResponseModel unauthorizedResponse()
    {
        return new ResponseModel("200", "text/html", "<script> window.location='/'; </script>");
    }


}
