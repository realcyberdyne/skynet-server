import com.cyberdyne.skynet.connection.manager.DTO.UsersDTO;
import com.cyberdyne.skynet.connection.manager.Models.Users_Model;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class UserDTOTests
{

    @Test
    public void GetInsertNewUserToDataBaseTest()
    {
        Users_Model NUser=new Users_Model("r","*****","");
        boolean result = new UsersDTO().GetInsertNewUser(NUser);
        Assert.assertEquals(result,true);
    }


    @Test
    public void GetSelectFromUserInDataBaseTest()
    {
        ArrayList Users = new UsersDTO().GetSelectUsers();
        int result = Users.size();
        Assert.assertEquals(result,1);
    }

}
