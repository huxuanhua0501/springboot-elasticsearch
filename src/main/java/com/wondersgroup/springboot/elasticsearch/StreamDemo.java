package com.wondersgroup.springboot.elasticsearch;

import org.junit.Test;
import org.omg.CORBA.OBJ_ADAPTER;

import java.sql.PreparedStatement;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * JDK8-stream学习
 */
public class StreamDemo {

    public static void main(String[] args) {
        List<String> list = Arrays.asList("zhaojigang", "nana", "tianya", "nana");
        Stream<String> stream = list.stream();
        list = stream.distinct().filter(str -> !str.equals("nana")).sorted(String::compareTo).collect(Collectors.toList());
        list.forEach(System.err::println);

    }

    @Test
    public void stream() {
        Arrays.asList("zhaojigang", "nana", "tianya", "nana")
                .stream()
                .distinct()
                .filter(str -> !str.equals("tianya"))
                .sorted(String::compareTo)
                .collect(Collectors.toList())
                .forEach(System.err::println);
    }

    /**
     * stream 转换成数组
     */
    @Test
    public void streamArray() {
        Stream<String> stringStream = Stream.of("java", "c++", "php");
        /**
         * 直接转换,只能转成object[]
         */
//        Object[] objects = stringStream.toArray();//只能返回object[]
//
//        for (Object obj : objects) {
//            System.err.println(obj);
//        }
        /**
         * 构造器转换
         */
        String[] strArray = stringStream.toArray(String[]::new);//构造器引用（类似于方法引用）,可以返回String[]
        for (String str : strArray) {
            System.err.println(str);
        }

    }

    /**
     * Stream转成集合
     */
    @Test
    public void stream2collection() {
        Stream<String> strStream = Stream.of("java", "c++", "python");
//        Set<String> strSet = strStream.collect(Collectors.toSet());//返回set
//        System.err.println(strSet);
//        List<String> strList = strStream.collect(Collectors.toList());//返回list
//        System.err.println(strList);
        /**
         * 构造器转换
         */
        List<String> strList = strStream.collect(Collectors.toCollection(ArrayList::new));
        System.err.println(strList);

    }

    /**
     * stream 中的元素拼接
     */
    @Test
    public void join() {
        Stream<String> strStream = Stream.of("java", "c++", "php", "python");
//        String str = strStream.collect(Collectors.joining());//所有元素拼接在一起
//        System.err.println(str);
        String str = strStream.collect(Collectors.joining(","));//用逗号隔开
        System.err.println(str);

    }

    /**
     * stream tumap,toConcurrentMap
     */
    @Test
    public void streamToMap() {
        Stream<String> strStream = Stream.of("java", "c++", "php", "python");
        Map<String, Integer> map = strStream.collect(Collectors.toMap(Function.identity(),//key
                (x) -> 0,//value
                (oldKey, newKey) -> oldKey,//如果出现重复的key,保留旧key
                HashMap::new)//返回具体的map
        );
        for (String str : map.keySet()) {
            System.err.println("key=" + str + ",value=" + map.get(str));
        }
    }

    @Test
    public void groupingBy() {
        /***************************groupingBy partitioningBy**************************/
//        Stream<Locale> localeStream = Stream.of(Locale.getAvailableLocales());
//        Map<String, List<Locale>> country2localeList = localeStream.collect(Collectors.groupingBy(Locale::getCountry));//根据国家分组，groupBy的参数是分类器
//        List<Locale> locales = country2localeList.get("CN");
//        for (Locale locale : locales) {
//            System.err.println(locale);
//        }


    }

    @Test
    public void jiandan() {
        Stream<String> strStream = Stream.of("java", "c++", "php", "python");
        strStream.filter(str -> str.startsWith("p")).forEach(System.err::println);
    }

    /**
     * if,or
     */
    @Test
    public void ifOr() {
        Stream<String> strStream = Stream.of("java", "c++", "php", "python");
        Optional<String> optionalValue = strStream.filter(str -> str.startsWith("p")).findFirst();
        optionalValue.ifPresent(str -> System.err.println(str));
        System.err.println(optionalValue.orElse("xxx"));

    }

    /**
     * 拼接
     */
    @Test
    public void contact() {
        Stream<String> strStream1 = Stream.of("java", "c++", "php", "python");
        Stream<String> strStream2 = Stream.of("java", "c++", "php", "python");

        Stream.concat(strStream1, strStream2).forEach(System.err::println);

    }

    /**
     * 聚合
     */
    @Test
    public void aggregation() {
        Stream<String> streamSelf = Stream.of("python", "basic", "php", "b");
        Optional<String> optional = streamSelf.max(String::compareToIgnoreCase);//获取最大值
        if (optional.isPresent()) {
            System.err.println(optional.get());
        }

    }

    /**
     * map to stream
     */
    @Test
    public void mapToStream() {
        Map<String, Object> map = new HashMap<>();
        map.put("county", "中国");
        map.put("sex", "男");
        map.put("age", "88");
//        map.forEach((id, val) -> System.err.println(id));
        Stream.of(map).filter(p ->!(p.get("county")=="中国") ).forEach(System.err::println);
    }
}
