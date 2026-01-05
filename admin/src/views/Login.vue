<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h2>北京中兴建机职业技能鉴定中心</h2>
        <p>管理后台</p>
      </div>
      <el-form :model="loginForm" :rules="loginRules" ref="loginFormRef" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            :prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            :prefix-icon="Lock"
            size="large"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            style="width: 100%"
            :loading="loading"
            @click="handleLogin"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/api'

const router = useRouter()
const loginFormRef = ref()
const loading = ref(false)

// 登录表单
const loginForm = reactive({
  username: 'admin',
  password: 'admin123'
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ]
}

// 处理登录
const handleLogin = async () => {
  try {
    await loginFormRef.value.validate()
    loading.value = true

    // 调用登录API
    const result = await request.post('/auth/login', {
      username: loginForm.username,
      password: loginForm.password
    })

    // 保存token和用户信息
    if (result.data.token) {
      localStorage.setItem('token', result.data.token)
      if (result.data.user) {
        localStorage.setItem('user', JSON.stringify(result.data.user))
      }
      ElMessage.success('登录成功')
      router.push('/')
    } else {
      ElMessage.error('登录失败：未获取到token')
    }
  } catch (error: any) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;

  h2 {
    font-size: 24px;
    font-weight: 500;
    color: #303133;
    margin-bottom: 10px;
  }

  p {
    font-size: 16px;
    color: #909399;
  }
}

.login-form {
  .el-form-item {
    margin-bottom: 25px;
  }
}
</style>