# Capistrano deploy file
# Your application name
set :application, "Rest"
role :web, "anivia"
 
# We're not deploying from a repo, since this is scala and we
# need to compile. Set SCM to none
set :scm, :jenkins
set :repository,  "http://anivia.lil-web.fr:8080/job/Rest-Server/"
set :jenkins_artifact_path, 'archive/dist'
set :scm_username, ENV['JENKINS_USERNAME']
set :scm_password, ENV['JENKINS_PASSWORD']

# Application configuration
set :log_path, "/var/log/rest/application.log"
 
# Configuration.
set :deploy_to, "/var/www/rest"
set :use_sudo, false

# Démarrage et arret de Play. 
namespace :deploy do

    # Zipper. 
    task :unzip do
        run "unzip #{release_path}/*.zip -d #{release_path}/"
    end

    # Override start run current/start. The options are options to play
    # specifying a config file and pidfile
    task :start do
        run "nohup #{release_path}/start \
            -Dhttp.port=9000 \
            -Dconfig.file=/var/www/rest/conf/application.conf \
            -Dpidfile.path=/var/www/rest/running.pid >> #{log_path} 2>&1 &"
    end

    # Handle killing a running instance
    task :stop do
        run "if [ -f /var/www/rest/running.pid ]; then kill -15 `cat /var/www/rest/running.pid`; fi"
    end
end

# Redémarrage
after "deploy:update_code", "deploy:unzip", "deploy:stop", "deploy:start"
