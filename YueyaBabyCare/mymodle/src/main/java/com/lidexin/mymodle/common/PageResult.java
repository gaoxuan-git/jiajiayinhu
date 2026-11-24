package com.lidexin.mymodle.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页响应结果类
 * 封装分页查询的结果数据
 * 
 * @param <T> 数据类型
 * @author 张三
 */
@Data
@ApiModel(description = "分页响应结果")
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    @ApiModelProperty(value = "当前页码")
    private Long current;

    /**
     * 每页显示条数
     */
    @ApiModelProperty(value = "每页显示条数")
    private Long size;

    /**
     * 总记录数
     */
    @ApiModelProperty(value = "总记录数")
    private Long total;

    /**
     * 总页数
     */
    @ApiModelProperty(value = "总页数")
    private Long pages;

    /**
     * 数据列表
     */
    @ApiModelProperty(value = "数据列表")
    private List<T> records;

    /**
     * 是否有上一页
     */
    @ApiModelProperty(value = "是否有上一页")
    private Boolean hasPrevious;

    /**
     * 是否有下一页
     */
    @ApiModelProperty(value = "是否有下一页")
    private Boolean hasNext;

    public PageResult() {
    }

    /**
     * 从MyBatis-Plus的IPage转换
     */
    public PageResult(IPage<T> page) {
        this.current = page.getCurrent();
        this.size = page.getSize();
        this.total = page.getTotal();
        this.pages = page.getPages();
        this.records = page.getRecords();
        this.hasPrevious = page.getCurrent() > 1;
        this.hasNext = page.getCurrent() < page.getPages();
    }

    /**
     * 静态工厂方法 - 从IPage创建
     */
    public static <T> PageResult<T> of(IPage<T> page) {
        return new PageResult<>(page);
    }

    /**
     * 静态工厂方法 - 创建空结果
     */
    public static <T> PageResult<T> empty() {
        PageResult<T> result = new PageResult<>();
        result.setCurrent(1L);
        result.setSize(10L);
        result.setTotal(0L);
        result.setPages(0L);
        result.setRecords(java.util.Collections.emptyList());
        result.setHasPrevious(false);
        result.setHasNext(false);
        return result;
    }
}

