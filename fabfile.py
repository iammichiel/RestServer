# coding=UTF-8

from __future__ import with_statement

from fabric.api import *
from fabric.operations import *
from fabric.colors import *
from time import *

import subprocess
import os
import signal


# Configuration technique
env.hosts = ['anivia']
env.use_ssh_config = True

# Configuration application
repo_user = os.environ['REPO_USERNAME']
repo_password = os.environ['REPO_PASSWORD']
repo_url = "http://repo.lil-web.fr/rest-server/"

application_name = "restserver"
install_dir = "/var/www/rest"
release_name = strftime("%Y-%m-%d_%H-%M-%S", gmtime())
release_folder = install_dir + "/" + release_name

def deploy():

    version = prompt(blue("Version a deployer ?"), default="0.1.0")
    version_name = application_name + "-" + version
    zipname =  version_name + ".zip"

    with cd(install_dir):
        print blue("Making release folder : " + release_folder)  
        run("mkdir " + release_name)

    with cd("/tmp"):
        print blue("Cleaning /tmp dir")
        run("rm -rf " + version_name + "*")

        print blue("Fetching the application...")
        run("wget --quiet --user=" + repo_user + " --password=" + repo_password + " " + repo_url + zipname)
        run("unzip " + zipname)

        with cd(version_name):
            print blue("Moving application to release folder")
            run("mv * " + release_folder)

    # Stop current
    with cd(install_dir):

        # Existe il un dossier current et un fichier RUNNING_PID? 
        if os.path.exists(install_dir + "current"):
            print "the folder exists"
            if os.path.exists(install_dir + "current/RUNNING_PID"):
                with cd("current"):
                    print blue("Stopping current application...")
                    run("kill `cat RUNNING_PID`")
            else:
                print "PID file not found"
        else:
            print blue("No current folder running application found...")

    # Start current
    with cd(release_folder):
        print blue("Starting new release...")
        run("chmod +x ./start")
        run("mkdir logs && touch logs/application.log")
        run("nohup sh ./start -DapplyEvolutions.default=true -DapplyDownEvolutions.default=true > ./logs/application.log & sleep 5")

    # Create current link
    with cd(install_dir):
        print blue("Deleting current link...")
        run("rm -rf current")

        print blue("Setting new current link...")
        run("ln -s " + release_name + " current")

    print green("Release deployed!")
    exit()
