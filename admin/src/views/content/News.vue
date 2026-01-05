<template>
  <div class="news-page">
    <div class="page-header">
      <div class="header-left">
        <h2>新闻管理</h2>
        <p>管理系统中的新闻内容</p>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新增新闻
        </el-button>
      </div>
    </div>

    <!-- 搜索表单 -->
    <el-card class="search-card" shadow="never">
      <el-form
        :model="searchForm"
        inline
        @submit.prevent="handleSearch"
        label-width="80px"
      >
        <el-form-item label="标题">
          <el-input
            v-model="searchForm.keyword"
            placeholder="请输入新闻标题"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="分类">
          <el-select
            v-model="searchForm.categoryId"
            placeholder="请选择分类"
            clearable
            style="width: 200px"
          >
            <el-option label="全部" :value="null" />
            <el-option
              v-for="cat in categories"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 150px"
          >
            <el-option label="全部" :value="null" />
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
        :data="newsList"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="author" label="作者" width="120" />
        <el-table-column prop="views" label="浏览量" width="100" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="置顶" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.isTop === 1" type="danger" size="small">置顶</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="publishedAt" label="发布时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import request from '@/api'

const router = useRouter()

// 类型定义
interface News {
  id: number
  title: string
  summary: string
  content: string
  coverImage?: string
  author: string
  views: number
  categoryId: number
  categoryName?: string
  status: number
  isTop: number
  publishedAt: string
  createdAt: string
  updatedAt: string
}

interface NewsCategory {
  id: number
  name: string
  code: string
}

// 搜索表单
const searchForm = reactive({
  keyword: '',
  categoryId: null as number | null,
  status: null as number | null
})

// 分页配置
const pagination = reactive({
  current: 1,
  size: 10
})

// 数据
const loading = ref(false)
const total = ref(0)
const newsList = ref<News[]>([])
const categories = ref<NewsCategory[]>([])

// 获取新闻分类列表
const fetchCategories = async () => {
  try {
    // 这里需要调用获取分类的API，如果没有，可以硬编码
    // 暂时使用硬编码的分类
    categories.value = [
      { id: 1, name: '学会动态', code: 'society' },
      { id: 2, name: '行业资讯', code: 'industry' },
      { id: 3, name: '通知公告', code: 'notice' },
      { id: 4, name: '政策法规', code: 'policy' },
      { id: 5, name: '学习考试', code: 'study' }
    ]
  } catch (error) {
    console.error('获取分类失败:', error)
  }
}

// 获取新闻列表
const fetchNewsList = async () => {
  try {
    loading.value = true
    const params: any = {
      page: pagination.current,
      pageSize: pagination.size
    }

    if (searchForm.keyword) {
      params.keyword = searchForm.keyword
    }
    if (searchForm.categoryId) {
      params.categoryId = searchForm.categoryId
    }
    if (searchForm.status !== null) {
      params.status = searchForm.status
    }

    const result = await request.get('/news/list', { params })
    newsList.value = result.data.list || []
    total.value = result.data.total || 0
  } catch (error: any) {
    console.error('查询失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchNewsList()
}

// 重置
const handleReset = () => {
  searchForm.keyword = ''
  searchForm.categoryId = null
  searchForm.status = null
  pagination.current = 1
  fetchNewsList()
}

// 新增
const handleCreate = () => {
  router.push('/content/news/create')
}

// 编辑
const handleEdit = (row: News) => {
  router.push(`/content/news/${row.id}/edit`)
}

// 删除
const handleDelete = (row: News) => {
  ElMessageBox.confirm(
    `确定要删除新闻"${row.title}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await request.delete(`/news/${row.id}`)
      ElMessage.success('删除成功')
      fetchNewsList()
    } catch (error: any) {
      console.error('删除失败:', error)
    }
  })
}

// 分页大小改变
const handleSizeChange = (size: number) => {
  pagination.size = size
  pagination.current = 1
  fetchNewsList()
}

// 当前页改变
const handleCurrentChange = (current: number) => {
  pagination.current = current
  fetchNewsList()
}

// 页面加载时获取数据
onMounted(() => {
  fetchCategories()
  fetchNewsList()
})
</script>

<style lang="scss" scoped>
.news-page {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 20px;

    .header-left {
      h2 {
        margin: 0 0 8px 0;
        font-size: 24px;
        font-weight: 500;
        color: #303133;
      }

      p {
        margin: 0;
        color: #909399;
        font-size: 14px;
      }
    }
  }

  .search-card {
    margin-bottom: 20px;
  }

  .table-card {
    .pagination-wrapper {
      margin-top: 20px;
      display: flex;
      justify-content: flex-end;
    }
  }
}
</style>
