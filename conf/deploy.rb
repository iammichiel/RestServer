# Capistrano deploy file
# Your application name
set :application, "Rest"
 
# We're not deploying from a repo, since this is scala and we
# need to compile. Set SCM to none
set :scm, :none

# Our deploy is to copy the contents of…
set :deploy_via, :copy

# the target directory! In this case repository is really
# a pointer to the directory
set :repository,  "target"
set :log_path, "/var/log/rest/application.log"
 
# You can use multiple here, if that's what you have
role :web, "anivia"

# Configuration.
set :deploy_to, "/var/www/rest"
set :copy_exclude, ["streams", "scala*"]
set :use_sudo, false

# Démarrage et arret de Play. 
namespace :deploy do

    # Override start run current/start. The options are options to play
    # specifying a config file and pidfile
    task :start do
        run "nohup #{release_path}/start \
            -Dhttp.port=9000 \
            -Dconfig.file=/var/www/rest/conf/application.conf \
            -Dpidfile.path=/var/www/rest/running.pid > #{log_path} 2>&1 &"
    end

    # Handle killing a running instance
    task :stop do
        run "if [ -f /var/www/rest/running.pid ]; then kill -15 `cat /var/www/rest/running.pid`; fi"
    end
end

namespace :play do 
    task :compile do 
        system("play clean compile stage")
    end
end

# Compilation 
before "deploy:update", "play:compile"

# Redémarrage
after "deploy:update_code", "deploy:stop", "deploy:start"
