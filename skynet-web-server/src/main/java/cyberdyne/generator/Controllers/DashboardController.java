package cyberdyne.generator.Controllers;

import com.cyberdyne.skynet.connection.manager.DTO.ConnectionDTO;
import com.cyberdyne.skynet.connection.manager.DTO.UsersDTO;
import com.cyberdyne.skynet.connection.manager.Services.ResourceMgmt.ResourceMgmt;
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

        int UsersCount = new UsersDTO().GetSelectUsers().size();
        int ConnectionCount = new ConnectionDTO().GetSelectConnections().size();
        String CpuUsage = ResourceMgmt.GetCpuUsage();
        String RamUsage = ResourceMgmt.GetRamUsage();


        return new ResponseModel("200",
                                "text/html",
                                            new HttpView().View("Dashboard/dashboard")
                                                    .replace("@UserCount",UsersCount+"")
                                                    .replace("@ConnectionsCount",ConnectionCount+"")
                                                    .replace("@CpuUsage",CpuUsage)
                                                    .replace("@RamUsage",RamUsage)
        );
    }



    //Get Lggout
    public ResponseModel Logout(JSONObject Header)
    {
        return unauthorizedResponse();
    }



    //When unauth
    private ResponseModel unauthorizedResponse()
    {
        return new ResponseModel("200", "text/html", "<script> document.cookie = '"+Auth.AuthCookieName+"=; path=/; max-age=0;'; window.location='/'; </script>");
    }


}
