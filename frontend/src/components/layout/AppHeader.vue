<template>
  <header class="app-header">
    <div class="container">
      <div class="header-content">
        <div class="logo">
          <!-- <img src="@/assets/images/logo.png" alt="北京中兴建机职业技能鉴定中心" v-if="false"> -->
          <h1>北京中兴建机职业技能鉴定中心</h1>
        </div>
        <div class="header-right">
          <nav class="nav-menu">
            <el-menu mode="horizontal" :default-active="activeIndex" :router="true">
              <el-menu-item index="/">首页</el-menu-item>
              <el-menu-item index="/news">新闻中心</el-menu-item>
              <el-menu-item index="/notice">通知公告</el-menu-item>
              <el-menu-item index="/policy">政策法规</el-menu-item>
              <el-menu-item index="/industry">行业动态</el-menu-item>
              <el-menu-item index="/study">学习考试</el-menu-item>
              <el-menu-item index="/certificate-intro">证书说明</el-menu-item>
              <el-menu-item index="/certificate-query">证书查询</el-menu-item>
            </el-menu>
          </nav>
          <div class="header-actions">
            <el-button type="text" @click="showFeedbackDialog = true">投诉反馈</el-button>
            <el-button type="text" @click="showContactDialog = true">联系我们</el-button>
          </div>
        </div>
      </div>

      <!-- 投诉反馈对话框 -->
      <el-dialog v-model="showFeedbackDialog" title="投诉反馈" width="500px">
        <el-form :model="feedbackForm" :rules="feedbackRules" ref="feedbackFormRef" label-width="80px">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="feedbackForm.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="feedbackForm.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="反馈内容" prop="content">
            <el-input
              v-model="feedbackForm.content"
              type="textarea"
              :rows="5"
              placeholder="请输入反馈内容"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showFeedbackDialog = false">取消</el-button>
          <el-button type="primary" @click="submitFeedback" :loading="feedbackLoading">提交</el-button>
        </template>
      </el-dialog>

      <!-- 联系我们对话框 -->
      <el-dialog v-model="showContactDialog" title="联系我们" width="500px">
        <el-form :model="contactForm" :rules="contactRules" ref="contactFormRef" label-width="80px">
          <el-form-item label="姓名" prop="name">
            <el-input v-model="contactForm.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="contactForm.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="留言内容" prop="message">
            <el-input
              v-model="contactForm.message"
              type="textarea"
              :rows="5"
              placeholder="请输入留言内容"
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showContactDialog = false">取消</el-button>
          <el-button type="primary" @click="submitContact" :loading="contactLoading">提交</el-button>
        </template>
      </el-dialog>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { request } from '@/api'

const route = useRoute()
const activeIndex = computed(() => route.path)

// 投诉反馈
const showFeedbackDialog = ref(false)
const feedbackFormRef = ref<FormInstance>()
const feedbackLoading = ref(false)
const feedbackForm = ref({
  name: '',
  phone: '',
  content: ''
})

const feedbackRules: FormRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  content: [{ required: true, message: '请输入反馈内容', trigger: 'blur' }]
}

const submitFeedback = async () => {
  if (!feedbackFormRef.value) return
  await feedbackFormRef.value.validate(async (valid) => {
    if (valid) {
      feedbackLoading.value = true
      try {
        await request.post('/feedback', feedbackForm.value)
        ElMessage.success('提交成功，我们会尽快处理')
        showFeedbackDialog.value = false
        feedbackForm.value = { name: '', phone: '', content: '' }
      } catch (error) {
        ElMessage.error('提交失败，请稍后重试')
      } finally {
        feedbackLoading.value = false
      }
    }
  })
}

// 联系我们
const showContactDialog = ref(false)
const contactFormRef = ref<FormInstance>()
const contactLoading = ref(false)
const contactForm = ref({
  name: '',
  phone: '',
  message: ''
})

const contactRules: FormRules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  message: [{ required: true, message: '请输入留言内容', trigger: 'blur' }]
}

const submitContact = async () => {
  if (!contactFormRef.value) return
  await contactFormRef.value.validate(async (valid) => {
    if (valid) {
      contactLoading.value = true
      try {
        await request.post('/contact', contactForm.value)
        ElMessage.success('提交成功，我们会尽快联系您')
        showContactDialog.value = false
        contactForm.value = { name: '', phone: '', message: '' }
      } catch (error) {
        ElMessage.error('提交失败，请稍后重试')
      } finally {
        contactLoading.value = false
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.app-header {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 1000;

  .header-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 80px;
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 20px;
  }

  .header-actions {
    display: flex;
    gap: 10px;

    .el-button {
      color: var(--text-primary);
      font-size: 14px;

      &:hover {
        color: var(--primary-color);
      }
    }
  }

  .logo {
    display: flex;
    align-items: center;

    img {
      height: 50px;
      margin-right: 15px;
    }

    h1 {
      font-size: 24px;
      font-weight: 500;
      color: var(--text-primary);
    }
  }

  .nav-menu {
    :deep(.el-menu) {
      border-bottom: none;

      .el-menu-item {
        font-size: 16px;
        height: 80px;
        line-height: 80px;

        &:hover {
          background-color: var(--background-base);
        }

        &.is-active {
          color: var(--primary-color);
          border-bottom-color: var(--primary-color);
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .app-header {
    .header-content {
      height: 60px;
      flex-direction: column;
      padding: 10px 0;
    }

    .logo {
      h1 {
        font-size: 20px;
      }
    }

    .nav-menu {
      width: 100%;

      :deep(.el-menu) {
        justify-content: center;

        .el-menu-item {
          font-size: 14px;
          height: 40px;
          line-height: 40px;
        }
      }
    }
  }
}
</style>