package cyberdyne.generator.Controllers;

import cyberdyne.generator.Http.Auth.Auth;
import cyberdyne.generator.Http.Models.ResponseModel;
import cyberdyne.generator.Http.View.HttpView;
import org.json.JSONObject;

public class DashboardController
{

    //Constractor for auth
    public DashboardController(JSONObject Header)
    {

    }


    //Get Dashboard Page
    public ResponseModel Dashboard(JSONObject Header)
    {
        return new ResponseModel("200", "text/html", new HttpView().View("Dashboard/dashboard"));
    }


}
