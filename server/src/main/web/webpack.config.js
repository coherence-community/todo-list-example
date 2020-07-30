// webpack.config.js
const webpack = require('webpack');
const path    = require('path');

module.exports = {
  entry:   path.join(__dirname, 'src', 'index.js'),
  output:  {
    path:     path.join(__dirname, 'src', 'static'),
    filename: 'app.js'
  },
  module:  {
    loaders: [
      {
        test:   /\.js$/,
        loader: 'babel-loader',
        query:  {
          cacheDirectory: 'babel_cache',
          presets:        ['react', 'es2015', 'stage-0']
        }
      }
    ]
  },
  plugins: [
    new webpack.DefinePlugin({
      'process.env.NODE_ENV': JSON.stringify(process.env.NODE_ENV)
    }),
    new webpack.EnvironmentPlugin([
       'USER',
     ]),
    new webpack.optimize.DedupePlugin(),
    new webpack.optimize.OccurenceOrderPlugin(),
    new webpack.optimize.UglifyJsPlugin({
      compress:  {warnings: false},
      mangle:    true,
      sourcemap: false,
      beautify:  false,
      dead_code: true
    })
  ]
};
