const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  
  // 开发服务器配置
  devServer: {
    port: 8082,
    open: true,
    // 代理配置 - 将 API 和静态资源请求转发到后端
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        pathRewrite: {
          '^/api': '/api'
        }
      },
      '/upload': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        pathRewrite: {
          '^/upload': '/upload'
        }
      }
    }
  },
  
  // 生产环境配置
  productionSourceMap: false,
  
  // 打包配置
  configureWebpack: {
    // 性能提示
    performance: {
      hints: false
    }
  }
})
