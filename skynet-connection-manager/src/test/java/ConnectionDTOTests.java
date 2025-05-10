import com.cyberdyne.skynet.connection.manager.DTO.ConnectionDTO;
import com.cyberdyne.skynet.connection.manager.DTO.UsersDTO;
import com.cyberdyne.skynet.connection.manager.Models.Connectin_Models;
import com.cyberdyne.skynet.connection.manager.Models.Users_Model;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class ConnectionDTOTests
{

    @Test
    public void GetInsertNewUserToDataBaseTest()
    {
        Connectin_Models Nconnection=new Connectin_Models("T100",1,true);
        Assert.assertEquals(new ConnectionDTO().GetInsertNewConnection(Nconnection),true);
    }

    @Test
    public void GetSelectFromConnectionsInDataBaseTest()
    {
        ArrayList Connections = new ConnectionDTO().GetSelectConnections();
        Assert.assertEquals(Connections.size(),1);
    }


    @Test
    public void GetCheckConnectionExist()
    {
        Connectin_Models Connection = new ConnectionDTO().GetConnection(1);
        Assert.assertEquals(Connection.getCreate_user_id(),1);
    }



    @Test
    public void GetUpdateConnectionInDataBaseTest()
    {
        Connectin_Models Uconnection=new Connectin_Models(1,"SKJBJKCBDKJSBCKDSK","T100",1,true);
        Assert.assertEquals(new ConnectionDTO().GetUpdateConnection(Uconnection),true);
    }

}
