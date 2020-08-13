package com.wisp.game.bet.GameConfig;

import com.wisp.game.bet.GameConfig.BaccaratConfig.*;
import com.wisp.game.bet.games.share.config.RMConfig;
import com.wisp.game.bet.games.share.config.RMStockConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GameConfig implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RMConfig rmConfig;

    @Autowired
    private RMStockConfig rmStockConfig;

    @Override
    public void afterPropertiesSet() throws Exception {

        boolean flag =  BaccaratBaseConfig.GetInstnace().Load()
                        && BaccaratBetMaxConfig.GetInstnace().Load()
                        && BaccaratMultiLanguageConfig.GetInstnace().Load()
                        && BaccaratProbConfig.GetInstnace().Load()
                        && BaccaratRobAIConfig.GetInstnace().Load()
                        && BaccaratRobConfig.GetInstnace().Load();

        if( !flag )
        {
            logger.error("the config load has error");
            System.exit(0);
        }

        flag = rmConfig.Load("./Config/BaccaratRoomConfig.xml")
                && rmStockConfig.Load("./Config/BaccaratStockConfig.xml")
                && PlayerStockConfig.GetInstnace().Load();


        if( !flag )
        {
            logger.error("the config load has error");
            System.exit(0);
        }
    }
}
