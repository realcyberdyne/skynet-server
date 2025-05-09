package com.cyberdyne.skynet.connection.manager.Services.DateTime;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateTime
{


    //Get Datetime
    public static String GetDateTime()
    {
        ZonedDateTime CurrentDateTime = ZonedDateTime.now(ZoneId.of("UTC"));
        return CurrentDateTime.getYear()+"/"+CurrentDateTime.getMonth()+"/"+CurrentDateTime.getDayOfMonth()+" "+CurrentDateTime.getHour()+":"+CurrentDateTime.getMinute()+":"+CurrentDateTime.getSecond();
    }


}
