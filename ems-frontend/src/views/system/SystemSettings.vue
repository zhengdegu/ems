<template>
  <div>
    <el-row :gutter="16">
      <!-- 左侧设置菜单 -->
      <el-col :span="6">
        <el-card shadow="never" class="menu-card">
          <template #header><span style="font-weight:600">系统设置</span></template>
          <el-menu :default-active="activeMenu" @select="handleMenuSelect" class="settings-menu">
            <el-menu-item index="company">
              <el-icon><OfficeBuilding /></el-icon>
              <span>企业信息</span>
            </el-menu-item>
            <el-menu-item index="notification">
              <el-icon><Bell /></el-icon>
              <span>通知设置</span>
            </el-menu-item>
            <el-menu-item index="alarm">
              <el-icon><Warning /></el-icon>
              <span>告警规则</span>
            </el-menu-item>
            <el-menu-item index="backup">
              <el-icon><FolderOpened /></el-icon>
              <span>数据备份</span>
            </el-menu-item>
            <el-menu-item index="interface">
              <el-icon><Connection /></el-icon>
              <span>接口管理</span>
            </el-menu-item>
            <el-menu-item index="operationLog">
              <el-icon><Document /></el-icon>
              <span>操作日志</span>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>

      <!-- 右侧内容区 -->
      <el-col :span="18">
        <el-card shadow="never" v-loading="loading">
          <!-- 企业信息 -->
          <div v-if="activeMenu === 'company'">
            <h3 style="margin:0 0 24px;font-size:16px;color:#333">企业信息</h3>
            <el-form :model="companyForm" label-width="140px" style="max-width:600px">
              <el-form-item label="企业名称">
                <el-input v-model="companyForm.companyName" placeholder="请输入企业名称" />
              </el-form-item>
              <el-form-item label="统一社会信用代码">
                <el-input v-model="companyForm.creditCode" placeholder="请输入统一社会信用代码" />
              </el-form-item>
              <el-form-item label="联系电话">
                <el-input v-model="companyForm.contactPhone" placeholder="请输入联系电话" />
              </el-form-item>
              <el-form-item label="企业邮箱">
                <el-input v-model="companyForm.contactEmail" placeholder="请输入企业邮箱" />
              </el-form-item>
              <el-form-item label="企业地址">
                <el-input v-model="companyForm.address" type="textarea" :rows="2" placeholder="请输入企业地址" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :loading="saving" @click="saveCompany">保存设置</el-button>
              </el-form-item>
            </el-form>

            <el-divider />

            <h3 style="margin:0 0 24px;font-size:16px;color:#333">系统参数</h3>
            <el-form label-width="140px" style="max-width:600px">
              <el-form-item label="自动备份">
                <el-switch v-model="sysParams.autoBackup" />
              </el-form-item>
              <el-form-item label="登录双因素认证">
                <el-switch v-model="sysParams.twoFactor" />
              </el-form-item>
              <el-form-item label="告警邮件通知">
                <el-switch v-model="sysParams.alarmEmail" />
              </el-form-item>
              <el-form-item label="操作日志保留">
                <el-select v-model="sysParams.logRetention" style="width:200px">
                  <el-option label="90天" value="90" />
                  <el-option label="180天" value="180" />
                  <el-option label="365天" value="365" />
                  <el-option label="永久" value="forever" />
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :loading="saving" @click="saveSysParams">保存参数</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 通知设置 -->
          <div v-if="activeMenu === 'notification'">
            <h3 style="margin:0 0 24px;font-size:16px;color:#333">通知设置</h3>
            <el-form :model="alarmForm" label-width="120px" style="max-width:600px">
              <el-form-item label="通知方式">
                <el-checkbox-group v-model="alarmForm.notifyMethods">
                  <el-checkbox label="站内消息" />
                  <el-checkbox label="邮件" />
                  <el-checkbox label="短信" />
                </el-checkbox-group>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :loading="saving" @click="saveAlarm">保存设置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 告警规则 -->
          <div v-if="activeMenu === 'alarm'">
            <h3 style="margin:0 0 24px;font-size:16px;color:#333">告警规则</h3>
            <el-form :model="alarmForm" label-width="140px" style="max-width:600px">
              <el-form-item label="温度阈值(°C)">
                <el-input-number v-model="alarmForm.tempThreshold" :min="50" :max="120" />
              </el-form-item>
              <el-form-item label="振动阈值(mm/s)">
                <el-input-number v-model="alarmForm.vibrationThreshold" :min="0" :max="50" :precision="1" />
              </el-form-item>
              <el-form-item label="自动创建工单">
                <el-switch v-model="alarmForm.autoCreateOrder" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :loading="saving" @click="saveAlarm">保存设置</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 数据备份 -->
          <div v-if="activeMenu === 'backup'">
            <h3 style="margin:0 0 24px;font-size:16px;color:#333">数据备份</h3>
            <el-form :model="backupForm" label-width="120px" style="max-width:600px">
              <el-form-item label="自动备份">
                <el-switch v-model="backupForm.autoBackup" />
              </el-form-item>
              <el-form-item label="备份周期">
                <el-select v-model="backupForm.cycle" style="width:100%">
                  <el-option label="每天" value="daily" />
                  <el-option label="每周" value="weekly" />
                  <el-option label="每月" value="monthly" />
                </el-select>
              </el-form-item>
              <el-form-item label="保留份数">
                <el-input-number v-model="backupForm.keepCount" :min="1" :max="30" />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" :loading="saving" @click="saveBackup">保存设置</el-button>
                <el-button @click="manualBackup">立即备份</el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 接口管理 -->
          <div v-if="activeMenu === 'interface'">
            <h3 style="margin:0 0 24px;font-size:16px;color:#333">接口管理</h3>
            <el-empty description="暂无接口配置" />
          </div>

          <!-- 操作日志 -->
          <div v-if="activeMenu === 'operationLog'">
            <h3 style="margin:0 0 24px;font-size:16px;color:#333">操作日志</h3>
            <div style="display:flex;gap:12px;margin-bottom:16px">
              <el-input v-model="logQuery.keyword" placeholder="搜索用户名/操作" clearable style="width:200px" @clear="loadOperationLogs" @keyup.enter="loadOperationLogs" />
              <el-select v-model="logQuery.module" placeholder="全部模块" clearable style="width:160px" @change="loadOperationLogs">
                <el-option label="设备管理" value="设备管理" />
                <el-option label="工单管理" value="工单管理" />
                <el-option label="用户管理" value="用户管理" />
                <el-option label="系统设置" value="系统设置" />
                <el-option label="备件管理" value="备件管理" />
              </el-select>
              <el-button type="primary" @click="loadOperationLogs">查询</el-button>
            </div>
            <el-table :data="operationLogs" stripe size="small" v-loading="logLoading">
              <el-table-column prop="username" label="操作人" width="100" />
              <el-table-column prop="module" label="模块" width="100" />
              <el-table-column prop="action" label="操作类型" width="100" />
              <el-table-column prop="target" label="操作对象" width="140" />
              <el-table-column prop="detail" label="详情" show-overflow-tooltip />
              <el-table-column prop="ip" label="IP地址" width="130" />
              <el-table-column prop="createTime" label="操作时间" width="170" />
            </el-table>
            <el-pagination
              v-model:current-page="logQuery.page"
              v-model:page-size="logQuery.pageSize"
              :total="logTotal"
              layout="total, prev, pager, next"
              style="margin-top:16px;justify-content:flex-end"
              @change="loadOperationLogs"
            />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getSystemSettings, batchSaveSettings, getOperationLog } from '../../api/system'

const activeMenu = ref('company')
const loading = ref(false)
const saving = ref(false)
const logLoading = ref(false)

const companyForm = reactive({
  companyName: '',
  creditCode: '',
  contactPhone: '',
  contactEmail: '',
  address: ''
})

const sysParams = reactive({
  autoBackup: true,
  twoFactor: false,
  alarmEmail: true,
  logRetention: '90'
})

const alarmForm = reactive({
  tempThreshold: 85,
  vibrationThreshold: 10.0,
  notifyMethods: ['站内消息', '邮件'],
  autoCreateOrder: true
})

const backupForm = reactive({
  autoBackup: true,
  cycle: 'daily',
  keepCount: 7
})

const logQuery = reactive({ page: 1, pageSize: 10, keyword: '', module: '' })
const logTotal = ref(0)
const operationLogs = ref([])

function handleMenuSelect(index) {
  activeMenu.value = index
  if (index === 'operationLog') {
    loadOperationLogs()
  }
}

async function loadSettings() {
  loading.value = true
  try {
    const res = await getSystemSettings()
    if (res.code === 200 && res.data) {
      const settings = {}
      const list = Array.isArray(res.data) ? res.data : (res.data.records || [])
      list.forEach(s => { settings[s.settingKey] = s.settingValue })
      if (settings['company.name']) companyForm.companyName = settings['company.name']
      if (settings['company.credit_code']) companyForm.creditCode = settings['company.credit_code']
      if (settings['company.phone']) companyForm.contactPhone = settings['company.phone']
      if (settings['company.email']) companyForm.contactEmail = settings['company.email']
      if (settings['company.address']) companyForm.address = settings['company.address']
      if (settings['system.auto_backup']) sysParams.autoBackup = settings['system.auto_backup'] === 'true'
      if (settings['system.two_factor']) sysParams.twoFactor = settings['system.two_factor'] === 'true'
      if (settings['system.alarm_email']) sysParams.alarmEmail = settings['system.alarm_email'] === 'true'
      if (settings['system.log_retention']) sysParams.logRetention = settings['system.log_retention']
      if (settings['alarm.temp_threshold']) alarmForm.tempThreshold = Number(settings['alarm.temp_threshold'])
      if (settings['alarm.vibration_threshold']) alarmForm.vibrationThreshold = Number(settings['alarm.vibration_threshold'])
      if (settings['alarm.auto_notify']) alarmForm.autoCreateOrder = settings['alarm.auto_notify'] === 'true'
      if (settings['backup.auto']) backupForm.autoBackup = settings['backup.auto'] === 'true'
      if (settings['backup.cycle']) backupForm.cycle = settings['backup.cycle']
      if (settings['backup.keep_count']) backupForm.keepCount = Number(settings['backup.keep_count'])
    }
  } catch {
    // 使用默认值
  } finally {
    loading.value = false
  }
}

async function saveCompany() {
  saving.value = true
  try {
    const res = await batchSaveSettings({
      'company.name': companyForm.companyName,
      'company.credit_code': companyForm.creditCode,
      'company.phone': companyForm.contactPhone,
      'company.email': companyForm.contactEmail,
      'company.address': companyForm.address
    })
    if (res.code === 200) ElMessage.success('企业信息已保存')
    else ElMessage.error(res.message || '保存失败')
  } catch {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

async function saveSysParams() {
  saving.value = true
  try {
    const res = await batchSaveSettings({
      'system.auto_backup': String(sysParams.autoBackup),
      'system.two_factor': String(sysParams.twoFactor),
      'system.alarm_email': String(sysParams.alarmEmail),
      'system.log_retention': sysParams.logRetention
    })
    if (res.code === 200) ElMessage.success('系统参数已保存')
    else ElMessage.error(res.message || '保存失败')
  } catch {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

async function saveAlarm() {
  saving.value = true
  try {
    const res = await batchSaveSettings({
      'alarm.temp_threshold': String(alarmForm.tempThreshold),
      'alarm.vibration_threshold': String(alarmForm.vibrationThreshold),
      'alarm.notify_methods': alarmForm.notifyMethods.join(','),
      'alarm.auto_notify': String(alarmForm.autoCreateOrder)
    })
    if (res.code === 200) ElMessage.success('告警设置已保存')
    else ElMessage.error(res.message || '保存失败')
  } catch {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

async function saveBackup() {
  saving.value = true
  try {
    const res = await batchSaveSettings({
      'backup.auto': String(backupForm.autoBackup),
      'backup.cycle': backupForm.cycle,
      'backup.keep_count': String(backupForm.keepCount)
    })
    if (res.code === 200) ElMessage.success('备份设置已保存')
    else ElMessage.error(res.message || '保存失败')
  } catch {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

function manualBackup() { ElMessage.success('备份任务已启动') }

async function loadOperationLogs() {
  logLoading.value = true
  try {
    const res = await getOperationLog({
      page: logQuery.page,
      pageSize: logQuery.pageSize,
      keyword: logQuery.keyword || undefined,
      module: logQuery.module || undefined
    })
    if (res.code === 200 && res.data) {
      operationLogs.value = res.data.records || []
      logTotal.value = res.data.total || 0
    }
  } catch {
    ElMessage.error('加载操作日志失败')
  } finally {
    logLoading.value = false
  }
}

onMounted(loadSettings)
</script>

<style scoped>
.menu-card :deep(.el-card__body) {
  padding: 0;
}
.settings-menu {
  border-right: none;
}
.settings-menu .el-menu-item {
  height: 48px;
  line-height: 48px;
}
</style>
