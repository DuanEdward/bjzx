// 基础响应类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 分页响应
export interface PaginationResponse<T> {
  list: T[]
  total: number
  page: number
  pageSize: number
}

// 分页参数
export interface PaginationParams {
  current?: number
  size?: number
  page?: number
  pageSize?: number
}

