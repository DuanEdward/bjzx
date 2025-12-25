import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 可以在这里添加token等
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    const { data } = response
    if (data.code === 200) {
      return data
    } else {
      console.error('API Error:', data.message)
      return Promise.reject(new Error(data.message || '请求失败'))
    }
  },
  (error) => {
    console.error('Request Error:', error)
    return Promise.reject(error)
  }
)

export default request