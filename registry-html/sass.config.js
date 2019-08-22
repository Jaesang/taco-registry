module.exports = function(target) {

  return {
      files: [{
          src: 'client/sass/'+target+'/style.scss',
          dest: 'client/front/'+target+'/css/'+target+'.css'
      }],
      options: {
          outputStyle: 'compressed',
          sourceMap: true,
          sourceMapEmbed: true
      }
  };
};
