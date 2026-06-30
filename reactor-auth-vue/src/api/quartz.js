import request from '@/utils/request'

export const getQuartzJobs = (params) => request({ url: '/api/quartz-jobs', method: 'get', params })
export const getQuartzJobById = (id) => request({ url: `/api/quartz-jobs/${id}`, method: 'get' })
export const createQuartzJob = (data) => request({ url: '/api/quartz-jobs', method: 'post', data })
export const updateQuartzJob = (id, data) => request({ url: `/api/quartz-jobs/${id}`, method: 'put', data })
export const deleteQuartzJob = (id) => request({ url: `/api/quartz-jobs/${id}`, method: 'delete' })
export const pauseJob = (id) => request({ url: `/api/quartz-jobs/${id}/pause`, method: 'post' })
export const resumeJob = (id) => request({ url: `/api/quartz-jobs/${id}/resume`, method: 'post' })
export const triggerJob = (id) => request({ url: `/api/quartz-jobs/${id}/trigger`, method: 'post' })
