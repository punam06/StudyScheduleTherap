const path = require('path');

module.exports = {
  mode: 'development',
  entry: {
    react: './src/main/resources/static/js/react/index.js',
    vue: './src/main/resources/static/js/vue/index.js'
  },
  output: {
    path: path.resolve(__dirname, 'src/main/resources/static/dist'),
    filename: '[name].bundle.js'
  },
  module: {
    rules: [
      {
        test: /\.jsx?$/,
        exclude: /node_modules/,
        use: {
          loader: 'babel-loader',
          options: {
            presets: ['@babel/preset-env', '@babel/preset-react']
          }
        }
      },
      {
        test: /\.vue$/,
        loader: 'vue-loader'
      },
      {
        test: /\.css$/,
        use: ['style-loader', 'css-loader', 'postcss-loader']
      }
    ]
  },
  resolve: {
    extensions: ['.js', '.jsx', '.vue'],
    alias: {
      vue: 'vue/dist/vue.esm-bundler.js'
    }
  },
  devServer: {
    static: {
      directory: path.join(__dirname, 'src/main/resources/static'),
    },
    compress: true,
    port: 3000,
    hot: true
  }
};
