package com.sky;

import com.sky.dto.UserDTO;
import com.sky.pojo.Shop;
import com.sky.service.IBlogCommentsService;
import com.sky.service.impl.ShopServiceImpl;
import com.sky.utils.RedisConstants;
import com.sky.utils.RedisIdWorker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@SpringBootTest
class RrdpApplicationTests {

    @Autowired
    private ShopServiceImpl shopService;

    @Resource
    private RedisIdWorker redisIdWorker;

    @Resource
    private IBlogCommentsService blogCommentsService;

    @Test
    void contextLoads() {
        Map<String, Object> map = new HashMap<>();
        UserDTO userDTO = new UserDTO(1L,"zhangsan","123");
        // 通过reflect获取所有属性
        for (Field field : userDTO.getClass().getDeclaredFields()) {
            // 设置暴力反射，获取私有属性
            field.setAccessible(true);
            try {
                map.put(field.getName(),field.get(userDTO));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        for (String s : map.keySet()) {
            System.out.println(s+"=="+map.get(s));
        }
    }



    // 可能一个属性里面套了一个实体类，这时候需要判断，然后通过递归来实现
    @Test
    void test1() throws Exception {
        UserDTO userDTO1 = new UserDTO(1L,"zhangsan","123");
        UserDTO userDTO2 = new UserDTO(2L,"lisi","123");
        UserDTO userDTO3 = new UserDTO(3L,"wangwu","123");
        ArrayList<UserDTO> userDTOS = new ArrayList<>();
        userDTOS.add(userDTO1);
        userDTOS.add(userDTO2);
        userDTOS.add(userDTO3);
        Clazz clazz = new Clazz("计算机科学与技术", userDTOS);
        Map<String, Object> map = beanToMap(clazz);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id",1L);
        hashMap.put("nickName","lisi");
        hashMap.put("icon","/baidu.com/meitukk");
        Object o = mapToBean(hashMap, UserDTO.class);
//        System.out.println(o.toString());
        Object o1 = mapToBean2(hashMap, UserDTO.class);
        System.out.println(o1);
//        for (String s : map.keySet()) {
//            System.out.println(s+"---"+map.get(s));
//        }
    }

    public Map<String,Object> beanToMap(Object o){
        Map<String, Object> map = new HashMap<>();
        // 通过reflect获取所有树形
        for (Field field : o.getClass().getDeclaredFields()) {
            // 设置暴力反射，获取私有属性
            field.setAccessible(true);
            try {
                if (field.get(o) != null){
                    Class<?> aClass = field.get(o).getClass();
                    OneSelf annotation = aClass.getAnnotation(OneSelf.class);
                    if (annotation!=null){
                        Map<String, Object> beanToMap = beanToMap(field.get(o));
                        map.put(field.getName(),beanToMap);
                    }else{
                        map.put(field.getName(),field.get(o));
                    }
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public Object mapToBean(Map<String,Object> map,Class<?> beanClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        // 利用反射调用构造器实例化对象
        Object object = beanClass.getDeclaredConstructor().newInstance();
        // 通过实例化对象的class对象，获取所有的字段
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            // 返回属性字段的修饰符
            int mod = field.getModifiers();
            // 如果是静态或者final修饰的不需要添加
            if (Modifier.isStatic(mod)|| Modifier.isFinal(mod)){
                continue;
            }
            // 暴力获取私有属性
            field.setAccessible(true);
            field.set(object,map.get(field.getName()));
        }

        return object;
    }

    public Object mapToBean2(Map<String,Object> map,Class<?> beanClass) throws Exception{
        // 利用class对象调用构造器实例化对象
        Object object = beanClass.getDeclaredConstructor().newInstance();
        // 内省Java bean并了解其属性，暴露的方法,==简单来说就是将属性封装到了BeanInfo里面==
        BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
        // 返回bean的所有属性的描述符。
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            /**
             * java.beans.PropertyDescriptor[
             * name=icon; values={expert=false; visualUpdate=false; hidden=false;
             * enumerationValues=[Ljava.lang.Object;@75c77add; required=false};
             * propertyType=class java.lang.String; readMethod=public java.lang.String com.sky.dto.UserDTO.getIcon();
             * writeMethod=public void com.sky.dto.UserDTO.setIcon(java.lang.String)]
             */
            // 获取属性的setter方法
            Method setter = propertyDescriptor.getWriteMethod();
            if (setter!=null){
                // 获取值
                Object o = map.get(propertyDescriptor.getName());
                if (o!=null){
                    // 利用反射将属性赋值
                    setter.invoke(object,o);
                }
            }
        }
        return object;
    }

    @Test
    public void test0001(){
        HashSet<Integer> hashSet = new HashSet<>();
        hashSet.add(1);
        hashSet.add(1);
        hashSet.add(1);
        hashSet.add(1);
        for (Integer integer : hashSet) {
            System.out.println(integer);
        }
    }
    private ExecutorService es = Executors.newFixedThreadPool(10);

    @Test
    public void test001() throws InterruptedException {
        String a = "";
        System.out.println(a == null);
        CountDownLatch latch = new CountDownLatch(300);
//        shopService.saveShopToRedis(1L,10L);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 100; i++) {
                    Long order = redisIdWorker.nextId("order");
                    System.out.println("id="+order);
                }
                latch.countDown();
            }
        };
        for (int i = 0; i < 300 ; i++) {
            es.submit(runnable);
        }
        latch.await();

    }

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 将店铺坐标信息缓存到 redis 中
     */
    @Test
    public void testGEO(){
        List<Shop> list = shopService.list();
        Map<Long, List<Shop>> collect = list.stream().collect(Collectors.groupingBy(Shop::getTypeId));
        for (Long typeId : collect.keySet()) {
            String key = RedisConstants.SHOP_GEO_KEY+typeId;
            List<Shop> shops = collect.get(typeId);
            List<RedisGeoCommands.GeoLocation<String>> geoLocations = new ArrayList<>(shops.size());
            for (Shop shop : shops) {
                geoLocations.add(new RedisGeoCommands.GeoLocation<>(shop.getId().toString(),new Point(shop.getX(),shop.getY())));
            }
            stringRedisTemplate.opsForGeo().add(key,geoLocations);

        }
    }

    @Test
    public void testBlogCommentTree(){
        blogCommentsService.showBlogComments(23L);
    }
}
