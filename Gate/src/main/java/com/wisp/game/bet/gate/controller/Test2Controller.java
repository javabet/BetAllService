package com.wisp.game.bet.gate.controller;

import client2gate_protocols.Client2GateProtocol;
import client2world_protocols.Client2WorldProtocol;
import com.wisp.game.bet.gate.unit.ClientManager;
import com.wisp.game.bet.gate.unit.GatePeer;
import com.wisp.game.bet.db.mongo.account.info.AccountTableInfo;
import com.wisp.game.bet.db.mongo.account.service.AccountTableServiceImpl;
import com.wisp.game.bet.db.mongo.config.info.ServerList;
import com.wisp.game.bet.db.mongo.config.service.ServerListImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class Test2Controller {

    @Autowired(required = false)
    private AccountTableServiceImpl accountTableService;

    @Autowired(required = false)
    private ServerListImpl serverListService;

    @RequestMapping("/test")
    public String Index()
    {
        return "i love you more than i can say";
    }



    @RequestMapping("/map")
    public Object IndexMap()
    {
        Map<String,String> map = new ConcurrentHashMap<String, String>();
        map.put("abc","def");

        AccountTableInfo info =  accountTableService.findByAccount("web_225_a811409be73c48eaa014d8767fb8c848");

        List<ServerList> list = serverListService.findAll();

        return list;
    }

    @RequestMapping("/send_msg")
    public Object sendMsg()
    {
        Collection<GatePeer>  gatePeers = ClientManager.Instance.get_map().values();
        Map<String,Object> map = new HashMap<String, Object>();
        if( gatePeers.size() <= 0 )
        {
            map.put("abc","nothing");
            return map;
        }
        GatePeer gatePeer =  gatePeers.iterator().next();

        Client2GateProtocol.packetcg2cg_start.Builder builder = Client2GateProtocol.packetcg2cg_start.newBuilder();
        gatePeer.send_msg(builder.build());

        Client2WorldProtocol.packetc2w_player_connect.Builder builder1 = Client2WorldProtocol.packetc2w_player_connect.newBuilder();
        builder1.setAccount("gothuis");
        builder1.setToken("tokebn");
        builder1.setSign("sign");
        builder1.setPlatform("platform");
        builder1.setMachineCode("1");
        builder1.setMachineType("2");
        builder1.setChannelid(11);
        gatePeer.send_msg(builder1.build());

        map.put("success","ok");
        map.put("peerId:",gatePeer.get_id());
        map.put("channellId",gatePeer.getChannelId().asLongText());
        return  map;
    }

}
