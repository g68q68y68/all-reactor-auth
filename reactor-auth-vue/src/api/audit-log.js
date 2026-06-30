import request from '@/utils/request'

export const getAuditLogs = (params) => request({ url: '/api/audit-logs', method: 'get', params })
