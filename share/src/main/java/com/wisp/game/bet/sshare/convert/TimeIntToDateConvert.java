package com.wisp.game.bet.sshare.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.util.Date;


// Direction: Java -> MongoDB
@WritingConverter
public class TimeIntToDateConvert implements Converter<TimeInt, Date> {
    @Override
    public Date convert(TimeInt intVal) {
        Date date = new Date();
        Integer secondTime = intVal.getSecondTime();
        date.setTime( secondTime.longValue() * 1000);
        return date;
    }
}
