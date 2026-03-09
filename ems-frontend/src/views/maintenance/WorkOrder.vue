<template>
  <div>
    <el-card shadow="never" style="margin-bottom:16px">
      <el-form :inline="true" :model="query">
        <el-form-item label="工单状态">
          <el-select v-model="query.status" placeholder="全部" clearable>
            <el-option label="待接单" value="pending" />
            <el-option label="处理中" value="processing" />
            <el-option label="已完成" value="completed" />
            <el-option label="已关闭" value="closed" />
          </el-select>
        </el-form-item>
        <el-form-item label="工单类型">
          <el-select v-model="query.type" placeholder="全部" clearable>
            <el-option label="故障维修" value="repair" />
            <el-option label="预防维护" value="preventive" />
            <el-option label="巡检" value="inspection" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="Object.assign(query, { status: '', type: '' }); loadData()">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <div style="display:flex;justify-content:space-between;margin-bottom:16px">
        <el-button type="primary" @click="showCreate = true"><el-icon><Plus /></el-icon> 创建工单</el-button>
        <span style="color:#999;font-size:13px;line-height:32px">共 {{ orders.length }} 条工单</span>
      </div>

      <el-table :data="orders" stripe>
        <el-table-column prop="code" label="工单编号" width="140" />
        <el-table-column prop="title" label="工单标题" min-width="180" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === '故障维修' ? 'danger' : row.type === '预防维护' ? 'primary' : 'success'" size="small">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="equipment" label="关联设备" width="160" />
        <el-table-column prop="priority" label="优先级" width="90">
          <template #default="{ row }">
            <el-tag :type="row.priority === '紧急' ? 'danger' : row.priority === '高' ? 'warning' : 'info'" size="small" effect="plain">{{ row.priority }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="assignee" label="负责人" width="90" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType[row.status]" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="$router.push({ name: 'work-order-detail', params: { id: row.id } })">详情</el-button>
            <el-button link type="primary">处理</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showCreate" title="创建工单" width="600px">
      <el-form :model="createForm" label-width="100px">
        <el-form-item label="工单标题"><el-input v-model="createForm.title" /></el-form-item>
        <el-form-item label="工单类型">
          <el-select v-model="createForm.type" style="width:100%">
            <el-option label="故障维修" value="故障维修" />
            <el-option label="预防维护" value="预防维护" />
            <el-option label="巡检" value="巡检" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联设备"><el-input v-model="createForm.equipment" /></el-form-item>
        <el-form-item label="优先级">
          <el-select v-model="createForm.priority" style="width:100%">
            <el-option label="紧急" value="紧急" />
            <el-option label="高" value="高" />
            <el-option label="中" value="中" />
            <el-option label="低" value="低" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述"><el-input v-model="createForm.desc" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCreate = false">取消</el-button>
        <el-button type="primary" @click="showCreate = false">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

const statusType = { '待接单': 'info', '处理中': 'warning', '已完成': 'success', '已关闭': '' }
const query = reactive({ status: '', type: '' })
const showCreate = ref(false)
const createForm = reactive({ title: '', type: '', equipment: '', priority: '', desc: '' })

const orders = ref([
  { id: 1, code: 'WO-20240308-001', title: 'CNC-001 主轴温度异常维修', type: '故障维修', equipment: 'CNC-001 五轴加工中心', priority: '紧急', assignee: '张工', status: '处理中', createTime: '2024-03-08 09:30' },
  { id: 2, code: 'WO-20240307-003', title: 'ROBOT-005 急停故障排查', type: '故障维修', equipment: 'ROBOT-005 搬运机器人', priority: '高', assignee: '李工', status: '待接单', createTime: '2024-03-07 14:20' },
  { id: 3, code: 'WO-20240306-002', title: '月度设备巡检-A区', type: '巡检', equipment: '多台设备', priority: '中', assignee: '王工', status: '处理中', createTime: '2024-03-06 08:00' },
  { id: 4, code: 'WO-20240305-001', title: 'PLC-003 通信模块更换', type: '故障维修', equipment: 'PLC-003 控制器', priority: '高', assignee: '赵工', status: '已完成', createTime: '2024-03-05 10:15' },
  { id: 5, code: 'WO-20240301-001', title: 'CNC-002 季度保养', type: '预防维护', equipment: 'CNC-002 数控车床', priority: '低', assignee: '张工', status: '已关闭', createTime: '2024-03-01 09:00' },
])

function loadData() { /* 后端对接后替换 */ }
</script>
