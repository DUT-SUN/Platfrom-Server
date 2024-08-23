package com.example.blogssm.CanalClient;

import com.example.blogssm.common.RedisCache;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Map;

/**
 * 功能描述
 * <p>
 * 成略在胸，良计速出
 *
 * @author SUN
 * @date 2024/03/25  20:37
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class CanalListener {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private RedisCache redisCache;
//    private final ISysPermissionService permissionService;
//    private final ISysOauthClientService oauthClientService;
//    private final ISysMenuService menuService;
    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(value = "canal.queue", durable = "true"),
                    exchange = @Exchange(value = "canal.exchange"),
                    key = "canal.routingKey"
            )
    })

        public void handleDataChange(String message) {
            try {
                // message就是要删除的key
                String key = message;
                System.out.println("Key: " + key);
                redisCache.deleteObject(key);
            } catch (Exception e) {
                e.printStackTrace();
            }

//        CanalMessage canalMessage = JSONUtil.toBean(message, CanalMessage.class);
//        String tableName = canalMessage.getTable();
//        log.info("Canal 监听 {} 发生变化；明细：{}", tableName, message);
//        if ("sys_oauth_client".equals(tableName)) {
//            log.info("======== 清除客户端信息缓存 ========");
//            oauthClientService.cleanCache();
//        } else if (Arrays.asList("sys_permission", "sys_role", "sys_role_permission").contains(tableName)) {
//            log.info("======== 刷新角色权限缓存 ========");
//            permissionService.refreshPermRolesRules();
//        } else if (Arrays.asList("sys_menu", "sys_role", "sys_role_menu").contains(tableName)) {
//            log.info("======== 清理菜单路由缓存 ========");
//            menuService.cleanCache();
//        }
    }
}