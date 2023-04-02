package com.sky.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**

 * @version 1.0
 * @time 2023/3/9
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel("博客评论返回视图")
public class BlogCommentsVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @ApiModelProperty("评论主键id")
    private Long id;

    /**
     * 用户id
     */
    @ApiModelProperty("评论用户id")
    private Long userId;

    /**
     * 回复的评论id
     */
    @ApiModelProperty("回复评论的id")
    private Long answerId;

    /**
     * 回复的内容
     */
    @ApiModelProperty("回复的内容")
    private String content;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 评论者的昵称
     */
    @ApiModelProperty("评论者的昵称")
    private String nickName;

    /** 评论者的头像 */
    @ApiModelProperty("评论者的头像")
    private String icon;

    /** 评论者的上级昵称 */
    @ApiModelProperty("评论者的上级昵称")
    private String pNickName;

}
