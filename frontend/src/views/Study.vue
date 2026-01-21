<template>
  <div class="study-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">学习考试</h1>
        <p class="page-subtitle">学习资源、考试信息、培训课程</p>
      </div>

      <!-- 学习考试列表 -->
      <div class="study-list">
        <div class="study-item" v-for="item in studyList" :key="item.id" @click="viewStudy(item.id)">
          <div class="study-image" v-if="item.coverImage">
            <img :src="item.coverImage" :alt="item.title">
          </div>
          <div class="study-content">
            <div class="study-header">
              <el-tag v-if="item.isTop" type="danger" size="small">置顶</el-tag>
              <el-tag type="info" size="small">{{ item.categoryName }}</el-tag>
              <span class="study-date">{{ item.publishedAt }}</span>
            </div>
            <h3 class="study-title">{{ item.title }}</h3>
            <p class="study-summary">{{ item.summary }}</p>
            <div class="study-footer">
              <span class="study-author">{{ item.author }}</span>
              <span class="study-views"><el-icon><View /></el-icon> {{ item.views }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-if="!loading && studyList.length === 0" description="暂无学习考试信息" />

      <!-- 分页 -->
      <div class="pagination-wrapper" v-if="total > 0">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { View } from '@element-plus/icons-vue'
import { getNewsList } from '@/api/modules/news'
import type { News, PaginationParams, PaginationResponse } from '@/types'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const studyList = ref<News[]>([])
const total = ref(0)
const pagination = reactive<PaginationParams>({
  page: 1,
  pageSize: 10
})

// 获取学习考试列表
const fetchStudyList = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      category: 'study' // 使用分类编码筛选学习考试
    }
    const response = await getNewsList(params)
    const pageData = response.data as unknown as PaginationResponse<News>
    studyList.value = pageData?.list || []
    total.value = pageData?.total || 0
  } catch (error) {
    console.error('获取学习考试列表失败:', error)
    ElMessage.error('获取学习考试列表失败')
  } finally {
    loading.value = false
  }
}

// 查看学习考试详情
const viewStudy = (id: number) => {
  router.push(`/study/${id}`)
}

// 每页数量变化
const handleSizeChange = () => {
  pagination.page = 1
  fetchStudyList()
}

// 页码变化
const handleCurrentChange = () => {
  fetchStudyList()
}

onMounted(() => {
  fetchStudyList()
})
</script>

<style lang="scss" scoped>
.study-page {
  padding: 40px 0;
  min-height: calc(100vh - 160px);

  .page-header {
    text-align: center;
    margin-bottom: 40px;

    .page-title {
      font-size: 32px;
      font-weight: 500;
      color: var(--text-primary);
      margin-bottom: 10px;
    }

    .page-subtitle {
      font-size: 16px;
      color: var(--text-regular);
    }
  }

  .study-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  .study-item {
    display: flex;
    gap: 20px;
    padding: 24px;
    background: #fff;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s;
    box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.1);
    }

    .study-image {
      width: 240px;
      height: 160px;
      flex-shrink: 0;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        border-radius: 4px;
      }
    }

    .study-content {
      flex: 1;
      display: flex;
      flex-direction: column;
    }

    .study-header {
      display: flex;
      align-items: center;
      gap: 10px;
      margin-bottom: 12px;

      .study-date {
        font-size: 13px;
        color: var(--text-secondary);
        margin-left: auto;
      }
    }

    .study-title {
      font-size: 20px;
      font-weight: 500;
      color: var(--text-primary);
      line-height: 1.4;
      margin-bottom: 12px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .study-summary {
      font-size: 14px;
      color: var(--text-regular);
      line-height: 1.6;
      margin-bottom: 15px;
      flex: 1;
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .study-footer {
      display: flex;
      justify-content: space-between;
      font-size: 13px;
      color: var(--text-secondary);
    }
  }

  .pagination-wrapper {
    display: flex;
    justify-content: center;
    margin-top: 40px;
  }
}

@media (max-width: 768px) {
  .study-page {
    padding: 20px 0;

    .study-item {
      flex-direction: column;

      .study-image {
        width: 100%;
        height: 200px;
      }
    }
  }
}
</style>

