package com.sky.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.dto.Result;
import com.sky.dto.ScrollResult;
import com.sky.dto.UserDTO;
import com.sky.pojo.Blog;
import com.sky.mapper.BlogMapper;
import com.sky.pojo.Follow;
import com.sky.pojo.User;
import com.sky.service.IBlogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sky.service.IFollowService;
import com.sky.service.IUserService;
import com.sky.utils.RedisConstants;
import com.sky.utils.SystemConstants;
import com.sky.utils.UserHolder;
import jodd.util.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService {

    @Resource
    private IUserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private IFollowService followService;

    /**
     * 将博客发布者信息存入blog
     * @param blog
     */
    public void queryBlogUser(Blog blog) {
        Long userId = blog.getUserId();
        User user = userService.getById(userId);
        blog.setName(user.getNickName());
        blog.setIcon(user.getIcon());
    }

    /**
     * 判断登录用户是否点赞博客
     * @param blog
     */
    public void isBlogLiked(Blog blog) {
        UserDTO user = UserHolder.getUser();
        // 用户未登录
        if (user == null){
            return;
        }
        // 判断当前用户是否点过赞
        Double score = stringRedisTemplate.opsForZSet().score(RedisConstants.BLOG_LIKED_KEY + blog.getId(), user.getId().toString());
        blog.setIsLike(score != null);
    }

    /**
     * 根据博客id查询详情
     * @param id 博客id
     * @return
     */
    @Override
    public Result queryBlogById(Long id) {
        Blog blog = query().eq("id", id).one();
        if (blog == null){
            return Result.fail("博客不存在");
        }
        queryBlogUser(blog);
        isBlogLiked(blog);
        return Result.ok(blog);
    }

    /**
     * 分页查询blog
     * @param current
     * @return
     */
    @Override
    public Result queryHotBlog(Integer current) {
        // 根据用户查询
        Page<Blog> page = query()
                .orderByDesc("liked")
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        // 获取当前页数据
        List<Blog> records = page.getRecords();
        // 查询用户
        records.forEach(blog -> {
            queryBlogUser(blog);
            isBlogLiked(blog);
        });
        return Result.ok(records);
    }

    /**
     * blog点赞
     * @param id
     * @return
     */
    @Override
    public Result likeBlog(Long id) {
        // 获取当前用户
        Long userId = UserHolder.getUser().getId();
        // 判断当前用户是否点赞
        Double score = stringRedisTemplate.opsForZSet().score(RedisConstants.BLOG_LIKED_KEY + id, userId.toString());
        if (score == null){
            // 没点过赞,更新数据库点赞数
            boolean success = update().setSql("liked = liked + 1").eq("id", id).update();
            // 更新数据库成功，将用户id存入redis中set保存 zadd
            if (success){
                stringRedisTemplate.opsForZSet().add(RedisConstants.BLOG_LIKED_KEY+id,userId.toString(),System.currentTimeMillis());
            }
        }else{
            // 点过赞,更新数据库点赞数
            boolean success = update().setSql("liked = liked - 1").eq("id", id).update();
            // 更新数据库成功，将用户id存入redis中set保存
            if (success){
                stringRedisTemplate.opsForZSet().remove(RedisConstants.BLOG_LIKED_KEY+id,userId.toString());
            }
        }
        return Result.ok();
    }

    /**
     * 查询点赞top5
     * @param id
     * @return
     */
    @Override
    public Result queryBlogLikes(Long id) {
        // redis中zset查询top5
        Set<String> range = stringRedisTemplate.opsForZSet().range(RedisConstants.BLOG_LIKED_KEY + id, 0, 4);
        if (range == null || range.isEmpty()){
            return Result.ok(Collections.emptyList());
        }
        // 将字符串的ids转为long
        List<Long> ids = range.stream().map(Long::valueOf).collect(Collectors.toList());
        // 拼接字符串 1 2 3 => 1,2,3
        String idStr = StrUtil.join(",", ids);
        // 如果直接使用 id in (3,2) 会有问题，需要 order by field
        List<User> userList = userService.query().in("id",ids).last("order by field ( id ,"+idStr+" ) ").list();
        // 将userList 转为userDTOList 返回
        List<UserDTO> userDTOList = userList.stream().map(user -> {
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user, userDTO);
            return userDTO;
        }).collect(Collectors.toList());
        return Result.ok(userDTOList);
    }

    /**
     * 发布日记
     * @param blog
     * @return
     */
    @Override
    public Result saveBlog(Blog blog) {
        // 获取登录用户
        UserDTO user = UserHolder.getUser();
        blog.setUserId(user.getId());
        // 保存探店博文
        boolean success = save(blog);
        if (!success){
            return Result.fail("发布失败！");
        }
        // 给粉丝推送消息
        List<Follow> follows = followService.query().eq("follow_user_id", user.getId()).list();
        for (Follow follow : follows) {
            // 获取粉丝id
            Long userId = follow.getUserId();
            String key = RedisConstants.FEED_KEY + userId;
            stringRedisTemplate.opsForZSet().add(key,blog.getId().toString(),System.currentTimeMillis());
        }

        // 返回id
        return Result.ok(blog.getId());
    }

    /**
     * 分页查询关注的人的博客
     * @param max
     * @param offset
     * @return
     */
    @Override
    public Result queryBlogOfFollow(Long max, Integer offset) {
        // 获取当前用户
        Long userId = UserHolder.getUser().getId();
        String key = RedisConstants.FEED_KEY+userId;
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet()
                .reverseRangeByScoreWithScores(key, 0, max, offset, 3);
        if (typedTuples == null || typedTuples.isEmpty()){
            return Result.fail("当前无数据！");
        }
        List<Long> ids = new ArrayList<>(typedTuples.size());
        // 最小时间
        long minTime = 0;
        // 最小数的次数
        int os = 1;
        for (ZSetOperations.TypedTuple<String> typedTuple : typedTuples) {
            ids.add(Long.valueOf(typedTuple.getValue()));
            long time = typedTuple.getScore().longValue();
            if (time == minTime){
                os += 1;
            }else{
                minTime = time;
                os = 1;
            }
        }
        // 根据ids查询blog
        String idStr = StrUtil.join(",", ids);
        List<Blog> blogList = query().in("id", ids).last("order by field ( id , " + idStr + ")").list();
        for (Blog blog : blogList) {
            queryBlogUser(blog);
            isBlogLiked(blog);
        }
        ScrollResult scrollResult = new ScrollResult();
        scrollResult.setList(blogList);
        scrollResult.setMinTime(minTime);
        scrollResult.setOffset(os);
        return Result.ok(scrollResult);
    }
}
