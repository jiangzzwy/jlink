package com.jlink.search.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
@Slf4j
public class FulltextXContentBuilderUtils {
    public static  void buildContent(Object objectValue ,XContentBuilder xContentBuilder) throws IOException {
        Method[] declaredMethods = objectValue.getClass().getMethods();
        List<Method> methods = Arrays.stream(declaredMethods)
                .filter(m -> m.getName().startsWith("get") && !m.getName().equals("getClass"))
                .collect(Collectors.toList());
        xContentBuilder.startObject();
        for (Method method : methods) {
            String key=method.getName().substring(3);
            String fieldName=new StringBuilder().append(Character.toLowerCase(key.charAt(0))).append(key.substring(1)).toString();
            try {
                Object v = method.invoke(objectValue);
                if(v!=null){
                    //如果有内嵌List属性
                    if(v instanceof List  && ((List<?>) v).size()>0 && !isPrimitive((List)v) ){
                        List listValue= (List) v;
                        Object o1 = listValue.get(0);
                        xContentBuilder.startArray(fieldName);
                        for (Object o : listValue) {
                            buildContent(o,xContentBuilder);
                        }
                        xContentBuilder.endArray();
                    }else if(v instanceof Map){//内嵌Map属性
                        Map<String,Object> mapValue= (Map) v;
                        xContentBuilder.startObject(fieldName);
                        Set<String> keys = mapValue.keySet();
                        for (String mk : keys) {
                            Object mv = mapValue.get(mk);
                            if(mv!=null){
                                if(mv instanceof List){
                                    xContentBuilder.startArray(mk);
                                    buildContent(mv,xContentBuilder);
                                    xContentBuilder.endArray();
                                }else{
                                    xContentBuilder.field(mk,mv.toString());
                                }
                            }
                        }
                        xContentBuilder.endObject();
                    }else{//基本属性
                        if(v instanceof String){ //如果是 空字符串，直接忽略
                            if(StringUtils.isNotBlank((String) v)){
                                xContentBuilder.field(fieldName,v);
                            }
                        }else{
                            xContentBuilder.field(fieldName,v);
                        }

                    }
                }

            } catch (Exception e) {
                log.error("将实体对象构建成XContentBuilder对象出现错误，错误信息-->{}",e.getMessage());
                e.printStackTrace();
            }
        }
        xContentBuilder.endObject();
    }
    private static boolean isPrimitive(List v){
        Object o = v.get(0);
        return o instanceof String || o instanceof Boolean
                || o  instanceof Long || o instanceof Float
                || o instanceof Double || o instanceof Character
                || o instanceof Byte || o instanceof Short;

    }
    private static boolean isNumberic(Object o){
        return  o  instanceof Long || o instanceof Float
                || o instanceof Double || o instanceof Integer || o instanceof Short;

    }
}
