<template>
  <div>
    <el-page-header @back="$router.back()" style="margin-bottom:16px">
      <template #content><span style="font-size:16px;font-weight:600">{{ isEdit ? '编辑设备' : '新增设备' }}</span></template>
    </el-page-header>

    <el-card shadow="never">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width:800px" v-loading="loading">
        <el-divider content-position="left">基本信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="设备编号" prop="code">
              <el-input v-model="form.code" placeholder="如 CNC-001" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入设备名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="设备类型" prop="type">
              <el-select v-model="form.type" placeholder="请选择" style="width:100%">
                <el-option label="数控机床" value="数控机床" />
                <el-option label="工业机器人" value="工业机器人" />
                <el-option label="输送设备" value="输送设备" />
                <el-option label="PLC控制器" value="PLC控制器" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格型号" prop="model">
              <el-input v-model="form.model" placeholder="请输入规格型号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="制造商">
              <el-input v-model="form.manufacturer" placeholder="请输入制造商" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出厂日期">
              <el-date-picker v-model="form.manufactureDate" type="date" placeholder="选择日期" style="width:100%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">位置与责任</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所在位置" prop="location">
              <el-select v-model="form.location" placeholder="请选择" style="width:100%">
                <el-option label="A区-生产线" value="A区-生产线" />
                <el-option label="B区-装配线" value="B区-装配线" />
                <el-option label="C区-仓储" value="C区-仓储" />
                <el-option label="D区-检测" value="D区-检测" />
                <el-option label="E区-办公" value="E区-办公" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="责任人">
              <el-input v-model="form.responsible" placeholder="请输入责任人" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">采购信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="采购日期">
              <el-date-picker v-model="form.purchaseDate" type="date" placeholder="选择日期" style="width:100%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="采购价格">
              <el-input-number v-model="form.price" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">附件与备注</el-divider>
        <el-form-item label="附件上传">
          <el-upload
            drag
            action="#"
            :auto-upload="false"
            :file-list="fileList"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            multiple
            :limit="5"
          >
            <el-icon :size="40" style="color:#c0c4cc"><UploadFilled /></el-icon>
            <div style="color:#666;margin-top:8px">将文件拖到此处，或<em style="color:#1890FF">点击上传</em></div>
            <template #tip>
              <div style="color:#999;font-size:12px;margin-top:8px">支持 PDF、Word、Excel、图片等格式，单个文件不超过 20MB</div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item label="备注说明">
          <el-input v-model="form.remark" type="textarea" :rows="4" placeholder="请输入备注信息..." />
        </el-form-item>

        <el-form-item>
          <div style="display:flex;gap:12px">
            <el-button @click="$router.back()">取消</el-button>
            <el-button :loading="savingDraft" @click="handleSaveDraft">保存草稿</el-button>
            <el-button type="primary" :loading="submitting" @click="handleSubmit">提交</el-button>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createEquipment, updateEquipment, getEquipmentDetail } from '../../api/equipment'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const loading = ref(false)
const submitting = ref(false)
const savingDraft = ref(false)
const isEdit = computed(() => !!route.params.id)
const fileList = ref([])

const form = reactive({
  code: '', name: '', type: '', model: '', manufacturer: '',
  manufactureDate: '', location: '', responsible: '',
  purchaseDate: '', price: 0, remark: ''
})

const rules = {
  code: [{ required: true, message: '请输入设备编号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择设备类型', trigger: 'change' }],
  model: [{ required: true, message: '请输入规格型号', trigger: 'blur' }],
  location: [{ required: true, message: '请选择所在位置', trigger: 'change' }]
}

function handleFileChange(file, uploadFileList) {
  const maxSize = 20 * 1024 * 1024 // 20MB
  if (file.size > maxSize) {
    ElMessage.warning('文件大小不能超过 20MB')
    uploadFileList.pop()
    return
  }
  fileList.value = uploadFileList
}

function handleFileRemove(file, uploadFileList) {
  fileList.value = uploadFileList
}

async function loadDetail() {
  const id = route.params.id
  if (!id) return
  loading.value = true
  try {
    const res = await getEquipmentDetail(id)
    if (res.code === 200 && res.data) {
      Object.assign(form, res.data)
    }
  } catch {
    ElMessage.error('加载设备信息失败')
  } finally {
    loading.value = false
  }
}

async function handleSubmit() {
  await formRef.value?.validate()
  submitting.value = true
  try {
    if (isEdit.value) {
      const res = await updateEquipment(route.params.id, form)
      if (res.code === 200) {
        ElMessage.success('修改成功')
        router.back()
      } else {
        ElMessage.error(res.message || '修改失败')
      }
    } else {
      const res = await createEquipment(form)
      if (res.code === 200) {
        ElMessage.success('新增成功')
        router.back()
      } else {
        ElMessage.error(res.message || '新增失败')
      }
    }
  } catch {
    ElMessage.error('操作失败')
  } finally {
    submitting.value = false
  }
}

async function handleSaveDraft() {
  savingDraft.value = true
  try {
    const draftData = { ...form, status: 'draft' }
    if (isEdit.value) {
      const res = await updateEquipment(route.params.id, draftData)
      if (res.code === 200) {
        ElMessage.success('草稿已保存')
      } else {
        ElMessage.error(res.message || '保存失败')
      }
    } else {
      const res = await createEquipment(draftData)
      if (res.code === 200) {
        ElMessage.success('草稿已保存')
        router.back()
      } else {
        ElMessage.error(res.message || '保存失败')
      }
    }
  } catch {
    ElMessage.error('保存失败')
  } finally {
    savingDraft.value = false
  }
}

onMounted(loadDetail)
</script>
