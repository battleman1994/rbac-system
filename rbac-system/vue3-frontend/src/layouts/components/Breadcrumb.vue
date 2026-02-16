<template>
  <el-breadcrumb separator="/">
    <el-breadcrumb-item :to="{ path: '/' }">Home</el-breadcrumb-item>
    <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
      {{ item.title }}
    </el-breadcrumb-item>
  </el-breadcrumb>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'

interface Breadcrumb {
  path: string
  title: string
}

const route = useRoute()
const breadcrumbs = ref<Breadcrumb[]>([])

const getBreadcrumbs = () => {
  const matched = route.matched.filter(item => item.meta?.title)
  breadcrumbs.value = matched.map(item => ({
    path: item.path,
    title: item.meta.title as string
  }))
}

watch(() => route.path, getBreadcrumbs, { immediate: true })
</script>
