package com.lidexin.business.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class NannyAttendanceVO {

    private Long id;
    private Long institutionId;
    private Long nannyId;
    private String nannyName;
    private String nannyPhone;
    private Long scheduleId;
    private LocalDate attendanceDate;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private Byte status;
    private String statusName;
    private String remark;
    private String createTime;

    public static String getStatusName(Byte status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "缺勤";
            case 1: return "正常";
            case 2: return "迟到";
            case 3: return "早退";
            case 4: return "请假";
            default: return "未知";
        }
    }
}
