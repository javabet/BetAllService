package com.wisp.game.bet.db.mongo;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.query.Update;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

public class BaseMongoDoc implements Serializable {

    private Logger logger = LoggerFactory.getLogger(getClass());

    //此字段不需要存入数据库中
    @Transient
    private boolean _update = false;
    @Transient
    private Set<String> updateKeys;
    @Transient
    private Map<String, org.springframework.data.mongodb.core.mapping.Field> allFieldsMap;

    public BaseMongoDoc() {
        updateKeys = new HashSet<>();
        allFieldsMap = new HashMap<>();
    }

    public boolean isUpdate() {
        return _update;
    }

    public void setUpdate(boolean update) {
        this._update = update;
    }

    public void addUpdateKeys(String...keys)
    {
        initFields();

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

    public Document to_bson(boolean to_all)
    {
        return new Document();
    }

    public Document getUpdateDoc()
    {
        return getUpdateDoc(false);
    }

    public Document getUpdateDoc(boolean to_all)
    {
        Update update = new Update();

        if( to_all )
        {

        }
        else
        {
            if( updateKeys.size() == 0 )
            {
                return update.getUpdateObject();
            }

            for( String childKey : updateKeys )
            {
                org.springframework.data.mongodb.core.mapping.Field mongoField = allFieldsMap.get(childKey);
                String fieldName = mongoField.name();
                if( fieldName.equals("") )
                {
                    fieldName = childKey;
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
                if( mongoField.targetType() == FieldType.STRING )
                {
                    update.set(fieldName,1);
                }
                else if(mongoField.targetType() == FieldType.INT32)
                {

                }
                else if(mongoField.targetType() == FieldType.INT64)
                {

                }
                else if(mongoField.targetType() == FieldType.TIMESTAMP)
                {

                }


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
                System.out.printf("fieldName:" + field.getName());
                Annotation[] annotations =  field.getDeclaredAnnotations();
                if( annotations.length == 0 )
                {
                    //写入的数据，需要指定类型
                }
                else
                {

                    Transient transienta =  field.getDeclaredAnnotation(Transient.class);
                    if( transienta != null )
                    {
                        continue;
                    }

                    org.springframework.data.mongodb.core.mapping.Field mongoField = field.getDeclaredAnnotation(org.springframework.data.mongodb.core.mapping.Field.class);

                    if( mongoField == null )
                    {
                        continue;
                    }

                    allFieldsMap.put(field.getName(),mongoField);
                }
            }

            System.out.printf("。。。。");
        }
    }
}
