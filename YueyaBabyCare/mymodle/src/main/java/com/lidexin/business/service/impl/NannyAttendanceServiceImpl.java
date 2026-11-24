package com.lidexin.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lidexin.business.mapper.NannyAttendanceMapper;
import com.lidexin.business.po.Nanny;
import com.lidexin.business.po.NannyAttendance;
import com.lidexin.business.service.INannyAttendanceService;
import com.lidexin.business.service.INannyService;
import com.lidexin.business.vo.NannyAttendanceVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NannyAttendanceServiceImpl extends ServiceImpl<NannyAttendanceMapper, NannyAttendance> implements INannyAttendanceService {

    @Autowired
    private INannyService nannyService;

    @Override
    public Page<NannyAttendanceVO> getAttendancePage(Long institutionId, Long nannyId,
                                                      Byte status, LocalDate startDate,
                                                      LocalDate endDate, Integer pageNum, Integer pageSize) {
        LambdaQueryWrapper<NannyAttendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NannyAttendance::getInstitutionId, institutionId)
               .eq(NannyAttendance::getDeleted, (byte) 0);

        if (nannyId != null) {
            wrapper.eq(NannyAttendance::getNannyId, nannyId);
        }
        if (status != null) {
            wrapper.eq(NannyAttendance::getStatus, status);
        }
        if (startDate != null) {
            wrapper.ge(NannyAttendance::getAttendanceDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(NannyAttendance::getAttendanceDate, endDate);
        }
        wrapper.orderByDesc(NannyAttendance::getAttendanceDate)
               .orderByDesc(NannyAttendance::getCreateTime);

        Page<NannyAttendance> page = this.page(new Page<>(pageNum, pageSize), wrapper);

        Set<Long> nannyIds = page.getRecords().stream()
                .map(NannyAttendance::getNannyId)
                .collect(Collectors.toSet());

        Map<Long, Nanny> nannyMap = new HashMap<>();
        if (!nannyIds.isEmpty()) {
            List<Nanny> nannies = nannyService.listByIds(nannyIds);
            for (Nanny n : nannies) {
                nannyMap.put(n.getId(), n);
            }
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Page<NannyAttendanceVO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        List<NannyAttendanceVO> voList = page.getRecords().stream().map(att -> {
            NannyAttendanceVO vo = new NannyAttendanceVO();
            vo.setId(att.getId());
            vo.setInstitutionId(att.getInstitutionId());
            vo.setNannyId(att.getNannyId());
            vo.setScheduleId(att.getScheduleId());
            vo.setAttendanceDate(att.getAttendanceDate());
            vo.setCheckInTime(att.getCheckInTime());
            vo.setCheckOutTime(att.getCheckOutTime());
            vo.setStatus(att.getStatus());
            vo.setStatusName(NannyAttendanceVO.getStatusName(att.getStatus()));
            vo.setRemark(att.getRemark());
            vo.setCreateTime(att.getCreateTime() != null ? att.getCreateTime().format(dtf) : null);

            Nanny nanny = nannyMap.get(att.getNannyId());
            if (nanny != null) {
                vo.setNannyName(nanny.getRealName());
                vo.setNannyPhone(nanny.getPhone());
            }
            return vo;
        }).collect(Collectors.toList());

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public void checkIn(Long institutionId, Long nannyId, String remark) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        LambdaQueryWrapper<NannyAttendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NannyAttendance::getInstitutionId, institutionId)
               .eq(NannyAttendance::getNannyId, nannyId)
               .eq(NannyAttendance::getAttendanceDate, today)
               .eq(NannyAttendance::getDeleted, (byte) 0);

        NannyAttendance existing = this.getOne(wrapper, false);
        if (existing != null && existing.getCheckInTime() != null) {
            throw new RuntimeException("今日已签到，请勿重复操作");
        }

        if (existing != null) {
            existing.setCheckInTime(now);
            existing.setStatus((byte) 1);
            existing.setRemark(remark);
            existing.setUpdateTime(now);
            this.updateById(existing);
        } else {
            NannyAttendance attendance = new NannyAttendance();
            attendance.setInstitutionId(institutionId);
            attendance.setNannyId(nannyId);
            attendance.setAttendanceDate(today);
            attendance.setCheckInTime(now);
            attendance.setStatus((byte) 1);
            attendance.setRemark(remark);
            attendance.setCreateTime(now);
            attendance.setUpdateTime(now);
            attendance.setDeleted((byte) 0);
            this.save(attendance);
        }
    }

    @Override
    public void checkOut(Long institutionId, Long nannyId, String remark) {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        LambdaQueryWrapper<NannyAttendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NannyAttendance::getInstitutionId, institutionId)
               .eq(NannyAttendance::getNannyId, nannyId)
               .eq(NannyAttendance::getAttendanceDate, today)
               .eq(NannyAttendance::getDeleted, (byte) 0);

        NannyAttendance existing = this.getOne(wrapper, false);
        if (existing == null || existing.getCheckInTime() == null) {
            throw new RuntimeException("今日尚未签到，无法签退");
        }
        if (existing.getCheckOutTime() != null) {
            throw new RuntimeException("今日已签退，请勿重复操作");
        }

        existing.setCheckOutTime(now);
        if (remark != null && !remark.isEmpty()) {
            existing.setRemark(remark);
        }
        existing.setUpdateTime(now);
        this.updateById(existing);
    }

    @Override
    public void updateStatus(Long institutionId, Long id, Byte status, String remark) {
        NannyAttendance attendance = this.getById(id);
        if (attendance == null || !attendance.getInstitutionId().equals(institutionId)) {
            throw new RuntimeException("考勤记录不存在");
        }
        attendance.setStatus(status);
        if (remark != null) {
            attendance.setRemark(remark);
        }
        attendance.setUpdateTime(LocalDateTime.now());
        this.updateById(attendance);
    }

    @Override
    public Map<String, Object> getStatistics(Long institutionId, LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            startDate = LocalDate.now().withDayOfMonth(1);
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        LambdaQueryWrapper<NannyAttendance> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NannyAttendance::getInstitutionId, institutionId)
               .eq(NannyAttendance::getDeleted, (byte) 0)
               .ge(NannyAttendance::getAttendanceDate, startDate)
               .le(NannyAttendance::getAttendanceDate, endDate);

        List<NannyAttendance> list = this.list(wrapper);

        long total = list.size();
        long normal = list.stream().filter(a -> a.getStatus() != null && a.getStatus() == 1).count();
        long late = list.stream().filter(a -> a.getStatus() != null && a.getStatus() == 2).count();
        long earlyLeave = list.stream().filter(a -> a.getStatus() != null && a.getStatus() == 3).count();
        long absent = list.stream().filter(a -> a.getStatus() != null && a.getStatus() == 0).count();
        long leave = list.stream().filter(a -> a.getStatus() != null && a.getStatus() == 4).count();

        // 今日考勤
        LocalDate today = LocalDate.now();
        long todayCheckedIn = list.stream()
                .filter(a -> today.equals(a.getAttendanceDate()) && a.getCheckInTime() != null)
                .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("normal", normal);
        stats.put("late", late);
        stats.put("earlyLeave", earlyLeave);
        stats.put("absent", absent);
        stats.put("leave", leave);
        stats.put("todayCheckedIn", todayCheckedIn);
        stats.put("attendanceRate", total > 0 ? Math.round(normal * 100.0 / total) : 0);

        return stats;
    }
}
