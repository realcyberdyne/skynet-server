package cyberdyne.generator.Controllers;

import com.cyberdyne.skynet.connection.manager.DTO.ConnectionDTO;
import com.cyberdyne.skynet.connection.manager.DTO.UsersDTO;
import com.cyberdyne.skynet.connection.manager.Models.Connectin_Models;
import com.cyberdyne.skynet.connection.manager.Models.Users_Model;
import com.cyberdyne.skynet.connection.manager.Services.KeyCoder.KeyCoder;
import com.cyberdyne.skynet.connection.manager.Services.ResourceMgmt.ResourceMgmt;
import cyberdyne.generator.Http.Auth.Auth;
import cyberdyne.generator.Http.Models.ResponseModel;
import cyberdyne.generator.Http.View.HttpView;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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




    //Get New User Page
    public ResponseModel Users(JSONObject Header)
    {
        if(!Authed)
        {
            return unauthorizedResponse();
        }

        //UsersTbl
        ArrayList<Users_Model> AllUsers = new UsersDTO().GetSelectUsers();
        String UserTbl="";
        for(int i=0;i<AllUsers.size();i++)
        {
            if(AllUsers.get(i).getId() != Auth.UserData.getId())
                UserTbl+="<tr><td>#"+AllUsers.get(i).getId()+"</td><td>"+AllUsers.get(i).getUseename()+"</td><td>"+AllUsers.get(i).getDatetime()+"</td><td><span class=\"status active\">Completed</span></td><td><button onclick=\"RemoveUserButtonClickEvent("+AllUsers.get(i).getId()+")\" class=\"action-btn delete\">❌</button></td></tr>";
        }


        return new ResponseModel("200",
                "text/html",
                new HttpView().View("Dashboard/users")
                        .replace("@UsersData",UserTbl)

        );
    }



    //Get New User Page
    public ResponseModel RemoveUserDone(JSONObject parametrs_json,JSONObject Header)
    {
        if(!Authed)
        {
            return unauthorizedResponse();
        }

        boolean remove_result = new UsersDTO().GetRemoveUser(Long.parseLong(parametrs_json.get("user_id").toString()));

        if(remove_result)
        {
            return new ResponseModel("200",
                    "text/json",
                    "{\"status\":\"successful\"}"
            );
        }
        else
        {
            return new ResponseModel("200",
                    "text/json",
                    "{\"status\":\"User Not Vaild\"}"
            );
        }
    }





    //Get Add new Connection
    public ResponseModel NewConnection(JSONObject Header)
    {
        if(!Authed)
        {
            return unauthorizedResponse();
        }

        return new ResponseModel("200",
                "text/html",
                new HttpView().View("Dashboard/newconnection")

        );
    }



    //Get Add new Connection Submit
    public ResponseModel NewConnectionDone(JSONObject parametrs_json,JSONObject Header)
    {
        if(!Authed)
        {
            return unauthorizedResponse();
        }

        boolean ConnectionStatus = false;
        if(parametrs_json.get("status").toString().equals("1"))
        {
            ConnectionStatus=true;
        }

        new ConnectionDTO().GetInsertNewConnection(new Connectin_Models(
                "",parametrs_json.get("protocol").toString(),Auth.UserData.getId(),ConnectionStatus
        ));

        return new ResponseModel("200",
                "text/html",
                "<script> window.location.href='/Panel/Connections'; </script>"
        );
    }




    //Get Connections
    public ResponseModel Connections(JSONObject Header)
    {
        if(!Authed)
        {
            return unauthorizedResponse();
        }

        //UsersTbl
        ArrayList<Connectin_Models> AllConnections = new ConnectionDTO().GetSelectConnections();
        String AllConnectionsTR="";
        for(int i=0;i<AllConnections.size();i++)
        {
            String ConnectionProp= KeyCoder.KeyCode(AllConnections.get(i));
            System.out.println("Status : "+AllConnections.get(i).isStatus());
            if (AllConnections.get(i).isStatus())
            {
                AllConnectionsTR += "<tr><td>#" + AllConnections.get(i).getId() + "</td><td>" + AllConnections.get(i).getProtocol() + "</td><td><span onclick=\"GetChangeConnectionStatusOnClickEvent('" + AllConnections.get(i).getId() + "',false)\" class=\"status active\">Active</span></td><td><button onclick=\"GetRemoveConnectionOnClickEvent(" + AllConnections.get(i).getId() + ")\" class=\"action-btn delete\">❌</button></td><td><button onclick=\"copyToClipboard('" + AllConnections.get(i).getProtocol() + "://" + ConnectionProp + "')\" alt=\"Copy\" class=\"action-btn delete\">\uD83D\uDD12</button></td></tr>";
            }
            else
            {
                AllConnectionsTR += "<tr><td>#" + AllConnections.get(i).getId() + "</td><td>" + AllConnections.get(i).getProtocol() + "</td><td><span onclick=\"GetChangeConnectionStatusOnClickEvent('" + AllConnections.get(i).getId() + "',true)\" class=\"status inactive\">Inactive</span></td><td><button onclick=\"GetRemoveConnectionOnClickEvent(" + AllConnections.get(i).getId() + ")\" class=\"action-btn delete\">❌</button></td><td><button onclick=\"copyToClipboard('" + AllConnections.get(i).getProtocol() + "://" + ConnectionProp + "')\" alt=\"Copy\" class=\"action-btn delete\">\uD83D\uDD12</button></td></tr>";
            }
        }


        return new ResponseModel("200",
                "text/html",
                new HttpView().View("Dashboard/connections")
                        .replace("@Connections",AllConnectionsTR)
        );
    }




    //Post change Connections status
    public ResponseModel GetChangeConnectionsStatus(JSONObject parametrs_json,JSONObject Header)
    {
        if(!Authed)
        {
            return unauthorizedResponse();
        }

        Connectin_Models connection = new ConnectionDTO().GetConnection(Long.parseLong(parametrs_json.get("c_id").toString()));
        System.out.println("Set statue : "+parametrs_json.get("status").toString());
        if(parametrs_json.get("status").toString().equals("false"))
        {
            connection.setStatus(false);
        }
        else
        {
            connection.setStatus(true);
        }

        System.out.println("Connection is : "+connection.isStatus());

        new ConnectionDTO().GetUpdateConnection(connection);

        return new ResponseModel("200",
                "text/html",
                "<script> window.location.href='/Connections'; </script>"
        );
    }




    //Post remove Connections
    public ResponseModel GetRemoveConnections(JSONObject parametrs_json,JSONObject Header)
    {
        if(!Authed)
        {
            return unauthorizedResponse();
        }

        Connectin_Models connection = new ConnectionDTO().GetConnection(Long.parseLong(parametrs_json.get("c_id").toString()));

        new ConnectionDTO().GetRemoveConnection(connection.getId());

        return new ResponseModel("200",
                "text/html",
                "<script> window.location.href='/Connections'; </script>"
        );
    }




    //Get New User Page
    public ResponseModel NewUser(JSONObject Header)
    {
        if(!Authed)
        {
            return unauthorizedResponse();
        }

        return new ResponseModel("200",
                "text/html",
                new HttpView().View("Dashboard/newuser")

        );
    }



    //Post New User Done
    public ResponseModel NewUserDone(JSONObject parametrs_json,JSONObject Header)
    {
        if(!Authed)
        {
            return unauthorizedResponse();
        }

        new UsersDTO().GetInsertNewUser(new Users_Model(parametrs_json.get("username").toString(),
                                                        parametrs_json.get("password").toString(),
                                                "")
        );

        return new ResponseModel("200","text/html","<script> window.location.href='/Panel/Dashboard'; </script>");
    }





    //Get Profile Page
    public ResponseModel Profile(JSONObject Header)
    {
        if(!Authed)
        {
            return unauthorizedResponse();
        }

        return new ResponseModel("200",
                "text/html",
                new HttpView().View("Dashboard/profile")
                        .replace("@username",Auth.UserData.getUseename())
        );
    }


    //Post Profile Page
    public ResponseModel ProfileDone(JSONObject parametrs_json,JSONObject Header)
    {
        if(!Authed)
        {
            return unauthorizedResponse();
        }

        new UsersDTO().GetUpdateUser(Auth.UserData.getId(), parametrs_json.get("username").toString(),parametrs_json.get("password").toString());

        return unauthorizedResponse();
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
