package com.wisp.game.bet.db.mongo;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.data.mongodb.core.query.Update;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

public class BaseMongoDoc implements Serializable {

    @Transient
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Transient
    private Set<String> updateKeys;
    @Transient
    private Map<String, MongoFieldItem> allFieldsMap;

    public BaseMongoDoc() {
        updateKeys = new HashSet<>();
        allFieldsMap = new HashMap<>();
        initFields();
    }



    public void addUpdateKeys(String...keys)
    {
        for(String childKey : keys)
        {
            if( !allFieldsMap.containsKey(childKey) )
            {
                logger.warn("the " + getClass().getName() +" propertyKey has not the property:" + childKey );
                continue;
            }
            updateKeys.add(childKey);
        }
    }

    public Document to_bson()
    {
        return to_bson(false);
    }


    /**
     * IMPLICIT(-1, Object.class), //
     * 	DOUBLE(1, Double.class), //
     * 	STRING(2, String.class), //
     * 	ARRAY(4, Object[].class), //
     * 	BINARY(5, Binary.class), //
     * 	OBJECT_ID(7, ObjectId.class), //
     * 	BOOLEAN(8, Boolean.class), //
     * 	DATE_TIME(9, Date.class), //
     * 	PATTERN(11, Pattern.class), //
     * 	SCRIPT(13, Code.class), //
     * 	INT32(15, Integer.class), //
     * 	TIMESTAMP(16, BSONTimestamp.class), //
     * 	INT64(17, Long.class), //
     * 	DECIMAL128(18, Decimal128.class);
     */
    public Document to_bson(boolean to_all)
    {
        Update update = new Update();

        if( to_all )
        {
            updateKeys.addAll( allFieldsMap.keySet() );
        }

        if( updateKeys.size() == 0 )
        {
            return update.getUpdateObject();
        }

        for( String childKey : updateKeys )
        {
            MongoFieldItem mongoFieldItem = allFieldsMap.get(childKey);
            try
            {
                Object valObj =  mongoFieldItem.field.get(this);
                if( valObj == null )
                {
                    continue;
                }

                if( mongoFieldItem.fieldType == FieldType.STRING)
                {
                    update.set(mongoFieldItem.mongoFieldName,valObj);
                }
                else if(mongoFieldItem.fieldType == FieldType.INT32 ||
                        mongoFieldItem.fieldType == FieldType.BOOLEAN ||
                        mongoFieldItem.fieldType == FieldType.DOUBLE ||
                        mongoFieldItem.fieldType == FieldType.INT64 ||
                        mongoFieldItem.fieldType == FieldType.TIMESTAMP)
                {
                    update.set(mongoFieldItem.mongoFieldName,valObj);
                }
                else if( mongoFieldItem.fieldType == FieldType.DATE_TIME )
                {
                    if( mongoFieldItem.field.getType().equals(Date.class) )
                    {
                        update.set(mongoFieldItem.mongoFieldName,valObj);
                    }
                    else if( mongoFieldItem.field.getType().equals(Integer.class) || mongoFieldItem.field.getType().equals(int.class))
                    {
                        Date date = new Date();
                        date.setTime( mongoFieldItem.field.getInt(this) * 1000 );
                        update.set(mongoFieldItem.mongoFieldName,date);
                    }
                    else if( mongoFieldItem.field.getType().equals(Long.class) || mongoFieldItem.field.getType().equals(long.class) )
                    {
                        Date date = new Date();
                        date.setTime( (Long)valObj );
                        update.set(mongoFieldItem.mongoFieldName,date);
                    }
                }
                else if( mongoFieldItem.fieldType == FieldType.ARRAY)
                {
                    update.set(mongoFieldItem.mongoFieldName,valObj);
                }
                else
                {
                    update.set(mongoFieldItem.mongoFieldName,valObj);
                }
            }
            catch (IllegalAccessException ex)
            {
                logger.error("the field get error:" + mongoFieldItem.fieldName);
            }
        }


        return update.getUpdateObject();
    }

    private void initFields()
    {
        if( allFieldsMap.size() == 0 )
        {
            Class<?> clz = getClass();
            Field[] fields = clz.getDeclaredFields();

            for(Field field : fields)
            {
                Transient transienta =  field.getDeclaredAnnotation(Transient.class);
                if( transienta != null )
                {
                    continue;
                }

                field.setAccessible(true);

                org.springframework.data.mongodb.core.mapping.Field mongoField = field.getDeclaredAnnotation(org.springframework.data.mongodb.core.mapping.Field.class);
                MongoId mongoId = field.getDeclaredAnnotation(MongoId.class);
                MongoFieldItem mongoFieldItem = new MongoFieldItem();
                mongoFieldItem.fieldName = field.getName();
                mongoFieldItem.field = field;

                if(mongoField != null || mongoId != null)
                {
                    if( mongoField != null )
                    {
                        mongoFieldItem.mongoFieldName = mongoField.name();
                        if("".equals(mongoFieldItem.mongoFieldName)  )
                        {
                            mongoFieldItem.mongoFieldName = mongoFieldItem.fieldName;
                        }
                        mongoFieldItem.fieldType = mongoField.targetType();
                        mongoFieldItem.isMongoId = false;
                    }
                    else
                    {
                        mongoFieldItem.mongoFieldName = field.getName();
                        mongoFieldItem.fieldType = mongoId.targetType();
                        mongoFieldItem.isMongoId = true;
                    }
                }
                else
                {
                    //写入的数据，需要指定类型
                    mongoFieldItem.mongoFieldName = field.getName();
                    mongoFieldItem.fieldType = getFieldTypeByDefault( field );
                    mongoFieldItem.isMongoId = false;
                }
                allFieldsMap.put(field.getName(),mongoFieldItem);
            }

        }
    }

    private FieldType getFieldTypeByDefault(Field field)
    {
        Class<?> clz =  field.getType();

        if( clz.equals(String.class) )
        {
            return FieldType.STRING;
        }
        else if( clz.equals(Integer.class) || clz.equals(int.class) )
        {
            return FieldType.INT32;
        }
        else if( clz.equals(Long.class) || clz.equals(long.class) )
        {
            return FieldType.INT64;
        }
        else if( clz.equals(List.class) )
        {
            return FieldType.ARRAY;
        }
        else if( clz.equals(double.class) || clz.equals(Double.class) )
        {
            return FieldType.DOUBLE;
        }
        else if( clz.equals(Date.class) )
        {
            return FieldType.DATE_TIME;
        }
        else if( clz.equals(Boolean.class) || clz.equals(boolean.class) )
        {
            return FieldType.BOOLEAN;
        }
        else
        {
            //logger.warn("the data is not exist,fieldName:" + field.getName());
        }

        return null;
    }



    public class MongoFieldItem
    {
        private String fieldName;       //当前的类里的字段名字
        private String mongoFieldName;  //当前数据库对应的字段的名字
        private FieldType fieldType;        //当前的数据库字段对应的数据类型
        private Field field;
        private boolean isMongoId;
    }

}
