package com.wisp.game.bet.sshare.convert;


import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.Date;


//https://my.oschina.net/u/4256066/blog/3840976

/**
 * // Direction: MongoDB -> Java
 * @ReadingConverter
 */

@ReadingConverter
public class DateToTimeIntConvert implements Converter<Date,TimeInt> {
    @Override
    public TimeInt convert(Date date) {
        long time = date.getTime();
        Long returnTime = time/1000;
        TimeInt timeInt = new TimeInt();
        timeInt.setSecondTime(returnTime.intValue());
        return timeInt;
    }
}
