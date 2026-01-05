<template>
  <div class="activity-edit">
    <div class="page-header">
      <div class="header-left">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/content/activities' }">活动管理</el-breadcrumb-item>
          <el-breadcrumb-item>{{ isEdit ? '编辑活动' : '新增活动' }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ isEdit ? '编辑活动' : '新增活动' }}</h2>
      </div>
      <div class="header-right">
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">
          保存
        </el-button>
      </div>
    </div>

    <el-card class="form-card" shadow="never">
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="120px"
        size="default"
      >
        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="活动标题" prop="title">
              <el-input
                v-model="formData.title"
                placeholder="请输入活动标题"
                maxlength="200"
                show-word-limit
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="活动地点" prop="location">
              <el-input
                v-model="formData.location"
                placeholder="请输入活动地点"
                maxlength="200"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker
                v-model="formData.startTime"
                type="datetime"
                placeholder="选择开始时间"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker
                v-model="formData.endTime"
                type="datetime"
                placeholder="选择结束时间"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="24">
          <el-col :span="12">
            <el-form-item label="报名截止时间">
              <el-date-picker
                v-model="formData.registrationDeadline"
                type="datetime"
                placeholder="选择报名截止时间（可选）"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大参与人数">
              <el-input-number
                v-model="formData.maxParticipants"
                :min="0"
                placeholder="0表示不限制"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="封面图片">
          <el-input
            v-model="formData.coverImage"
            placeholder="请输入封面图片URL"
            maxlength="500"
          />
        </el-form-item>

        <el-form-item label="活动摘要" prop="summary">
          <el-input
            v-model="formData.summary"
            type="textarea"
            :rows="3"
            placeholder="请输入活动摘要"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="活动内容" prop="content">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="15"
            placeholder="请输入活动内容"
          />
        </el-form-item>

        <el-row :gutter="24">
          <el-col :span="8">
            <el-form-item label="活动状态" prop="status">
              <el-radio-group v-model="formData.status">
                <el-radio :label="0">未开始</el-radio>
                <el-radio :label="1">进行中</el-radio>
                <el-radio :label="2">已结束</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="是否开放报名">
              <el-switch
                v-model="formData.isRegistrationOpen"
                :active-value="1"
                :inactive-value="0"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance } from 'element-plus'
import request from '@/api'

const router = useRouter()
const route = useRoute()

const formRef = ref<FormInstance>()
const saving = ref(false)

// 判断是编辑还是新增
const isEdit = computed(() => {
  return !!route.params.id && route.params.id !== 'create'
})

const activityId = computed(() => {
  return isEdit.value ? Number(route.params.id) : null
})

// 表单数据
const formData = reactive({
  title: '',
  summary: '',
  content: '',
  coverImage: '',
  location: '',
  startTime: '',
  endTime: '',
  registrationDeadline: '',
  maxParticipants: null as number | null,
  status: 0,
  isRegistrationOpen: 0
})

// 表单验证规则
const formRules = {
  title: [
    { required: true, message: '请输入活动标题', trigger: 'blur' },
    { min: 2, max: 200, message: '标题长度在 2 到 200 个字符', trigger: 'blur' }
  ],
  summary: [
    { required: true, message: '请输入活动摘要', trigger: 'blur' },
    { max: 500, message: '摘要长度不能超过 500 个字符', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入活动内容', trigger: 'blur' }
  ],
  location: [
    { required: true, message: '请输入活动地点', trigger: 'blur' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择活动状态', trigger: 'change' }
  ]
}

// 获取活动详情
const fetchActivityDetail = async () => {
  if (!isEdit.value || !activityId.value) return

  try {
    const result = await request.get(`/activities/${activityId.value}`)
    const activity = result.data
    Object.assign(formData, {
      title: activity.title || '',
      summary: activity.summary || '',
      content: activity.content || '',
      coverImage: activity.coverImage || '',
      location: activity.location || '',
      startTime: activity.startTime || '',
      endTime: activity.endTime || '',
      registrationDeadline: activity.registrationDeadline || '',
      maxParticipants: activity.maxParticipants || null,
      status: activity.status ?? 0,
      isRegistrationOpen: activity.isRegistrationOpen ?? 0
    })
  } catch (error: any) {
    console.error('获取活动详情失败:', error)
  }
}

// 保存
const handleSave = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()

    // 验证时间逻辑
    if (formData.startTime && formData.endTime) {
      if (new Date(formData.startTime) > new Date(formData.endTime)) {
        ElMessage.error('开始时间不能大于结束时间')
        return
      }
    }

    saving.value = true

    if (isEdit.value) {
      await request.put(`/activities/${activityId.value}`, formData)
    } else {
      await request.post('/activities', formData)
    }
    
    ElMessage.success(isEdit.value ? '更新成功' : '创建成功')
    router.push('/content/activities')
  } catch (error: any) {
    console.error('保存失败:', error)
  } finally {
    saving.value = false
  }
}

// 取消
const handleCancel = () => {
  router.push('/content/activities')
}

// 页面加载时获取数据
onMounted(() => {
  if (isEdit.value) {
    fetchActivityDetail()
  }
})
</script>

<style lang="scss" scoped>
.activity-edit {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 20px;

    .header-left {
      .el-breadcrumb {
        margin-bottom: 8px;
      }

      h2 {
        margin: 0;
        font-size: 24px;
        font-weight: 500;
        color: #303133;
      }
    }
  }

  .form-card {
    max-width: 1200px;
  }
}
</style>
