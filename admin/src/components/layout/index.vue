<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <div class="sidebar" :class="{ collapsed: isCollapsed }">
      <div class="logo">
        <!-- <img src="@/assets/logo.png" alt="Logo" v-if="false"> -->
        <h2 v-show="!isCollapsed">管理后台</h2>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapsed"
        :router="true"
        background-color="#2c3e50"
        text-color="#ecf0f1"
        active-text-color="#409eff"
      >
        <template v-for="route in menuRoutes" :key="route.path">
          <el-sub-menu v-if="route.children && route.children.length > 1" :index="route.path">
            <template #title>
              <el-icon><component :is="route.meta?.icon" /></el-icon>
              <span>{{ route.meta?.title }}</span>
            </template>
            <el-menu-item
              v-for="child in route.children"
              :key="child.path"
              :index="child.path"
              v-show="!child.meta?.hidden"
            >
              {{ child.meta?.title }}
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="route.children?.[0].path || route.path">
            <el-icon><component :is="route.meta?.icon" /></el-icon>
            <span>{{ route.meta?.title }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </div>

    <!-- 主内容区 -->
    <div class="main-container">
      <!-- 顶部导航 -->
      <div class="navbar">
        <div class="navbar-left">
          <el-button type="text" @click="toggleSidebar">
            <el-icon><Expand v-if="isCollapsed" /><Fold v-else /></el-icon>
          </el-button>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item v-for="item in breadcrumbList" :key="item.path">
              {{ item.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="navbar-right">
          <el-dropdown>
            <div class="user-info">
              <el-avatar :size="32">Admin</el-avatar>
              <span class="username">管理员</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人中心</el-dropdown-item>
                <el-dropdown-item @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>

      <!-- 页面内容 -->
      <div class="page-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Expand, Fold, ArrowDown } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const isCollapsed = ref(false)

// 获取当前激活的菜单
const activeMenu = computed(() => route.path)

// 获取菜单路由
const menuRoutes = computed(() => {
  return router.options.routes
    .filter(route => !route.meta?.hidden && route.path !== '/login')
    .map(route => ({
      ...route,
      children: route.children?.filter(child => !child.meta?.hidden)
    }))
})

// 获取面包屑
const breadcrumbList = computed(() => {
  const breadcrumbs = [{ title: '首页', path: '/dashboard' }]
  const matched = route.matched.filter(item => item.meta?.title)

  matched.forEach(item => {
    if (item.path !== '/') {
      breadcrumbs.push({
        title: item.meta?.title as string,
        path: item.path
      })
    }
  })

  return breadcrumbs
})

// 切换侧边栏
const toggleSidebar = () => {
  isCollapsed.value = !isCollapsed.value
}

// 退出登录
const handleLogout = () => {
  ElMessageBox.confirm('确认退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.removeItem('token')
    router.push('/login')
  })
}
</script>

<style lang="scss" scoped>
.admin-layout {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 220px;
  background-color: #2c3e50;
  transition: width 0.3s;

  &.collapsed {
    width: 64px;
  }

  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 20px;
    background: rgba(0, 0, 0, 0.1);

    h2 {
      color: #fff;
      font-size: 18px;
      margin: 0;
    }
  }

  :deep(.el-menu) {
    border-right: none;
  }
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.navbar {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);

  .navbar-left {
    display: flex;
    align-items: center;
    gap: 20px;
  }

  .navbar-right {
    .user-info {
      display: flex;
      align-items: center;
      gap: 10px;
      cursor: pointer;

      .username {
        font-size: 14px;
        color: #606266;
      }
    }
  }
}

.page-content {
  flex: 1;
  padding: 20px;
  background-color: #f0f2f5;
  overflow-y: auto;
}
</style>