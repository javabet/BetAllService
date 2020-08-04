package com.wisp.game.bet.sshare.convert;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.util.Date;

// Direction: Java -> MongoDB
@WritingConverter
public class TimeLongToDateConvert implements Converter<TimeLong, Date> {
    @Override
    public Date convert(TimeLong timeLong) {
        Date date = new Date();
        date.setTime(timeLong.getMillsSecond());
        return date;
    }
}
