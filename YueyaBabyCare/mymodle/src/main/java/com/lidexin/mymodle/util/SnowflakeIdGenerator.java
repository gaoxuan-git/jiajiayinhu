package com.lidexin.mymodle.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.stereotype.Component;

/**
 * 雪花ID生成器
 * 用于生成分布式唯一ID
 * 
 * @author 张三
 */
@Component
public class SnowflakeIdGenerator {

    /**
     * 工作机器ID（0-31）
     */
    private static final long WORKER_ID = 1L;

    /**
     * 数据中心ID（0-31）
     */
    private static final long DATACENTER_ID = 1L;

    /**
     * 雪花算法实例
     */
    private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(WORKER_ID, DATACENTER_ID);

    /**
     * 生成ID
     */
    public static long nextId() {
        return SNOWFLAKE.nextId();
    }

    /**
     * 生成ID（字符串形式）
     */
    public static String nextIdStr() {
        return String.valueOf(nextId());
    }
}

