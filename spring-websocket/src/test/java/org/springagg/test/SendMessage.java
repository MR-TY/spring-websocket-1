package org.springagg.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springagg.redis.RedisTemplateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author tangyu
 * @date 2020/8/2
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring-context.xml")
public class SendMessage {

    @Autowired
    private RedisTemplateUtils redisTemplate;

    @Test
    public void testRedisSet() {
        redisTemplate.set( "t","帅帅帅" );
        redisTemplate.set( "t","帅帅帅" );
        redisTemplate.set( "t","帅帅帅" );
    }

    @Test
    public void testRedisGet() {
        System.out.println( redisTemplate.get( "t") );
    }

    @Test
    public void testRedisSetValue() {
        redisTemplate.setValue( "admin", "你好啊2" );
        redisTemplate.setValue( "admin", "你好啊3" );
        redisTemplate.setValue( "admin", "你好啊4" );
    }

    @Test
    public  void testRedisGetValue() {
        Iterator<String> strings = redisTemplate.getValue( "admin" ).iterator();
        while (strings.hasNext()) {
            System.out.println( strings.next() );
        }
    }

    @Test
    public void testRedisMapValue() {
        Map<String, String> map = new HashMap<String, String>(  );
        map.put( "1", "你好啊" );
        map.put( "1", "你好啊1" );
        map.put( "1", "你好啊2" );
        redisTemplate.mapValue( "admin", map);
    }

    @Test
    public void testRedisMapGetValue() {
        Map<String, String>  maps = redisTemplate.getmapValue( "admin");
        maps.forEach( (k,v) -> {
            System.out.println( v );
            redisTemplate.deletemapValue( "admin",k );
        });
    }
}
