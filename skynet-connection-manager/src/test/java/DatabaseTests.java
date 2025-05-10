import static org.junit.Assert.*;

import com.cyberdyne.skynet.connection.manager.Database.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;;

public class DatabaseTests
{

    @Test
    public void GetDatabaseExistFile()
    {
        assertTrue(new DatabaseManager().createTablesInDB());
    }


    @Test
    public void GetINSERTDatabaseTest()
    {
        assertTrue(new DatabaseManager().OprationOnDatabase("INSERT INTO users_tbl (username, password, datetime) VALUES ('reza', '******', '2025SEP3')"));
    }


    @Test
    public void GetSELECTDatabaseTest()
    {
        assertEquals(new DatabaseManager().SelectFromDatabase("SELECT * FROM users_tbl;").size(),1);
    }




}
