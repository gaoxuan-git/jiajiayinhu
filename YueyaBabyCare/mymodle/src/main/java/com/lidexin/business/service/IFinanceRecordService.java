package com.lidexin.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lidexin.business.po.FinanceRecord;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 财务记录服务接口
 *
 * @author 张三
 * @since 2025-12-21
 */
public interface IFinanceRecordService extends IService<FinanceRecord> {

    /**
     * 新增财务记录
     */
    void addFinanceRecord(FinanceRecord record);

    /**
     * 获取财务记录列表（分页）
     */
    Page<FinanceRecordVO> getFinanceRecordList(Long institutionId, Byte recordType,
            String startDate, String endDate, Integer pageNum, Integer pageSize);

    /**
     * 获取财务统计
     */
    FinanceStatsVO getFinanceStats(Long institutionId, String startDate, String endDate);

    /**
     * 获取月度汇总
     */
    List<MonthlySummaryVO> getMonthlySummary(Long institutionId, Integer year);

    /**
     * 导出财务报表数据
     */
    FinanceExportVO exportFinanceReport(Long institutionId, String startDate, String endDate);

    // ==================== VO类定义 ====================

    /**
     * 财务记录VO
     */
    class FinanceRecordVO {
        private Long id;
        private Long institutionId;
        private Byte recordType;
        private String recordTypeName;
        private BigDecimal amount;
        private Byte sourceType;
        private String sourceTypeName;
        private Long sourceId;
        private String remark;
        private String recordDate;
        private Long operatorId;
        private String operatorName;
        private String createTime;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getInstitutionId() { return institutionId; }
        public void setInstitutionId(Long institutionId) { this.institutionId = institutionId; }
        public Byte getRecordType() { return recordType; }
        public void setRecordType(Byte recordType) { this.recordType = recordType; }
        public String getRecordTypeName() { return recordTypeName; }
        public void setRecordTypeName(String recordTypeName) { this.recordTypeName = recordTypeName; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public Byte getSourceType() { return sourceType; }
        public void setSourceType(Byte sourceType) { this.sourceType = sourceType; }
        public String getSourceTypeName() { return sourceTypeName; }
        public void setSourceTypeName(String sourceTypeName) { this.sourceTypeName = sourceTypeName; }
        public Long getSourceId() { return sourceId; }
        public void setSourceId(Long sourceId) { this.sourceId = sourceId; }
        public String getRemark() { return remark; }
        public void setRemark(String remark) { this.remark = remark; }
        public String getRecordDate() { return recordDate; }
        public void setRecordDate(String recordDate) { this.recordDate = recordDate; }
        public Long getOperatorId() { return operatorId; }
        public void setOperatorId(Long operatorId) { this.operatorId = operatorId; }
        public String getOperatorName() { return operatorName; }
        public void setOperatorName(String operatorName) { this.operatorName = operatorName; }
        public String getCreateTime() { return createTime; }
        public void setCreateTime(String createTime) { this.createTime = createTime; }
    }

    /**
     * 财务统计VO
     */
    class FinanceStatsVO {
        private BigDecimal totalIncome;         // 总收入
        private BigDecimal totalExpense;        // 总支出
        private BigDecimal netIncome;           // 净收入
        private BigDecimal todayIncome;         // 今日收入
        private BigDecimal todayExpense;        // 今日支出
        private BigDecimal monthIncome;         // 本月收入
        private BigDecimal monthExpense;        // 本月支出
        private Integer totalRecords;           // 记录总数
        private List<Map<String, Object>> incomeBySource;   // 收入来源分布
        private List<Map<String, Object>> expenseBySource;  // 支出来源分布
        private List<Map<String, Object>> incomeTrend;      // 收入趋势（近7天）
        private List<Map<String, Object>> expenseTrend;     // 支出趋势（近7天）

        public BigDecimal getTotalIncome() { return totalIncome; }
        public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }
        public BigDecimal getTotalExpense() { return totalExpense; }
        public void setTotalExpense(BigDecimal totalExpense) { this.totalExpense = totalExpense; }
        public BigDecimal getNetIncome() { return netIncome; }
        public void setNetIncome(BigDecimal netIncome) { this.netIncome = netIncome; }
        public BigDecimal getTodayIncome() { return todayIncome; }
        public void setTodayIncome(BigDecimal todayIncome) { this.todayIncome = todayIncome; }
        public BigDecimal getTodayExpense() { return todayExpense; }
        public void setTodayExpense(BigDecimal todayExpense) { this.todayExpense = todayExpense; }
        public BigDecimal getMonthIncome() { return monthIncome; }
        public void setMonthIncome(BigDecimal monthIncome) { this.monthIncome = monthIncome; }
        public BigDecimal getMonthExpense() { return monthExpense; }
        public void setMonthExpense(BigDecimal monthExpense) { this.monthExpense = monthExpense; }
        public Integer getTotalRecords() { return totalRecords; }
        public void setTotalRecords(Integer totalRecords) { this.totalRecords = totalRecords; }
        public List<Map<String, Object>> getIncomeBySource() { return incomeBySource; }
        public void setIncomeBySource(List<Map<String, Object>> incomeBySource) { this.incomeBySource = incomeBySource; }
        public List<Map<String, Object>> getExpenseBySource() { return expenseBySource; }
        public void setExpenseBySource(List<Map<String, Object>> expenseBySource) { this.expenseBySource = expenseBySource; }
        public List<Map<String, Object>> getIncomeTrend() { return incomeTrend; }
        public void setIncomeTrend(List<Map<String, Object>> incomeTrend) { this.incomeTrend = incomeTrend; }
        public List<Map<String, Object>> getExpenseTrend() { return expenseTrend; }
        public void setExpenseTrend(List<Map<String, Object>> expenseTrend) { this.expenseTrend = expenseTrend; }
    }

    /**
     * 月度汇总VO
     */
    class MonthlySummaryVO {
        private Integer year;
        private Integer month;
        private String monthName;
        private BigDecimal income;
        private BigDecimal expense;
        private BigDecimal netIncome;
        private Integer recordCount;

        public Integer getYear() { return year; }
        public void setYear(Integer year) { this.year = year; }
        public Integer getMonth() { return month; }
        public void setMonth(Integer month) { this.month = month; }
        public String getMonthName() { return monthName; }
        public void setMonthName(String monthName) { this.monthName = monthName; }
        public BigDecimal getIncome() { return income; }
        public void setIncome(BigDecimal income) { this.income = income; }
        public BigDecimal getExpense() { return expense; }
        public void setExpense(BigDecimal expense) { this.expense = expense; }
        public BigDecimal getNetIncome() { return netIncome; }
        public void setNetIncome(BigDecimal netIncome) { this.netIncome = netIncome; }
        public Integer getRecordCount() { return recordCount; }
        public void setRecordCount(Integer recordCount) { this.recordCount = recordCount; }
    }

    /**
     * 财务导出VO
     */
    class FinanceExportVO {
        private String startDate;
        private String endDate;
        private BigDecimal totalIncome;
        private BigDecimal totalExpense;
        private BigDecimal netIncome;
        private List<FinanceRecordVO> records;

        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }
        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }
        public BigDecimal getTotalIncome() { return totalIncome; }
        public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }
        public BigDecimal getTotalExpense() { return totalExpense; }
        public void setTotalExpense(BigDecimal totalExpense) { this.totalExpense = totalExpense; }
        public BigDecimal getNetIncome() { return netIncome; }
        public void setNetIncome(BigDecimal netIncome) { this.netIncome = netIncome; }
        public List<FinanceRecordVO> getRecords() { return records; }
        public void setRecords(List<FinanceRecordVO> records) { this.records = records; }
    }
}
