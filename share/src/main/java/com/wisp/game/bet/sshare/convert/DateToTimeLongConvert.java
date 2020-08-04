package com.wisp.game.bet.sshare.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.util.Date;


/**
 * // Direction: MongoDB -> Java
 * @ReadingConverter
 */

@ReadingConverter
public class DateToTimeLongConvert implements Converter<Date,TimeLong> {
    @Override
    public TimeLong convert(Date date) {
        long time = date.getTime();
        TimeLong timeLong = new TimeLong();
        timeLong.setMillsSecond(time);
        return timeLong;
    }
}
