package com.robot.host.base.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.robot.host.base.entry.PatrolTaskExecEntity;
import com.robot.host.base.mapper.PatrolTaskExecMapper;
import com.robot.host.base.service.PatrolTaskExecService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service("patrolTaskExecService")
@Slf4j
public class PatrolTaskExecServiceImpl extends ServiceImpl<PatrolTaskExecMapper, PatrolTaskExecEntity> implements PatrolTaskExecService {
//    @Autowired
//    private PatrolTaskService taskService;

//    @Override
//    public PageUtils2 queryPage(Map<String, Object> params) {
//        IPage<PatrolTaskExecEntity> page = this.page(
//                new Query<PatrolTaskExecEntity>().getPage(params),
//                new QueryWrapper<PatrolTaskExecEntity>()
//        );
//
//        return new PageUtils2(page);
//    }
//
//    public static void main(String[] args) {
//        String month ="2012-1";
//            String[] arr = month.split("-");
//            if (arr[1].length() == 1) {
//                month = arr[0] + "-0" + arr[1];
//            }
//            month = DateUtil.format(new Date(), "yyyy-MM");
//    }
//    @Override
//    public List<PatrolMonthlyCalendarItemDTO> monthlyCalendar(PatrolTaskVO params) {
//        //结果集
//        List<PatrolMonthlyCalendarItemDTO> resList = Lists.newArrayList();
//        String month = params.getMonth();
//        if (StringUtils.isBlank(month)) {
//            month = DateUtil.format(new Date(), "yyyy-MM");
//        }else {
//            String[] arr = month.split("-");
//            if (arr[1].length() == 1) {
//                month = arr[0] + "-0" + arr[1];
//            }
//        }
//        List<String> monthList = DateUtils.getMonthFullDay(month);
//        if (monthList == null) {
//            log.error("【巡检月历】参数异常{}", params.toString());
//            return Lists.newArrayList();
//        }
//        //判断当前日期、月初、月末，三种情况
//        String firstDay = DateUtils.getFirstDayOfMonth(month);
//        String lastDay = DateUtils.getLastDayOfMonth(month);
//        String current = DateUtil.formatDate(new Date());
//        //当前日期---月初----月末
//        if (firstDay.compareTo(current) >= 0) {
//            List<PatrolMonthlyCalendarItemDTO> futureList = this.calcFutureMonthlyPatrol(firstDay, lastDay, params.getSiteId());
//            resList.addAll(futureList);
//        } else if (firstDay.compareTo(current) < 0 && lastDay.compareTo(current) > 0) {
//            //月初---当前日期--月末
//            List<PatrolMonthlyCalendarItemDTO> futureList = this.calcFutureMonthlyPatrol(current, lastDay, params.getSiteId());
//            List<PatrolMonthlyCalendarItemDTO> oldList = this.calcOldMonthlyPatrol(current, lastDay, params.getSiteId());
//            resList.addAll(oldList);
//            resList.addAll(futureList);
//        } else if (current.compareTo(lastDay) > 0) {
//            //月初--月末---当前日期
//            List<PatrolMonthlyCalendarItemDTO> oldList = this.calcOldMonthlyPatrol(current, lastDay, params.getSiteId());
//            resList.addAll(oldList);
//        }
//        return resList;
//    }
//
//
//    List<PatrolMonthlyCalendarItemDTO> calcOldMonthlyPatrol(String startDate, String endDate, Long siteId) {
//        List<PatrolMonthlyCalendarItemDTO> resList = Lists.newArrayList();
//
//        List<MothlyPatrolMapperDTO> patrolMapperDTOS = this.baseMapper.monthlyPatrol(startDate, endDate, siteId);
//        patrolMapperDTOS.forEach(dto -> {
//            //全面巡检
//            PatrolMonthlyCalendarItemDTO overallDTO = new PatrolMonthlyCalendarItemDTO();
//            overallDTO.setStart(dto.getDate());
//            overallDTO.setTitle(EnumPatrolType.overall.getText() + " " + dto.getOverallPatrolNum() + "次");
//            resList.add(overallDTO);
//            //例行巡检
//            PatrolMonthlyCalendarItemDTO routineDTO = new PatrolMonthlyCalendarItemDTO();
//            routineDTO.setStart(dto.getDate());
//            routineDTO.setTitle(EnumPatrolType.routine.getText() + " " + dto.getRoutinePatrolNum() + "次");
//            resList.add(routineDTO);
//            //专项巡检
//            PatrolMonthlyCalendarItemDTO assignDTO = new PatrolMonthlyCalendarItemDTO();
//            assignDTO.setStart(dto.getDate());
//            assignDTO.setTitle(EnumPatrolType.assign.getText() + " " + dto.getProjectPatrolNum() + "次");
//            resList.add(assignDTO);
//            //特殊巡检
//            PatrolMonthlyCalendarItemDTO specialDTO = new PatrolMonthlyCalendarItemDTO();
//            specialDTO.setStart(dto.getDate());
//            specialDTO.setTitle(EnumPatrolType.special.getText() + " " + dto.getSpecialPatrolNum() + "次");
//            resList.add(specialDTO);
//            //熄灯巡检
//            PatrolMonthlyCalendarItemDTO lightoutDTO = new PatrolMonthlyCalendarItemDTO();
//            lightoutDTO.setStart(dto.getDate());
//            lightoutDTO.setTitle(EnumPatrolType.lightout.getText() + " " + dto.getLightsOutPatrolNum() + "次");
//            resList.add(lightoutDTO);
//            //自定义巡检
//            PatrolMonthlyCalendarItemDTO customDTO = new PatrolMonthlyCalendarItemDTO();
//            customDTO.setStart(dto.getDate());
//            customDTO.setTitle(EnumPatrolType.custom.getText() + " " + dto.getSelfPatrolNum() + "次");
//            resList.add(customDTO);
//        });
//        return resList;
//    }
//
//    /**
//     * 计算未来的月历，
//     * 把所有 待执行 的巡检任务的cron取出来
//     * //判断时间段内的时间是否在巡检任务中
//     *
//     * @param startDate
//     * @param endDate
//     * @return
//     */
//    List<PatrolMonthlyCalendarItemDTO> calcFutureMonthlyPatrol(String startDate, String endDate, Long siteId) {
//        //结果集合
//        List<PatrolMonthlyCalendarItemDTO> resList = Lists.newArrayList();
//        //把所有 待执行 的巡检任务的cron取出来
//        List<PatrolTaskEntity> taskEntities = taskService.list(new QueryWrapper<PatrolTaskEntity>().lambda().select(PatrolTaskEntity::getCron, PatrolTaskEntity::getTaskBeginTime, PatrolTaskEntity::getTaskEndTime, PatrolTaskEntity::getPatrolType)
//                .eq(PatrolTaskEntity::getAuditStatus, EnumPatrolAuditStatus.pass.getValue())
//                .in(PatrolTaskEntity::getExecStatus, EnumPatrolExecStatus.notstart.getValue(), EnumPatrolExecStatus.underway.getValue()
//                        , EnumPatrolExecStatus.pause.getValue())
//                .eq(PatrolTaskEntity::getSiteId, siteId)
//        );
//        Multimap<Integer, PatrolTaskEntity> cronMultimap = ArrayListMultimap.create();
//        taskEntities.forEach(v -> {
//            cronMultimap.put(v.getPatrolType(), v);
//        });
//        //判断时间段内的时间是否在巡检任务中
//        String[] startDateArr = startDate.split("-");
//        int startDay = Integer.valueOf(startDateArr[2]);
//        String[] endDateArr = endDate.split("-");
//        int endDay = Integer.valueOf(endDateArr[2]);
//        Calendar cal = Calendar.getInstance();
//        for (int i = startDay; i <= endDay; i++) {
//            cal.set(Calendar.YEAR, Integer.parseInt(startDateArr[0]));
//            cal.set(Calendar.MONTH, Integer.parseInt(startDateArr[1]) - 1);
//            cal.set(Calendar.DATE, i);
//            cal.set(Calendar.HOUR_OF_DAY, 0);
//            cal.set(Calendar.MINUTE, 0);
//            cal.set(Calendar.SECOND, 0);
//            Date date = cal.getTime();
//            //2例行巡检、1全面巡检、4特殊巡检、5熄灯巡检、3专项巡检、6自定义巡检
//            for (int typeIndex = 1; typeIndex <= 6; typeIndex++) {
//                Collection<PatrolTaskEntity> cronTask = cronMultimap.get(typeIndex);
//                if (org.apache.commons.collections.CollectionUtils.isEmpty(cronTask)) {
//                    continue;
//                }
//                int typeNum = 0;
//                for (PatrolTaskEntity itemCron : cronTask) {
//                    Date taskBeginTime = itemCron.getTaskBeginTime();
//                    Date taskEndTime = itemCron.getTaskEndTime();
//                    if ((taskBeginTime == null && taskEndTime == null) || (date.getTime() >= taskBeginTime.getTime()
//                            && date.getTime() <= taskEndTime.getTime())) {
//                        //当前日期符合cron
//                        if (DateUtils.filterWithCronTime(itemCron.getCron(), date)) {
//                            typeNum += 1;
//                        }
//                    }
//                }
//                if (typeNum > 0) {
//                    PatrolMonthlyCalendarItemDTO itemDTO = new PatrolMonthlyCalendarItemDTO();
//                    itemDTO.setStart(DateUtil.formatDate(date));
//                    EnumPatrolType type = EnumPatrolType.valueOf(typeIndex);
//                    itemDTO.setTitle(type.getText() + " " + typeNum + "次");
//                    itemDTO.setType(typeIndex);
//                    resList.add(itemDTO);
//                }
//
//            }
//            //当前日期符合定时任务cron
//
//        }
//        return resList;
//    }


}
