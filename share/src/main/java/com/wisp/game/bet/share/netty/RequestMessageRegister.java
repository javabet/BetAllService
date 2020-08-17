package com.wisp.game.bet.share.netty;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.wisp.game.bet.share.common.ClassScanner;
import com.wisp.game.bet.share.utils.ProtocolClassUtils;
import com.wisp.game.bet.core.SpringContextHolder;
import com.wisp.game.bet.share.netty.PacketManager.IRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
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
@DependsOn({"springContextHolder"})
public class RequestMessageRegister implements InitializingBean {

    @Autowired
    private SpringContextHolder springContextHolder;

    private Logger logger = LoggerFactory.getLogger(getClass());
    //消息号与处理函数相关的处理
    private ConcurrentHashMap<Integer,ProtocolStruct> classConcurrentHashMap = new ConcurrentHashMap<>();

    public static RequestMessageRegister Instance;

    public RequestMessageRegister() {
        Instance = this;
    }

    public void afterPropertiesSet() throws Exception
    {
        //String requestMessageName = IRequestMessage.class.getName();

        Set<Class<?>> clzSet = ClassScanner.listClassesWithAnnotation("com.wisp.game",IRequest.class);
        Iterator<Class<?>> clzIt =  clzSet.iterator();

        while (clzIt.hasNext())
        {
            Class<?> clz =  clzIt.next();

            boolean isRequestMessageFlag =  isHertFromIRequestMessage(clz);

            if( !isRequestMessageFlag )
            {
                logger.error("the class is not IRequestMessage:" + clz.getName());
                continue;
            }

           Type parentType = clz.getGenericSuperclass();
            ParameterizedType p=(ParameterizedType)parentType;

            Class findTypeClass =(Class) p.getActualTypeArguments()[0];

            int protocolId = ProtocolClassUtils.getProtocolByClass(findTypeClass);

            if( protocolId == -1 )
            {
                logger.error("parse the protocol has error,the clz:" + findTypeClass);
                continue;
            }

//            boolean debug = true;
//            if(debug)
//            {
//                continue;
//            }


            ProtocolStruct protocolStruct = new ProtocolStruct();
            protocolStruct.setProtocolId(protocolId);
            protocolStruct.setProtocolCls(findTypeClass);
            protocolStruct.handlerInstance = (IRequestMessage)SpringContextHolder.getBean( clz ); //(IRequestMessage) clz.newInstance();


            try
            {
//                Method parseFromMethod = findTypeClass.getMethod("parseFrom",byte[].class);
//                protocolStruct.setStaticByteArrParseFromMethod(parseFromMethod);

                Method parseByteStringFromMethod = findTypeClass.getMethod("parseFrom",ByteString.class);
                protocolStruct.setStaticByteStringParseFromMethod(parseByteStringFromMethod);
            }
            catch (Exception exception)
            {
                logger.error("the parseFrom has error,the procolId:" + protocolId);
                continue;
            }

            if( classConcurrentHashMap.containsKey(protocolId) )
            {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("the repeated the protocolId:").append(protocolId);
                stringBuilder.append(" handlerClz:").append( clz.getName());
                stringBuilder.append(" oldHandlerClz:").append( classConcurrentHashMap.get(protocolId).handlerInstance.getClass().getName() );
                logger.error( stringBuilder.toString() );
                continue;
            }

            classConcurrentHashMap.put(protocolId,protocolStruct);
        }

        logger.info("init the all protocols");
    }

    private boolean isHertFromIRequestMessage(Class<?> clz)
    {
        if( clz.getSuperclass() == null )
        {
            return false;
        }

        Type[] types = clz.getGenericInterfaces();

        if( types.length == 0 )
        {
            return isHertFromIRequestMessage(clz.getSuperclass());
        }

        for(int i = 0; i < types.length;i++)
        {
            Type type = types[i];

            if( !(type instanceof ParameterizedType ))
            {
                continue;
            }

            Type rawType = ((ParameterizedType) type).getRawType();

            if( rawType.getTypeName() == IRequestMessage.class.getName() )
            {
                return true;
            }
        }

        return isHertFromIRequestMessage( clz.getSuperclass() );
    }

    /**
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
    **/

    public Message getMessageByProtocolId( int protocolId, ByteString byteString )
    {
        if( !classConcurrentHashMap.containsKey(protocolId) )
        {
            return null;
        }

        ProtocolStruct protocolStruct = classConcurrentHashMap.get(protocolId);

        Object object = null;

        try
        {
            object = protocolStruct.getStaticByteStringParseFromMethod().invoke(null,byteString);
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


        private Class<? extends Message> protocolCls;      //需要处理的消息号的protocol的class

        private IRequestMessage handlerInstance; //需要处理消息号的实体对象

        //private Method staticByteArrParseFromMethod; //静态Byte[]的ParseFrom函数
        private Method staticByteStringParseFromMethod; //静态Byte[]的ParseFrom函数

        public int getProtocolId() {
            return ProtocolId;
        }

        public void setProtocolId(int protocolId) {
            ProtocolId = protocolId;
        }

        public Class<? extends Message> getProtocolCls() {
            return protocolCls;
        }

        public void setProtocolCls(Class<? extends Message> protocolCls) {
            this.protocolCls = protocolCls;
        }

        public IRequestMessage getHandlerInstance() {
            return handlerInstance;
        }

        public void setHandlerInstance(IRequestMessage handlerInstance) {
            this.handlerInstance = handlerInstance;
        }

//        public Method getStaticByteArrParseFromMethod() {
//            return staticByteArrParseFromMethod;
//        }
//
//        public void setStaticByteArrParseFromMethod(Method staticByteArrParseFromMethod) {
//            this.staticByteArrParseFromMethod = staticByteArrParseFromMethod;
//        }

        public Method getStaticByteStringParseFromMethod() {
            return staticByteStringParseFromMethod;
        }

        public void setStaticByteStringParseFromMethod(Method staticByteStringParseFromMethod) {
            this.staticByteStringParseFromMethod = staticByteStringParseFromMethod;
        }
    }
}


