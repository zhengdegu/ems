<template>
  <div>
    <el-card shadow="never" style="margin-bottom:16px">
      <div style="display:flex;justify-content:space-between;margin-bottom:16px">
        <div>
          <el-button type="primary" @click="showDialog = true"><el-icon><Plus /></el-icon> 新建计划</el-button>
        </div>
        <el-radio-group v-model="viewMode" size="small">
          <el-radio-button label="table">列表</el-radio-button>
          <el-radio-button label="calendar">日历</el-radio-button>
        </el-radio-group>
      </div>

      <el-table v-if="viewMode === 'table'" :data="plans" stripe>
        <el-table-column prop="name" label="计划名称" min-width="160" />
        <el-table-column prop="type" label="维护类型" width="120">
          <template #default="{ row }">
            <el-tag :type="row.type === '预防维护' ? 'primary' : row.type === '定期保养' ? 'success' : 'warning'" size="small">{{ row.type }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="equipment" label="关联设备" width="160" />
        <el-table-column prop="cycle" label="执行周期" width="120" />
        <el-table-column prop="nextDate" label="下次执行" width="120" />
        <el-table-column prop="responsible" label="负责人" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === '启用' ? 'success' : 'info'" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default>
            <el-button link type="primary">编辑</el-button>
            <el-button link type="primary">执行</el-button>
            <el-button link type="danger">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-else style="text-align:center;padding:60px;color:#999">
        <el-icon :size="48"><Calendar /></el-icon>
        <p style="margin-top:12px">日历视图开发中...</p>
      </div>
    </el-card>

    <!-- 新建计划弹窗 -->
    <el-dialog v-model="showDialog" title="新建维护计划" width="600px">
      <el-form :model="planForm" label-width="100px">
        <el-form-item label="计划名称"><el-input v-model="planForm.name" /></el-form-item>
        <el-form-item label="维护类型">
          <el-select v-model="planForm.type" style="width:100%">
            <el-option label="预防维护" value="预防维护" />
            <el-option label="定期保养" value="定期保养" />
            <el-option label="巡检" value="巡检" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联设备"><el-input v-model="planForm.equipment" /></el-form-item>
        <el-form-item label="执行周期">
          <el-select v-model="planForm.cycle" style="width:100%">
            <el-option label="每周" value="每周" />
            <el-option label="每月" value="每月" />
            <el-option label="每季度" value="每季度" />
            <el-option label="每半年" value="每半年" />
            <el-option label="每年" value="每年" />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人"><el-input v-model="planForm.responsible" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showDialog = false">取消</el-button>
        <el-button type="primary" @click="showDialog = false">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

const viewMode = ref('table')
const showDialog = ref(false)
const planForm = reactive({ name: '', type: '', equipment: '', cycle: '', responsible: '' })

const plans = ref([
  { name: 'CNC机床月度保养', type: '定期保养', equipment: 'CNC-001 五轴加工中心', cycle: '每月', nextDate: '2024-04-01', responsible: '张工', status: '启用' },
  { name: '机器人季度检查', type: '预防维护', equipment: 'ROBOT-001 焊接机器人', cycle: '每季度', nextDate: '2024-06-01', responsible: '李工', status: '启用' },
  { name: '输送线周巡检', type: '巡检', equipment: 'CONV-008 皮带输送机', cycle: '每周', nextDate: '2024-03-11', responsible: '王工', status: '启用' },
  { name: 'PLC年度校验', type: '预防维护', equipment: 'PLC-003 控制器', cycle: '每年', nextDate: '2025-01-15', responsible: '赵工', status: '停用' },
])
</script>
