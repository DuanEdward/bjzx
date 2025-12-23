// 基础响应类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// Banner类型
export interface Banner {
  id: number
  title: string
  image: string
  link?: string
  sort: number
  status: 0 | 1 // 0-禁用 1-启用
  createdAt: string
  updatedAt: string
}

// 新闻类型
export interface News {
  id: number
  title: string
  summary: string
  content: string
  coverImage?: string
  author: string
  views: number
  categoryId: number
  categoryName: string
  status: 0 | 1
  isTop: 0 | 1
  publishedAt: string
  createdAt: string
  updatedAt: string
}

// 活动类型
export interface Activity {
  id: number
  title: string
  summary: string
  content: string
  coverImage?: string
  location: string
  startTime: string
  endTime: string
  registrationDeadline?: string
  maxParticipants?: number
  currentParticipants: number
  status: 0 | 1 // 0-未开始 1-进行中 2-已结束
  isRegistrationOpen: 0 | 1
  createdAt: string
  updatedAt: string
}

// 会员类型
export interface Member {
  id: number
  name: string
  title?: string
  company?: string
  email?: string
  phone?: string
  avatar?: string
  introduction?: string
  level: 1 | 2 | 3 // 1-普通会员 2-高级会员 3-理事
  status: 0 | 1 // 0-待审核 1-已通过
  joinedAt: string
}

// 资源类型
export interface Resource {
  id: number
  title: string
  description?: string
  fileUrl: string
  fileName: string
  fileSize: number
  downloadCount: number
  categoryId: number
  categoryName: string
  status: 0 | 1
  createdAt: string
  updatedAt: string
}

// 分页参数
export interface PaginationParams {
  page: number
  pageSize: number
}

// 分页响应
export interface PaginationResponse<T> {
  list: T[]
  total: number
  page: number
  pageSize: number
}