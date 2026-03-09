<template>
  <div>
    <el-card shadow="never" style="margin-bottom:16px">
      <el-form :inline="true" :model="query">
        <el-form-item label="告警级别">
          <el-select v-model="query.level" placeholder="全部" clearable>
            <el-option label="紧急" value="紧急" />
            <el-option label="重要" value="重要" />
            <el-option label="一般" value="一般" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理状态">
          <el-select v-model="query.status" placeholder="全部" clearable>
            <el-option label="未处理" value="未处理" />
            <el-option label="处理中" value="处理中" />
            <el-option label="已处理" value="已处理" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 告警统计 -->
    <el-row :gutter="16" style="margin-bottom:16px">
      <el-col :span="8">
        <div class="stat-card" style="border-left:4px solid #ff4d4f">
          <div style="color:#999;font-size:13px">紧急告警</div>
          <div style="font-size:28px;font-weight:700;color:#ff4d4f;margin-top:8px">5</div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-card" style="border-left:4px solid #faad14">
          <div style="color:#999;font-size:13px">重要告警</div>
          <div style="font-size:28px;font-weight:700;color:#faad14;margin-top:8px">12</div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="stat-card" style="border-left:4px solid #1890FF">
          <div style="color:#999;font-size:13px">一般告警</div>
          <div style="font-size:28px;font-weight:700;color:#1890FF;margin-top:8px">28</div>
        </div>
      </el-col>
    </el-row>

    <el-card shadow="never">
      <el-table :data="alarms" stripe>
        <el-table-column prop="time" label="告警时间" width="160" />
        <el-table-column prop="level" label="级别" width="90">
          <template #default="{ row }">
            <el-tag :type="row.level === '紧急' ? 'danger' : row.level === '重要' ? 'warning' : 'info'" size="small">{{ row.level }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="equipment" label="设备" width="180" />
        <el-table-column prop="message" label="告警内容" min-width="200" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '未处理' ? 'danger' : row.status === '处理中' ? 'warning' : 'success'" size="small" effect="plain">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" v-if="row.status === '未处理'">处理</el-button>
            <el-button link type="primary">详情</el-button>
            <el-button link type="primary">创建工单</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

const query = reactive({ level: '', status: '' })

const alarms = ref([
  { time: '2024-03-08 10:15', level: '紧急', equipment: 'CNC-001 五轴加工中心', message: '主轴温度异常，当前92°C，阈值85°C', status: '处理中' },
  { time: '2024-03-08 09:50', level: '紧急', equipment: 'ROBOT-005 搬运机器人', message: '急停信号触发，设备已停机', status: '未处理' },
  { time: '2024-03-08 09:30', level: '重要', equipment: 'PLC-003 控制器', message: '通信中断，持续时间超过5分钟', status: '处理中' },
  { time: '2024-03-08 08:45', level: '一般', equipment: 'CONV-008 皮带输送机', message: '皮带张力偏低，建议检查', status: '未处理' },
  { time: '2024-03-08 08:00', level: '一般', equipment: 'PUMP-012 液压泵站', message: '运行时长超过维护周期（500h）', status: '未处理' },
  { time: '2024-03-07 16:30', level: '重要', equipment: 'CNC-002 数控车床', message: '刀具磨损预警，剩余寿命<10%', status: '已处理' },
  { time: '2024-03-07 14:00', level: '一般', equipment: 'ROBOT-001 焊接机器人', message: '焊丝余量不足，请及时补充', status: '已处理' },
])

function loadData() { /* 后端对接后替换 */ }
</script>
