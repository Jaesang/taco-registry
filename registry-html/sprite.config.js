module.exports = function(target,ratio) {

  return {
    cssFormat: 'scss',
    imgPath: '../images/'+target+'.png',
    cssTemplate: 'client/sass/_sprite/sprite'+ratio+'x.scss.mustache',
    src: 'client/images/'+target+'/*.png',
    dest: 'client/dist/'+target+'/images/'+target+'.png',
    destCss: 'client/sass/'+target+'/import/common/sprite.scss',
    padding: 4
  };

};
