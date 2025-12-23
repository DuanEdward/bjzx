<template>
  <header class="app-header">
    <div class="container">
      <div class="header-content">
        <div class="logo">
          <!-- <img src="@/assets/images/logo.png" alt="北京中兴建机职业技能鉴定中心" v-if="false"> -->
          <h1>北京中兴建机职业技能鉴定中心</h1>
        </div>
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
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeIndex = computed(() => route.path)
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