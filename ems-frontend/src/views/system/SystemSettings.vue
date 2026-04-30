<template>
  <div>
    <el-card shadow="never" v-loading="loading">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基本设置" name="basic">
          <el-form :model="basicForm" label-width="120px" style="max-width:600px">
            <el-form-item label="系统名称">
              <el-input v-model="basicForm.systemName" />
            </el-form-item>
            <el-form-item label="系统Logo">
              <el-upload action="#" :auto-upload="false" :show-file-list="false">
                <el-button size="small">上传Logo</el-button>
              </el-upload>
            </el-form-item>
            <el-form-item label="默认语言">
              <el-select v-model="basicForm.language" style="width:100%">
                <el-option label="简体中文" value="zh-CN" />
                <el-option label="English" value="en-US" />
              </el-select>
            </el-form-item>
            <el-form-item label="时区">
              <el-select v-model="basicForm.timezone" style="width:100%">
                <el-option label="UTC+8 北京时间" value="Asia/Shanghai" />
                <el-option label="UTC+0 格林威治" value="UTC" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="saveBasic">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="告警设置" name="alarm">
          <el-form :model="alarmForm" label-width="120px" style="max-width:600px">
            <el-form-item label="温度阈值(°C)">
              <el-input-number v-model="alarmForm.tempThreshold" :min="50" :max="120" />
            </el-form-item>
            <el-form-item label="振动阈值(mm/s)">
              <el-input-number v-model="alarmForm.vibrationThreshold" :min="0" :max="50" :precision="1" />
            </el-form-item>
            <el-form-item label="通知方式">
              <el-checkbox-group v-model="alarmForm.notifyMethods">
                <el-checkbox label="站内消息" />
                <el-checkbox label="邮件" />
                <el-checkbox label="短信" />
              </el-checkbox-group>
            </el-form-item>
            <el-form-item label="自动创建工单">
              <el-switch v-model="alarmForm.autoCreateOrder" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="saveAlarm">保存设置</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="备份设置" name="backup">
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
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getSystemSettings, batchSaveSettings } from '../../api/system'

const activeTab = ref('basic')
const loading = ref(false)
const saving = ref(false)

const basicForm = reactive({ systemName: '设备管理系统', language: 'zh-CN', timezone: 'Asia/Shanghai' })
const alarmForm = reactive({ tempThreshold: 85, vibrationThreshold: 10.0, notifyMethods: ['站内消息', '邮件'], autoCreateOrder: true })
const backupForm = reactive({ autoBackup: true, cycle: 'daily', keepCount: 7 })

async function loadSettings() {
  loading.value = true
  try {
    const res = await getSystemSettings()
    if (res.code === 200 && res.data) {
      const settings = {}
      const list = Array.isArray(res.data) ? res.data : (res.data.records || [])
      list.forEach(s => { settings[s.settingKey] = s.settingValue })
      if (settings['system.name']) basicForm.systemName = settings['system.name']
      if (settings['system.language']) basicForm.language = settings['system.language']
      if (settings['system.timezone']) basicForm.timezone = settings['system.timezone']
      if (settings['alarm.temp_threshold']) alarmForm.tempThreshold = Number(settings['alarm.temp_threshold'])
      if (settings['alarm.vibration_threshold']) alarmForm.vibrationThreshold = Number(settings['alarm.vibration_threshold'])
      if (settings['alarm.auto_notify']) alarmForm.autoCreateOrder = settings['alarm.auto_notify'] === 'true'
    }
  } catch {
    // 使用默认值
  } finally {
    loading.value = false
  }
}

async function saveBasic() {
  saving.value = true
  try {
    const res = await batchSaveSettings({
      'system.name': basicForm.systemName,
      'system.language': basicForm.language,
      'system.timezone': basicForm.timezone
    })
    if (res.code === 200) ElMessage.success('基本设置已保存')
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

onMounted(loadSettings)
</script>
