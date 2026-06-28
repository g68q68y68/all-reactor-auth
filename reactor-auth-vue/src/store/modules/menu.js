import { defineStore } from 'pinia'
import { getMenuTree } from '@/api/menu'

export const useMenuStore = defineStore('menu', {
  state: () => ({
    menuTree: [],
    dynamicRoutesLoaded: false
  }),

  actions: {
    async fetchMenuTree() {
      const tree = await getMenuTree()
      this.menuTree = tree || []
      return this.menuTree
    },

    setDynamicRoutesLoaded(value) {
      this.dynamicRoutesLoaded = value
    }
  }
})
