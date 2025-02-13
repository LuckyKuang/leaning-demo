package com.luckykuang.excel.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
*
* @author luckykuang
* @since 2025/2/11 15:30
*/
@Getter
@AllArgsConstructor
public enum ExportStatus {
    FAILURE(0,"失败"),
    SUCCESS(1,"成功"),
    ING(2,"进行中"),
    ;
    private final int code;
    private final String msg;

}
