package com.wisp.game.bet.monitor.controller;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.wisp.game.bet.share.netty.RequestMessageRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server_protocols.ServerBase;
import server_protocols.ServerProtocol;

@RestController
public class TestController {

    @Autowired
    public RequestMessageRegister messageRegister;

    @RequestMapping("/monitor/test")
    public Object test()
    {
        ServerProtocol.packet_server_register.Builder register = ServerProtocol.packet_server_register.newBuilder();
        register.setServerPort(1000);
        register.setServerType(ServerBase.e_server_type.e_st_gate);

        ByteString byteString = register.build().toByteString();
        byte[] bytes = byteString.toByteArray();

        Message message1 = messageRegister.getMessageByProtocolId(0,bytes);

        RequestMessageRegister.ProtocolStruct protocolStruct = messageRegister.getProtocolStruct(0);

        try
        {
            /**
            IRequestMessage requestMessage = (IRequestMessage)(protocolStruct.getHandlerCls().newInstance());


            Method[] methods = ServerProtocol.packet_server_register.class.getMethods();

            for( Method method : methods)
            {
                System.out.printf("method:" + method.getName() + "\n");
            }

            Method parseFromMethod0 =  ServerProtocol.packet_server_register.class.getMethod("parseFrom", ByteString.class);

            Method parseFromMethod =  protocolStruct.getProtocolCls().getMethod("parseFrom",ByteString.class);

            Object obj = parseFromMethod.invoke(null,byteString);

            Method parseFromMethod2 =  protocolStruct.getProtocolCls().getMethod("parseFrom",byte[].class);

            Object obj1 = parseFromMethod2.invoke(null,bytes);
             **/

            System.out.printf("obj");
        }
        catch (Exception ex)
        {

        }



        return "lo";

    }
}
