<template>
  <div class="forbidden-page">
    <div class="forbidden-content">
      <el-icon :size="80" color="#ff4d4f"><Warning /></el-icon>
      <h1>403</h1>
      <p class="forbidden-text">抱歉，您没有权限访问此页面</p>
      <p class="forbidden-desc">请联系管理员获取相应权限</p>
      <el-button type="primary" @click="$router.push('/dashboard')">返回首页</el-button>
    </div>
  </div>
</template>

<script setup>
</script>

<style scoped>
.forbidden-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
}

.forbidden-content {
  text-align: center;
}

.forbidden-content h1 {
  font-size: 72px;
  font-weight: 700;
  color: #333;
  margin: 16px 0 8px;
}

.forbidden-text {
  font-size: 18px;
  color: #666;
  margin-bottom: 8px;
}

.forbidden-desc {
  font-size: 14px;
  color: #999;
  margin-bottom: 24px;
}
</style>
