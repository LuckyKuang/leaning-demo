package com.luckykuang.excel.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.CellData;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import com.luckykuang.excel.model.dto.StudentDTO;
import com.luckykuang.excel.model.entity.Student;
import com.luckykuang.excel.service.StudentService;
import com.luckykuang.excel.utils.ThreadPoolUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 学生信息导入监听器
 * @author luckykuang
 * @since 2025/2/11 16:23
 */
@Slf4j
@Component
@AllArgsConstructor
public class StudentImportListener implements ReadListener<StudentDTO> {

    // 成功数据
    private final List<StudentDTO> successList = new ArrayList<>();
    // 单次处理条数
    private static final int BATCH_COUNT = 100000;

    private final StudentService studentService;

    /**
     * 读取表格内容，每一条数据解析都会来调用
     * @param studentDTO    one row value. It is same as {@link AnalysisContext#readRowHolder()}
     * @param analysisContext analysis context
     */
    @Override
    public void invoke(StudentDTO studentDTO, AnalysisContext analysisContext) {
        if (StringUtils.isBlank(studentDTO.getName()) || studentDTO.getAge() == null) {
            log.error("读取错误的行数据:{}", studentDTO);
            return;
        }
        successList.add(studentDTO);
        if (successList.size() >= BATCH_COUNT) {
            log.info("读取数据量：{}条", successList.size());
            List<CompletableFuture<Boolean>> futures = new ArrayList<>();
            List<List<StudentDTO>> lists = ListUtils.partition(successList, 1000);
            for (List<StudentDTO> list : lists) {
                // 异步处理数据
                CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
                    List<Student> students = list.stream().map(item -> {
                        Student student = new Student();
                        student.setName(item.getName());
                        student.setAge(item.getAge());
                        return student;
                    }).collect(Collectors.toList());
                    return studentService.saveBatch(students);
                }, ThreadPoolUtils.EXECUTOR_SERVICE);
                futures.add(future);
            }
            // 等待所有异步任务完成(此处会阻塞，直到所有线程处理完成)
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            // 清空集合，释放资源
            successList.clear();
            lists.clear();
        }
    }

    /**
     * 所有数据读取完成之后调用
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        // 读取剩余数据
        if (CollectionUtils.isNotEmpty(successList)) {
            log.info("最后读取数据量：{}条", successList.size());
            List<Student> students = successList.stream().map(item -> {
                Student student = new Student();
                student.setName(item.getName());
                student.setAge(item.getAge());
                return student;
            }).collect(Collectors.toList());
            studentService.saveBatch(students);
            successList.clear();
        }
        log.info("数据导入完成，总共导入{}行...",analysisContext.readRowHolder().getRowIndex());
    }

    /**
     * 读取标题，里面实现在读完标题后会回调
     */
    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        ReadListener.super.invokeHead(headMap, context);
        log.info("标题:{}",headMap.values().stream().map(CellData::getStringValue).collect(Collectors.toList()));
    }

    /**
     * 转换异常 获取其他异常下会调用本接口。抛出异常则停止读取。如果这里不抛出异常则 继续读取下一行
     */
    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        ReadListener.super.onException(exception, context);
        log.error("数据读取异常,正在读取的行数:{}行",context.readRowHolder(),exception);
    }
}
