<template>
  <div>
    <el-page-header @back="$router.back()" style="margin-bottom:16px">
      <template #content><span style="font-size:16px;font-weight:600">{{ isEdit ? '编辑设备' : '新增设备' }}</span></template>
    </el-page-header>

    <el-card shadow="never">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width:800px">
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
              <el-date-picker v-model="form.manufactureDate" type="date" placeholder="选择日期" style="width:100%" />
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
              <el-date-picker v-model="form.purchaseDate" type="date" placeholder="选择日期" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="采购价格">
              <el-input-number v-model="form.price" :min="0" :precision="2" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注信息" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit">{{ isEdit ? '保存修改' : '提交' }}</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const isEdit = computed(() => !!route.params.id)

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

async function handleSubmit() {
  await formRef.value?.validate()
  ElMessage.success(isEdit.value ? '修改成功' : '新增成功')
  router.back()
}
</script>
