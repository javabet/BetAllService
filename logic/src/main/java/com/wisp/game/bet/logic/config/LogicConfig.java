package com.wisp.game.bet.logic.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class LogicConfig implements InitializingBean {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public void afterPropertiesSet() throws Exception {
        boolean configFlag = true;
        configFlag =  MainBaseConfig.GetInstnace().Load();
        if(configFlag )
        {
            configFlag = MainMultiLanguageConfig.GetInstnace().Load();
        }
        if( configFlag )
        {
            configFlag = MainRobotBaseConfig.GetInstnace().Load();
        }
        if(configFlag)
        {
            configFlag = MainRobotNameConfig.GetInstnace().Load();
        }

        if( configFlag )
        {
            configFlag = MainRobotTypeConfig.GetInstnace().Load();
        }

        if( !configFlag )
        {
            logger.error("load the data has error");
            System.exit(0);
        }
    }
}
