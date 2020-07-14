package com.wisp.game.share.netty;

import com.google.protobuf.Message;
import com.wisp.game.share.common.ClassScanner;
import com.wisp.game.share.netty.PacketManager.IRequestMessage;
import com.wisp.game.share.utils.ProtocolClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 收集消息号相关
 */
@Component
public class RequestMessageRegister implements InitializingBean {

    private Logger logger = LoggerFactory.getLogger(getClass());
    //消息号与处理函数相关的处理
    private ConcurrentHashMap<Integer,ProtocolStruct> classConcurrentHashMap = new ConcurrentHashMap<>();

    public static RequestMessageRegister Instance;

    public RequestMessageRegister() {
        Instance = this;
    }

    public void afterPropertiesSet() throws Exception
    {
        String requestMessageName = IRequestMessage.class.getName();

        Set<Class<?>> clzSet = ClassScanner.listClassesWithAnnotation("com.wisp.game",IRequest.class);
        Iterator<Class<?>> clzIt =  clzSet.iterator();

        while (clzIt.hasNext())
        {
            Class<?> clz =  clzIt.next();
            Class<?>[] interfaces =  clz.getInterfaces();
            if( interfaces.length == 0 )
            {
                logger.error("the request has not the protocol class");
                continue;
            }

            boolean isRequestMessage = false;
            for(Class interfaceClz : interfaces)
            {
                if( interfaceClz.getName() == requestMessageName )
                {
                    isRequestMessage = true;
                    break;
                }
            }

            if( !isRequestMessage )
            {
                logger.error("the class is not implement the IRequestMessage");
                continue;
            }


            Type[] types =  clz.getGenericInterfaces();

            Class findTypeClass = null;
            for( java.lang.reflect.Type type : types)
            {
                if( !(type instanceof ParameterizedType))
                {
                    continue;
                }

                Type rType = ((ParameterizedType) type).getRawType();
                java.lang.reflect.Type[] types2=((ParameterizedType) type).getActualTypeArguments();

                Class<?> clzType = (Class<?>) types2[0];

                findTypeClass = clzType;
            }

            if( findTypeClass == null )
            {
                continue;
            }

            int protocolId = ProtocolClassUtils.getProtocolByClass(findTypeClass);

            if( protocolId == -1 )
            {
                logger.error("parse the protocol has error");
                continue;
            }

            ProtocolStruct protocolStruct = new ProtocolStruct();
            protocolStruct.setProtocolId(protocolId);
            protocolStruct.setHandlerCls(clz);
            protocolStruct.setProtocolCls(findTypeClass);
            protocolStruct.handlerInstance = clz.newInstance();

            try
            {
                Method parseFromMethod = findTypeClass.getMethod("parseFrom",byte[].class);
                protocolStruct.setStaticByteArrParseFromMethod(parseFromMethod);
            }
            catch (Exception exception)
            {
                logger.error("the parseFrom has error,the procolId:" + protocolId);
                continue;
            }

            classConcurrentHashMap.put(protocolId,protocolStruct);
        }
    }


    public Message getMessageByProtocolId( int protocolId,byte[] bytes )
    {
        if( !classConcurrentHashMap.containsKey(protocolId) )
        {
            return null;
        }

        ProtocolStruct protocolStruct = classConcurrentHashMap.get(protocolId);

        Object object = null;

        try
        {
            object = protocolStruct.getStaticByteArrParseFromMethod().invoke(null,bytes);
        }
        catch (Exception exception)
        {
            logger.error("getMessageByProtocolId has error ,the protocolId:" + protocolId);
        }

        if( object == null )
        {
            return null;
        }


        return (Message)object;
    }

    //获取当前的
    public ProtocolStruct getProtocolStruct(Integer protocolId)
    {
        if( classConcurrentHashMap.containsKey(protocolId) )
        {
            return classConcurrentHashMap.get(protocolId);
        }

        return null;
    }


    public class ProtocolStruct
    {
        private int ProtocolId;         //需要处理的消息号

        private Class handlerCls;       //处理此消息号的对象的cls

        private Class protocolCls;      //需要处理的消息号的protocol的class

        private Object handlerInstance; //需要处理消息号的实体对象

        private Method staticByteArrParseFromMethod; //静态Byte[]的ParseFrom函数

        public int getProtocolId() {
            return ProtocolId;
        }

        public void setProtocolId(int protocolId) {
            ProtocolId = protocolId;
        }

        public Class getHandlerCls() {
            return handlerCls;
        }

        public void setHandlerCls(Class handlerCls) {
            this.handlerCls = handlerCls;
        }

        public Class getProtocolCls() {
            return protocolCls;
        }

        public void setProtocolCls(Class protocolCls) {
            this.protocolCls = protocolCls;
        }

        public Object getHandlerInstance() {
            return handlerInstance;
        }

        public void setHandlerInstance(Object handlerInstance) {
            this.handlerInstance = handlerInstance;
        }


        public Method getStaticByteArrParseFromMethod() {
            return staticByteArrParseFromMethod;
        }

        public void setStaticByteArrParseFromMethod(Method staticByteArrParseFromMethod) {
            this.staticByteArrParseFromMethod = staticByteArrParseFromMethod;
        }
    }
}


