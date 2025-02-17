package com.luckykuang.es.controller;

import com.luckykuang.es.model.dto.EsDocumentDTO;
import com.luckykuang.es.model.entity.EsDocument;
import com.luckykuang.es.model.qo.EsDocumentQO;
import com.luckykuang.es.service.EsDocumentService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Easy-ES 书本信息
 * @author luckykuang
 * @since 2025/2/14 12:20
 */
@RestController
@RequestMapping("api/v1/es/document")
@AllArgsConstructor
public class EsDocumentController {

    private final EsDocumentService esDocumentService;

    /**
     * 创建索引(相当于mysql中的表)
     */
    @GetMapping("/createIndex")
    public String createIndex() {
        return esDocumentService.createIndex();
    }

    /**
     * 删除索引(相当于mysql中的表)
     */
    @GetMapping("/deleteIndex")
    public String deleteIndex() {
        return esDocumentService.deleteIndex();
    }

    /**
     * 插入数据
     * <p>
     * {
     *   "mysqlId": 10001,
     *   "title": "张三自传",
     *   "content": "张三是一个非常值得尊敬的人...",
     *   "price": 79.9988,
     *   "ratio": 0.81,
     *   "rate": 4.5,
     *   "createTime": "2025-02-14 10:00:00",
     *   "dateTime": "2025-02-14 10:00:00",
     *   "number": 50
     * }
     * </p>
     */
    @PostMapping("/insert")
    public String insert(@RequestBody @Validated EsDocumentDTO esDocumentDTO) {
        return esDocumentService.insert(esDocumentDTO);
    }

    /**
     * 更新数据
     */
    @PostMapping("/update")
    public String update(@RequestBody @Validated EsDocumentDTO esDocumentDTO) {
        return esDocumentService.update(esDocumentDTO);
    }

    /**
     * 根据esId删除数据
     */
    @PostMapping("/deleteByEsId/{esId}")
    public String deleteByEsId(@PathVariable("esId") String esId) {
        return esDocumentService.deleteByEsId(esId);
    }

    /**
     * 根据mysqlId删除数据
     */
    @PostMapping("/deleteByMysqlId/{mysqlId}")
    public String deleteByMysqlId(@PathVariable("mysqlId") Long mysqlId) {
        return esDocumentService.deleteByMysqlId(mysqlId);
    }

    /**
     * 查询数据
     */
    @GetMapping("/search")
    public List<EsDocument> search(EsDocumentQO esDocumentQO) {
        return esDocumentService.search(esDocumentQO);
    }
}
