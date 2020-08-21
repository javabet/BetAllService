package com.wisp.game.bet.games.share;

import com.wisp.game.bet.db.mongo.games.doc.GameRoomIndexDoc;
import com.wisp.game.bet.db.mongo.games.doc.GameRoomMgrDoc;
import com.wisp.game.bet.db.mongo.games.doc.GameRoomSetDoc;
import com.wisp.game.bet.db.mongo.games.doc.GameRoomStockDoc;
import com.wisp.game.bet.games.baccarat.mgr.GameEngine;
import com.wisp.game.bet.games.share.config.RMConfig;
import com.wisp.game.bet.games.share.config.RMConfigData;
import com.wisp.game.bet.games.share.config.RMStockConfig;
import com.wisp.game.bet.games.share.config.RMStockConfigData;
import com.wisp.game.bet.logic.db.DbGame;
import com.wisp.game.bet.logic.gameMgr.GameManager;
import com.wisp.game.bet.logic.unit.LogicServer;
import com.wisp.game.bet.sshare.DbBase;
import com.wisp.game.core.utils.CommonUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RoomMgr{

    private Logger logger = LoggerFactory.getLogger(getClass());
    public static RoomMgr Instance;

    private Map<Integer, GameRoomMgrDoc> m_rooms;
    private RMConfig m_cfg;
    private ICheckRoomCfgFun check_roomcfg_fun;

    public RoomMgr() {
        Instance = this;
        m_rooms = new HashMap<>();
    }

    public boolean check_config(int agent_id, int gameId, int rid, RMConfig roomcfg,int template_id)
    {
        if (!GameManager.Instance.is_room_config_check_act())
        {
            return true;
        }
        if (check_roomcfg_fun != null)
        {
            if (!check_roomcfg_fun.check_roomcfg_fun(agent_id,gameId, rid, m_cfg, template_id)) {
                //SLOG_CRITICAL << "set_room  [s]check_roomcfg_fun  game[" << gameId << "] \
                //roomid[" << rid << "] err!";
                return false;
            }
        }
        else
        {
            if (!check_room_config(agent_id,gameId, rid, m_cfg, template_id)) {
                //SLOG_CRITICAL << "set_room  check_room_config game[" << gameId << "] \
                //roomid[" << rid << "] err!";
                return false;
            }
        }
        return true;
    }


    public void init(RMConfig cfg, ICheckRoomCfgFun chkfun)
    {
        m_cfg = cfg;
        check_roomcfg_fun = chkfun;
    }


    public void register_room(int agentid)
    {
        //查找本服务进程内的房间
        boolean bfind = false;
        int serverId = LogicServer.Instance.get_serverid();
        int gameId = GameEngine.Instance.get_gameid();

        Criteria criteria = Criteria.where("AgentId").is(agentid).and("ServerId").is(serverId).and("GameId").is(gameId)
                .and("IsOpen").is(true);
        Query query = new Query(criteria);
        List<GameRoomMgrDoc> gameRoomMgrDocs = DbGame.Instance.getMongoTemplate().find(query,GameRoomMgrDoc.class);
        if(gameRoomMgrDocs != null && gameRoomMgrDocs.size() > 0 )
        {
            bfind = true;
            open_room(gameRoomMgrDocs);
        }

        //没有房间,按照默认配置，创建房间
        if (!bfind)
        {
            Criteria criteria1 = Criteria.where("AgentId").is(agentid).and("ServerId").is(serverId).and("GameId").is(gameId);
            query = new Query(criteria);
            gameRoomMgrDocs = DbGame.Instance.getMongoTemplate().find(query,GameRoomMgrDoc.class);
            if( gameRoomMgrDocs == null || gameRoomMgrDocs.size() == 0  )
            {
                create_default_room(agentid);
            }
        }
    }

    public Map<Integer,GameRoomMgrDoc>  get_rooms()
    {
        return m_rooms;
    }


    public String get_uniqueid(int roomid)
    {
        GameRoomMgrDoc doc = m_rooms.get(roomid);
        if (doc != null)
            return doc.getUid();

        return "";
    }


    public int get_agentid(int roomid)
    {
        GameRoomMgrDoc doc = m_rooms.get(roomid);
        if ( doc != null )
            return doc.getAgentId();

        return 0;
    }


    public int set_room(int agentid, int roomid)
    {
        int gameId = GameEngine.Instance.get_gameid();
        int serverId = LogicServer.Instance.get_serverid();

        int type = 0;
        if (roomid != 0)
        {
            Criteria criteria = Criteria.where("AgentId").is(agentid).and("GameId").is(gameId).and("RoomId").is(roomid);
            Query query = new Query(criteria);
            GameRoomSetDoc gameRoomSetDoc =  DbGame.Instance.getMongoTemplate().findAndRemove(query, GameRoomSetDoc.class);

            if (gameRoomSetDoc != null)
            {
                if (gameRoomSetDoc.getType() == 1)
                {
                    //添加房间
                    int templateId =  gameRoomSetDoc.getTemplateId(); //mongo_helper::instance().get_number_int(view["TemplateId"]);
                    int roomNameType = gameRoomSetDoc.getRoomNameType(); //mongo_helper::instance().get_number_int(view["RoomNameType"]);
                    String roomIDTxt =  gameRoomSetDoc.getRoomIDTxt() ;//mongo_helper::instance().get_string_field(view["RoomIDTxt"]);

                    RMConfigData cfgData = m_cfg.GetData(templateId);
                    if (cfgData == null)
                        return 0;
                    if (!check_config(agentid,gameId, templateId, m_cfg, templateId))
                    {
                        return 0;
                    }

                    RMConfigData cfgCpy = CommonUtils.deepClone(cfgData);
                    //生成RoomId
                    GameRoomMgrDoc obj = new GameRoomMgrDoc();// game_room_object::malloc();
                    obj.setTemplateId( cfgCpy.getmRoomID());
                    obj.setRoomId(roomid);
                    cfgCpy.setmRoomID(obj.getRoomId());

                    //后台创建时指定房间编号
                    if (!roomIDTxt.isEmpty())
                        obj.setRoomIDTxt(roomIDTxt);
                    else
                        obj.setRoomIDTxt( cfgCpy.getmRoomIDTxt() );
                    //后台创建时指定场次名称类型
                    if(roomNameType > 0)
                        obj.setRoomNameType( roomNameType );
                    else
                        obj.setRoomNameType( cfgCpy.getmRoomNameType() );

                    obj.setUid(new ObjectId().toHexString());//->Uid->set_oid(bsoncxx::oid());
                    obj.setGameId( gameId );//->GameId->set_value(gameId);
                    obj.setAgentId(agentid);//->AgentId->set_value(agentid);
                    obj.setPlayerMaxCount( cfgCpy.getmPlayerMaxCount() );//->mPlayerMaxCount->set_value(cfgCpy.mPlayerMaxCount);
                    obj.setServerId( serverId );//->ServerId->set_value(serverId);
                    obj.setStatus(1);//->Status->set_value(1);
                    obj.setOpen(cfgCpy.getmIsOpen());//->IsOpen->set_value(cfgCpy.mIsOpen);

                    obj.setRoomName( cfgCpy.getmRoomName() );//->mRoomName->set_string(cfgCpy.mRoomName);
                    obj.setBankerCondition( cfgCpy.getmBankerCondition() );//->mBankerCondition->set_value(cfgCpy.mBankerCondition);
                    obj.setFirstBankerCost( cfgCpy.getmFirstBankerCost() );//->mFirstBankerCost->set_value(cfgCpy.mFirstBankerCost);
                    obj.setAddBankerCost( cfgCpy.getmAddBankerCost() );//->mAddBankerCost->set_value(cfgCpy.mAddBankerCost);
                    obj.setAutoLeaveBanker( cfgCpy.getmAutoLeaveBanker() );//->mAutoLeaveBanker->set_value(cfgCpy.mAutoLeaveBanker);
                    obj.setPlayerMaxCount( cfgCpy.getmPlayerMaxCount() );//->mPlayerMaxCount->set_value(cfgCpy.mPlayerMaxCount);
                    obj.setGoldCondition(cfgCpy.getmGoldCondition());//->mGoldCondition->set_value(cfgCpy.mGoldCondition);
                    obj.setBetCondition(cfgCpy.getmBetCondition());//->mBetCondition->set_value(cfgCpy.mBetCondition);
                    obj.setBaseGold(cfgCpy.getmBaseGold());//->mBaseGold->set_value(cfgCpy.mBaseGold);
                    obj.setMaxAnte(cfgCpy.getmMaxAnte());//->mMaxAnte->set_value(cfgCpy.mMaxAnte);
                    obj.setRoomType(cfgCpy.getmRoomType());// ->mRoomType->set_value(cfgCpy.mRoomType);
                    obj.setFreeGold(cfgCpy.getmFreeGold());//->mFreeGold->set_value(cfgCpy.mFreeGold);
                    obj.setLeopardLimit(cfgCpy.getmLeopardLimit());//->mLeopardLimit->set_value(cfgCpy.mLeopardLimit);
                    obj.setEveryLeopardLimit(cfgCpy.getmEveryLeopardLimit());//->mEveryLeopardLimit->set_value(cfgCpy.mEveryLeopardLimit);
                    obj.setProfitRateCheckIntervaltime( cfgCpy.getmProfitRateCheckIntervaltime() );//->mProfitRateCheckIntervaltime->set_value(cfgCpy.mProfitRateCheckIntervaltime);
                    obj.setFreeItem( cfgCpy.getmFreeItem() );//->mFreeItem->set_value(cfgCpy.mFreeItem);
                    obj.setCheckOpenRate(cfgCpy.getmCheckOpenRate());//->mCheckOpenRate->set_value(cfgCpy.mCheckOpenRate);
                    obj.setCanGetExp(cfgCpy.getmCanGetExp());//->mCanGetExp->set_value(cfgCpy.mCanGetExp);
                    obj.setMissileCost( cfgCpy.getmMissileCost() );//->set_value(cfgCpy.mMissileCost);
                    obj.setBuyPowerCost(cfgCpy.getmBuyPowerCost());//->mBuyPowerCost->set_value(cfgCpy.mBuyPowerCost);
                    obj.setPowerParam(cfgCpy.getmPowerParam());//->mPowerParam->set_value(cfgCpy.mPowerParam);
                    obj.setLevelCondition( cfgCpy.getmLevelCondition() );//->mLevelCondition->set_value(cfgCpy.mLevelCondition);
                    obj.setBotMaxGold(cfgCpy.getmBotMaxGold());//->mBotMaxGold->set_value(cfgCpy.mBotMaxGold);
                    obj.setBotMinGold(cfgCpy.getmBotMinGold());//->mBotMinGold->set_value(cfgCpy.mBotMinGold);
                    obj.setForceLeaveGold(cfgCpy.getmForceLeaveGold());//->mForceLeaveGold->set_value(cfgCpy.mForceLeaveGold);
                    obj.setBigBlind(cfgCpy.getmBigBlind());//->mBigBlind->set_value(cfgCpy.mBigBlind);
                    obj.setSmallBlind(cfgCpy.getmSmallBlind());//->mSmallBlind->set_value(cfgCpy.mSmallBlind);
                    obj.setCarryRestriction(cfgCpy.getmCarryRestriction());//->mCarryRestriction->set_value(cfgCpy.mCarryRestriction);
                    obj.setHuaGold(cfgCpy.getmHuaGold());//->mHuaGold->set_value(cfgCpy.mHuaGold);
                    obj.setTableCount(cfgCpy.getmTableCount());//->mTableCount->set_value(cfgCpy.mTableCount);
                    obj.setGameModel(cfgCpy.getmGameModel());//->mGameModel->set_string(cfgCpy.mGameModel);


                    obj.setBetNames(CommonUtils.deepSimpleList(cfgCpy.getmBetNames()));
                    obj.setWeightList(CommonUtils.deepSimpleList(cfgCpy.getmWeightList()));
                    obj.setBetLimit(CommonUtils.deepSimpleList(cfgCpy.getmBetLimit()));
                    obj.setBetRange(CommonUtils.deepSimpleList(cfgCpy.getmBetRange()));
                    obj.setPlatList(CommonUtils.deepSimpleList(cfgCpy.getmPlatList()));
                    obj.setCustomList(CommonUtils.deepSimpleList(cfgCpy.getmCustomList()));
                    obj.setmRatePoolList(CommonUtils.deepSimpleList(cfgCpy.getmRatePoolList()));
                    obj.store_game_object(DbGame.Instance,true);

                    if( cfgCpy.getmIsOpen() )
                    {
                        m_rooms.put(obj.getRoomId(),obj);
                        m_cfg.GetMapData().put(obj.getRoomId(),cfgCpy);
                    }
                }
                else if (gameRoomSetDoc.getType()  == 2)
                {
                    if(m_rooms.containsKey(roomid))
                    {
                       m_rooms.get(roomid).setOpen(false);
                        Map<Integer, RMConfigData> cfgMap =  m_cfg.GetMapData();
                       cfgMap.get(roomid).setmIsOpen(false);
                       m_rooms.get(roomid).store_game_object(DbGame.Instance.getMongoTemplate());
                    }
                }
                else if (type == 3)
                {
                    //设置房间参数
                    //do nothing 暂时
                }

            }
        }
        else
        {
             Criteria criteria = Criteria.where("AgentId").is(agentid).and("ServerId").is(serverId).and("GameId").is(gameId).and("Type").is(4);
             GameRoomSetDoc gameRoomSetDoc =  DbGame.Instance.getMongoTemplate().findAndRemove(Query.query(criteria),GameRoomSetDoc.class);
            if( gameRoomSetDoc != null )
            {
                criteria = Criteria.where("AgentId").is(agentid).and("GameId").is(gameId);
                GameRoomMgrDoc gameRoomMgrDoc =  DbGame.Instance.getMongoTemplate().findOne(Query.query(criteria),GameRoomMgrDoc.class);
                if( gameRoomMgrDoc == null )
                {
                    type = 4;
                    create_default_room(agentid);
                }
            }

        }
        return type;
    }


    public void close_room(int roomid)
    {
        GameRoomMgrDoc doc = m_rooms.get(roomid);
        if (doc != null )
        {
            GameManager.Instance.notify_close_room(doc.getAgentId(),roomid);
            doc.setStatus(0);
            doc.store_game_object( DbGame.Instance.getMongoTemplate(),true );
            m_rooms.remove(doc.getRoomId());
        }
    }

    public GameRoomMgrDoc get_room(int roomid)
    {
        return m_rooms.get(roomid);
    }

    public boolean open_room(int agentid, boolean open)
    {
        if (open)
        {
            int gameId = GameEngine.Instance.get_gameid();
            int serverId = LogicServer.Instance.get_serverid();

            Criteria criteria = Criteria.where("AgentId").is(agentid).and("ServerId").is(serverId).and("GameId").is(gameId).is("IsOpen").is(true);
            Query query = new Query(criteria);
            List<GameRoomMgrDoc> gameRoomMgrDocs =  DbGame.Instance.getMongoTemplate().find(query,GameRoomMgrDoc.class);
            if( gameRoomMgrDocs != null && gameRoomMgrDocs.size() > 0 )
            {
                open_room(gameRoomMgrDocs);
                return true;
            }
        }
        else
        {
            for (GameRoomMgrDoc doc : m_rooms.values())
            {
                if ( doc.getAgentId() == agentid)
                {
                    Map<Integer, RMConfigData> rmConfigDataMap =  m_cfg.GetMapData();
                    if( rmConfigDataMap.containsKey(doc.getRoomId()) )
                    {
                        rmConfigDataMap.get(doc.getRoomId()).setmIsOpen(false);
                    }
                }
            }
        }
        return false;
    }


    public int get_template_id(int roomid)
    {
        GameRoomMgrDoc doc = m_rooms.get(roomid);
        if (doc == null )
            return roomid;

        return doc.getTemplateId();
    }


    public int generic_roomid()
    {
        int gameId = GameEngine.Instance.get_gameid();

        Criteria criteria = Criteria.where("GameId").is(gameId);
        Update update = new Update();
        update.inc("Index");
        FindAndModifyOptions findAndModifyOptions = new FindAndModifyOptions();
        findAndModifyOptions.upsert(true);
        findAndModifyOptions.upsert(true);

         GameRoomIndexDoc doc =  DbGame.Instance.getMongoTemplate().findAndModify(new Query(criteria),update,findAndModifyOptions, GameRoomIndexDoc.class);
         if(doc == null )
         {
             return 0;
         }

         return doc.getIndex();
    }

    public void open_room(List<GameRoomMgrDoc> list)
    {
        int gameId = GameManager.Instance.get_gameid();
        Map<Integer,RMConfigData> map = m_cfg.GetMapData();// ->GetMapData();

        for (GameRoomMgrDoc obj : list)
        {
            if(!map.containsKey(obj.getRoomId()))
            {
                map.put(obj.getRoomId(),new RMConfigData());
            }

            RMConfigData rmConfigData  = map.get(obj.getRoomId());
            rmConfigData.setmRoomID(obj.getRoomId());
            rmConfigData.setmRoomName(obj.getRoomName());
            rmConfigData.setmRoomIDTxt(obj.getRoomIDTxt());
            rmConfigData.setmBankerCondition((int)obj.getBankerCondition());
            rmConfigData.setmFirstBankerCost((int)obj.getFirstBankerCost());
            rmConfigData.setmAddBankerCost((int)obj.getAddBankerCost());
            rmConfigData.setmAutoLeaveBanker((int)obj.getAutoLeaveBanker());
            rmConfigData.setmPlayerMaxCount((int)obj.getPlayerMaxCount());// = roomObj->mPlayerMaxCount->get_value();
            rmConfigData.setmGoldCondition((int)obj.getGoldCondition());// = roomObj->mGoldCondition->get_value();
            rmConfigData.setmBetCondition((int)obj.getBetCondition());// = roomObj->mBetCondition->get_value();
            rmConfigData.setmBaseGold((int)obj.getBaseGold());// = roomObj->mBaseGold->get_value();
            rmConfigData.setmIsOpen(obj.isOpen());// = roomObj->IsOpen->get_value();
            rmConfigData.setmMaxAnte(obj.getMaxAnte());// = roomObj->mMaxAnte->get_value();
            rmConfigData.setmRoomType(obj.getRoomType());// = roomObj->mRoomType->get_value();
            rmConfigData.setmFreeGold((int)obj.getFreeGold());// = roomObj->mFreeGold->get_value();
            rmConfigData.setmLeopardLimit(obj.getLeopardLimit());// = roomObj->mLeopardLimit->get_value();
            rmConfigData.setmEveryLeopardLimit(obj.getEveryLeopardLimit());// = roomObj->mEveryLeopardLimit->get_value();
            rmConfigData.setmProfitRateCheckIntervaltime(obj.getProfitRateCheckIntervaltime());// = roomObj->mProfitRateCheckIntervaltime->get_value();
            rmConfigData.setmFreeItem(obj.isFreeItem());// = roomObj->mFreeItem->get_value();
            rmConfigData.setmCheckOpenRate(obj.isCheckOpenRate());// = roomObj->mCheckOpenRate->get_value();
            rmConfigData.setmCanGetExp(obj.isCanGetExp());// = roomObj->mCanGetExp->get_value();
            rmConfigData.setmMissileCost(obj.getMissileCost());// = roomObj->mMissileCost->get_value();
            rmConfigData.setmBuyPowerCost(obj.getBuyPowerCost());// = roomObj->mBuyPowerCost->get_value();
            rmConfigData.setmPowerParam(obj.getPowerParam());// = roomObj->mPowerParam->get_value();
            rmConfigData.setmLevelCondition(obj.getLevelCondition());// = roomObj->mLevelCondition->get_value();
            rmConfigData.setmBotMaxGold(obj.getBotMaxGold());// = roomObj->mBotMaxGold->get_value();
            rmConfigData.setmBotMinGold(obj.getBotMinGold());// = roomObj->mBotMinGold->get_value();
            rmConfigData.setmForceLeaveGold(obj.getForceLeaveGold());// = roomObj->mForceLeaveGold->get_value();
            rmConfigData.setmBigBlind(obj.getBigBlind());// = roomObj->mBigBlind->get_value();
            rmConfigData.setmSmallBlind(obj.getSmallBlind());// = roomObj->mSmallBlind->get_value();
            rmConfigData.setmCarryRestriction(obj.getCarryRestriction());// = roomObj->mCarryRestriction->get_value();
            rmConfigData.setmRoomNameType(obj.getRoomNameType());// = roomObj->mRoomNameType->get_value();
            rmConfigData.setmTableCount(obj.getTableCount());// = roomObj->mTableCount->get_value();
            rmConfigData.setmHuaGold(obj.getHuaGold());// = roomObj->mHuaGold->get_value();
            rmConfigData.setmGameModel(obj.getGameModel());// = roomObj->mGameModel->get_string();

            rmConfigData.setmBetNames(new ArrayList<>());
            for (int i = 0; i < obj.getBetNames().size(); i++)
            {
                rmConfigData.getmBetNames().add(obj.getBetNames().get(i));
            }

            rmConfigData.setmWeightList(new ArrayList<>());
            for (int i = 0; i < obj.getWeightList().size(); i++)
            {
                rmConfigData.getmWeightList().add(obj.getWeightList().get(i));
            }

            rmConfigData.setmBetRange(new ArrayList<>());//
            for (int i = 0; i < obj.getBetRange().size(); i++)
            {
                rmConfigData.getmBetRange().add(obj.getBetRange().get(i));
            }

            rmConfigData.setmBetLimit(new ArrayList<>());//
            for (int i = 0; i < obj.getBetLimit().size(); i++)
            {
                rmConfigData.getmBetLimit().add(obj.getBetLimit().get(i));
            }

            rmConfigData.setmCustomList(new ArrayList<>());//.getmCustomList().clear();
            for (int i = 0; i < obj.getCustomList().size(); i++)
            {
                rmConfigData.getmCustomList().add(obj.getCustomList().get(i));
            }

            rmConfigData.setmPlatList(new ArrayList<>());//.getmPlatList().clear();
            for (int i = 0; i < obj.getPlatList().size(); i++)
            {
                rmConfigData.getmPlatList().add(obj.getPlatList().get(i));
            }

            rmConfigData.setmRatePoolList(new ArrayList<>());
            for (int i = 0; i < obj.getmRatePoolList().size(); i++)
            {
                rmConfigData.getmRatePoolList().add(obj.getmRatePoolList().get(i));
            }
        }

        for (GameRoomMgrDoc obj : list)
        {
            int roomId = obj.getRoomId();
            if (!check_config(obj.getAgentId(),gameId, roomId, m_cfg,obj.getTemplateId())) {
                obj.setOpen(false);
                obj.store_game_object( DbGame.Instance.getMongoTemplate(),true);
                continue;
            }

            obj.setStatus(1);
            obj.store_game_object( DbGame.Instance.getMongoTemplate(),true);
;
            m_rooms.put( roomId,obj );
        }
    }


    public void create_default_room(int agentid)
    {
        int serverId = LogicServer.Instance.get_serverid();// game_engine::instance().get_handler()->get_serverid();
        int gameId = GameEngine.Instance.get_gameid();

        Map<Integer,RMConfigData> cfgMap = m_cfg.GetMapData();
        int count = m_cfg.GetCount();
        for (int i = 1; i <= count; i++)
        {
            RMConfigData cfgData = m_cfg.GetData(i);
            if (cfgData == null)
                continue;
            if (!check_config(agentid,gameId, cfgData.getmRoomID(), m_cfg, 0)) {
                //SLOG_CRITICAL << "create_default_room  check_room_config game[" << gameId << "] \
                //roomid["<<cfgData->mRoomID<<"] err!";
                continue;
            }

            RMConfigData cfgCpy = CommonUtils.deepClone(cfgData);// cfgData.Clone();
            if (!cfgCpy.getmIsOpen())
                continue;

            //防止循环创建
            GameRoomMgrDoc srcRoom = get_room(cfgCpy.getmRoomID());
            if (srcRoom != null && srcRoom.getTemplateId() != cfgCpy.getmRoomID())
                continue;

            //obj->RoomId->set_value(it.second.mRoomID);
            //生成RoomId
            GameRoomMgrDoc doc = new GameRoomMgrDoc();
            doc.setTemplateId(cfgCpy.getmRoomID());
            doc.setRoomId( generic_roomid());
            cfgCpy.setmRoomID(doc.getRoomId());
            doc.setRoomIDTxt(cfgCpy.getmRoomIDTxt());
            doc.setUid(new ObjectId().toHexString());
            doc.setGameId(gameId);
            doc.setAgentId(agentid);
            doc.setPlayerMaxCount(cfgCpy.getmPlayerMaxCount());
            doc.setServerId(serverId);
            doc.setStatus(1);
            doc.setOpen(cfgCpy.getmIsOpen());
            doc.setRoomName(cfgCpy.getmRoomName());
            doc.setBankerCondition(cfgCpy.getmBankerCondition());
            doc.setFirstBankerCost(cfgCpy.getmFirstBankerCost());
            doc.setAddBankerCost(cfgCpy.getmAddBankerCost());
            doc.setAutoLeaveBanker(cfgCpy.getmAutoLeaveBanker());
            doc.setPlayerMaxCount(cfgCpy.getmPlayerMaxCount());
            doc.setGoldCondition(cfgCpy.getmGoldCondition());
            doc.setBetCondition(cfgCpy.getmBetCondition());
            doc.setBaseGold(cfgCpy.getmBaseGold());
            doc.setMaxAnte(cfgCpy.getmMaxAnte());
            doc.setRoomType(cfgCpy.getmRoomType());
            doc.setFreeGold(cfgCpy.getmFreeGold());
            doc.setLeopardLimit(cfgCpy.getmLeopardLimit());
            doc.setEveryLeopardLimit(cfgCpy.getmEveryLeopardLimit());
            doc.setProfitRateCheckIntervaltime(cfgCpy.getmProfitRateCheckIntervaltime());
            doc.setFreeItem(cfgCpy.getmFreeItem());
            doc.setCheckOpenRate( cfgCpy.getmCheckOpenRate() );
            doc.setCanGetExp( cfgCpy.getmCanGetExp() );
            doc.setLeopardLimit(cfgCpy.getmLeopardLimit());
            doc.setEveryLeopardLimit(cfgCpy.getmEveryLeopardLimit());
            doc.setProfitRateCheckIntervaltime(cfgCpy.getmProfitRateCheckIntervaltime());
            doc.setFreeItem(cfgCpy.getmFreeItem());
            doc.setCheckOpenRate(cfgCpy.getmCheckOpenRate());
            doc.setCanGetExp(cfgCpy.getmCanGetExp());
            doc.setMissileCost(cfgCpy.getmMissileCost());
            doc.setBuyPowerCost(cfgCpy.getmBuyPowerCost());
            doc.setPowerParam(cfgCpy.getmPowerParam());

            doc.setLevelCondition(cfgCpy.getmLevelCondition());
            doc.setBotMinGold(cfgCpy.getmBotMaxGold());
            doc.setBotMinGold(cfgCpy.getmBotMinGold());
            doc.setForceLeaveGold(cfgCpy.getmForceLeaveGold());
            doc.setBigBlind(cfgCpy.getmBigBlind());
            doc.setSmallBlind(cfgCpy.getmSmallBlind());
            doc.setCarryRestriction(cfgCpy.getmSmallBlind());
            doc.setCarryRestriction(cfgCpy.getmCarryRestriction());
            doc.setRoomNameType(cfgCpy.getmRoomNameType());
            doc.setHuaGold(cfgCpy.getmHuaGold());
            doc.setTableCount(cfgCpy.getmTableCount());
            doc.setGameModel(cfgCpy.getmGameModel());

            doc.setBetNames(CommonUtils.deepSimpleList(cfgCpy.getmBetNames()));
            doc.setWeightList(CommonUtils.deepSimpleList(cfgCpy.getmWeightList()));
            doc.setBetLimit(CommonUtils.deepSimpleList(cfgCpy.getmBetLimit()));

            doc.setBetRange(CommonUtils.deepSimpleList(cfgCpy.getmBetRange()));
            doc.setPlatList(CommonUtils.deepSimpleList(cfgCpy.getmPlatList()));
            doc.setCustomList(CommonUtils.deepSimpleList(cfgCpy.getmCustomList()));
            doc.setmRatePoolList(CommonUtils.deepSimpleList(cfgCpy.getmRatePoolList()));

            doc.store_game_object( DbGame.Instance.getMongoTemplate(),true);

            m_rooms.put(doc.getRoomId(),doc);

            cfgMap.put(doc.getRoomId(),cfgCpy);

            //create_room_log(agentid, gameId, obj->RoomId->get_value(), obj->TemplateId->get_value());
            //SLOG_CRITICAL << "create default room id:" << obj->RoomId->get_value();
            logger.info("create default room id:",doc.getRoomId());
        }
    }



    public void  set_stock_cfg(RMStockConfig cfg)
    {
        int gameId = GameEngine.Instance.get_gameid();

        for (GameRoomMgrDoc it : m_rooms.values())
        {
            Criteria criteria = Criteria.where("GameId").is(gameId).and("RoomId").is(it.getRoomId());
            GameRoomStockDoc gameRoomStockDoc =  DbGame.Instance.getMongoTemplate().findOne(Query.query(criteria),GameRoomStockDoc.class);

            if (gameRoomStockDoc != null)
            {
                //加载数据库中的数据
                int templateId = gameRoomStockDoc.getTemplateId();
                if (templateId <= 0)
                    templateId = 1;

                RMStockConfigData cfgData = cfg.GetData(templateId);
                if (cfgData == null)
                    continue;

                RMStockConfigData cfgCpy = CommonUtils.deepClone(cfgData);

                cfgCpy.setmRoomID(gameRoomStockDoc.getRoomId());
                cfgCpy.setmBaseStock(gameRoomStockDoc.getBaseStock());
                cfgCpy.setmInitStock(gameRoomStockDoc.getInitStock());
                cfgCpy.setmStockRate(gameRoomStockDoc.getStockRate());
                cfgCpy.setmBrightWater(gameRoomStockDoc.getBrightWater());
                cfgCpy.setmMaxStock(gameRoomStockDoc.getMaxStock());
                cfgCpy.setmRelushTime(gameRoomStockDoc.getRelushTime());
                cfgCpy.setmRoomName(gameRoomStockDoc.getRoomName());

                cfgCpy.setmStockStage(CommonUtils.deepSimpleList(gameRoomStockDoc.getStockStage()));
                cfgCpy.setmStockBuff(gameRoomStockDoc.getStockBuff());
                cfgCpy.setmStrongKill( CommonUtils.deepSimpleList(gameRoomStockDoc.getStrongKill()) );
                cfgCpy.setmWeakKill(CommonUtils.deepSimpleList(gameRoomStockDoc.getWeakKill()));


                Map<Integer, RMStockConfigData> cfgMap = cfg.GetMapData();
                cfgMap.put(cfgCpy.getmRoomID(),cfgCpy);


            }
            else
            {
                //数据库里不存在，写入数据库
                RMStockConfigData pStock = cfg.GetData(it.getTemplateId());
                if (pStock == null)
                {
                    //SLOG_CRITICAL << "set_stock_cfg GetData null RoomId:" << it.second->TemplateId->get_value();
                    continue;
                }

                GameRoomStockDoc gmstockDoc = new GameRoomStockDoc();

                gmstockDoc.setAgentId(it.getAgentId());
                gmstockDoc.setRoomId(it.getRoomId());
                gmstockDoc.setRoomName(pStock.getmRoomName());
                gmstockDoc.setBaseStock(pStock.getmBaseStock());
                gmstockDoc.setInitStock(pStock.getmInitStock());
                gmstockDoc.setStockRate(pStock.getmStockRate());
                gmstockDoc.setBrightWater(pStock.getmBrightWater());
                gmstockDoc.setMaxStock(pStock.getmMaxStock());
                gmstockDoc.setRelushTime(pStock.getmRelushTime());
                gmstockDoc.setTemplateId(pStock.getmRoomID());
                gmstockDoc.setExtKillPer(0);

                gmstockDoc.setStockStage(CommonUtils.deepSimpleList(pStock.getmStockStage()));
                gmstockDoc.setStockBuff(  CommonUtils.deepSimpleList(pStock.getmStockBuff() )  );
                gmstockDoc.setStrongKill(CommonUtils.deepSimpleList(pStock.getmStrongKill()));
                gmstockDoc.setWeakKill( CommonUtils.deepSimpleList(pStock.getmWeakKill()) );


                Criteria criteria1 = Criteria.where("GameId").is(gameId).and("RoomId").is(it.getRoomId());
                DbGame.Instance.getMongoTemplate().updateFirst(Query.query(criteria),Update.fromDocument(gmstockDoc.to_bson(true)),GameRoomStockDoc.class);

                Map<Integer, RMStockConfigData> cfgMap = cfg.GetMapData();
                cfgMap.put( it.getRoomId(),pStock );
            }
        }

    }



    private boolean check_room_config(int agent_id, int gameId, int rid, RMConfig room_cfgs, int template_id) {
        /**
        auto limitcfg = game_engine::instance().get_handler()->getRoomCheckConfig(gameId);
        auto roomcfg = room_cfgs->GetSingleton()->GetData(rid);
        if (limitcfg == NULL)
        {
            SLOG_NOTICE << "check_room_config  game[" << gameId << "] no MainRoomCheckConfig!";
            return true; //没配置，就不检测
        }

        if (check_room_config_vec(roomcfg->mWeightList,
                limitcfg->mWeightList, "mWeightList", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_vec(roomcfg->mBetLimit,
                limitcfg->mBetLimit, "mBetLimit", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_vec(roomcfg->mBetRange,
                limitcfg->mBetRange, "mBetRange", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_vec(roomcfg->mCustomList,
                limitcfg->mCustomList, "mCustomList", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_vec(roomcfg->mPlatList,
                limitcfg->mPlatList, "mPlatList", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_vec(roomcfg->mOdds,
                limitcfg->mOdds, "mOdds", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_vec(roomcfg->mRatePoolList,
                limitcfg->mRatePoolList, "mRatePoolList", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mGoldCondition,
                limitcfg->mGoldCondition, "mGoldCondition", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mBetCondition,
                limitcfg->mBetCondition, "mBetCondition", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mBaseGold,
                limitcfg->mBaseGold, "mBaseGold", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mVipCondition,
                limitcfg->mVipCondition, "mVipCondition", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mTableCount,
                limitcfg->mTableCount, "mTableCount", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mBankerCondition,
                limitcfg->mBankerCondition, "mBankerCondition", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mAutoLeaveBanker,
                limitcfg->mAutoLeaveBanker, "mAutoLeaveBanker", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mLeaveBankerCost,
                limitcfg->mLeaveBankerCost, "mLeaveBankerCost", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mFirstBankerCost,
                limitcfg->mFirstBankerCost, "mFirstBankerCost", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mAddBankerCost,
                limitcfg->mAddBankerCost, "mAddBankerCost", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if ((roomcfg->mFreeGold != 0) && check_room_config_int(roomcfg->mFreeGold,
                limitcfg->mFreeGold, "mFreeGold", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mPlayerMaxCount,
                limitcfg->mPlayerMaxCount, "mPlayerMaxCount", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mCarryRestriction,
                limitcfg->mCarryRestriction, "mCarryRestriction", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mSmallBlind,
                limitcfg->mSmallBlind, "mSmallBlind", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mBigBlind,
                limitcfg->mBigBlind, "mBigBlind", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mForceLeaveGold,
                limitcfg->mForceLeaveGold, "mForceLeaveGold", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mBotMinGold,
                limitcfg->mBotMinGold, "mBotMinGold", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mBotMaxGold,
                limitcfg->mBotMaxGold, "mBotMaxGold", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mRoomType,
                limitcfg->mRoomType, "mRoomType", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mMaxAnte,
                limitcfg->mMaxAnte, "mMaxAnte", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mLevelCondition,
                limitcfg->mLevelCondition, "mLevelCondition", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mPowerParam,
                limitcfg->mPowerParam, "mPowerParam", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mBuyPowerCost,
                limitcfg->mBuyPowerCost, "mBuyPowerCost", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mMissileCost,
                limitcfg->mMissileCost, "mMissileCost", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mCanGetExp,
                limitcfg->mCanGetExp, "mCanGetExp", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mCheckOpenRate,
                limitcfg->mCheckOpenRate, "mCheckOpenRate", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mFreeItem,
                limitcfg->mFreeItem, "mFreeItem", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mEveryLeopardLimit,
                limitcfg->mEveryLeopardLimit, "mEveryLeopardLimit", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mLeopardLimit,
                limitcfg->mLeopardLimit, "mLeopardLimit", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }


        if (check_room_config_int(roomcfg->mProfitRate,
                limitcfg->mProfitRate, "mProfitRate", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mProfitRateCheckIntervaltime,
                limitcfg->mProfitRateCheckIntervaltime, "mProfitRateCheckIntervaltime", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mRoomNameType,
                limitcfg->mRoomNameType, "mRoomNameType", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (check_room_config_int(roomcfg->mHuaGold,
                limitcfg->mHuaGold, "mHuaGold", gameId, rid, template_id, agent_id) != 1)
        {
            return false;
        }

        if (!check_by_game_type(gameId, rid, room_cfgs, template_id, agent_id))
        {
            return false;
        }
        return true;
         **/

        return true;
    }


}
