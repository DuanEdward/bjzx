import request from '../index'
import type { Member, ApiResponse } from '@/types'

// 申请会员
export function applyMember(data: {
  name: string
  gender: string
  birthDate: string
  title?: string
  company: string
  phone: string
  email: string
  research: string
  level: number
  introduction?: string
}) {
  return request.post<ApiResponse<null>>('/member/apply', data)
}

// 获取会员列表
export function getMemberList(params?: {
  page: number
  pageSize: number
  level?: number
  status?: number
}) {
  return request.get<ApiResponse<any>>('/member/list', { params })
}