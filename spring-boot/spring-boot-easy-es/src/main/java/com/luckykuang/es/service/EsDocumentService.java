package com.luckykuang.es.service;

import com.luckykuang.es.model.dto.EsDocumentDTO;
import com.luckykuang.es.model.entity.EsDocument;
import com.luckykuang.es.model.qo.EsDocumentQO;

import java.util.List;

/**
 * @author luckykuang
 * @since 2025/2/14 14:42
 */
public interface EsDocumentService {
    String createIndex();

    String deleteIndex();

    String insert(EsDocumentDTO esDocumentDTO);

    String update(EsDocumentDTO esDocumentDTO);

    String deleteByEsId(String esId);

    String deleteByMysqlId(Long mysqlId);

    List<EsDocument> search(EsDocumentQO esDocumentQO);
}
