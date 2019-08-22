module.exports = function(grunt) {
    // SASS Config
    var sassConfig = require('./sass.config.js');
    // SPRITE SMITH Config
    var spriteConfig = require('./sprite.config.js');

    // GRUNT
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),

        sass: {
            'taco': sassConfig('taco')
        },

        sprite: {
            'taco': spriteConfig('taco','1')
        },

        watch: {
            all: {
                files: [
                    'client/images/**/*.png',
                    'client/sass/**/*.scss'
                ],
                tasks: ['sprite', 'sass']
            },
            options: {
                livereload: true
            }
        }
    });

    grunt.loadNpmTasks('grunt-sass');
    grunt.loadNpmTasks('grunt-spritesmith');
    grunt.loadNpmTasks('grunt-contrib-watch');

    grunt.registerTask('default', ['sprite', 'sass']);
};
