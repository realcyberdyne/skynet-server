import static org.junit.Assert.*;

import com.cyberdyne.skynet.connection.manager.Database.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;;

public class DatabaseTests
{

    @Test
    public void GetDatabaseExistFile()
    {
        //assertTrue(new DatabaseManager().createTablesInDB());
        boolean result = new DatabaseManager().createTablesInDB();
        Assert.assertEquals(result,true);
    }


    @Test
    public void GetINSERTDatabaseTest()
    {
        boolean result = new DatabaseManager().OprationOnDatabase("INSERT INTO users_tbl (username, password, datetime) VALUES ('reza', '******', '2025SEP3')");
        Assert.assertTrue(result);
    }


    @Test
    public void GetSELECTDatabaseTest()
    {
        int result = new DatabaseManager().SelectFromDatabase("SELECT * FROM users_tbl;").size();
        Assert.assertEquals(result,1);
    }




}
