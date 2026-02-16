<template>
  <div class="error-page">
    <div class="error-content">
      <h1>404</h1>
      <h2>Page Not Found</h2>
      <p>The page you are looking for doesn't exist.</p>
      <el-button type="primary" @click="$router.push('/')">
        Go Home
      </el-button>
    </div>
  </div>
</template>

<style scoped>
.error-page {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f5f7fa;
}

.error-content {
  text-align: center;
}

.error-content h1 {
  font-size: 120px;
  color: #409eff;
  margin: 0;
}

.error-content h2 {
  font-size: 30px;
  color: #303133;
  margin: 20px 0;
}

.error-content p {
  color: #606266;
  margin-bottom: 30px;
}
</style>
