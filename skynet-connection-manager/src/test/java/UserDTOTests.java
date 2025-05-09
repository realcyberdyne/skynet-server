import com.cyberdyne.skynet.connection.manager.DTO.UsersDTO;
import com.cyberdyne.skynet.connection.manager.Models.Users_Model;
import org.junit.Assert;
import org.junit.Test;

public class UserDTOTests
{

    @Test
    public void GetInsertNewUserToDataBaseTest()
    {
        Users_Model NUser=new Users_Model("r","*****","");
        Assert.assertEquals(new UsersDTO().GetInsertNewUser(NUser),true);
    }

}
