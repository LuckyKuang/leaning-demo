package com.luckykuang.es.service.impl;

import com.luckykuang.es.mapper.EsDocumentMapper;
import com.luckykuang.es.model.dto.EsDocumentDTO;
import com.luckykuang.es.model.entity.EsDocument;
import com.luckykuang.es.model.qo.EsDocumentQO;
import com.luckykuang.es.service.EsDocumentService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.elasticsearch.ElasticsearchStatusException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.TimeZone;

/**
 * @author luckykuang
 * @since 2025/2/14 14:42
 */
@Service
@AllArgsConstructor
public class EsDocumentServiceImpl implements EsDocumentService {

    private static final String SUCCESS = "SUCCESS";
    private static final String FAILURE = "FAILURE";

    private final EsDocumentMapper esDocumentMapper;

    @Override
    public String createIndex() {
        Boolean indexStatus;
        try {
            indexStatus = esDocumentMapper.createIndex();
        } catch (ElasticsearchStatusException e) {
            indexStatus = Boolean.FALSE;
        }catch (Exception e) {
            throw new RuntimeException("create index error");
        }
        return Boolean.TRUE.equals(indexStatus) ? SUCCESS : FAILURE;
    }

    @Override
    public String deleteIndex() {
        Boolean indexStatus;
        try {
            indexStatus = esDocumentMapper.deleteIndex("es_document");
        } catch (ElasticsearchStatusException e) {
            indexStatus = Boolean.FALSE;
        }catch (Exception e) {
            throw new RuntimeException("delete index error");
        }
        return Boolean.TRUE.equals(indexStatus) ? SUCCESS : FAILURE;
    }

    @Override
    public String insert(EsDocumentDTO esDocumentDTO) {
        EsDocument esDocument = new EsDocument();
        BeanUtils.copyProperties(esDocumentDTO, esDocument);
        esDocumentMapper.insert(esDocument);
        return SUCCESS;
    }

    @Override
    public String update(EsDocumentDTO esDocumentDTO) {
        EsDocument esDocument = new EsDocument();
        BeanUtils.copyProperties(esDocumentDTO, esDocument);
        esDocumentMapper.updateById(esDocument);
        return SUCCESS;
    }

    @Override
    public String deleteByEsId(String esId) {
        esDocumentMapper.deleteById(esId);
        return SUCCESS;
    }

    @Override
    public String deleteByMysqlId(Long mysqlId) {
        esDocumentMapper.delete(new LambdaEsQueryWrapper<EsDocument>().eq(EsDocument::getMysqlId, mysqlId));
        return SUCCESS;
    }

    /**
     * 时间查询还存在问题，时间字段后面必须添加".keyword"，目前Easy-ES没有添加，等后续优化
     * <p>
     * "query": {
     * 		"bool": {
     * 			"must": [
     * 				                {
     * 					"range": {
     * 						"create_time.keyword": {
     * 				      "from": "2025-02-14 10:00:00",
     * 				      "to": "2025-02-14 10:00:00",
     * 							"include_lower": true,
     * 							"include_upper": true,
     * 							"time_zone": "GMT+08:00",
     * 							"format": "yyyy-MM-dd HH:mm:ss",
     * 							"boost": 1.0
     *                        }
     *                    }
     *                }
     * 			],
     * 			"adjust_pure_negative": true,
     * 			"boost": 1.0
     * 		}
     * 	}
     * 	</p>
     * @param esDocumentQO
     * @return
     */
    @Override
    public List<EsDocument> search(EsDocumentQO esDocumentQO) {
            LambdaEsQueryWrapper<EsDocument> wrapper = new LambdaEsQueryWrapper<>();
            wrapper.eq(StringUtils.isNotBlank(esDocumentQO.getId()), EsDocument::getId, esDocumentQO.getId());
            wrapper.eq(esDocumentQO.getMysqlId() != null, EsDocument::getMysqlId, esDocumentQO.getId());
            wrapper.like(StringUtils.isNotBlank(esDocumentQO.getTitle()), EsDocument::getTitle, esDocumentQO.getTitle());
            wrapper.like(StringUtils.isNotBlank(esDocumentQO.getContent()), EsDocument::getContent, esDocumentQO.getContent());
            wrapper.between(esDocumentQO.getStartTime() != null && esDocumentQO.getEndTime() != null,
                    EsDocument::getDateTime, esDocumentQO.getStartTime(), esDocumentQO.getEndTime(),
                    TimeZone.getDefault().toZoneId(), "yyyy-MM-dd HH:mm:ss");
            return esDocumentMapper.selectList(wrapper);
    }
}
