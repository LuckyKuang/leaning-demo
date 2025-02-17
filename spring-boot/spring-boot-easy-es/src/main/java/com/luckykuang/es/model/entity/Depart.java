package com.luckykuang.es.model.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 部门索引
 * @author luckykuang
 * @since 2024/8/28 11:24
 */
@Data
public class Depart implements Serializable {
    @Serial
    private static final long serialVersionUID = 2567822969212633185L;
    private Integer id;
    private String name;
    private String address;
    private String description;
}
