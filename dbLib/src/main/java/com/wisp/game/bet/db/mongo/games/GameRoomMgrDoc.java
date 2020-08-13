package com.wisp.game.bet.db.mongo.games;

import com.mongodb.client.result.UpdateResult;
import com.wisp.game.bet.db.mongo.BaseMongoDoc;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "GameRoomMgr")
public class GameRoomMgrDoc extends BaseMongoDoc {
    @Field(targetType = FieldType.OBJECT_ID) private String Uid;
    private long AddBankerCost;
    private int AgentId;
    private long AutoLeaveBanker;
    private long BankerCondition;
    private long BaseGold;
    private long BetCondition;
    private List<Integer> BetLimit;
    private List<String> BetNames;
    private List<Integer> BetRange;
    @Field(targetType = FieldType.INT32) private int BigBlind;
    @Field(targetType = FieldType.INT32) private int BotMaxGold;
    @Field(targetType = FieldType.INT32) private int BotMinGold;
    @Field(targetType = FieldType.INT32) private int BuyPowerCost;
    private boolean CanGetExp;
    @Field(targetType = FieldType.INT32) private int CarryRestriction;
    private boolean CheckOpenRate;
    private List<Integer> CustomList;

    @Field(targetType = FieldType.INT32) private int EveryLeopardLimit;
    @Field(targetType = FieldType.INT64) private long FirstBankerCost;
    @Field(targetType = FieldType.INT32) private int ForceLeaveGold;
    @Field(targetType = FieldType.INT64) private long FreeGold;
    private boolean FreeItem;
    @Field(targetType = FieldType.INT32) private int GameId;
    private String GameModel;
    @Field(targetType = FieldType.INT64) private long GoldCondition;
    @Field(targetType = FieldType.INT32) private int HuaGold;
    private boolean IsOpen;
    @Field(targetType = FieldType.INT32) private int LeopardLimit;
    @Field(targetType = FieldType.INT32) private int LevelCondition;
    @Field(targetType = FieldType.INT32) private int MaxAnte;
    @Field(targetType = FieldType.INT32) private int MissileCost;
    private List<Integer> PlatList;
    @Field(targetType = FieldType.INT64) private long PlayerMaxCount;
    @Field(targetType = FieldType.INT32) private int PowerParam;
    @Field(targetType = FieldType.INT32) private int ProfitRateCheckIntervaltime;
    private String RoomIDTxt;
    @Field(targetType = FieldType.INT32) private int RoomId;
    private String RoomName;
    @Field(targetType = FieldType.INT32) private int RoomNameType;
    @Field(targetType = FieldType.INT32) private int RoomType;
    @Field(targetType = FieldType.INT32) private int ServerId;
    @Field(targetType = FieldType.INT32) private int SmallBlind;
    @Field(targetType = FieldType.INT32) private int Status;
    @Field(targetType = FieldType.INT32) private int TableCount;
    @Field(targetType = FieldType.INT32) private int TemplateId;
    private List<Integer> WeightList;
    private List<Integer> mRatePoolList;

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public long getAddBankerCost() {
        return AddBankerCost;
    }

    public void setAddBankerCost(long addBankerCost) {
        AddBankerCost = addBankerCost;
    }

    public int getAgentId() {
        return AgentId;
    }

    public void setAgentId(int agentId) {
        AgentId = agentId;
    }

    public long getAutoLeaveBanker() {
        return AutoLeaveBanker;
    }

    public void setAutoLeaveBanker(long autoLeaveBanker) {
        AutoLeaveBanker = autoLeaveBanker;
    }

    public long getBankerCondition() {
        return BankerCondition;
    }

    public void setBankerCondition(long bankerCondition) {
        BankerCondition = bankerCondition;
    }

    public long getBaseGold() {
        return BaseGold;
    }

    public void setBaseGold(long baseGold) {
        BaseGold = baseGold;
    }

    public long getBetCondition() {
        return BetCondition;
    }

    public void setBetCondition(long betCondition) {
        BetCondition = betCondition;
    }

    public List<Integer> getBetLimit() {
        return BetLimit;
    }

    public void setBetLimit(List<Integer> betLimit) {
        BetLimit = betLimit;
    }

    public List<String> getBetNames() {
        return BetNames;
    }

    public void setBetNames(List<String> betNames) {
        BetNames = betNames;
    }

    public List<Integer> getBetRange() {
        return BetRange;
    }

    public void setBetRange(List<Integer> betRange) {
        BetRange = betRange;
    }

    public int getBigBlind() {
        return BigBlind;
    }

    public void setBigBlind(int bigBlind) {
        BigBlind = bigBlind;
    }

    public int getBotMaxGold() {
        return BotMaxGold;
    }

    public void setBotMaxGold(int botMaxGold) {
        BotMaxGold = botMaxGold;
    }

    public int getBotMinGold() {
        return BotMinGold;
    }

    public void setBotMinGold(int botMinGold) {
        BotMinGold = botMinGold;
    }

    public int getBuyPowerCost() {
        return BuyPowerCost;
    }

    public void setBuyPowerCost(int buyPowerCost) {
        BuyPowerCost = buyPowerCost;
    }

    public boolean isCanGetExp() {
        return CanGetExp;
    }

    public void setCanGetExp(boolean canGetExp) {
        CanGetExp = canGetExp;
    }

    public int getCarryRestriction() {
        return CarryRestriction;
    }

    public void setCarryRestriction(int carryRestriction) {
        CarryRestriction = carryRestriction;
    }

    public boolean isCheckOpenRate() {
        return CheckOpenRate;
    }

    public void setCheckOpenRate(boolean checkOpenRate) {
        CheckOpenRate = checkOpenRate;
    }

    public List<Integer> getCustomList() {
        return CustomList;
    }

    public void setCustomList(List<Integer> customList) {
        CustomList = customList;
    }

    public int getEveryLeopardLimit() {
        return EveryLeopardLimit;
    }

    public void setEveryLeopardLimit(int everyLeopardLimit) {
        EveryLeopardLimit = everyLeopardLimit;
    }

    public long getFirstBankerCost() {
        return FirstBankerCost;
    }

    public void setFirstBankerCost(long firstBankerCost) {
        FirstBankerCost = firstBankerCost;
    }

    public int getForceLeaveGold() {
        return ForceLeaveGold;
    }

    public void setForceLeaveGold(int forceLeaveGold) {
        ForceLeaveGold = forceLeaveGold;
    }

    public long getFreeGold() {
        return FreeGold;
    }

    public void setFreeGold(long freeGold) {
        FreeGold = freeGold;
    }

    public boolean isFreeItem() {
        return FreeItem;
    }

    public void setFreeItem(boolean freeItem) {
        FreeItem = freeItem;
    }

    public int getGameId() {
        return GameId;
    }

    public void setGameId(int gameId) {
        GameId = gameId;
    }

    public String getGameModel() {
        return GameModel;
    }

    public void setGameModel(String gameModel) {
        GameModel = gameModel;
    }

    public long getGoldCondition() {
        return GoldCondition;
    }

    public void setGoldCondition(long goldCondition) {
        GoldCondition = goldCondition;
    }

    public int getHuaGold() {
        return HuaGold;
    }

    public void setHuaGold(int huaGold) {
        HuaGold = huaGold;
    }

    public boolean isOpen() {
        return IsOpen;
    }

    public void setOpen(boolean open) {
        IsOpen = open;
    }

    public int getLeopardLimit() {
        return LeopardLimit;
    }

    public void setLeopardLimit(int leopardLimit) {
        LeopardLimit = leopardLimit;
    }

    public int getLevelCondition() {
        return LevelCondition;
    }

    public void setLevelCondition(int levelCondition) {
        LevelCondition = levelCondition;
    }

    public int getMaxAnte() {
        return MaxAnte;
    }

    public void setMaxAnte(int maxAnte) {
        MaxAnte = maxAnte;
    }

    public int getMissileCost() {
        return MissileCost;
    }

    public void setMissileCost(int missileCost) {
        MissileCost = missileCost;
    }

    public List<Integer> getPlatList() {
        return PlatList;
    }

    public void setPlatList(List<Integer> platList) {
        PlatList = platList;
    }

    public long getPlayerMaxCount() {
        return PlayerMaxCount;
    }

    public void setPlayerMaxCount(long playerMaxCount) {
        PlayerMaxCount = playerMaxCount;
    }

    public int getPowerParam() {
        return PowerParam;
    }

    public void setPowerParam(int powerParam) {
        PowerParam = powerParam;
    }

    public int getProfitRateCheckIntervaltime() {
        return ProfitRateCheckIntervaltime;
    }

    public void setProfitRateCheckIntervaltime(int profitRateCheckIntervaltime) {
        ProfitRateCheckIntervaltime = profitRateCheckIntervaltime;
    }

    public String getRoomIDTxt() {
        return RoomIDTxt;
    }

    public void setRoomIDTxt(String roomIDTxt) {
        RoomIDTxt = roomIDTxt;
    }

    public int getRoomId() {
        return RoomId;
    }

    public void setRoomId(int roomId) {
        RoomId = roomId;
    }

    public String getRoomName() {
        return RoomName;
    }

    public void setRoomName(String roomName) {
        RoomName = roomName;
    }

    public int getRoomNameType() {
        return RoomNameType;
    }

    public void setRoomNameType(int roomNameType) {
        RoomNameType = roomNameType;
    }

    public int getRoomType() {
        return RoomType;
    }

    public void setRoomType(int roomType) {
        RoomType = roomType;
    }

    public int getServerId() {
        return ServerId;
    }

    public void setServerId(int serverId) {
        ServerId = serverId;
    }

    public int getSmallBlind() {
        return SmallBlind;
    }

    public void setSmallBlind(int smallBlind) {
        SmallBlind = smallBlind;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public int getTableCount() {
        return TableCount;
    }

    public void setTableCount(int tableCount) {
        TableCount = tableCount;
    }

    public int getTemplateId() {
        return TemplateId;
    }

    public void setTemplateId(int templateId) {
        TemplateId = templateId;
    }

    public List<Integer> getWeightList() {
        return WeightList;
    }

    public void setWeightList(List<Integer> weightList) {
        WeightList = weightList;
    }


    public List<Integer> getmRatePoolList() {
        return mRatePoolList;
    }

    public void setmRatePoolList(List<Integer> mRatePoolList) {
        this.mRatePoolList = mRatePoolList;
    }

    @Override
    protected GameRoomMgrDoc clone() throws CloneNotSupportedException {

        GameRoomMgrDoc doc = new GameRoomMgrDoc();
        doc.setAddBankerCost(this.getAddBankerCost());
        doc.setAgentId(AgentId);
        doc.setAutoLeaveBanker(this.getAutoLeaveBanker());
        doc.setBankerCondition(this.getBankerCondition());
        doc.setBaseGold( this.getBaseGold() );
        doc.setBetCondition(this.getBetCondition());
        doc.setBetLimit( cloneList(this.getBetLimit()) );
        doc.setBetNames(cloneList(this.getBetNames()));
        doc.setBetRange(cloneList(this.getBetRange()));
        doc.setBigBlind(getBigBlind());
        doc.setBotMaxGold(getBotMaxGold());
        doc.setBotMinGold(getBotMinGold());
        doc.setBuyPowerCost(getBuyPowerCost());
        doc.setCanGetExp(isCanGetExp());
        doc.setCarryRestriction(getCarryRestriction());
        doc.setCheckOpenRate(isCheckOpenRate());
        doc.setCustomList(cloneList(getCustomList()));
        doc.setEveryLeopardLimit(getEveryLeopardLimit());
        doc.setFirstBankerCost(getFirstBankerCost());
        doc.setForceLeaveGold(getForceLeaveGold());
        doc.setFreeGold(getFreeGold());
        doc.setFreeItem(isFreeItem());
        doc.setGameId(getGameId());
        doc.setGameModel(getGameModel());
        doc.setGoldCondition(getGoldCondition());
        doc.setHuaGold(getHuaGold());
        doc.setOpen(isOpen());
        doc.setLeopardLimit(getLeopardLimit());
        doc.setLevelCondition(getLevelCondition());
        doc.setMaxAnte(getMaxAnte());
        doc.setMissileCost(getMissileCost());
        doc.setPlatList(cloneList(getPlatList()));
        doc.setPlayerMaxCount(getPlayerMaxCount());
        doc.setPowerParam(getPowerParam());
        doc.setProfitRateCheckIntervaltime(getProfitRateCheckIntervaltime());
        doc.setRoomIDTxt(getRoomIDTxt());
        doc.setRoomId(getRoomId());
        doc.setRoomName(getRoomName());
        doc.setRoomNameType(getRoomNameType());
        doc.setRoomType(getRoomType());
        doc.setServerId(getServerId());
        doc.setSmallBlind(getSmallBlind());
        doc.setStatus(getStatus());
        doc.setTableCount(getTableCount());
        doc.setTemplateId(getTemplateId());
        doc.setWeightList(cloneList(getWeightList()));

        return doc;
    }

    private <T> List<T> cloneList(List<T> source)
    {
        List<T> list = new ArrayList<>();
        for(int i = 0; i < source.size();i++)
        {
            list.add(source.get(i));
        }

        return list;
    }

    public boolean store_game_object( MongoTemplate mongoTemplate )
    {
        return store_game_object(mongoTemplate,false);
    }

    public boolean store_game_object(MongoTemplate mongoTemplate, boolean to_all )
    {
        org.bson.Document doc = to_bson(to_all);
        if( doc.isEmpty() )
        {
            return true;
        }

        Criteria criteria = Criteria.where("Uid").is(Uid);

        UpdateResult updateResult = mongoTemplate.updateFirst( Query.query(criteria), Update.fromDocument(doc),getClass());

        if( updateResult.getModifiedCount() < 1 )
        {
            logger.error("game_room_object::store_game_object :");
            return false;
        }

        return true;
    }
}
