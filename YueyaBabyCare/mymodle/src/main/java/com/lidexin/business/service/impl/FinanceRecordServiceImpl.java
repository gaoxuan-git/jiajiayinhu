package com.lidexin.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lidexin.business.mapper.FinanceRecordMapper;
import com.lidexin.business.mapper.SysUserMapper;
import com.lidexin.business.po.FinanceRecord;
import com.lidexin.business.po.SysUser;
import com.lidexin.business.service.IFinanceRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 财务记录服务实现类
 *
 * @author 张三
 * @since 2025-12-21
 */
@Service
public class FinanceRecordServiceImpl extends ServiceImpl<FinanceRecordMapper, FinanceRecord> implements IFinanceRecordService {

    @Autowired
    private SysUserMapper sysUserMapper;

    private static final String[] RECORD_TYPE_NAMES = {"", "收入", "支出"};
    private static final String[] SOURCE_TYPE_NAMES = {"", "订单收入", "服务收入", "物资采购", "人员工资", "其他"};

    @Override
    public void addFinanceRecord(FinanceRecord record) {
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        record.setDeleted((byte) 0);
        if (record.getRecordDate() == null) {
            record.setRecordDate(LocalDate.now());
        }
        this.save(record);
    }

    @Override
    public Page<FinanceRecordVO> getFinanceRecordList(Long institutionId, Byte recordType,
            String startDate, String endDate, Integer pageNum, Integer pageSize) {

        Page<FinanceRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<FinanceRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceRecord::getDeleted, 0);

        if (institutionId != null) {
            wrapper.eq(FinanceRecord::getInstitutionId, institutionId);
        }
        if (recordType != null) {
            wrapper.eq(FinanceRecord::getRecordType, recordType);
        }
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge(FinanceRecord::getRecordDate, LocalDate.parse(startDate));
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le(FinanceRecord::getRecordDate, LocalDate.parse(endDate));
        }

        wrapper.orderByDesc(FinanceRecord::getRecordDate);
        wrapper.orderByDesc(FinanceRecord::getCreateTime);

        Page<FinanceRecord> resultPage = this.page(page, wrapper);

        // 转换为VO
        Page<FinanceRecordVO> voPage = new Page<>(pageNum, pageSize);
        voPage.setTotal(resultPage.getTotal());
        voPage.setPages(resultPage.getPages());

        List<FinanceRecordVO> voList = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (FinanceRecord record : resultPage.getRecords()) {
            FinanceRecordVO vo = convertToVO(record, dateFormatter, timeFormatter);
            voList.add(vo);
        }

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public FinanceStatsVO getFinanceStats(Long institutionId, String startDate, String endDate) {
        FinanceStatsVO vo = new FinanceStatsVO();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime monthStart = LocalDateTime.of(LocalDate.now().withDayOfMonth(1), LocalTime.MIN);

        LocalDate start = startDate != null && !startDate.isEmpty() ? LocalDate.parse(startDate) : null;
        LocalDate end = endDate != null && !endDate.isEmpty() ? LocalDate.parse(endDate) : null;

        // 总收入
        vo.setTotalIncome(getAmountByType(institutionId, (byte) 1, start, end));
        // 总支出
        vo.setTotalExpense(getAmountByType(institutionId, (byte) 2, start, end));
        // 净收入
        vo.setNetIncome(vo.getTotalIncome().subtract(vo.getTotalExpense()));

        // 今日收入/支出
        vo.setTodayIncome(getAmountByTypeAndTime(institutionId, (byte) 1, todayStart, now));
        vo.setTodayExpense(getAmountByTypeAndTime(institutionId, (byte) 2, todayStart, now));

        // 本月收入/支出
        vo.setMonthIncome(getAmountByTypeAndTime(institutionId, (byte) 1, monthStart, now));
        vo.setMonthExpense(getAmountByTypeAndTime(institutionId, (byte) 2, monthStart, now));

        // 记录总数
        LambdaQueryWrapper<FinanceRecord> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(FinanceRecord::getDeleted, 0);
        if (institutionId != null) {
            countWrapper.eq(FinanceRecord::getInstitutionId, institutionId);
        }
        vo.setTotalRecords((int) this.count(countWrapper));

        // 收入来源分布
        vo.setIncomeBySource(getAmountBySource(institutionId, (byte) 1));
        // 支出来源分布
        vo.setExpenseBySource(getAmountBySource(institutionId, (byte) 2));

        // 收入趋势（近7天）
        vo.setIncomeTrend(getAmountTrend(institutionId, (byte) 1, 7));
        // 支出趋势（近7天）
        vo.setExpenseTrend(getAmountTrend(institutionId, (byte) 2, 7));

        return vo;
    }

    @Override
    public List<MonthlySummaryVO> getMonthlySummary(Long institutionId, Integer year) {
        List<MonthlySummaryVO> result = new ArrayList<>();

        if (year == null) {
            year = LocalDate.now().getYear();
        }

        for (int month = 1; month <= 12; month++) {
            LocalDate monthStart = LocalDate.of(year, month, 1);
            LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);

            MonthlySummaryVO vo = new MonthlySummaryVO();
            vo.setYear(year);
            vo.setMonth(month);
            vo.setMonthName(month + "月");

            // 月收入
            BigDecimal income = getAmountByType(institutionId, (byte) 1, monthStart, monthEnd);
            vo.setIncome(income);

            // 月支出
            BigDecimal expense = getAmountByType(institutionId, (byte) 2, monthStart, monthEnd);
            vo.setExpense(expense);

            // 净收入
            vo.setNetIncome(income.subtract(expense));

            // 记录数
            LambdaQueryWrapper<FinanceRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FinanceRecord::getDeleted, 0);
            if (institutionId != null) {
                wrapper.eq(FinanceRecord::getInstitutionId, institutionId);
            }
            wrapper.between(FinanceRecord::getRecordDate, monthStart, monthEnd);
            vo.setRecordCount((int)this.count(wrapper));

            result.add(vo);
        }

        return result;
    }

    @Override
    public FinanceExportVO exportFinanceReport(Long institutionId, String startDate, String endDate) {
        FinanceExportVO vo = new FinanceExportVO();
        vo.setStartDate(startDate);
        vo.setEndDate(endDate);

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        // 总收入/支出
        vo.setTotalIncome(getAmountByType(institutionId, (byte) 1, start, end));
        vo.setTotalExpense(getAmountByType(institutionId, (byte) 2, start, end));
        vo.setNetIncome(vo.getTotalIncome().subtract(vo.getTotalExpense()));

        // 获取所有记录
        LambdaQueryWrapper<FinanceRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceRecord::getDeleted, 0);
        if (institutionId != null) {
            wrapper.eq(FinanceRecord::getInstitutionId, institutionId);
        }
        wrapper.between(FinanceRecord::getRecordDate, start, end);
        wrapper.orderByDesc(FinanceRecord::getRecordDate);

        List<FinanceRecord> records = this.list(wrapper);

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<FinanceRecordVO> recordVOs = new ArrayList<>();
        for (FinanceRecord record : records) {
            recordVOs.add(convertToVO(record, dateFormatter, timeFormatter));
        }
        vo.setRecords(recordVOs);

        return vo;
    }

    // ==================== 私有辅助方法 ====================

    private FinanceRecordVO convertToVO(FinanceRecord record, DateTimeFormatter dateFormatter,
            DateTimeFormatter timeFormatter) {
        FinanceRecordVO vo = new FinanceRecordVO();
        vo.setId(record.getId());
        vo.setInstitutionId(record.getInstitutionId());
        vo.setRecordType(record.getRecordType());
        vo.setRecordTypeName(getRecordTypeName(record.getRecordType()));
        vo.setAmount(record.getAmount());
        vo.setSourceType(record.getSourceType());
        vo.setSourceTypeName(getSourceTypeName(record.getSourceType()));
        vo.setSourceId(record.getSourceId());
        vo.setRemark(record.getRemark());
        vo.setRecordDate(record.getRecordDate() != null ? record.getRecordDate().format(dateFormatter) : null);
        vo.setOperatorId(record.getOperatorId());
        vo.setOperatorName(getOperatorName(record.getOperatorId()));
        vo.setCreateTime(record.getCreateTime() != null ? record.getCreateTime().format(timeFormatter) : null);
        return vo;
    }

    private String getRecordTypeName(Byte recordType) {
        if (recordType == null || recordType < 1 || recordType >= RECORD_TYPE_NAMES.length) {
            return "未知";
        }
        return RECORD_TYPE_NAMES[recordType];
    }

    private String getSourceTypeName(Byte sourceType) {
        if (sourceType == null || sourceType < 1 || sourceType >= SOURCE_TYPE_NAMES.length) {
            return "未知";
        }
        return SOURCE_TYPE_NAMES[sourceType];
    }

    private String getOperatorName(Long operatorId) {
        if (operatorId == null) return null;
        SysUser user = sysUserMapper.selectById(operatorId);
        return user != null ? user.getUsername() : null;
    }

    private BigDecimal getAmountByType(Long institutionId, Byte recordType, LocalDate start, LocalDate end) {
        LambdaQueryWrapper<FinanceRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceRecord::getDeleted, 0);
        wrapper.eq(FinanceRecord::getRecordType, recordType);
        if (institutionId != null) {
            wrapper.eq(FinanceRecord::getInstitutionId, institutionId);
        }
        if (start != null) {
            wrapper.ge(FinanceRecord::getRecordDate, start);
        }
        if (end != null) {
            wrapper.le(FinanceRecord::getRecordDate, end);
        }

        List<FinanceRecord> records = this.list(wrapper);
        return records.stream()
                .map(r -> r.getAmount() != null ? r.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getAmountByTypeAndTime(Long institutionId, Byte recordType,
            LocalDateTime start, LocalDateTime end) {
        LambdaQueryWrapper<FinanceRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceRecord::getDeleted, 0);
        wrapper.eq(FinanceRecord::getRecordType, recordType);
        if (institutionId != null) {
            wrapper.eq(FinanceRecord::getInstitutionId, institutionId);
        }
        wrapper.between(FinanceRecord::getCreateTime, start, end);

        List<FinanceRecord> records = this.list(wrapper);
        return records.stream()
                .map(r -> r.getAmount() != null ? r.getAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<Map<String, Object>> getAmountBySource(Long institutionId, Byte recordType) {
        List<Map<String, Object>> result = new ArrayList<>();

        // 收入来源：1-订单收入，2-服务收入，5-其他
        // 支出来源：3-物资采购，4-人员工资，5-其他
        int[] sourceTypes = recordType == 1 ? new int[]{1, 2, 5} : new int[]{3, 4, 5};

        for (int sourceType : sourceTypes) {
            LambdaQueryWrapper<FinanceRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FinanceRecord::getDeleted, 0);
            wrapper.eq(FinanceRecord::getRecordType, recordType);
            wrapper.eq(FinanceRecord::getSourceType, sourceType);
            if (institutionId != null) {
                wrapper.eq(FinanceRecord::getInstitutionId, institutionId);
            }

            List<FinanceRecord> records = this.list(wrapper);
            BigDecimal amount = records.stream()
                    .map(r -> r.getAmount() != null ? r.getAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> item = new HashMap<>();
            item.put("sourceType", sourceType);
            item.put("name", getSourceTypeName((byte) sourceType));
            item.put("amount", amount);
            result.add(item);
        }

        return result;
    }

    private List<Map<String, Object>> getAmountTrend(Long institutionId, Byte recordType, int days) {
        List<Map<String, Object>> trend = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);

            LambdaQueryWrapper<FinanceRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FinanceRecord::getDeleted, 0);
            wrapper.eq(FinanceRecord::getRecordType, recordType);
            wrapper.eq(FinanceRecord::getRecordDate, date);
            if (institutionId != null) {
                wrapper.eq(FinanceRecord::getInstitutionId, institutionId);
            }

            List<FinanceRecord> records = this.list(wrapper);
            BigDecimal amount = records.stream()
                    .map(r -> r.getAmount() != null ? r.getAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> item = new HashMap<>();
            item.put("date", date.format(formatter));
            item.put("amount", amount);
            trend.add(item);
        }

        return trend;
    }
}