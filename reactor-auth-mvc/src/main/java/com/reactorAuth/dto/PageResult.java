package com.reactorAuth.dto;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private List<T> records;
    private long total;
    private long page;
    private long size;
    private long totalPages;

    public static <T> PageResult<T> of(IPage<T> page) {
        PageResult<T> r = new PageResult<>();
        r.records = page.getRecords();
        r.total = page.getTotal();
        r.page = page.getCurrent();
        r.size = page.getSize();
        r.totalPages = page.getPages();
        return r;
    }
}
