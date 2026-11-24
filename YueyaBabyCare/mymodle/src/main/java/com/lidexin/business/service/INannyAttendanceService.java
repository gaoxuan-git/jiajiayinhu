package com.lidexin.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lidexin.business.po.NannyAttendance;
import com.lidexin.business.vo.NannyAttendanceVO;

import java.time.LocalDate;
import java.util.Map;

public interface INannyAttendanceService extends IService<NannyAttendance> {

    Page<NannyAttendanceVO> getAttendancePage(Long institutionId, Long nannyId,
                                               Byte status, LocalDate startDate,
                                               LocalDate endDate, Integer pageNum, Integer pageSize);

    void checkIn(Long institutionId, Long nannyId, String remark);

    void checkOut(Long institutionId, Long nannyId, String remark);

    void updateStatus(Long institutionId, Long id, Byte status, String remark);

    Map<String, Object> getStatistics(Long institutionId, LocalDate startDate, LocalDate endDate);
}
