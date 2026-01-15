<template>
  <footer class="app-footer">
    <div class="container">
      <div class="footer-content">
        <div class="footer-section">
          <h3>北京中兴建机职业技能鉴定中心</h3>
          <p>Beijing Zhongxing Construction Machinery Vocational Skills Appraisal Center</p>
          <p>致力于职业技能鉴定与人才培养</p>
        </div>
        <div class="footer-section">
          <h4>快速链接</h4>
          <ul>
            <li><router-link to="/">首页</router-link></li>
            <li><router-link to="/news">新闻中心</router-link></li>
            <li><router-link to="/notice">通知公告</router-link></li>
            <li><router-link to="/policy">政策法规</router-link></li>
            <li><router-link to="/industry">行业动态</router-link></li>
            <li><router-link to="/study">学习考试</router-link></li>
            <li><router-link to="/certificate/intro">证书说明</router-link></li>
            <li><router-link to="/certificate/query">证书查询</router-link></li>
          </ul>
        </div>
      </div>
      <div class="footer-bottom">
        <p>&copy; {{ currentYear }} 北京中兴建机职业技能鉴定中心 版权所有</p>
        <p><a href="https://beian.miit.gov.cn" target="_blank">京ICP备2025158024号-1</a></p>
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const currentYear = computed(() => new Date().getFullYear())
</script>

<style lang="scss" scoped>
.app-footer {
  background: #2c3e50;
  color: #ecf0f1;
  padding: 40px 0 20px;

  .footer-content {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 30px;
    margin-bottom: 30px;
  }

  .footer-section {
    h3 {
      font-size: 20px;
      margin-bottom: 15px;
      color: #fff;
    }

    h4 {
      font-size: 16px;
      margin-bottom: 10px;
      color: #fff;
    }

    p {
      margin-bottom: 8px;
      font-size: 14px;
      display: flex;
      align-items: center;
      gap: 8px;
    }

    ul {
      list-style: none;

      li {
        margin-bottom: 8px;

        a {
          color: #bdc3c7;
          font-size: 14px;

          &:hover {
            color: #fff;
          }
        }
      }
    }

    .qrcode {
      text-align: center;

      img {
        width: 100px;
        height: 100px;
        margin-bottom: 10px;
        background: #fff;
        padding: 5px;
        border-radius: 4px;
      }

      p {
        font-size: 12px;
      }
    }
  }

  .footer-bottom {
    text-align: center;
    padding-top: 20px;
    border-top: 1px solid #34495e;

    p {
      font-size: 13px;
      color: #95a5a6;
      margin-bottom: 5px;

      a {
        color: #95a5a6;

        &:hover {
          color: #bdc3c7;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .app-footer {
    padding: 30px 0 15px;

    .footer-content {
      gap: 20px;
    }
  }
}
</style>