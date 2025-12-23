<template>
  <div class="dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6" :xs="12" v-for="stat in statsData" :key="stat.title">
        <div class="stat-card">
          <div class="stat-icon" :style="{ background: stat.color }">
            <el-icon :size="32"><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ stat.value }}</div>
            <div class="stat-title">{{ stat.title }}</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :span="12" :xs="24">
        <div class="chart-card">
          <div class="card-header">
            <h3>新闻发布趋势</h3>
          </div>
          <div class="chart-content">
            <v-chart :option="newsChartOption" style="height: 300px" />
          </div>
        </div>
      </el-col>
      <el-col :span="12" :xs="24">
        <div class="chart-card">
          <div class="card-header">
            <h3>活动参与情况</h3>
          </div>
          <div class="chart-content">
            <v-chart :option="activityChartOption" style="height: 300px" />
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 最新动态 -->
    <el-row :gutter="20" class="latest-row">
      <el-col :span="12" :xs="24">
        <div class="latest-card">
          <div class="card-header">
            <h3>最新新闻</h3>
            <el-button type="text" @click="$router.push('/content/news')">查看更多</el-button>
          </div>
          <div class="latest-list">
            <div class="latest-item" v-for="item in latestNews" :key="item.id">
              <div class="latest-info">
                <h4>{{ item.title }}</h4>
                <p>{{ item.publishedAt }}</p>
              </div>
              <el-tag :type="item.status === 1 ? 'success' : 'info'">
                {{ item.status === 1 ? '已发布' : '草稿' }}
              </el-tag>
            </div>
          </div>
        </div>
      </el-col>
      <el-col :span="12" :xs="24">
        <div class="latest-card">
          <div class="card-header">
            <h3>会员申请</h3>
            <el-button type="text" @click="$router.push('/member/applications')">查看更多</el-button>
          </div>
          <div class="latest-list">
            <div class="latest-item" v-for="item in latestApplications" :key="item.id">
              <div class="latest-info">
                <h4>{{ item.name }} - {{ item.company }}</h4>
                <p>{{ item.appliedAt }}</p>
              </div>
              <el-tag :type="getStatusType(item.status)">
                {{ getStatusText(item.status) }}
              </el-tag>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { User, Document, Calendar, TrendCharts } from '@element-plus/icons-vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, BarChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'

// 注册必要的组件
use([
  CanvasRenderer,
  LineChart,
  BarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

// 统计数据
const statsData = ref([
  {
    title: '新闻总数',
    value: 156,
    icon: Document,
    color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    title: '活动总数',
    value: 42,
    icon: Calendar,
    color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    title: '会员总数',
    value: 1256,
    icon: User,
    color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  },
  {
    title: '资源总数',
    value: 89,
    icon: TrendCharts,
    color: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
  }
])

// 最新新闻
const latestNews = ref([
  { id: 1, title: '河北省计算机学会2024年年会通知', publishedAt: '2024-01-10', status: 1 },
  { id: 2, title: '关于举办人工智能技术研讨会的通知', publishedAt: '2024-01-08', status: 1 },
  { id: 3, title: '学会荣获2023年度优秀组织奖', publishedAt: '2024-01-05', status: 1 },
  { id: 4, title: '2024年工作计划（草案）', publishedAt: '2024-01-03', status: 0 }
])

// 最新申请
const latestApplications = ref([
  { id: 1, name: '张三', company: '河北大学', appliedAt: '2024-01-10', status: 0 },
  { id: 2, name: '李四', company: '燕山大学', appliedAt: '2024-01-09', status: 1 },
  { id: 3, name: '王五', company: '河北科技大学', appliedAt: '2024-01-08', status: 0 },
  { id: 4, name: '赵六', company: '石家庄铁道大学', appliedAt: '2024-01-07', status: 1 }
])

// 新闻趋势图表配置
const newsChartOption = ref({
  title: {
    text: '最近12个月新闻发布数量'
  },
  tooltip: {
    trigger: 'axis'
  },
  xAxis: {
    type: 'category',
    data: ['2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月', '1月']
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      data: [8, 12, 15, 10, 18, 20, 14, 16, 22, 18, 25, 15],
      type: 'line',
      smooth: true,
      itemStyle: {
        color: '#409eff'
      }
    }
  ]
})

// 活动参与图表配置
const activityChartOption = ref({
  title: {
    text: '活动参与人数统计'
  },
  tooltip: {
    trigger: 'axis',
    axisPointer: {
      type: 'shadow'
    }
  },
  xAxis: {
    type: 'category',
    data: ['学术会议', '技术培训', '研讨会', '会员大会', '专题讲座']
  },
  yAxis: {
    type: 'value'
  },
  series: [
    {
      data: [120, 85, 95, 150, 65],
      type: 'bar',
      itemStyle: {
        color: '#67c23a'
      }
    }
  ]
})

// 获取状态类型
const getStatusType = (status: number) => {
  const types = { 0: 'warning', 1: 'success', 2: 'danger' }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: number) => {
  const texts = { 0: '待审核', 1: '已通过', 2: '已拒绝' }
  return texts[status] || '未知'
}

onMounted(() => {
  // 初始化数据
})
</script>

<style lang="scss" scoped>
.dashboard {
  .stats-row {
    margin-bottom: 20px;
  }

  .stat-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    display: flex;
    align-items: center;
    gap: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);

    .stat-icon {
      width: 80px;
      height: 80px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
    }

    .stat-content {
      .stat-number {
        font-size: 32px;
        font-weight: bold;
        color: #303133;
        line-height: 1;
        margin-bottom: 5px;
      }

      .stat-title {
        font-size: 14px;
        color: #909399;
      }
    }
  }

  .charts-row {
    margin-bottom: 20px;
  }

  .chart-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;

      h3 {
        font-size: 16px;
        font-weight: 500;
        color: #303133;
        margin: 0;
      }
    }
  }

  .latest-row {
    margin-bottom: 20px;
  }

  .latest-card {
    background: #fff;
    border-radius: 8px;
    padding: 20px;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
    height: 400px;
    display: flex;
    flex-direction: column;

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;

      h3 {
        font-size: 16px;
        font-weight: 500;
        color: #303133;
        margin: 0;
      }
    }

    .latest-list {
      flex: 1;
      overflow-y: auto;

      .latest-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 12px 0;
        border-bottom: 1px solid #f0f0f0;

        &:last-child {
          border-bottom: none;
        }

        .latest-info {
          flex: 1;

          h4 {
            font-size: 14px;
            color: #303133;
            margin-bottom: 5px;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          p {
            font-size: 12px;
            color: #909399;
            margin: 0;
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .dashboard {
    .stat-card {
      margin-bottom: 20px;

      .stat-icon {
        width: 60px;
        height: 60px;
      }

      .stat-content {
        .stat-number {
          font-size: 24px;
        }
      }
    }

    .latest-card {
      margin-bottom: 20px;
      height: 300px;
    }
  }
}
</style>