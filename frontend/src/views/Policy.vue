<template>
  <div class="policy-page">
    <div class="container">
      <div class="page-header">
        <h1 class="page-title">政策法规</h1>
        <p class="page-subtitle">了解国家和地方相关政策法规</p>
      </div>

      <!-- 政策法规列表 -->
      <div class="policy-list">
        <div class="policy-item" v-for="policy in policyList" :key="policy.id" @click="viewPolicy(policy.id)">
          <div class="policy-image" v-if="policy.coverImage">
            <img :src="policy.coverImage" :alt="policy.title">
          </div>
          <div class="policy-content">
            <div class="policy-header">
              <el-tag v-if="policy.isTop" type="danger" size="small">置顶</el-tag>
              <el-tag type="info" size="small">{{ policy.categoryName }}</el-tag>
              <span class="policy-date">{{ policy.publishedAt }}</span>
            </div>
            <h3 class="policy-title">{{ policy.title }}</h3>
            <p class="policy-summary">{{ policy.summary }}</p>
            <div class="policy-footer">
              <span class="policy-author">{{ policy.author }}</span>
              <span class="policy-views"><el-icon><View /></el-icon> {{ policy.views }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty v-if="!loading && policyList.length === 0" description="暂无政策法规" />

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
import type { News, PaginationParams } from '@/types'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const policyList = ref<News[]>([])
const total = ref(0)
const pagination = reactive<PaginationParams>({
  page: 1,
  pageSize: 10
})

// 获取政策法规列表
const fetchPolicyList = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.page,
      pageSize: pagination.pageSize,
      keyword: '政策法规' // 可以通过关键词或分类筛选
    }
    const response = await getNewsList(params)
    policyList.value = response.data?.list || []
    total.value = response.data?.total || 0
  } catch (error) {
    console.error('获取政策法规列表失败:', error)
    ElMessage.error('获取政策法规列表失败')
  } finally {
    loading.value = false
  }
}

// 查看政策法规详情
const viewPolicy = (id: number) => {
  router.push(`/policy/${id}`)
}

// 每页数量变化
const handleSizeChange = () => {
  pagination.page = 1
  fetchPolicyList()
}

// 页码变化
const handleCurrentChange = () => {
  fetchPolicyList()
}

onMounted(() => {
  fetchPolicyList()
})
</script>

<style lang="scss" scoped>
.policy-page {
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

  .policy-list {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  .policy-item {
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

    .policy-image {
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

    .policy-content {
      flex: 1;
      display: flex;
      flex-direction: column;
    }

    .policy-header {
      display: flex;
      align-items: center;
      gap: 10px;
      margin-bottom: 12px;

      .policy-date {
        font-size: 13px;
        color: var(--text-secondary);
        margin-left: auto;
      }
    }

    .policy-title {
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

    .policy-summary {
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

    .policy-footer {
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
  .policy-page {
    padding: 20px 0;

    .policy-item {
      flex-direction: column;

      .policy-image {
        width: 100%;
        height: 200px;
      }
    }
  }
}
</style>

