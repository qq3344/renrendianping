package com.sky.common.constant;

public class RedisConstants {
    /** 验证码 */
    public static final String LOGIN_CODE_KEY = "login:code:";
    /** 验证码保存时间 */
    public static final Long LOGIN_CODE_TTL = 2L;
    /** token */
    public static final String LOGIN_USER_KEY = "login:token:";
    /** token的保存时间 */
    public static final Long LOGIN_USER_TTL = 30L;
    /** null的过期时间，缓存穿透 */
    public static final Long CACHE_NULL_TTL = 2L;
    /** 商铺保存时间 */
    public static final Long CACHE_SHOP_TTL = 30L;
    /** 商铺缓存前缀 */
    public static final String CACHE_SHOP_KEY = "cache:shop:";
    /** 商铺类型缓存前缀 */
    public static final String CACHE_SHOP_TYPE_KEY = "cache:shopType";
    /** 互斥锁缓存前缀 */
    public static final String LOCK_SHOP_KEY = "lock:shop:";
    /** 互斥锁过期时间 */
    public static final Long LOCK_SHOP_TTL = 10L;
    /** 优惠卷库存 */
    public static final String SECKILL_STOCK_KEY = "seckill:stock:";
    /** 博客点赞用户 */
    public static final String BLOG_LIKED_KEY = "blog:liked:";
    /** 博客评论点赞用户 */
    public static final String BLOG_COMMENT_LIKED_KEY = "blog:commnet:liked:";
    /** 关注 */
    public static final String FOLLOW_KEY = "follows:";
    /** 推送消息 */
    public static final String FEED_KEY = "feed:";
    /** 商铺地址 */
    public static final String SHOP_GEO_KEY = "shop:geo:";
    /** 签到 */
    public static final String USER_SIGN_KEY = "sign:";
}
