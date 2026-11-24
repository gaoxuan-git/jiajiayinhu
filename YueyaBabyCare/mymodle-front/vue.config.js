const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  
  // 开发服务器配置
  devServer: {
    port: 8081,
    open: true,
    // 代理配置
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
  },
  
  // Webpack链式配置
  chainWebpack: config => {
    // 生产环境优化
    if (process.env.NODE_ENV === 'production') {
      // 代码压缩
      config.optimization.minimize(true)
      
      // 分割代码
      config.optimization.splitChunks({
        chunks: 'all',
        cacheGroups: {
          libs: {
            name: 'chunk-libs',
            test: /[\\/]node_modules[\\/]/,
            priority: 10,
            chunks: 'initial'
          },
          elementPlus: {
            name: 'chunk-element-plus',
            priority: 20,
            test: /[\\/]node_modules[\\/]element-plus[\\/]/
          }
        }
      })
    }
  }
})
