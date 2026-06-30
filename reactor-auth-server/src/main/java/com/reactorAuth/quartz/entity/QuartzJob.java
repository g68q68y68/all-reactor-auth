package com.reactorAuth.quartz.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("sys_quartz_jobs")
public class QuartzJob {
    @Id
    private Long id;

    @Column("job_name")
    private String jobName;

    @Column("job_group")
    private String jobGroup;

    @Column("cron_expression")
    private String cronExpression;

    @Column("job_class")
    private String jobClass;

    @Column("description")
    private String description;

    @Column("params")
    private String params;  // JSON

    @Column("status")
    private Integer status;  // 1=运行中 0=暂停

    @Column("created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column("updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
